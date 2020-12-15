package cn.iwehdio.sell.service;

import cn.iwehdio.sell.dto.OrderDTO;
import cn.iwehdio.sell.exception.SellException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderMasterService {
    //创建、查询单个、查询所有、取消、完结、支付
    OrderDTO create(OrderDTO orderDTO);
    OrderDTO findOne(String orderId);
    Page<OrderDTO> findList(String buyerOpenid, Pageable pageable);
    OrderDTO cancel(OrderDTO orderDTO);
    OrderDTO finish(OrderDTO orderDTO);
    OrderDTO paid(OrderDTO orderDTO);

    Page<OrderDTO> findAll(Pageable pageable);
}
