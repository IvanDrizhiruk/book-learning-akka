package ua.dp.akka;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import ua.dp.akka.AkkademyDb;
import ua.dp.akka.messages.DeleteRequest;
import ua.dp.akka.messages.SetRequest;

public class AkkademyDbTest {

    ActorSystem system = ActorSystem.create();

    @Test
    public void itShouldPlaceKeyValueFromSetMessageIntoMap() {
        TestActorRef<AkkademyDb> actorRef = TestActorRef.create(system, Props.create(AkkademyDb.class));
        actorRef.tell(new SetRequest("key", "value"), ActorRef.noSender());

        AkkademyDb akkademyDb = actorRef.underlyingActor();
        assertEquals(akkademyDb.map.get("key"), "value");
    }


    @Test
    public void valueShouldBeRemovedFromMap() {
        TestActorRef<AkkademyDb> actorRef = TestActorRef.create(system, Props.create(AkkademyDb.class));
        actorRef.tell(new SetRequest("key1", "value1"), ActorRef.noSender());
        actorRef.tell(new SetRequest("key2", "value2"), ActorRef.noSender());
        actorRef.tell(new DeleteRequest("key1"), ActorRef.noSender());

        AkkademyDb akkademyDb = actorRef.underlyingActor();
        assertEquals(akkademyDb.map.get("key2"), "value2");
    }

}
