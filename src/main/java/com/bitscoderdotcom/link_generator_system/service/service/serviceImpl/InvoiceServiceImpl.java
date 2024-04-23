package com.bitscoderdotcom.link_generator_system.service.service.serviceImpl;

import com.bitscoderdotcom.link_generator_system.constant.Status;
import com.bitscoderdotcom.link_generator_system.dto.ApiResponse;
import com.bitscoderdotcom.link_generator_system.dto.EmailDetails;
import com.bitscoderdotcom.link_generator_system.dto.InvoiceDto;
import com.bitscoderdotcom.link_generator_system.entities.Company;
import com.bitscoderdotcom.link_generator_system.entities.Invoice;
import com.bitscoderdotcom.link_generator_system.entities.PaymentLink;
import com.bitscoderdotcom.link_generator_system.exception.ResourceNotFoundException;
import com.bitscoderdotcom.link_generator_system.repository.CompanyRepository;
import com.bitscoderdotcom.link_generator_system.repository.InvoiceRepository;
import com.bitscoderdotcom.link_generator_system.repository.PaymentLinkRepository;
import com.bitscoderdotcom.link_generator_system.service.EmailService;
import com.bitscoderdotcom.link_generator_system.service.service.InvoiceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private final PaymentLinkRepository paymentLinkRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    @Transactional
    public ResponseEntity<ApiResponse<InvoiceDto.Response>> generateInvoice(InvoiceDto invoiceDto, Principal principal) {
        log.info("Generating invoice for company");

        Company company = companyRepository.findUserByCompanyEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "User email", principal.getName()));

        // Create a new invoice
        Invoice invoice = createInvoice(invoiceDto, company);

        invoiceRepository.save(invoice);

        PaymentLink paymentLink = new PaymentLink();
        paymentLink.setInvoice(invoice);
        paymentLinkRepository.save(paymentLink);
        String baseUrl = "http://localhost:8080";
        paymentLink.setUrl(baseUrl + generateUniqueUrl(invoice.getId()));
        paymentLinkRepository.save(paymentLink);

        String messageBody = createMessageBody(invoice, paymentLink);

        // Send the payment link to the customer via email
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(invoice.getCustomerEmail());
        emailDetails.setSubject("Invoice Payment Link");
        emailDetails.setMessageBody(messageBody);
        emailService.sendEmail(emailDetails);

        log.info("Invoice generated successfully for company with id: {}", company.getId());

        InvoiceDto.Response invoiceResponseDto = convertEntityToDto(invoice);

        ApiResponse<InvoiceDto.Response> apiResponse = new ApiResponse<>(
                LocalDateTime.now(),
                UUID.randomUUID().toString(),
                true,
                "Invoice generated successfully",
                invoiceResponseDto
        );

        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public Invoice getInvoiceById(String id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
    }

    @Transactional
    public ResponseEntity<String> processPayment(String linkId, String invoiceId) {
        // Find the payment link by linkId
        PaymentLink paymentLink = paymentLinkRepository.findById(linkId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentLink", "id", linkId));

        if (!paymentLink.getInvoice().getId().equals(invoiceId)) {
            throw new IllegalArgumentException("The provided invoiceId does not match the invoiceId associated with the payment link.");
        }

        Invoice invoice = paymentLink.getInvoice();

        // Check if the invoice has already been paid
        if (invoice.getStatus() == Status.Paid) {
            return new ResponseEntity<>("This invoice has already been paid for.", HttpStatus.BAD_REQUEST);
        }

        // If the invoice has not been paid, process the payment
        invoice.setStatus(Status.Paid);
        invoiceRepository.save(invoice);

        String receipt = generateReceipt(invoice);

        // Send a success email to the customer with the receipt
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(invoice.getCustomerEmail());
        emailDetails.setSubject("Payment Successful");
        emailDetails.setMessageBody(receipt);
        emailService.sendEmail(emailDetails);

        return new ResponseEntity<>("Payment for invoice " + invoice.getId() + " was successful.", HttpStatus.OK);
    }

    private Invoice createInvoice(InvoiceDto invoiceDto, Company company) {
        Invoice invoice = new Invoice();
        invoice.setCompany(company);
        invoice.setCustomerName(invoiceDto.getCustomerName());
        invoice.setCustomerEmail(invoiceDto.getCustomerEmail());
        invoice.setDescription(invoiceDto.getDescription());
        invoice.setQuantity(invoiceDto.getQuantity());
        invoice.setUnitPrice(invoiceDto.getUnitPrice());
        invoice.setTotalAmount(invoiceDto.getTotalAmount());
        invoice.setInvoiceGenerationDate(LocalDate.now());
        invoice.setPaymentDueDate(LocalDate.now().plusDays(1));
        invoice.setStatus(Status.Unpaid);

        return invoiceRepository.save(invoice);
    }

    private String createMessageBody(Invoice invoice, PaymentLink paymentLink) {
        String messageBody = "<html><body>";
        messageBody += "<p>Dear " + invoice.getCustomerName() + ",</p>";
        messageBody += "<p>An invoice has been generated for you. Here are the details:</p>";
        messageBody += "<hr />";
        messageBody += "<p>Description: " + invoice.getDescription() + "</p>";
        messageBody += "<p>Quantity: " + invoice.getQuantity() + "</p>";
        messageBody += "<p>Unit Price: $" + invoice.getUnitPrice() + "</p>";
        messageBody += "<hr />";
        messageBody += "<p>Total Amount: $" + invoice.getTotalAmount() + "</p>";
        messageBody += "<hr />";
        messageBody += "<p>Invoice Date: " + invoice.getInvoiceGenerationDate() + "</p>";
        messageBody += "<p>Payment Due Date: " + invoice.getPaymentDueDate() + "</p>";
        messageBody += "<hr />";
        messageBody += "<p>Please click the following link to view and pay your invoice: <a href=\"" +
                paymentLink.getUrl() + "\">" + paymentLink.getUrl() + "</a></p>";
        messageBody += "<p>Thank you.</p>";
        messageBody += "</body></html>";
        return messageBody;
    }

    private String generateUniqueUrl(String invoiceId) {
        String baseUrl = "/lgsApp/v1";
        return baseUrl + "/invoice/" + invoiceId + "/payment";
    }

    private InvoiceDto.Response convertEntityToDto(Invoice invoice) {
        return new InvoiceDto.Response(
                invoice.getId(),
                invoice.getCustomerName(),
                invoice.getCustomerEmail(),
                invoice.getDescription(),
                invoice.getQuantity(),
                invoice.getUnitPrice(),
                invoice.getTotalAmount(),
                invoice.getInvoiceGenerationDate(),
                invoice.getPaymentDueDate(),
                invoice.getStatus()
        );
    }

    private String generateReceipt(Invoice invoice) {
        Context context = new Context();
        context.setVariable("invoice", invoice);
        context.setVariable("paymentDate", LocalDate.now());
        return templateEngine.process("receipt", context);
    }
}
