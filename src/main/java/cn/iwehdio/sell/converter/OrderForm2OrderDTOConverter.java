package cn.iwehdio.sell.converter;

import cn.iwehdio.sell.controller.BuyerOrderController;
import cn.iwehdio.sell.dataObject.OrderDetail;
import cn.iwehdio.sell.dto.OrderDTO;
import cn.iwehdio.sell.enums.ResultEnum;
import cn.iwehdio.sell.exception.SellException;
import cn.iwehdio.sell.form.OrderForm;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * form转换DTO
 */
public class OrderForm2OrderDTOConverter {
    private static Logger logger = LoggerFactory.getLogger(OrderForm2OrderDTOConverter.class);
    public static OrderDTO convert(OrderForm orderForm) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerName(orderForm.getName());
        orderDTO.setBuyerPhone(orderForm.getPhone());
        orderDTO.setBuyerOpenid(orderForm.getOpenid());
        orderDTO.setBuyerAddress(orderForm.getAddress());

        Gson gson = new Gson();
        List<OrderDetail> orderDetailList;
        try {
            orderDetailList = gson.fromJson(orderForm.getItems(),new TypeToken<List<OrderDetail>>(){}.getType());
        } catch (JsonSyntaxException e) {
            logger.error("【对象转换】错误，string={}",orderForm.getItems());
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        orderDTO.setOrderDetailList(orderDetailList);
        return orderDTO;
    }
}
