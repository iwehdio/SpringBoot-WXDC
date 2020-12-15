package cn.iwehdio.sell.service;

import cn.iwehdio.sell.dto.OrderDTO;
import com.alipay.api.domain.AlipayTradeWapPayModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface PayService {
    Map<String, String> create(OrderDTO orderDTO);
    boolean notifyUrl(HttpServletRequest request, HttpServletResponse response);
    void refund(String orderId);
}
