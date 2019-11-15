package rosa.isabelle.inventorycontrol.model.request;

import lombok.Data;

@Data
public class StockRequestModel {
    private String item;
    private int quantity;
}
