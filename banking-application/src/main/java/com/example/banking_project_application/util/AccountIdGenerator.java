package com.example.banking_project_application.util;

import com.example.banking_project_application.repositories.AccountRepository;

public class AccountIdGenerator {

    public static long generateUniqueId(AccountRepository repo) {
        while (true) {
            long id = 1000 + (long)(Math.random() * 9000);
            if (!repo.existsById(id)) {
                return id;
            }
        }
    }
}