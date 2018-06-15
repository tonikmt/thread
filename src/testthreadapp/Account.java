/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testthreadapp;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author ivan
 */
public class Account {
    
    private int balance;
    private Lock lock;
    private AtomicInteger failCounter = new AtomicInteger(0);

    public AtomicInteger getFailCounter() {
        return failCounter;
    }
    
    public void incFailedTransferCount() {
        failCounter.incrementAndGet();
    }

    @Override
    public String toString() {
        return "Account{" + "balance=" + balance + "\tfailCounter=" + failCounter.get() +'}';
    }

    public Lock getLock() {
        return lock;
    }
    

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
    
    public Account (int initBalance) {
        this.balance = initBalance;
        this.lock = new ReentrantLock();
    }
    
    public void withdraw (int amount, int id) {
        System.out.println("Thread id: " + id + " --- start withdraw");
        System.out.println("Thread id: " + id + " --- withdraw balance before: " + this.balance);
        System.out.println("Thread id: " + id + " --- withdraw amount: " + amount);
        balance -= amount;
        System.out.println("Thread id: " + id + " --- withdraw balance after: " + this.balance);
        System.out.println("Thread id: " + id + " --- stop withdraw");
    }
    
    public void deposit (int amount, int id) {
        System.out.println("Thread id: " + id + " --- start deposit");
        System.out.println("Thread id: " + id + " --- deposit balance before: " + this.balance);
        System.out.println("Thread id: " + id + " --- deposit amount: " + amount);
        balance += amount;
        System.out.println("Thread id: " + id + " --- deposit balance after: " + this.balance);
        System.out.println("Thread id: " + id + " --- stop deposit");
    }
    
}
