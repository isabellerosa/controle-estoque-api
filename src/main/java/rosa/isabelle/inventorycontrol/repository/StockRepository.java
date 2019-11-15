package rosa.isabelle.inventorycontrol.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import rosa.isabelle.inventorycontrol.model.entity.StockEntity;
import rosa.isabelle.inventorycontrol.model.entity.StoreEntity;

@Repository
public interface StockRepository extends PagingAndSortingRepository<StockEntity, Long> {
    Page<StockEntity> findByStore(StoreEntity store, Pageable pageable);

}
