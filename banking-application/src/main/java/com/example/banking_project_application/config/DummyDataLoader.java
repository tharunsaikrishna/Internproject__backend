package com.example.banking_project_application.config;

import com.example.banking_project_application.entity.*;
import com.example.banking_project_application.repositories.*;
import com.example.banking_project_application.util.AccountIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {

    private final UserRepository userRepo;
    private final AdminRepository adminRepo;
    private final AccountRepository accountRepo;
    private final LoanRequestRepository loanRepo;
    private final ChequeBookRequestRepository chequeRepo;
    private final AccountRequestRepository accountReqRepo;
    private final TransactionRepository txRepo;

    @Override
    public void run(String... args) {

        if (userRepo.count() > 0) {
            System.out.println("Dummy data already exists. Skipping...");
            return;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // --------------------------------------------------------------------
        // USERS (15)
        // --------------------------------------------------------------------
        User[] users = new User[5];
        for (int i = 1; i <= 5; i++) {
            User u = new User();
            u.setUsername("user" + i);
            u.setPassword(encoder.encode("user" + i));
            u.setFullName("User " + i);
            u.setEmail("user" + i + "@test.com");
            u.setPhone("999990000" + i);
            u.setAddress("City " + i);
            u.setStatus(i % 4 == 0 ? User.Status.BLOCKED : User.Status.ACTIVE);

            users[i - 1] = userRepo.save(u);
        }

        // --------------------------------------------------------------------
        // ADMINS (15)
        // --------------------------------------------------------------------
        Admin[] admins = new Admin[5];
        for (int i = 1; i <= 5; i++) {
            Admin a = new Admin();
            a.setUsername("admin" + i);
            a.setPassword(encoder.encode("admin" + i));

            admins[i - 1] = adminRepo.save(a);
        }

        // --------------------------------------------------------------------
        // ACCOUNTS (15) → using unique ID generator
        // --------------------------------------------------------------------
        Account[] accounts = new Account[15];
        for (int i = 1; i <= 15; i++) {

            Account acc = new Account();

            // UNIQUE 4-digit ID
            acc.setAccountId(AccountIdGenerator.generateUniqueId(accountRepo));

            acc.setUser(users[(i - 1) % 5]);
            acc.setAccountType(i % 2 == 0 ? Account.AccountType.PRIMARY : Account.AccountType.SAVINGS);
            acc.setBalance(BigDecimal.valueOf(2000L * i));
            acc.setStatus(i % 4 == 0 ? Account.Status.BLOCKED : Account.Status.ACTIVE);

            accounts[i - 1] = accountRepo.save(acc);
        }

        // --------------------------------------------------------------------
        // LOAN REQUESTS (15) → approve_date ONLY here
        // --------------------------------------------------------------------
        for (int i = 1; i <= 15; i++) {
            LoanRequest lr = new LoanRequest();
            lr.setUser(users[(i - 1) % 5]);
            lr.setLoanAmount(new BigDecimal(10000 + (i * 1000)));
            lr.setLoanType(i % 2 == 0 ? "Home" : "Personal");

            if (i % 3 == 0) { // APPROVED
                lr.setStatus(LoanRequest.Status.APPROVED);
                lr.setAdmin(admins[(i - 1) % 5]);
                lr.setApproveDate(new Date());

            } else if (i % 4 == 0) { // REJECTED

                lr.setStatus(LoanRequest.Status.REJECTED);
                lr.setAdmin(admins[(i - 1) % 5]); // ✔ MUST SAVE admin
                lr.setApproveDate(new Date());


            } else { // PENDING
                lr.setStatus(LoanRequest.Status.PENDING);
                lr.setAdmin(null);
                lr.setApproveDate(null);
            }

            loanRepo.save(lr);
        }

        // --------------------------------------------------------------------
        // CHEQUE BOOK REQUESTS (15)
        // --------------------------------------------------------------------
        for (int i = 1; i <= 15; i++) {
            ChequeBookRequest cb = new ChequeBookRequest();
            cb.setUser(users[(i - 1) % 5]);
            cb.setAccount(accounts[(i - 1) % 5]);

            if (i % 2 == 0) {
                cb.setStatus(ChequeBookRequest.Status.APPROVED);
                cb.setAdmin(admins[(i - 1) % 5]);
            }
            if(i%3==0){
                cb.setStatus(ChequeBookRequest.Status.PENDING);
                cb.setAdmin(null);
            }
            else {
                cb.setStatus(ChequeBookRequest.Status.REJECTED);
                cb.setAdmin(admins[(i - 1) % 5]);
            }

            chequeRepo.save(cb);
        }

        // --------------------------------------------------------------------
        // ACCOUNT REQUESTS (15) → NO approve_date here
        // --------------------------------------------------------------------
        for (int i = 1; i <= 15; i++) {
            AccountRequest ar = new AccountRequest();
            ar.setUser(users[(i - 1) % 5]);
            ar.setAccountType(i % 2 == 0 ? Account.AccountType.PRIMARY : Account.AccountType.SAVINGS);

            if (i % 3 == 0) {
                ar.setStatus(AccountRequest.Status.APPROVED);
                ar.setAdmin(admins[(i - 1) % 5]);

            } else if (i % 4 == 0) {
                ar.setStatus(AccountRequest.Status.REJECTED);
                ar.setAdmin(admins[(i - 1) % 5]);

            } else {
                ar.setStatus(AccountRequest.Status.PENDING);
                ar.setAdmin(null);
            }

            accountReqRepo.save(ar);
        }

        // --------------------------------------------------------------------
        // TRANSACTIONS (15)
        // --------------------------------------------------------------------
        for (int i = 1; i <= 15; i++) {
            Transaction t = new Transaction();
            t.setFromAccount(accounts[(i - 1) % 5]);
            t.setToAccount(accounts[(i) % 5]);
            t.setAmount(new BigDecimal(500 * i));

            t.setTransactionType(
                    i % 2 == 0 ? Transaction.TransactionType.CREDIT : Transaction.TransactionType.DEBIT
            );

            t.setStatus(
                    i % 5 == 0 ? Transaction.Status.FAILED : Transaction.Status.SUCCESS
            );

            t.setDescription("Transaction " + i);

            txRepo.save(t);
        }

        System.out.println("------------------------------------------------------");
        System.out.println(" 15 ROWS INSERTED INTO ALL TABLES SUCCESSFULLY!");
        System.out.println("------------------------------------------------------");
    }
}