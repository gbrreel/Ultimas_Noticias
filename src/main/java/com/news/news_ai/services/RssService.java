package com.news.news_ai.services;

import com.news.news_ai.model.Article;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RssService {

    private static final List<String> FEEDS = List.of(
        "https://g1.globo.com/dynamo/rss2.xml",
        "https://feeds.bbci.co.uk/portuguese/rss.xml",
        "https://rss.uol.com.br/feed/noticias.xml"
    );

    public List<Article> fetchLastHours(int hours) {
        ZonedDateTime cutoff = ZonedDateTime.now().minusHours(hours);
        List<Article> articles = new ArrayList<>();

        for (String feedUrl : FEEDS) {
            try (XmlReader reader = new XmlReader(new URL(feedUrl))) {
                var feed = new SyndFeedInput().build(reader);
                for (SyndEntry e : feed.getEntries()) {
                    ZonedDateTime pub = e.getPublishedDate() != null
                        ? ZonedDateTime.ofInstant(e.getPublishedDate().toInstant(), ZonedDateTime.now().getZone())
                        : ZonedDateTime.now();

                    if (pub.isAfter(cutoff)) {
                        articles.add(new Article(
                            e.getTitle(),
                            e.getLink(),
                            feed.getTitle(),
                            pub
                        ));
                    }
                }
            } catch (Exception ignored) {}
        }

        return articles;
    }
}
