package schema_validator.recea.cosmina.schema_validator.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import schema_validator.recea.cosmina.schema_validator.entity.Schemas;
import schema_validator.recea.cosmina.schema_validator.entity.Users;
import schema_validator.recea.cosmina.schema_validator.service.UserAndSchemaService;

import java.io.IOException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/json-schema")
@AllArgsConstructor
public class ValidatorClassController {

    private UserAndSchemaService userAndSchemaService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/{schemaName}")
    public ResponseEntity<String> createValidation(@PathVariable String schemaName, @RequestBody String request) {
        try {
            String username = getLoggedinUsername();
            Optional<Users> userOpt = userAndSchemaService.getUserByUsername(username);
            if (userOpt.isPresent()) {
                Users user = userOpt.get();
                Optional<Schemas> schemaOpt = userAndSchemaService.getSchemasForUser(username).stream()
                        .filter(schema -> schema.getSchemaName().equals(schemaName))
                        .findFirst();

                if (schemaOpt.isPresent()) {
                    Schemas schemaEntity = schemaOpt.get();
                    JsonNode schemaNode = objectMapper.readTree(schemaEntity.getSchemaBody());
                    JsonSchema schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(schemaNode);

                    JsonNode jsonNode = objectMapper.readTree(request);
                    Set<ValidationMessage> errors = schema.validate(jsonNode);

                    if (!errors.isEmpty()) {
                        String errorsCombined = errors.stream()
                                .map(ValidationMessage::getMessage)
                                .collect(Collectors.joining("\n"));
                        return ResponseEntity.badRequest().body("Please fix your JSON! \n" + errorsCombined);
                    }

                    return ResponseEntity.ok("JSON is valid against the schema.");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Schema not found for this user.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading the schema or JSON: " + e.getMessage());
        }
    }
    private String getLoggedinUsername() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
