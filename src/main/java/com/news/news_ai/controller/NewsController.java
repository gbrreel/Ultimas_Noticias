package com.news.news_ai.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.news.news_ai.services.OpenAiService;
import com.news.news_ai.services.RssService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@CrossOrigin("*") // Permite que qualquer site acesse
public class NewsController {
    private final RssService rssService;
    private final OpenAiService openAiService;

    public NewsController(RssService rssService, OpenAiService openAiService) {
        this.rssService = rssService;
        this.openAiService = openAiService;
    }

    @GetMapping("/summary")
    public JsonNode getSummary(@RequestParam(defaultValue = "24") int hours) throws Exception {
        var articles = rssService.fetchLastHours(hours);
        return openAiService.summarize(articles);
    }
}
