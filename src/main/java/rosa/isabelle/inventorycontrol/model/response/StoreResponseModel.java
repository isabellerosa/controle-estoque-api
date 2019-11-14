package rosa.isabelle.inventorycontrol.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StoreResponseModel {
    @JsonProperty("id")
    private String publicId;
    private String name;
}
