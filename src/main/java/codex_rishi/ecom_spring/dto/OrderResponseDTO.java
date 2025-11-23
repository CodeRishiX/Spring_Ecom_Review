package codex_rishi.ecom_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private Long orderId;
    private BigDecimal totalAmount;
    private String status;

    private String productName;
    private String imageUrl;
}
