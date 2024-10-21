package com.example;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {

    private final UUID walletId;

    private  AtomicReference<BigDecimal> balance;

    private Lock lock = new ReentrantLock();

    public BankAccount(UUID walletId, BigDecimal balance) {
        this.walletId = walletId;
        this.balance = new AtomicReference<BigDecimal>(balance);
    }

    public UUID getWalletId() {
        return walletId;
    }

    public BigDecimal getBalance() {
        return balance.get();
    }

    public Lock getLock() {
        return lock;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            balance.updateAndGet(b -> b.add(amount));
        }
    }

    public boolean withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            while (true) {
                BigDecimal currentBalance = balance.get();
                if (currentBalance.compareTo(amount) >= 0) {
                    BigDecimal newBalance = currentBalance.subtract(amount);
                    if (balance.compareAndSet(currentBalance, newBalance)) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }
}
