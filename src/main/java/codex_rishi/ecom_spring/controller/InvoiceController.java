package codex_rishi.ecom_spring.controller;

import codex_rishi.ecom_spring.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    // GET /api/orders/{orderId}/invoice  -> returns PDF
    @GetMapping("/{orderId}/invoice")
    public ResponseEntity<byte[]> getInvoice(@PathVariable Long orderId) {
        try {
            byte[] pdf = invoiceService.generateInvoicePdf(orderId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice-order-" + orderId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
