/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testthreadapp;

/**
 *
 * @author ivan
 */
class InsufficientFundsException extends Exception {

    public InsufficientFundsException() {
        
    }
    
    public InsufficientFundsException(String e) {
        super(e);
    }
    
}
