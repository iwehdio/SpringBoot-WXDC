package cn.iwehdio.sell.controller;

import cn.iwehdio.sell.dataObject.OrderDetail;
import cn.iwehdio.sell.dto.OrderDTO;
import cn.iwehdio.sell.enums.PayStatusEnum;
import cn.iwehdio.sell.enums.ResultEnum;
import cn.iwehdio.sell.exception.SellException;
import cn.iwehdio.sell.service.OrderMasterService;
import cn.iwehdio.sell.service.PayService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pay")
public class PayController {
    private Logger logger = LoggerFactory.getLogger(PayController.class);
    @Autowired
    private OrderMasterService masterService;
    @Autowired
    private PayService payService;
    @GetMapping("/create")
    public ModelAndView create(@RequestParam("orderId") String orderId,
                               @RequestParam("returnUrl") String returnUrl) {
        OrderDTO orderDTO = masterService.findOne(orderId);
        if (orderDTO == null) {
            logger.error("【支付出错】订单不存在，orderId={}",orderId);
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }
        //拼接订单中的商品名
        StringBuilder subject = new StringBuilder();
        List<OrderDetail> orderDetailList = orderDTO.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            subject.append(orderDetail.getProductName()).append(" ");
        }
        Map<String, String> returnMap = payService.create(orderDTO);
        Map<String, String> map = new HashMap<>();
        map.put("toUrl", returnMap.get("toUrl"));
        try {
            map.put("checkUrl", returnMap.get("checkUrl") + "&returnUrl=" + URLEncoder.encode(returnUrl,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        map.put("productName",subject.toString());
        map.put("amount", orderDTO.getOrderAmount().toString());
        map.put("buyerName",orderDTO.getBuyerName());
        map.put("orderTime", orderDTO.getCreateTime().toString());
        map.put("orderId", orderId);
        return new ModelAndView("confirm_order", map);
    }

    @GetMapping("/checkpay")
    public ModelAndView checkPay(@RequestParam("orderId") String orderId,
                           @RequestParam("returnUrl") String returnUrl){
        OrderDTO orderDTO = masterService.findOne(orderId);
        if (PayStatusEnum.SUCCESS.getCode().equals(orderDTO.getPayStatus())) {
            return new ModelAndView(new RedirectView(returnUrl));
        } else {
            return new ModelAndView("payfail");
        }

    }



    @PostMapping("/notifyUrl")
    public void notifyUrl(HttpServletRequest request, HttpServletResponse response) {
        boolean signVerified = payService.notifyUrl(request, response);
        if (signVerified) {
            masterService.paid(masterService.findOne(request.getParameter("out_trade_no")));
        }
        logger.info("【异步回调】回调结果{},orderId={}",signVerified,request.getParameter("out_trade_no"));
    }

    @GetMapping("/backwechat")
    public ModelAndView backwechat(){
        return new ModelAndView("jumpback");
    }

}
