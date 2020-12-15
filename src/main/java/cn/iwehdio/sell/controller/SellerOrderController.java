package cn.iwehdio.sell.controller;

import cn.iwehdio.sell.dto.OrderDTO;
import cn.iwehdio.sell.exception.SellException;
import cn.iwehdio.sell.service.OrderMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * 卖家订单操作
 */
@Controller
@RequestMapping("/seller/order")
public class SellerOrderController {
    @Autowired
    private OrderMasterService masterService;

    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "page",defaultValue = "1") Integer page,
                             @RequestParam(value = "size",defaultValue = "10") Integer size,
                             Map<String,Object> map){
        PageRequest request = new PageRequest(page-1, size);
        Page<OrderDTO> orderDTOPage = masterService.findAll(request);
        map.put("orderDTOPage",orderDTOPage);
        map.put("currentPage",page);
        map.put("size",size);
        return new ModelAndView("sellerOrder/list",map);
    }

    private String returnUrl = "/sell/seller/order/list";
    @GetMapping("/cancel")
    public ModelAndView cancel(@RequestParam("orderId") String orderId,
                               Map<String,Object> map){
        OrderDTO orderDTO;
        try {
            orderDTO = masterService.findOne(orderId);
            masterService.cancel(orderDTO);
        } catch (SellException e) {
            map.put("msg", e.getMessage());
            map.put("url",returnUrl);
            return new ModelAndView("common/error",map);
        }

        map.put("msg", "SUCCESS");
        map.put("url",returnUrl);
        return new ModelAndView("common/success",map);
    }

    @GetMapping("/detail")
    public ModelAndView detail(@RequestParam("orderId") String orderId,
                               Map<String,Object> map) {
        OrderDTO orderDTO;
        try {
            orderDTO = masterService.findOne(orderId);
        } catch (SellException e) {
            map.put("msg", e.getMessage());
            map.put("url",returnUrl);
            return new ModelAndView("sellerOrder/error",map);
        }
        map.put("orderDTO", orderDTO);
        return new ModelAndView("sellerOrder/detail",map);
    }

    @GetMapping("/finish")
    public ModelAndView finish(@RequestParam("orderId") String orderId,
                               Map<String,Object> map) {
        OrderDTO orderDTO;
        try {
            orderDTO = masterService.findOne(orderId);
            masterService.finish(orderDTO);
        } catch (SellException e) {
            map.put("msg", e.getMessage());
            map.put("url",returnUrl);
            return new ModelAndView("common/error",map);
        }
        map.put("msg", "SUCCESS");
        map.put("url",returnUrl);
        return new ModelAndView("common/success",map);
    }
}
