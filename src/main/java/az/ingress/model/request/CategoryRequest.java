package az.ingress.model.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {
    Long parentId;
    @NotBlank(message = "Name cannot be null or empty")
    String name;

    @NotBlank(message = "Slug cannot be null or empty")
    String slug;
    Integer sortOrder;
}
