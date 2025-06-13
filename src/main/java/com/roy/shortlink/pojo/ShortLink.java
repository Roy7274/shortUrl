package com.roy.shortlink.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShortLink {
    private Long id;
    private String shortCode;
    private String originalUrl;
    private String userToken;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}