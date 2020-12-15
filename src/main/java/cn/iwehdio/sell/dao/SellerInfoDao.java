package cn.iwehdio.sell.dao;

import cn.iwehdio.sell.dataObject.SellerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerInfoDao extends JpaRepository<SellerInfo,String> {
    SellerInfo findByOpenid(String openid);
    SellerInfo findByUsername(String username);
}
