package rosa.isabelle.inventorycontrol.service;

import rosa.isabelle.inventorycontrol.dto.StockDTO;

public interface StockService {
    StockDTO findStocks(String storeId, int page, int size);
}
