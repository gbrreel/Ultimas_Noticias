package com.news.news_ai.model;
import java.time.ZonedDateTime;

public record Article (
    String title,
    String url,
    String source,
    ZonedDateTime publishedAt
){}
