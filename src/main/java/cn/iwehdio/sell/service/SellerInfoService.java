package cn.iwehdio.sell.service;

import cn.iwehdio.sell.dataObject.SellerInfo;

public interface SellerInfoService {
    SellerInfo findSellerInfoByOpenid(String openid);
    SellerInfo findSellerInfoByUsername(String username);
}
