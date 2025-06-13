package com.roy.shortlink.service;
import java.util.List;
import com.roy.shortlink.pojo.dto.CreateShortLinkRequest;
import com.roy.shortlink.pojo.vo.ShortLinkVO;

public interface ShortLinkService {
    ShortLinkVO createShortLink(CreateShortLinkRequest request, String userToken);

    List<ShortLinkVO> getUserShortLinks(String userToken);

    boolean deleteShortLink(String shortCode, String userToken);

    String getOriginalUrl(String shortCode);
}
