package rosa.isabelle.inventorycontrol.model.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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

    @NotBlank
    private String publicId;

    @NotBlank
    private String name;

    private BigDecimal price;

    @NotBlank
    private String sellerId;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StockEntity> stocks;
}
