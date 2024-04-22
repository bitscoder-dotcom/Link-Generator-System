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
        Invoice invoice = new Invoice();
        invoice.setCompany(company);
        invoice.setCustomerName(invoiceDto.getCustomerName());
        invoice.setCustomerEmail(invoiceDto.getCustomerEmail());
        invoice.setDescription(invoiceDto.getDescription());
        invoice.setQuantity(invoiceDto.getQuantity());
        invoice.setUnitPrice(invoiceDto.getUnitPrice());
        invoice.setTotalAmount(invoiceDto.getTotalAmount());
        invoice.setInvoiceGenerationDate(invoiceDto.getInvoiceGenerationDate());
        invoice.setPaymentDueDate(invoiceDto.getPaymentDueDate());
        invoice.setStatus(Status.Unpaid);

        invoiceRepository.save(invoice);

        PaymentLink paymentLink = new PaymentLink();
        paymentLink.setInvoice(invoice);
        paymentLinkRepository.save(paymentLink);
        String baseUrl = "http://localhost:8080";
        paymentLink.setUrl(baseUrl + generateUniqueUrl(invoice.getId()));
        paymentLinkRepository.save(paymentLink);

        // Send the payment link to the customer via email
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(invoice.getCustomerEmail());
        emailDetails.setSubject("Invoice Payment Link");
        emailDetails.setMessageBody("Please click the following link to view and pay your invoice: <a href=\"" + paymentLink.getUrl() + "\">" + paymentLink.getUrl() + "</a>");
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
    public String processPayment(String linkId, String invoiceId) {
        // Find the payment link by linkId
        PaymentLink paymentLink = paymentLinkRepository.findById(linkId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentLink", "id", linkId));

        if (!paymentLink.getInvoice().getId().equals(invoiceId)) {
            throw new IllegalArgumentException("The provided invoiceId does not match the invoiceId associated with the payment link.");
        }

        Invoice invoice = paymentLink.getInvoice();

        // Check if the invoice has already been paid
        if (invoice.getStatus() == Status.Paid) {
            return "This invoice has already been paid for.";
        }

        // If the invoice has not been paid, process the payment
        invoice.setStatus(Status.Paid);
        invoiceRepository.save(invoice);

        String receipt = generateReceipt(invoice);

        // Send a success email to the customer with the receipt
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(invoice.getCustomerEmail());
        emailDetails.setSubject("Payment Successful, please check your email for details. Thank you and please come again.");
        emailDetails.setMessageBody(receipt);
        emailService.sendEmail(emailDetails);

        return "Payment for invoice " + invoice.getId() + " was successful.";
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
