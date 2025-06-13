package com.roy.shortlink.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkResponse {
    private String shortCode;
    private String originalUrl;
    private String shortUrl;
}
