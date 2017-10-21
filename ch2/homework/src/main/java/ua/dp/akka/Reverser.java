package ua.dp.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Reverser {

    private ActorSystem system;
    private ActorRef reverserActor;

    public Reverser() {
        system = ActorSystem.create("recerser");
        reverserActor = system.actorOf(Props.create(ReverserActor.class));
    }


    public String revers(String str, int timeoutMillis) throws Exception {
        CompletableFuture<String> resultFuture = new CompletableFuture<>();
        ActorRef callbackActor = system.actorOf(Props.create(CallBackActor.class, resultFuture));

        reverserActor.tell(str, callbackActor);

        return resultFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
    }


    public Object reversEny(Object message, int timeoutMillis) throws Exception {
        CompletableFuture<Object> resultFuture = new CompletableFuture<>();
        ActorRef callbackActor = system.actorOf(Props.create(CallBackActor.class, resultFuture));

        reverserActor.tell(message, callbackActor);

        return resultFuture
                .get(timeoutMillis, TimeUnit.MILLISECONDS);
    }
}
