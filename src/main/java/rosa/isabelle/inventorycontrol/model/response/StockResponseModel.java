package rosa.isabelle.inventorycontrol.model.response;

import lombok.Data;

import java.util.List;

@Data
public class StockResponseModel {
    private StoreResponseModel store;
    private List<StockItemModel> items;
}
