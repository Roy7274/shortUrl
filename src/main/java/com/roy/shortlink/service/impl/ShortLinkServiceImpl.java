package com.roy.shortlink.service.impl;

import com.roy.shortlink.mapper.ShortLinkMapper;
import com.roy.shortlink.pojo.ShortLink;
import com.roy.shortlink.pojo.dto.CreateShortLinkRequest;
import com.roy.shortlink.pojo.vo.ShortLinkVO;
import com.roy.shortlink.service.ShortLinkService;
import com.roy.shortlink.util.generateShortCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ShortLinkServiceImpl implements ShortLinkService {

    @Autowired
    private ShortLinkMapper shortLinkMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "short_link:";

    @Override
    public String getOriginalUrl(String shortCode) {
        String redisKey = REDIS_KEY_PREFIX + shortCode;

        String originalUrl = redisTemplate.opsForValue().get(redisKey);
        if (originalUrl != null && !originalUrl.isEmpty()) {
            return originalUrl;
        }

        ShortLink shortLink = shortLinkMapper.selectByShortCode(shortCode);
        if (shortLink == null || shortLink.getIsDeleted() == 1) {

            redisTemplate.opsForValue().set(redisKey, "", 5, TimeUnit.MINUTES);
            throw new RuntimeException("短链接不存在");
        }

        redisTemplate.opsForValue().set(redisKey, shortLink.getOriginalUrl());
        return shortLink.getOriginalUrl();
    }

    @Override
    public ShortLinkVO createShortLink(CreateShortLinkRequest request, String userToken) {

        String shortCode;

        if (StringUtils.hasText(request.getCustomCode())) {

            if (shortLinkMapper.selectByShortCode(request.getCustomCode()) != null) {
                throw new RuntimeException("自定义短码已存在，请换一个");
            }
            shortCode = request.getCustomCode();
        } else {

            shortCode = generateShortCodeUtil.generateShort(request.getOriginalUrl());
        }

        ShortLink link = new ShortLink();
        link.setShortCode(shortCode);
        link.setOriginalUrl(request.getOriginalUrl());
        link.setUserToken(userToken);
        link.setCreateTime(LocalDateTime.now());
        link.setUpdateTime(LocalDateTime.now());
        link.setIsDeleted(0);

        shortLinkMapper.insert(link);

        ShortLinkVO vo = new ShortLinkVO();
        vo.setShortCode(shortCode);
        vo.setOriginalUrl(link.getOriginalUrl());
        return vo;
    }

    @Override
    public List<ShortLinkVO> getUserShortLinks(String userToken) {
        List<ShortLink> links = shortLinkMapper.selectByUserToken(userToken);
        return links.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteShortLink(String shortCode, String userToken) {
        ShortLink link = shortLinkMapper.selectByShortCode(shortCode);
        if (link == null || !link.getUserToken().equals(userToken)) {
            return false;
        }

        int rows = shortLinkMapper.deleteByShortCodeAndToken(shortCode, userToken);
        String redisKey = "short_link:" + shortCode;
        redisTemplate.delete(redisKey);
        return rows > 0;
    }

    private ShortLinkVO convertToVO(ShortLink shortLink) {
        ShortLinkVO vo = new ShortLinkVO();
        vo.setShortCode(shortLink.getShortCode());
        vo.setOriginalUrl(shortLink.getOriginalUrl());
        return vo;
    }
}
