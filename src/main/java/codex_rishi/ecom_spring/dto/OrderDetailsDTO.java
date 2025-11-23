package codex_rishi.ecom_spring.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsDTO {
    private Long id;
    private String paymentId;
    private String razorpayOrderId;
    private String status;
    private String createdAt;
    private double totalAmount;
    private List<OrderItemDTO> items;

}
