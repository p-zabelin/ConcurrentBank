package com.example;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentBank {

    private final Map<UUID, BankAccount> accounts = new ConcurrentHashMap<>();

    public BankAccount createBankAccount(BigDecimal initialBalance) {
        UUID walletId = UUID.randomUUID();
        BankAccount bankAccount = new BankAccount(walletId, initialBalance);
        accounts.put(walletId, bankAccount);
        return bankAccount;
    }

    public void transfer(BankAccount fromBankAccount, BankAccount toBankAccount, BigDecimal amount) {
        BankAccount first, second;
        if (fromBankAccount.getWalletId().compareTo(toBankAccount.getWalletId()) < 0) {
            first = fromBankAccount;
            second = toBankAccount;
        } else {
            first = toBankAccount;
            second = fromBankAccount;
        }

        first.getLock().lock();
        try {
            second.getLock().lock();
            try {
                if (fromBankAccount.withdraw(amount)) {
                    toBankAccount.deposit(amount);
                }
            } finally {
                second.getLock().unlock();
            }
        } finally {
            first.getLock().unlock();
        }
    }

    public BigDecimal getTotalBalance() {
        BigDecimal total = BigDecimal.ZERO;
        for (BankAccount bankAccount : accounts.values()) {
            total = total.add(bankAccount.getBalance());
        }
        return total;
    }
}
