package ua.opnu.bankist.rest;

import org.springframework.http.HttpStatus;
import ua.opnu.bankist.model.User;
import ua.opnu.bankist.repo.UserRepository;
import ua.opnu.bankist.service.TransactionService;
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

    @Autowired
    private TransactionService transactionService;

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
    public ResponseEntity<User> createUser(@RequestBody Map<String, Object> userData) {
        User user = new User();
        user.setUsername((String) userData.get("username"));
        user.setEmail((String) userData.get("email"));
        user.setPassword((String) userData.get("password"));
        user.setPin((String) userData.get("pin"));
        String currency = (String) userData.get("currency");
        User savedUser = userService.saveUser(user, currency);
        return ResponseEntity.ok(savedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
            // Return a JSON response with a message
            return ResponseEntity.ok(Map.of("message", "Account Closed Successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User Not Found"));
        }
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

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(@RequestBody Map<String, Object> transferDetails) {
        Long fromId = Long.parseLong(transferDetails.get("fromId").toString());
        Long toId = Long.parseLong(transferDetails.get("toId").toString());
        double amount = Double.parseDouble(transferDetails.get("amount").toString());

        boolean transferSuccessful = transactionService.transferMoney(fromId, toId, amount);
        if (transferSuccessful) {
            return ResponseEntity.ok(Map.of("message", "Transfer Successful"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Transfer Failed"));
    }

    @PostMapping("/{userId}/requestLoan")
    public ResponseEntity<Map<String, String>> requestLoan(@PathVariable Long userId, @RequestBody Map<String, Double> request) {
        Double amount = request.get("amount");
        if (amount == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid amount"));
        }

        boolean isApproved = transactionService.requestLoan(userId, amount);
        if (isApproved) {
            return ResponseEntity.ok(Map.of("message", "Loan Approved"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Loan Request Failed"));
    }
}
