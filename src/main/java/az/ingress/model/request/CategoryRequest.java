package az.ingress.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

import static az.ingress.model.constants.ValidationMessages.CATEGORY_NAME_NOT_BLANK;
import static az.ingress.model.constants.ValidationMessages.CATEGORY_SLUG_NOT_BLANK;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    private Long parentId;
    @NotBlank(message = CATEGORY_NAME_NOT_BLANK)
    private String name;

    @NotBlank(message = CATEGORY_SLUG_NOT_BLANK)
    private String slug;
    private Integer sortOrder;
}
