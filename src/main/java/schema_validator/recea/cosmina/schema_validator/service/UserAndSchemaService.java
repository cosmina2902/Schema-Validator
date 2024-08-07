package schema_validator.recea.cosmina.schema_validator.service;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import schema_validator.recea.cosmina.schema_validator.entity.Schemas;
import schema_validator.recea.cosmina.schema_validator.entity.Users;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserAndSchemaService {
    private static final String FILE_PATH = "config.json";
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<Users> users = new ArrayList<>();
    private List<Schemas> schemas = new ArrayList<>();

    public UserAndSchemaService() {
        loadData();
    }

    private void loadData() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists() && file.length() > 0) {
                JsonData jsonData = objectMapper.readValue(file, JsonData.class);
                this.users = jsonData.getUsers();
                this.schemas = jsonData.getSchemas();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read data file", e);
        }
    }

    private void saveData() {
        try {
            JsonData jsonData = new JsonData();
            jsonData.setUsers(this.users);
            jsonData.setSchemas(this.schemas);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), jsonData);
        } catch (IOException e) {
            throw new RuntimeException("Could not write data to file", e);
        }
    }

    public Optional<Users> getUserByUsername(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findFirst();
    }

    public Optional<Users> authenticate(String username, String password) {
        return users.stream().filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password)).findFirst();
    }

    public Users addUser(Users user) {
        user.setId((long) (users.size() + 1));
        users.add(user);
        saveData();
        return user;
    }

    public void addSchemaToUser(String username, String schemaName, String schemaBody) {
        try {
            Optional<Users> userOpt = getUserByUsername(username);
            if (userOpt.isPresent()) {
                Users user = userOpt.get();

                // Parse schemaBody to a JsonNode
                JsonNode schemaNode = objectMapper.readTree(schemaBody);

                // Convert JsonNode back to a formatted JSON string
                String formattedSchemaBody = objectMapper.writeValueAsString(schemaNode);

                Schemas schema = new Schemas();
                schema.setId((long) (schemas.size() + 1));
                schema.setSchemaName(schemaName);
                schema.setSchemaBody(formattedSchemaBody);
                schemas.add(schema);
                user.getSchemas().add(schema.getId());
                saveData();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not parse schema JSON", e);
        }
    }

    public List<Schemas> getSchemasForUser(String username) {
        Optional<Users> userOpt = getUserByUsername(username);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            return schemas.stream().filter(schema -> user.getSchemas().contains(schema.getId())).collect(Collectors.toList());
        }
        return List.of();
    }

    private static class JsonData {
        private List<Users> users = new ArrayList<>();
        private List<Schemas> schemas = new ArrayList<>();

        public List<Users> getUsers() {
            return users;
        }

        public void setUsers(List<Users> users) {
            this.users = users;
        }

        public List<Schemas> getSchemas() {
            return schemas;
        }

        public void setSchemas(List<Schemas> schemas) {
            this.schemas = schemas;
        }
    }
}

