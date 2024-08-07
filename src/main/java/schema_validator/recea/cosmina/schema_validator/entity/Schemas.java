package schema_validator.recea.cosmina.schema_validator.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Schemas {
    private Long id;
    private String schemaName;
    private String schemaBody;
}
