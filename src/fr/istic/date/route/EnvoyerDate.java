package fr.istic.date.route;
/**
 * 
 */

import java.util.Date;

/**
 * @author VinYarD
 *
 * IPR_TP3//EmitLog.java
 * 6 févr. 2018
 */

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EnvoyerDate {

  private static final String EXCHANGE_NAME = "date_route";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri("amqp://mri:64GbL3k7uc33QCtc@localhost:8082/mri");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);


    boolean loop = true;
    while(loop) {
    	channel.basicPublish(EXCHANGE_NAME, "locale", null, EnvoyerDate.getDate().getBytes("UTF-8"));
    	channel.basicPublish(EXCHANGE_NAME, "gmt", null, EnvoyerDate.getDateGMT().getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + getDate() + "'");
        Thread.sleep(1000);
    }
    

    channel.close();
    connection.close();
  }
  
  private static String getDateGMT() {
	    return (new Date()).toGMTString();
	}

  private static String getDate(){
    return (new Date()).toString();
  }

  private static String joinStrings(String[] strings, String delimiter) {
    int length = strings.length;
    if (length == 0) return "";
    StringBuilder words = new StringBuilder(strings[0]);
    for (int i = 1; i < length; i++) {
        words.append(delimiter).append(strings[i]);
    }
    return words.toString();
  }
}