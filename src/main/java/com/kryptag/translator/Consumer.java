/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kryptag.translator;

import com.google.gson.Gson;
import com.kryptag.rabbitmqconnector.Enums.ExchangeNames;
import com.kryptag.rabbitmqconnector.MessageClasses.CreditMessage;
import com.kryptag.rabbitmqconnector.RMQConnection;
import com.kryptag.rabbitmqconnector.RMQConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author florenthaxha
 */
public class Consumer extends RMQConsumer {

    public Consumer(ConcurrentLinkedQueue q, RMQConnection rmq) {
        super(q, rmq);
    }

    @Override
    public void run() {
        while(Thread.currentThread().isAlive()){
            doWork();
        }
    }

    private void doWork() {
        Gson g = new Gson();
        BasicProperties props = new BasicProperties.Builder().replyTo(ExchangeNames.NORMALIZER.toString()).build();
        RMQConnection rmq = this.getRmq();
        if (!this.getQueue().isEmpty()) {
            if (rmq.getQueuename().contains("XML")) {
                CreditMessage cmsg = g.fromJson(this.getQueue().remove().toString(), CreditMessage.class);
                String xml = getXMLMarshalled(cmsg);
                rmq.sendMessageWithProps(xml, props);
            } else {
                CreditMessage cmsg = g.fromJson(this.getQueue().remove().toString(), CreditMessage.class);
                rmq.sendMessageWithProps(g.toJson(cmsg), props);
            }
        }
    }

    private String getXMLMarshalled(CreditMessage cmsg) {
        String xml = "";
        try {
            JAXBContext jaxb = JAXBContext.newInstance(CreditMessage.class);
            Marshaller jaxbMarshaller = jaxb.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(cmsg, sw);
            xml = sw.toString();
            return xml;
        } catch (JAXBException ex) {
            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xml;
    }

}
