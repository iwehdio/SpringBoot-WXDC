package cn.iwehdio.sell.service.impl;

import cn.iwehdio.sell.config.AlipayConfig;
import cn.iwehdio.sell.dataObject.OrderDetail;
import cn.iwehdio.sell.dto.OrderDTO;
import cn.iwehdio.sell.service.OrderMasterService;
import cn.iwehdio.sell.service.PayService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 支付操作
 */
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    private OrderMasterService masterService;
    private Logger logger = LoggerFactory.getLogger(PayServiceImpl.class);
    @Override
    public Map<String, String> create(OrderDTO orderDTO) {
        String subject = getProductNames(orderDTO);
        String CHARSET = alipayConfig.getCharset();
        String APP_ID = alipayConfig.getAppId();
        String APP_PRIVATE_KEY = alipayConfig.getAppPrivateKey();
        String ALIPAY_PUBLIC_KEY = alipayConfig.getAlipayPublicKey();
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getServerUrl(), APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2"); //获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        String baseUrl = alipayConfig.getBaseUrl();
        alipayRequest.setReturnUrl(baseUrl + "/pay/backwechat");
        alipayRequest.setNotifyUrl(baseUrl + "/pay/notifyUrl");//在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                " \"out_trade_no\":\"" + orderDTO.getOrderId() + "\"," +
                " \"total_amount\":\"" + orderDTO.getOrderAmount() + "\"," +
                " \"subject\":\"" + subject + "\"," +
                " \"product_code\":\"QUICK_WAP_PAY\"" +
                " }");
        String form = "";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        String oriUrl = StringUtils.substringBetween(form, alipayConfig.getServerUrl(), "\">");
        String biz_content = "";
        try {
            biz_content = URLEncoder.encode(alipayRequest.getBizContent(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String toUrl = oriUrl + "&biz_content=" + biz_content;

        toUrl = alipayConfig.getServerUrl() + toUrl;
        String checkUrl = baseUrl + "/pay/checkpay?orderId=" + orderDTO.getOrderId();
        Map<String, String> map = new HashMap<>();
        map.put("toUrl",toUrl);
        map.put("checkUrl",checkUrl);

        return map;
    }

    private String getProductNames(OrderDTO orderDTO) {
        StringBuilder subject = new StringBuilder();
        List<OrderDetail> orderDetailList = orderDTO.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            subject.append(orderDetail.getProductName()).append(" ");
        }
        return subject.toString();
    }

    @Override
    public boolean notifyUrl(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");

        Map<String, String> paramsMap = new HashMap<>(); //将异步通知中收到的所有参数都存放到map中
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            paramsMap.put(name, valueStr);
        }

        boolean signVerified = false; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(paramsMap, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSign_type());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }


        boolean check = checkNotifyRequest(paramsMap);
        try {
            PrintWriter writer = response.getWriter();
            check = checkNotifyRequest(paramsMap);
            if(signVerified && check){
                writer.write("success");
                logger.info("【回调支付成功】,orderId={}",request.getParameter("out_trade_no"));
            }else{
                writer.write("failure");
                logger.info("【回调支付失败】,orderId={}",request.getParameter("out_trade_no"));
            }
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return signVerified && check;
    }

    @Override
    public void refund(String orderId) {
        String CHARSET = alipayConfig.getCharset();
        String APP_ID = alipayConfig.getAppId();
        String APP_PRIVATE_KEY = alipayConfig.getAppPrivateKey();
        String ALIPAY_PUBLIC_KEY = alipayConfig.getAlipayPublicKey();
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getServerUrl(), APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2"); //获得初始化的AlipayClient
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();//创建API对应的request类
        OrderDTO orderDTO = masterService.findOne(orderId);
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + orderId + "\"," +
                "\"out_request_no\":\"1000001\"," +
                "\"refund_amount\":\"0" + orderDTO.getOrderAmount() + "\"}"); //设置业务参数
        AlipayTradeRefundResponse response = null;//通过alipayClient调用API，获得对应的response类
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        System.out.print(response.getBody());
    }

    private boolean checkNotifyRequest(Map<String,String> paramMap){
        OrderDTO orderDTO = masterService.findOne(paramMap.get("out_trade_no"));
        if (orderDTO==null) {
            return false;
        }
        if (!"TRADE_SUCCESS".equals(paramMap.get("trade_status"))) {
            return false;
        }
        if (!orderDTO.getOrderAmount().equals(new BigDecimal(paramMap.get("total_amount")))){
            return false;
        }
        return true;
    }
}
