package rosa.isabelle.inventorycontrol.model.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Data
@Entity(name = "Item")
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(length = 64)
    private String publicId;

    @NotEmpty
    private String name;

    private BigDecimal price;

    @NotEmpty
    private String sellerId;
}
