package cn.iwehdio.sell.service;

import cn.iwehdio.sell.dto.OrderDTO;

public interface PushMessageService {
    void orderStatus(OrderDTO orderDTO);
}
