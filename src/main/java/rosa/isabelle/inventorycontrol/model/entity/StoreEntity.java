package rosa.isabelle.inventorycontrol.model.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@Entity(name = "Store")
public class StoreEntity implements Serializable {

    private static final long serialVersionUID = 3205684501776086095L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String publicId;

    @NotBlank
    private String name;

    @NotBlank
    private String ownerId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "store", fetch = FetchType.LAZY)
    private List<StockEntity> stocks;
}
