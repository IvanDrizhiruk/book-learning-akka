package ua.dp.akka;

import static akka.pattern.Patterns.ask;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Status;
import akka.testkit.TestProbe;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import ua.dp.akka.messages.GetRequest;

public class AskTest {
    ActorSystem system = ActorSystem.create("testSystem");
    Timeout timeout = Timeout.apply(10000, TimeUnit.MILLISECONDS);

    TestProbe cacheProbe = new TestProbe(system);
    TestProbe httpClientProbe = new TestProbe(system);
    ActorRef articleParseActor = system.actorOf(Props.create(ParsingActor.class));

    ActorRef askDemoActor = system.actorOf(
            Props.create(AskDemoArticleParser.class,
                    cacheProbe.ref().path().toString(),
                    httpClientProbe.ref().path().toString(),
                    articleParseActor.path().toString(),
                    timeout)
    );


    @Test
    public void itShouldParseArticleTest() throws Exception {
        Future f = ask(askDemoActor, new ParseArticle(("http://www.google.com")), timeout);
        cacheProbe.expectMsgClass(GetRequest.class);
        cacheProbe.reply(new Status.Failure(new Exception("no cache")));

        httpClientProbe.expectMsgClass(String.class);
        httpClientProbe.reply(new HttpResponse(Articles.article1));

        String result = (String) Await.result(f, timeout.duration());
        assert(result.contains("I’ve been writing a lot in emacs lately"));
        assert(!result.contains("<body>"));
    }
}
