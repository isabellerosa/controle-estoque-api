package rosa.isabelle.inventorycontrol.dto;

import lombok.Data;

@Data
public class StockItemDTO {
    private ItemDTO item;
    private int quantity;
}
