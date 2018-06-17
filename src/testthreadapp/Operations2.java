/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testthreadapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ivan
 */
public class Operations2 {
    private static final CountDownLatch CDL = new CountDownLatch(11);
    
    public static void main(String[] args) throws InterruptedException, ExecutionException, InsufficientFundsException {
        
        Account acc1 = new Account(new Random().nextInt(500000));
        Account acc2 = new Account(new Random().nextInt(500000));
        List<Future> list = new ArrayList<>();
        
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleAtFixedRate(new Thread(() -> {
                                            System.out.println("acc1: " + acc1.getFailCounter().get());
                                                            }),5,1, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleAtFixedRate(new Thread(() -> {
                                            System.out.println("acc2: " + acc2.getFailCounter().get());
                                                            }),5,1, TimeUnit.SECONDS);
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i=0;i<10;i++) {
            list.add(service.submit(
                    new Random().nextBoolean()?
                            new Transfer(acc1, acc2, new Random().nextInt(1000) , i, CDL):
                            new Transfer(acc2, acc1, new Random().nextInt(1000) , i, CDL)));
        }
        CDL.countDown();
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
