package testthreadapp;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivan
 */
public class Operations {

    public static void main(String[] args) throws Exception {

        final Account a = new Account(1000);
        final Account b = new Account(2000);

        new Thread(() -> {
            try {
                System.out.println("Start thread 1");
                transfers(a, b, 500);
                System.out.println("Stop thread 1");
            } catch (InsufficientFundsException ex) {
                System.out.println("InsufficientFundsException transfers(a,b,500)");
            } catch (InterruptedException ex) {
                System.out.println("InterruptedException transfers(a,b,500)");
            }
        }).start();

        new Thread(() -> {
            try {
                System.out.println("Start thread 2");
                transfers(a, b, 300);
                System.out.println("Stop thread 2");
            } catch (InsufficientFundsException ex) {
                System.out.println("InsufficientFundsException transfers(a,b,300)");
            } catch (InterruptedException ex) {
                System.out.println("InterruptedException transfers(a,b,300)");
            }
        }).start();

        new Thread(() -> {
            try {
                System.out.println("Start thread 3");
                transfers(b, a, 100);
                System.out.println("Stop thread 3");
            } catch (InsufficientFundsException ex) {
                System.out.println("InsufficientFundsException transfers(b,a,100)");
            } catch (InterruptedException ex) {
                System.out.println("InterruptedException transfers(b,a,100)");
            }
        }).start();

        new Thread(() -> {
            try {
                System.out.println("Start thread 4");
                transfers(b, a, 700);
                System.out.println("Stop thread 4");
            } catch (InsufficientFundsException ex) {
                System.out.println("InsufficientFundsException transfers(b,a,700)");
            } catch (InterruptedException ex) {
                System.out.println("InterruptedException transfers(b,a,700)");
            }
        }).start();

    }

    static void transfers(Account acc1, Account acc2, int amount) throws InterruptedException, InsufficientFundsException {
        if (acc1.getBalance() < amount) {
            System.out.println("transfers InsufficientFundsException");
            throw new InsufficientFundsException();
        }
        System.out.println("Вызван метод transfers с параметрами-- acc1: " + acc1.toString() + " acc2: " + acc2 + " amount: " + amount);
        if (acc1.getLock().tryLock(3, TimeUnit.SECONDS)) {
            try {
                System.out.println("transfers synchronized acc1: " + acc1.toString());
                if (acc2.getLock().tryLock(3, TimeUnit.SECONDS)) {
                    try {
                        System.out.println("transfers synchronized acc2: " + acc2.toString());
                        acc1.withdraw(amount);
                        acc2.deposit(amount);
                    } finally {
                        System.out.println("Освобождаем acc2");
                        acc2.getLock().unlock();
                    }

                } else {
                    acc1.incFailedTransferCount();
                    acc2.incFailedTransferCount();
                    System.out.println("Объект занят!");
                    System.out.println("acc1 getFailCounter(): " + acc1.getFailCounter().get());
                    System.out.println("acc2 getFailCounter(): " + acc2.getFailCounter().get());
                }
            } finally {
                System.out.println("Освобождаем acc1");
                acc1.getLock().unlock();
            }
        } else {
            acc1.incFailedTransferCount();
            acc2.incFailedTransferCount();
            System.out.println("Объект занят!");
            System.out.println("acc1 getFailCounter(): " + acc1.getFailCounter().get());
            System.out.println("acc2 getFailCounter(): " + acc2.getFailCounter().get());
        }

    }

}
