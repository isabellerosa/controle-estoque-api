package rosa.isabelle.inventorycontrol.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import rosa.isabelle.inventorycontrol.model.entity.ItemEntity;

@Repository
public interface ItemRepository extends PagingAndSortingRepository<ItemEntity, Long> {
    ItemEntity save(ItemEntity itemEntity);
    ItemEntity findByPublicId(String publicId);
    ItemEntity findByNameAndSellerId(String name, String sellerId);
    Page<ItemEntity> findBySellerId(String sellerId, Pageable pageable);
}
