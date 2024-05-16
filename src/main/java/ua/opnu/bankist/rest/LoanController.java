package ua.opnu.bankist.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.opnu.bankist.service.LoanService;

import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/{loanId}/repay")
    public ResponseEntity<?> repayLoan(@PathVariable Long loanId, @RequestBody Map<String, Double> request) {
        Double repaymentAmount = request.get("amount");
        try {
            loanService.repayLoan(loanId, repaymentAmount);
            return ResponseEntity.ok().body("Loan repaid successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

