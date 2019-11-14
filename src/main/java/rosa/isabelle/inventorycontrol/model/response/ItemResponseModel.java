package rosa.isabelle.inventorycontrol.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemResponseModel {
    @JsonProperty("id")
    private String publicId;
    private String name;
    private BigDecimal price;
}
