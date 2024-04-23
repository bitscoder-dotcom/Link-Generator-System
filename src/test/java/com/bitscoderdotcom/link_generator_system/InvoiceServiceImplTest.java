package com.bitscoderdotcom.link_generator_system;

import com.bitscoderdotcom.link_generator_system.constant.Status;
import com.bitscoderdotcom.link_generator_system.dto.ApiResponse;
import com.bitscoderdotcom.link_generator_system.dto.InvoiceDto;
import com.bitscoderdotcom.link_generator_system.entities.Company;
import com.bitscoderdotcom.link_generator_system.entities.Invoice;
import com.bitscoderdotcom.link_generator_system.entities.PaymentLink;
import com.bitscoderdotcom.link_generator_system.exception.ResourceNotFoundException;
import com.bitscoderdotcom.link_generator_system.repository.CompanyRepository;
import com.bitscoderdotcom.link_generator_system.repository.InvoiceRepository;
import com.bitscoderdotcom.link_generator_system.repository.PaymentLinkRepository;
import com.bitscoderdotcom.link_generator_system.service.EmailService;
import com.bitscoderdotcom.link_generator_system.service.service.serviceImpl.InvoiceServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.thymeleaf.TemplateEngine;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private PaymentLinkRepository paymentLinkRepository;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Test
    public void testGenerateInvoice() {

        InvoiceDto invoiceDto = new InvoiceDto();
        // Set properties for invoiceDto...
        invoiceDto.setCustomerName("John Eze");
        invoiceDto.setCustomerEmail("johnEze@example.com");
        invoiceDto.setDescription("Test Invoice");
        invoiceDto.setQuantity(10);
        invoiceDto.setUnitPrice(100.00);
        invoiceDto.setTotalAmount(1000.00);
        invoiceDto.setInvoiceGenerationDate(LocalDate.now());
        invoiceDto.setPaymentDueDate(LocalDate.now().plusDays(1));

        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("test@test.com");

        Company company = new Company();
        company.setId("comp12345");
        company.setUserName("testUser");
        company.setPassword("testPassword");
        company.setCompanyEmail("test@test.com");
        company.setCompanyName("Test Company");

        Mockito.when(companyRepository.findUserByCompanyEmail(principal.getName())).thenReturn(Optional.of(company));

        Invoice invoice = new Invoice();
        invoice.setId("inv12345");
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

        Mockito.when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        PaymentLink paymentLink = new PaymentLink();
        paymentLink.setId("url12345");
        paymentLink.setInvoice(invoice);
        paymentLink.setUrl("http://localhost:8080" + "/invoice/" + invoice.getId());

        Mockito.when(paymentLinkRepository.save(any(PaymentLink.class))).thenReturn(paymentLink);

        ResponseEntity<ApiResponse<InvoiceDto.Response>> response = invoiceService.generateInvoice(invoiceDto, principal);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invoice generated successfully", response.getBody().getMessage());
        assertEquals(invoiceDto.getCustomerName(), response.getBody().getData().getCustomerName());
        assertEquals(invoiceDto.getCustomerEmail(), response.getBody().getData().getCustomerEmail());
        assertEquals(invoiceDto.getDescription(), response.getBody().getData().getDescription());
        assertEquals(invoiceDto.getQuantity(), response.getBody().getData().getQuantity());
        assertEquals(invoiceDto.getUnitPrice(), response.getBody().getData().getUnitPrice(), 0.0001);
        assertEquals(invoiceDto.getTotalAmount(), response.getBody().getData().getTotalAmount(), 0.0001);
        assertEquals(invoiceDto.getInvoiceGenerationDate(), response.getBody().getData().getInvoiceGenerationDate());
        assertEquals(invoiceDto.getPaymentDueDate(), response.getBody().getData().getPaymentDueDate());
        assertEquals(Status.Unpaid, response.getBody().getData().getStatus());
    }

    @Test
    public void testProcessPayment() {
        String linkId = "url12345";
        String invoiceId = "inv12345";

        PaymentLink paymentLink = new PaymentLink();
        paymentLink.setId(linkId);

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setStatus(Status.Unpaid);
        paymentLink.setInvoice(invoice);

        Mockito.when(paymentLinkRepository.findById(linkId)).thenReturn(Optional.of(paymentLink));
        Mockito.when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        ResponseEntity<String> responseEntity = invoiceService.processPayment(linkId, invoiceId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Payment for invoice " + invoice.getId() + " was successful.", responseEntity.getBody());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testProcessPaymentWithInvalidLinkId() {

        String invalidLinkId = "invalidLinkId";
        String invoiceId = "inv12345";

        invoiceService.processPayment(invalidLinkId, invoiceId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessPaymentWithMismatchedInvoiceId() {

        String linkId = "url12345";
        String mismatchedInvoiceId = "mismatchedInvoiceId";

        PaymentLink paymentLink = new PaymentLink();
        paymentLink.setId(linkId);

        Invoice invoice = new Invoice();
        invoice.setId("inv12345");
        invoice.setStatus(Status.Unpaid);
        paymentLink.setInvoice(invoice);

        Mockito.when(paymentLinkRepository.findById(linkId)).thenReturn(Optional.of(paymentLink));

        invoiceService.processPayment(linkId, mismatchedInvoiceId);
    }

    @Test
    public void testProcessPaymentWithAlreadyPaidInvoice() {
        String linkId = "url12345";
        String invoiceId = "inv12345";

        PaymentLink paymentLink = new PaymentLink();
        paymentLink.setId(linkId);

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setStatus(Status.Paid);
        paymentLink.setInvoice(invoice);

        Mockito.when(paymentLinkRepository.findById(linkId)).thenReturn(Optional.of(paymentLink));

        ResponseEntity<String> responseEntity = invoiceService.processPayment(linkId, invoiceId);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("This invoice has already been paid for.", responseEntity.getBody());
    }
}

