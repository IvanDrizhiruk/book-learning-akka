package ua.dp.akka;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

public class Producer {

	public static void main(String[] args) throws Exception {
		ActorSystem system = ActorSystem.create("generate-numbers-one-to-ten");
		ActorRef printNumbersConsumer = system.actorOf(Props.create(Consumer.class));

		for (int i = 1; i <= 10; i++) {
			System.out.println(">>> Producing & sending a number " + i);
			printNumbersConsumer.tell(i, ActorRef.noSender());
			TimeUnit.SECONDS.sleep(1); // sleep for 1 second before sending the next number
		}

		Await.result(system.terminate(), Duration.create(10, TimeUnit.SECONDS));

		System.out.println("===== Finished producing & sending numbers 1 to 10");

	}
}