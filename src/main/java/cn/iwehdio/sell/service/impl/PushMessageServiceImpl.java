package cn.iwehdio.sell.service.impl;

import cn.iwehdio.sell.config.WechatAccountConfig;
import cn.iwehdio.sell.dto.OrderDTO;
import cn.iwehdio.sell.service.PushMessageService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PushMessageServiceImpl implements PushMessageService {
    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private WechatAccountConfig wechatAccountConfig;
    private final String TEMPLATEID = "confirmMessage";
    @Override
    public void orderStatus(OrderDTO orderDTO) {
        WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
        templateMessage.setTemplateId(wechatAccountConfig.getTemplateIds().get(TEMPLATEID));
        templateMessage.setToUser(orderDTO.getBuyerOpenid());
        List<WxMpTemplateData> data = Arrays.asList(
                new WxMpTemplateData("first","请确认订单"),
                new WxMpTemplateData("sellerName","iwehdio"),
                new WxMpTemplateData("orderId",orderDTO.getOrderId()),
                new WxMpTemplateData("orderStatus",orderDTO.getOrderStatusEnum().getMessage()),
                new WxMpTemplateData("orderAmount",orderDTO.getOrderAmount().toString()),
                new WxMpTemplateData("orderAddress",orderDTO.getBuyerAddress()),
                new WxMpTemplateData("endMsg","---------------------")
        );
        templateMessage.setData(data);
        try {
            wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
    }
}
