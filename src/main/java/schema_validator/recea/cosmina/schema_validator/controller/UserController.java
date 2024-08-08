package schema_validator.recea.cosmina.schema_validator.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import schema_validator.recea.cosmina.schema_validator.entity.Users;
import schema_validator.recea.cosmina.schema_validator.service.UserAndSchemaService;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private UserAndSchemaService userAndSchemaService;

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody Users user) {
        try {
            Users newUser = userAndSchemaService.addUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied. Only admins can add users.");
        }
    }
    @GetMapping("/users")
    public Set<Users> getUsers() {
        return userAndSchemaService.getUsers();
    }

    @PostMapping("/schemas")
    public ResponseEntity<?> addSchema(@RequestParam String schemaName, @RequestBody String schemaBody) {
        String username = getLoggedinUsername();
        Optional<Users> userOpt = userAndSchemaService.getUserByUsername(username);
        if (userOpt.isPresent()) {
            userAndSchemaService.addSchemaToUser(username, schemaName, schemaBody);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public String welcome() {
        String username = getLoggedinUsername();
        return "welcome " + username;
    }

    private String getLoggedinUsername() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
