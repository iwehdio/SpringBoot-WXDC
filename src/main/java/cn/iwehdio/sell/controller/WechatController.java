package cn.iwehdio.sell.controller;

import cn.iwehdio.sell.config.AlipayConfig;
import cn.iwehdio.sell.enums.ResultEnum;
import cn.iwehdio.sell.exception.SellException;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 微信获取openid
 */
@Controller
@RequestMapping("/wechat")
public class WechatController {
    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private AlipayConfig alipayConfig;
    private final Logger logger = LoggerFactory.getLogger(WechatController.class);
    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl){
        String url = alipayConfig.getBaseUrl()+"/wechat/userInfo";
        String redirectUrl = null;
        try {
            redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_BASE, URLEncoder.encode(returnUrl,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logger.info(redirectUrl);
        return "redirect:"+redirectUrl;

    }
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,
                         @RequestParam("state") String returnUrl){
        WxMpOAuth2AccessToken accessToken;
        try {
            accessToken = wxMpService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {
            throw new SellException(ResultEnum.WECHAT_MP_ERROR,e.getError().getErrorMsg());
        }
        String openId = accessToken.getOpenId();
        return "redirect:"+returnUrl+"?openid="+openId;
    }
}
