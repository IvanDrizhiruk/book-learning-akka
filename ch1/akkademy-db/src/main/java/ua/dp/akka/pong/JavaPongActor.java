package ua.dp.akka.pong;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Status;
import akka.japi.pf.ReceiveBuilder;

public class JavaPongActor extends AbstractActor {

	@Override
	public Receive createReceive() {
		return ReceiveBuilder
				.create()
				.matchEquals("Ping", s -> sender().tell("Pong", ActorRef.noSender()))
				.matchAny(x ->sender().tell(new Status.Failure(new Exception("unknown message")), self()))
				.build();
	}
}
