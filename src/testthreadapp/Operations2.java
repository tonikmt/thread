/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testthreadapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ivan
 */
public class Operations2 {
    public static void main(String[] args) throws InterruptedException, ExecutionException, InsufficientFundsException {
        
        Account acc1 = new Account(new Random().nextInt(5000));
        Account acc2 = new Account(new Random().nextInt(5000));
        List<Future> list = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(3);
        for (int i=0;i<100;i++) {
            list.add(service.submit(
                    new Random().nextBoolean()?
                            new Transfer(acc1, acc2, new Random().nextInt(1000) , i):
                            new Transfer(acc2, acc1, new Random().nextInt(1000) , i)));
        }
        service.shutdown();
        
        if (service.awaitTermination(2, TimeUnit.MINUTES)) {
            System.out.println("Результат выполнения всех потоков: ");
            list.forEach((f) -> {
                try {
                    System.out.println("Future get(): " + f.get().toString());
                } catch (ExecutionException e) {
                    System.out.println("ExecutionException  e: "+ e.getMessage());
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException  e: "+ e.getMessage());
                }
            });
            System.out.println("acc1: " + acc1.toString());
            System.out.println("acc2: " + acc2.toString());
        }
    }
}
