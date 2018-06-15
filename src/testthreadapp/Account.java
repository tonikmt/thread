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
    
    public void withdraw (int amount) {
        System.out.println("start withdraw");
        System.out.println("withdraw balance before: " + this.balance);
        System.out.println("withdraw amount: " + amount);
        balance -= amount;
        System.out.println("withdraw balance after: " + this.balance);
        System.out.println("stop withdraw");
    }
    
    public void deposit (int amount) {
        System.out.println("start deposit");
        System.out.println("deposit balance before: " + this.balance);
        System.out.println("deposit amount: " + amount);
        balance += amount;
        System.out.println("deposit balance after: " + this.balance);
        System.out.println("stop deposit");
    }
    
}
