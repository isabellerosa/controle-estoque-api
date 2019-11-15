package rosa.isabelle.inventorycontrol.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "Stock")
public class StockEntity implements Serializable {

    private static final long serialVersionUID = 1195088353961406555L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "publicId")
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", referencedColumnName = "publicId")
    private ItemEntity item;

    private int quantity;
}
