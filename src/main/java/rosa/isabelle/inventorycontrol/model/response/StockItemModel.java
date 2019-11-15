package rosa.isabelle.inventorycontrol.model.response;

import lombok.Data;

@Data
public class StockItemModel {
    private ItemResponseModel item;
    private int quantity;
}
