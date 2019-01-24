/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kryptag.translator;

import com.kryptag.rabbitmqconnector.Enums.ExchangeNames;
import com.kryptag.rabbitmqconnector.RMQConnection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author florenthaxha
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         // Takes request from RMQ and puts in into the internal queue
        RMQConnection rmqPub = new RMQConnection("guest", "guest", "datdb.cphbusiness.dk", 5672, "Bank_Translator_Exchange_Name");
        // Takes the request from internal queue and procceses it before sending it back to RMQ
        RMQConnection rmqCon = new RMQConnection("guest", "guest", "datdb.cphbusiness.dk", 5672, "Bank_Exchange_Name");
        // Internal queue
        ConcurrentLinkedQueue q = new ConcurrentLinkedQueue();
        // producer implementation 
        Producer producer = new Producer(q, rmqPub);
        // procceses implementation 
        Consumer consumer = new Consumer(q, rmqCon);
        // start thread
        producer.start();
        // start thread 
        consumer.start();
        
    }
    
}
