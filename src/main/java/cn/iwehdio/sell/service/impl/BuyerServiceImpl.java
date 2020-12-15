package cn.iwehdio.sell.service.impl;

import cn.iwehdio.sell.dto.OrderDTO;
import cn.iwehdio.sell.enums.ResultEnum;
import cn.iwehdio.sell.exception.SellException;
import cn.iwehdio.sell.service.BuyerService;
import cn.iwehdio.sell.service.OrderMasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 买家端业务
 */

@Service
public class BuyerServiceImpl implements BuyerService {
    @Autowired
    private OrderMasterService masterService;
    private final Logger logger = LoggerFactory.getLogger(BuyerServiceImpl.class);

    @Override
    public OrderDTO findOneOrder(String openid, String orderId) {
        return checkOrderOwner(openid,orderId);
    }

    @Override
    public OrderDTO cancelOrder(String openid, String orderId) {
        OrderDTO orderDTO = checkOrderOwner(openid, orderId);
        if (orderDTO == null) {
            logger.error("【取消订单】查不到该订单,orderId={}",orderId);
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }
        return masterService.cancel(orderDTO);
    }

    private OrderDTO checkOrderOwner(String openid,String orderId){
        OrderDTO orderDTO = masterService.findOne(orderId);
        if (orderDTO == null) {
            return null;
        }
        if (!orderDTO.getBuyerOpenid().equalsIgnoreCase(openid)) {
            logger.error("【查询订单】订单的openid不一致,openid={},orderDTO={}",openid,orderDTO);
            throw new SellException(ResultEnum.ORDER_OWNER_ERROR);
        }
        return orderDTO;
    }
}
