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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/json-schema")
@AllArgsConstructor
public class ValidatorClassController {

    @PostMapping("/validator")
    public ResponseEntity<String> createValidation(@RequestBody String request) {
        try {
            InputStream schemaStream = Files.newInputStream(Paths.get("json-Jobschema.json"));
            JsonSchema schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(schemaStream);

            ObjectMapper om = new ObjectMapper();
            JsonNode jsonNode = om.readTree(request);
            Set<ValidationMessage> errors = schema.validate(jsonNode);

            if (!errors.isEmpty()) {
                String errorsCombined = errors.stream()
                        .map(ValidationMessage::getMessage)
                        .collect(Collectors.joining("\n"));
                return ResponseEntity.badRequest().body("Please fix your JSON! \n" + errorsCombined);
            }

            return ResponseEntity.ok("JSON is valid against the schema.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading the schema or JSON: " + e.getMessage());
        }
    }
}
