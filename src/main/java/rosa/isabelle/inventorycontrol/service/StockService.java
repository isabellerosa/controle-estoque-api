package rosa.isabelle.inventorycontrol.service;

import rosa.isabelle.inventorycontrol.dto.StockDTO;
import rosa.isabelle.inventorycontrol.dto.StockItemDTO;

public interface StockService {
    StockItemDTO addStockItem(StockItemDTO stockDTO);
    StockDTO getStock(String storeId, int page, int size);
}
