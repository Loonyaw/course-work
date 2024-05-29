package ua.opnu.bankist.rest;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.opnu.bankist.annotations.LogController;
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
@LogController // Custom annotation to enable logging for this controller
@RequestMapping("/api/users") // Base URL for all endpoints in this controller
public class UserController {

    @Autowired
    private UserService userService; // Injecting the UserService dependency

    @Autowired
    private UserRepository userRepository; // Injecting the UserRepository dependency

    @Autowired
    private TransactionService transactionService; // Injecting the TransactionService dependency

    @Autowired
    private PasswordEncoder passwordEncoder; // Injecting the PasswordEncoder dependency

    @GetMapping
    public List<User> getAllUsers() {
        // Fetch all users
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        // Fetch a user by its ID
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody Map<String, Object> userData) {
        // Create a new user
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
        // Delete a user by its ID
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Account Closed Successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User Not Found"));
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> userExists(@RequestParam(required = false) String username, @RequestParam(required = false) String email) {
        // Check if a user exists by username or email
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
        // Authenticate user and return user ID if successful
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
        // Validate user credentials
        String username = credentials.get("username");
        String password = credentials.get("password");
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.ok(true);
            }
        }
        return ResponseEntity.ok(false);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(@RequestBody Map<String, Object> transferDetails) {
        // Transfer money between users
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
    public ResponseEntity<Map<String, String>> requestLoan(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        // Request a loan for the user
        Object rawAmount = request.get("amount");
        Double amount = null;

        if (rawAmount instanceof Integer) {
            amount = ((Integer) rawAmount).doubleValue();
        } else if (rawAmount instanceof Double) {
            amount = (Double) rawAmount;
        }

        if (amount == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid loan request parameters"));
        }

        try {
            boolean isApproved = transactionService.requestLoan(userId, amount);
            if (isApproved) {
                return ResponseEntity.ok(Map.of("message", "Loan Approved"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Loan Request Failed"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
}
