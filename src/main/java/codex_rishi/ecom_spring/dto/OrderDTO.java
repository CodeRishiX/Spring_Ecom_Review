package codex_rishi.ecom_spring.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private String productName;
    private String imageUrl;
    private String status;
    private String totalAmount;
}
