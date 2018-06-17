/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testthreadapp;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ivan
 */
public class Transfer implements Callable<Boolean> {

    private final Account accountFrom;
    private final Account accountTo;
    private final int amount;
    private final int id;
    private final CountDownLatch cdl;

    public Transfer(Account accountFrom, Account accountTo, int amount, int id, CountDownLatch cdl) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
        this.id = id;
        this.cdl = cdl;
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("Старт потока: " + id);
        cdl.countDown();
        cdl.await();

        try {
            if (this.accountFrom.getBalance() < this.amount) {
                System.out.println("Поток: " + id + " --- InsufficientFundsException");
                throw new InsufficientFundsException("Сумма списания больше суммы счета!");
            }
        } catch (InsufficientFundsException e) {
            System.out.println("InsufficientFundsException потока: " + id + " --- останавливаем поток!");
            return false;
            //Thread.currentThread().interrupt();
        }

        if (this.accountFrom.getLock().tryLock(new Random().nextInt(5), TimeUnit.SECONDS)) {
            try {
                System.out.println("Поток: " + id + " --- получили Lock на accountFrom: " + accountFrom.toString());
                System.out.println("параметры потока " + id + ": " + "accountFrom: " + accountFrom.toString() + " amount: " + amount);
                if (this.accountTo.getLock().tryLock(new Random().nextInt(5), TimeUnit.SECONDS)) {
                    try {
                        System.out.println("Поток: " + id + " --- получили Lock на accountTo: " + accountTo.toString());
                        System.out.println("параметры потока " + id + ": " + " accountTo: " + accountTo.toString() + " amount: " + amount);
                        System.out.println("Поток: " + id + " --- выполняем withdraw и deposit");
                        this.accountFrom.withdraw(amount, id);
                        this.accountTo.deposit(amount, id);
                        Thread.sleep(new Random().nextInt(3000));
                        return true;

                    } finally {
                        System.out.println("Поток: " + id + " --- освобождаем accountTo");
                        this.accountTo.getLock().unlock();
                    }

                } else {
                    System.out.println("Поток: " + id + " --- объект accountTo занят!");
                    this.accountTo.incFailedTransferCount();
                    System.out.println("Поток: " + id + " --- accountTo.getFailCounter(): " + accountTo.getFailCounter().get());
                    return false;
                }
            } finally {
                System.out.println("Поток: " + id + " --- освобождаем accountFrom");
                this.accountFrom.getLock().unlock();
            }
        } else {
            System.out.println("Поток: " + id + " --- объект accountFrom занят!");
            this.accountFrom.incFailedTransferCount();
            System.out.println("Поток: " + id + " --- accountFrom.getFailCounter(): " + accountFrom.getFailCounter().get());
            return false;
        }
    }

}
