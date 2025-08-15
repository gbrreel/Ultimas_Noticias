package com.news.news_ai.services;

import com.news.news_ai.model.Article;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String API_URL = "https://api.openai.com/v1/responses";

    public JsonNode summarize(List<Article> articles) throws Exception {
        String text = articles.stream()
            .map(a -> "- " + a.title() + " (" + a.source() + ") — " + a.url())
            .reduce("", (acc, s) -> acc + s + "\n");

        Map<String, Object> body = Map.of(
            "model", "gpt-3.5-turbo",
            "input", """
                Resuma as seguintes notícias em até 5 frases,
                liste 5 tópicos principais
                e 5 destaques (título + link).
                Notícias:
                %s
            """.formatted(text),
            "max_output_tokens", 1000
        );

        var client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

        var request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body), StandardCharsets.UTF_8))
            .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(response.body());
    }
}
