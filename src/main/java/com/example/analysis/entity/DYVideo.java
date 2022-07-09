package com.example.analysis.entity;

import lombok.Data;

import java.util.List;

@Data
public class DYVideo {
    private String titleUrl;
    private String musicUrl;
    private String videoUrl;
    private String coverUrl;
    private String author;
    private List<String> imgUrls;
}
