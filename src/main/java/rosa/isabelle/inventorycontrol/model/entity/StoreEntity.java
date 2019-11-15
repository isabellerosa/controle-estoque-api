package rosa.isabelle.inventorycontrol.model.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@Entity(name = "Store")
public class StoreEntity implements Serializable {

    private static final long serialVersionUID = 3205684501776086095L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(length = 64)
    private String publicId;

    @NotEmpty
    private String name;

    @NotBlank
    @Column(length = 64)
    private String ownerId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "store", fetch = FetchType.LAZY)
    private List<StockEntity> stocks;
}
