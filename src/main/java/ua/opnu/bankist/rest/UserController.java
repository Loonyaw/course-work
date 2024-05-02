package ua.opnu.bankist.rest;

import org.springframework.http.HttpStatus;
import ua.opnu.bankist.model.User;
import ua.opnu.bankist.repo.UserRepository;
import ua.opnu.bankist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.findUserById(id)
                .map(existingUser -> {
                    user.setId(id);
                    return ResponseEntity.ok(userService.saveUser(user));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(user -> {
                    userService.deleteUser(id);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> userExists(@RequestParam(required = false) String username, @RequestParam(required = false) String email) {
        boolean exists = false;
        if (username != null && userService.existsByUsername(username)) {
            exists = true;
        }
        if (email != null && userService.existsByEmail(email)) {
            exists = true;
        }
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        boolean authenticated = userService.authenticate(credentials.get("username"), credentials.get("password"), credentials.get("pin"));
        if (authenticated) {
            Optional<User> user = userRepository.findByUsername(credentials.get("username"));
            if (user.isPresent()) {
                Long userId = user.get().getId();
                return ResponseEntity.ok().body(Map.of("userId", userId));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch user data.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/validateCredentials")
    public ResponseEntity<Boolean> validateCredentials(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }
}
