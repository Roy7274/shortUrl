package com.roy.shortlink.pojo.dto;

import lombok.Data;

@Data
public class CreateShortLinkRequest {
    private String originalUrl;
    private String customCode;
}
