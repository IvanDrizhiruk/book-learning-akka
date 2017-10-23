package ua.dp.akka;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class ParsingActor extends AbstractActor {
	@Override
	public Receive createReceive() {
		return ReceiveBuilder
				.create()
				.match(ParseHtmlArticle.class, this::handleParseHtmlArticle)
				.build();
	}

	private void handleParseHtmlArticle(ParseHtmlArticle msg) throws BoilerpipeProcessingException {
		String body = ArticleExtractor.INSTANCE.getText(msg.htmlString);
		sender().tell(new ArticleBody(msg.uri, body), self());
	}
}
