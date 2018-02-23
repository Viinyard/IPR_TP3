package fr.istic.date.topic;
/**
 * 
 */

import java.io.IOException;
import java.util.Scanner;

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

	private static final String EXCHANGE_NAME = "chat";

	public static void main(String[] argv) throws Exception {
		String pseudo = null;
		if(argv.length > 0 && argv[0].length() > 3) {
			pseudo = argv[0];
		} else {
			System.err.println("Error usage : pseudo > 3");
			System.exit(1);
		}
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri("amqp://mri:64GbL3k7uc33QCtc@localhost:8082/mri");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
		
		String topic = "chat.mri";

		boolean loop = true;
		while (loop) {
			String message = lireString();
			if(message.startsWith("/")) {
				String command[] = message.split(" ");
				switch(command[0]) {
				case "/quit":
					String quitMessage = ".";
					if(command.length > 1) {
						quitMessage = " (";
						for(int i = 1; i < command.length; i++) {
							quitMessage += command[i];
						}
						quitMessage += ").";
					}
					message = topic+"#"+pseudo+" has quit"+quitMessage;
					break;
				case "/join" :
					if(command.length == 2) {
						topic = command[1];
						message = topic+"#"+pseudo+" has join.";
					} else {
						System.err.println("Erreur usage : /join topic.subtopic");
						message = "";
					}
					break;
				case "/nick" :
					if(command.length == 2) {
						if(command[1].length() > 3) {
							message = topic+"#"+pseudo+" is now has "+ command[1];
							pseudo = command[1];
						} else {
							System.err.println("Erreur usage : /nick pseudo");
							message = "";
						}
					}
					default :
						System.err.println("Erreur usage : /join, /quit, /nick");
						message = "";
				}
			}
			
			if(message.length() > 0) {
				channel.basicPublish(EXCHANGE_NAME, topic, null, message.getBytes("UTF-8"));
			}
		}

		channel.close();
		connection.close();
	}
	
	public static String lireString() throws IOException {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

}