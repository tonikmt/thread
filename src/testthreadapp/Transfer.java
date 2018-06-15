/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testthreadapp;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ivan
 */
public class Transfer implements Callable<Boolean> {

    private Account accountFrom;
    private Account accountTo;
    private int amount;
    private int id;

    public Transfer(Account accountFrom, Account accountTo, int amount, int id) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
        this.id = id;
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("Старт потока: " + id);
        System.out.println("параметры потока " + id + ": " + "accountFrom: " + accountFrom.toString() + " accountTo: " + accountTo + " amount: " + amount);

        try {
            if (accountFrom.getBalance() < amount) {
                System.out.println("Поток: " + id + " --- InsufficientFundsException");
                throw new InsufficientFundsException("Сумма списания больше суммы счета!");
            }
        } catch (InsufficientFundsException e) {
            System.out.println("InsufficientFundsException потока: " + id + " --- останавливаем поток!");
            Thread.currentThread().interrupt();
        }

        if (accountFrom.getLock().tryLock(new Random().nextInt(3), TimeUnit.SECONDS)) {
            try {
                System.out.println("Поток: " + id + " --- получили Lock на accountFrom: " + accountFrom.toString());
                if (accountTo.getLock().tryLock(new Random().nextInt(3), TimeUnit.SECONDS)) {
                    try {
                        System.out.println("Поток: " + id + " --- получили Lock на accountTo: " + accountTo.toString());
                        System.out.println("Поток: " + id + " --- выполняем withdraw и deposit");
                        accountFrom.withdraw(amount);
                        accountTo.deposit(amount);
                        Thread.sleep(new Random().nextInt(3000));
                        return true;
                    } finally {
                        System.out.println("Поток: " + id + " --- освобождаем accountTo");
                        accountTo.getLock().unlock();
                    }

                } else {
                    System.out.println("Поток: " + id + " --- объект accountTo занят!");
                    accountTo.incFailedTransferCount();
                    System.out.println("Поток: " + id + " --- accountTo.getFailCounter(): " + accountTo.getFailCounter().get());
                    return false;
                }
            } finally {
                System.out.println("Поток: " + id + " --- освобождаем accountFrom");
                accountFrom.getLock().unlock();
            }
        } else {
            System.out.println("Поток: " + id + " --- объект accountFrom занят!");
            accountFrom.incFailedTransferCount();
            System.out.println("Поток: " + id + " --- accountFrom.getFailCounter(): " + accountFrom.getFailCounter().get());
            return false;
        }
    }

}
