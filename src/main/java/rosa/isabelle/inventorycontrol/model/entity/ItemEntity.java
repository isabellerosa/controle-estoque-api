package rosa.isabelle.inventorycontrol.model.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity(name = "Item")
public class ItemEntity implements Serializable {

    private static final long serialVersionUID = 7787817532723281743L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(unique = true)
    private String publicId;

    @NotEmpty
    private String name;

    @NotNull
    private BigDecimal price;

    @NotEmpty
    private String sellerId;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StockEntity> stocks;
}
