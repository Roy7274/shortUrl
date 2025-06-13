package com.roy.shortlink.mapper;

import com.roy.shortlink.pojo.ShortLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShortLinkMapper {

    void insert(ShortLink shortLink);

    ShortLink selectByShortCode(@Param("shortCode") String shortCode);

    int deleteByShortCodeAndToken(@Param("shortCode") String shortCode, @Param("userToken") String userToken);

    List<ShortLink> selectByUserToken(@Param("userToken") String userToken);
}
