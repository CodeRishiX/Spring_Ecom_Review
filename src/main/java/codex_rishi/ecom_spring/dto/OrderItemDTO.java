package codex_rishi.ecom_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private String name;
    private double price;
    private int quantity;
    private String imageUrl;


}
