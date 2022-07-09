package com.example.analysis.entity;

import lombok.Data;

import java.util.List;

@Data
public class XHSPhotoAndText {
    private String msg;
    private String titleUrl;
    private String author;
    private List<String> imgUrls;
    private List<String> textUrls;
}
