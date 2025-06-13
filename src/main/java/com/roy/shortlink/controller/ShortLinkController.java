package com.roy.shortlink.controller;

import com.roy.shortlink.pojo.ShortLink;
import com.roy.shortlink.pojo.dto.CreateShortLinkRequest;
import com.roy.shortlink.pojo.dto.ShortLinkResponse;
import com.roy.shortlink.pojo.vo.ShortLinkVO;
import com.roy.shortlink.service.ShortLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.roy.shortlink.mapper.ShortLinkMapper;


import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class ShortLinkController {

    @Autowired
    private ShortLinkService shortLinkService;
    @Autowired
    private ShortLinkMapper shortLinkMapper;
    @PostMapping
    public ResponseEntity<ShortLinkResponse> createShortLink(@RequestBody CreateShortLinkRequest request,
                                                             HttpServletRequest httpRequest,
                                                             HttpServletResponse httpResponse) {
        String userToken = httpRequest.getHeader("token");

        if (userToken == null || userToken.isEmpty()) {

            userToken = UUID.randomUUID().toString().replace("-", "");

            Cookie tokenCookie = new Cookie("token", userToken);
            tokenCookie.setHttpOnly(true);
            tokenCookie.setMaxAge(60 * 60 * 24 * 7);
            tokenCookie.setPath("/");
            httpResponse.addCookie(tokenCookie);
        }


        ShortLinkVO shortLink = shortLinkService.createShortLink(request,userToken);


        String domain = httpRequest.getScheme() + "://" + httpRequest.getServerName();
        int port = httpRequest.getServerPort();
        if (port != 80 && port != 443) {
            domain += ":" + port;
        }

        String shortUrl = domain + "/" + shortLink.getShortCode();

        ShortLinkResponse response = new ShortLinkResponse(
                shortLink.getShortCode(),
                shortLink.getOriginalUrl(),
                shortUrl
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{shortCode}")
    public void redirectToOriginalUrl(@PathVariable("shortCode") String shortCode,
                                      HttpServletResponse response) throws IOException {

        String originalUrl = shortLinkService.getOriginalUrl(shortCode);
        if (originalUrl == null || originalUrl.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "短链接不存在");
            return;
        }
        response.sendRedirect(originalUrl);
    }

    @GetMapping("/links")
    public ResponseEntity<List<ShortLinkVO>> getUserLinks(HttpServletRequest request) {
        String userToken = request.getHeader("token");
        if (userToken == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        userToken = cookie.getValue();
                        break;
                    }
                }
            }
        }
        if (userToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<ShortLinkVO> list = shortLinkService.getUserShortLinks(userToken);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/links/delete/")
    public ResponseEntity<Map<String, String>> deleteShortLink(@RequestParam("shortCode") String shortCode, HttpServletRequest request) {
        String userToken = request.getHeader("token");

        if (userToken == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        userToken = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (userToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean isDeleted = shortLinkService.deleteShortLink(shortCode, userToken);
        if (isDeleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "短链接删除成功");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "删除失败，权限不足或短链接不存在");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }
}
