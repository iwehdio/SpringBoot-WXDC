package cn.iwehdio.sell.service.impl;

import cn.iwehdio.sell.dao.SellerInfoDao;
import cn.iwehdio.sell.dataObject.SellerInfo;
import cn.iwehdio.sell.service.SellerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SellerInforServiceImpl implements SellerInfoService {
    @Autowired
    private SellerInfoDao dao;
    @Override
    public SellerInfo findSellerInfoByOpenid(String openid) {
        return dao.findByOpenid(openid);
    }

    @Override
    public SellerInfo findSellerInfoByUsername(String username) {
        return dao.findByUsername(username);
    }
}
