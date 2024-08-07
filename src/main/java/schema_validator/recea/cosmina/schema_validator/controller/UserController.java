package schema_validator.recea.cosmina.schema_validator.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import schema_validator.recea.cosmina.schema_validator.entity.Users;
import schema_validator.recea.cosmina.schema_validator.service.UserAndSchemaService;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private UserAndSchemaService userAndSchemaService;

    @PostMapping
    public Users addUser(@RequestBody Users user) {
        return userAndSchemaService.addUser(user);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody Users user) {
        Optional<Users> authenticatedUser = userAndSchemaService.authenticate(user.getUsername(), user.getPassword());
        if (authenticatedUser.isPresent()) {
            return ResponseEntity.ok(authenticatedUser.get());
        } else {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    @PostMapping("/{username}/schemas")
    public ResponseEntity<?> addSchema(@PathVariable String username, @RequestParam String schemaName, @RequestBody String schemaBody) {
        Optional<Users> userOpt = userAndSchemaService.getUserByUsername(username);
        if (userOpt.isPresent()) {
            userAndSchemaService.addSchemaToUser(username, schemaName, schemaBody);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
