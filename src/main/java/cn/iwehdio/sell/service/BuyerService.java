package cn.iwehdio.sell.service;

import cn.iwehdio.sell.dto.OrderDTO;

public interface BuyerService {
    OrderDTO findOneOrder(String openid,String orderId);
    OrderDTO cancelOrder(String openid,String orderId);
}
