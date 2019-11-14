package rosa.isabelle.inventorycontrol.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemDTO {
    private String publicId;
    private String name;
    private BigDecimal price;
    private String sellerId;
}
