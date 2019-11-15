package rosa.isabelle.inventorycontrol.dto;

import lombok.Data;

import java.util.List;

@Data
public class StockDTO {
    private StoreDTO store;
    private List<StockItemDTO> items;
}
