package codex_rishi.ecom_spring.service;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import codex_rishi.ecom_spring.model.Order;
import codex_rishi.ecom_spring.model.OrderItem;
import codex_rishi.ecom_spring.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class InvoiceService {

    @Autowired
    private OrderRepository orderRepository;

    public byte[] generateInvoicePdf(Long orderId) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document doc = new Document(PageSize.A4, 36, 36, 54, 36);
        PdfWriter.getInstance(doc, baos);
        doc.open();

        // Fonts
        Font titleF = new Font(Font.HELVETICA, 18, Font.BOLD);
        Font headF = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font normal = new Font(Font.HELVETICA, 11);

        // Header
        Paragraph title = new Paragraph("SpringCart Invoice", titleF);
        title.setAlignment(Element.ALIGN_LEFT);
        doc.add(title);

        // meta row
        Paragraph meta = new Paragraph(String.format("Order ID: %d   |   Payment ID: %s", order.getId(),
                order.getPaymentId() == null ? "-" : order.getPaymentId()), normal);
        meta.setSpacingBefore(8);
        doc.add(meta);

        Paragraph dateP = new Paragraph("Date: " + order.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), normal);
        dateP.setSpacingBefore(6);
        doc.add(dateP);

        doc.add(Chunk.NEWLINE);

        // Items table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{6, 2, 2, 2});

        table.addCell(new Phrase("Item", headF));
        table.addCell(new Phrase("Unit Price", headF));
        table.addCell(new Phrase("Qty", headF));
        table.addCell(new Phrase("Total", headF));

        List<OrderItem> items = order.getOrderItems();

        BigDecimal subtotal = BigDecimal.ZERO;

        if (items != null) {
            for (OrderItem it : items) {
                String name = it.getProduct() != null ? it.getProduct().getName() : "Product";
                BigDecimal price = it.getPrice() != null ? it.getPrice() : BigDecimal.ZERO;
                int qty = it.getQuantity();
                BigDecimal total = price.multiply(BigDecimal.valueOf(qty));

                table.addCell(new Phrase(name, normal));
                table.addCell(new Phrase("₹" + price.toString(), normal));
                table.addCell(new Phrase(String.valueOf(qty), normal));
                table.addCell(new Phrase("₹" + total.toString(), normal));

                subtotal = subtotal.add(total);
            }
        }

        doc.add(table);

        doc.add(Chunk.NEWLINE);

        // Summary
        PdfPTable sumTable = new PdfPTable(2);
        sumTable.setWidthPercentage(40);
        sumTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        sumTable.addCell(new Phrase("Subtotal", normal));
        sumTable.addCell(new Phrase("₹" + subtotal.toString(), normal));

        // OPTIONAL: If you saved fees in order entity, use them; otherwise you can compute or leave out
        sumTable.addCell(new Phrase("Grand Total", headF));
        sumTable.addCell(new Phrase("₹" + (order.getTotalAmount() != null ? order.getTotalAmount().toString() : subtotal.toString()), headF));

        doc.add(sumTable);

        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("Thank you for shopping with SpringCart.", normal));

        doc.close();

        return baos.toByteArray();
    }
}
