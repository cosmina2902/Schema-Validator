package schema_validator.recea.cosmina.schema_validator.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Users {
    private Long id;
    private String username;
    private String password;
    private List<Long> schemas;
}
