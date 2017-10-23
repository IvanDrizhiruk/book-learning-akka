package ua.dp.akka;

import static akka.pattern.Patterns.ask;
import static scala.compat.java8.FutureConverters.toJava;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.japi.pf.ReceiveBuilder;
import akka.util.Timeout;
import ua.dp.akka.messages.GetRequest;

public class AskDemoArticleParser extends AbstractActor {

	private final ActorSelection cacheActor;
	private final ActorSelection httpClientActor;
	private final ActorSelection artcileParseActor;
	private final Timeout timeout;

	public AskDemoArticleParser(String cacheActorPath, String httpClientActorPath, String artcileParseActorPath, Timeout timeout) {
		this.cacheActor = context().actorSelection(cacheActorPath);
		this.httpClientActor = context().actorSelection(httpClientActorPath);
		this.artcileParseActor = context().actorSelection(artcileParseActorPath);
		this.timeout = timeout;
	}

	/**
	 * Note there are 3 asks so this potentially creates 6 extra objects:
	 * - 3 Promises
	 * - 3 Extra actors
	 * It's a bit simpler than the tell example.
	 */
	@Override
	public Receive createReceive() {
		return ReceiveBuilder.create()
				.match(ParseArticle.class, this::ParseArticleHendle)
				.build();
	}

	private void ParseArticleHendle(ParseArticle msg) {
		final CompletionStage cacheResult = toJava(ask(cacheActor, new GetRequest(msg.url), timeout));
		final CompletionStage result = cacheResult
				.handle((x, t) -> processCacheResult(msg, x))
				.thenCompose(x -> x);

		final ActorRef senderRef = sender();
		result.handle((x, t) -> {
			if (x != null) {
				if (x instanceof ArticleBody) {
					String body = ((ArticleBody) x).body; //parsed article
					cacheActor.tell(body, self()); //cache it
					senderRef.tell(body, self()); //reply
				} else if (x instanceof String) //cached article
				{
					senderRef.tell(x, self());
				}
			} else if (x == null) {
				senderRef.tell(new akka.actor.Status.Failure((Throwable) t), self());
			}
			return null;
		});
	}

	private Object processCacheResult(ParseArticle msg, Object x) {
		if (x != null) {
			return CompletableFuture.completedFuture(x);
		}

		return toJava(ask(httpClientActor, msg.url, timeout))
				.thenCompose(
						rawArticle -> toJava(ask(
								artcileParseActor,
								new ParseHtmlArticle(msg.url, ((HttpResponse) rawArticle).body), timeout)
						));
	}
}
