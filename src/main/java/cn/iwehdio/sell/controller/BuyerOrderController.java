package cn.iwehdio.sell.controller;

import cn.iwehdio.sell.converter.OrderForm2OrderDTOConverter;
import cn.iwehdio.sell.dto.OrderDTO;
import cn.iwehdio.sell.enums.ResultEnum;
import cn.iwehdio.sell.exception.SellException;
import cn.iwehdio.sell.form.OrderForm;
import cn.iwehdio.sell.service.BuyerService;
import cn.iwehdio.sell.service.OrderMasterService;
import cn.iwehdio.sell.utils.ProductResultVOUtil;
import cn.iwehdio.sell.viewObject.ProductResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 买家订单功能
 */
@RestController
@RequestMapping("/buyer/order")
public class BuyerOrderController {
    private final Logger logger = LoggerFactory.getLogger(BuyerOrderController.class);
    @Autowired
    private OrderMasterService masterService;
    @Autowired
    private BuyerService buyerService;
    //创建订单
    @PostMapping("/create")
    public ProductResultVO<Map<String,String>> create(@Valid OrderForm orderForm,
                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("【创建订单】参数不正确，orderForm={}",orderForm);
            throw new SellException(ResultEnum.PARAM_ERROR,
                    bindingResult.getFieldError().getDefaultMessage());
        }
        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);
        if(orderDTO.getOrderDetailList().isEmpty()){
            logger.error("【创建订单】购物车不能为空");
            throw new SellException(ResultEnum.CART_EMPTY);
        }
        OrderDTO result = masterService.create(orderDTO);
        Map<String,String> map = new HashMap<>();
        map.put("orderId",result.getOrderId());
        return ProductResultVOUtil.success(map);
    }
    //订单列表

    @GetMapping("/list")
    public ProductResultVO<List<OrderDTO>> list(@RequestParam("openid") String openid,
                                                @RequestParam(value = "page",defaultValue = "0") Integer page,
                                                @RequestParam(value = "size",defaultValue = "10") Integer size){
        if (StringUtils.isEmpty(openid)){
            logger.error("【查询订单列表】openid为空");
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        PageRequest request = new PageRequest(page,size);
        Page<OrderDTO> list = masterService.findList(openid, request);
        return ProductResultVOUtil.success(list.getContent());
    }
    //订单详情
    @GetMapping("/detail")
    public ProductResultVO<OrderDTO> detail(@RequestParam("openid") String openid,
                                            @RequestParam("orderId") String orderId){
        OrderDTO orderDTO = buyerService.findOneOrder(openid,orderId);
        return ProductResultVOUtil.success(orderDTO);
    }

    //取消订单
    @PostMapping("/cancel")
    public ProductResultVO<OrderDTO> cancel(@RequestParam("openid") String openid,
                                            @RequestParam("orderId") String orderId){
        buyerService.cancelOrder(openid,orderId);
        return ProductResultVOUtil.success();
    }
}
