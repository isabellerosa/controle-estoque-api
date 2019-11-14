package rosa.isabelle.inventorycontrol.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemRequestModel {
    private String name;
    private BigDecimal price;
}
