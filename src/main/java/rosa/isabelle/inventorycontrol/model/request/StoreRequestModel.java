package rosa.isabelle.inventorycontrol.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StoreRequestModel {
    @JsonProperty("id")
    private String publicId;
    private String name;
}
