package cn.iwehdio.sell.controller;

import cn.iwehdio.sell.dataObject.SellerInfo;
import cn.iwehdio.sell.service.SellerInfoService;
import cn.iwehdio.sell.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 卖家端登录
 */
@Controller
@RequestMapping("/seller")
public class SellerLogController {
    private final String prefix = "token_";
    private final Integer expire = 7200; //过期时间

    @Autowired
    private SellerInfoService sellerInfoService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @GetMapping("/login")
    public ModelAndView login(){
        return new ModelAndView("login/login");
    }

    @GetMapping("/loginning")
    public ModelAndView loginning(@RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  Map<String,Object> map,
                                  HttpServletResponse response){
        SellerInfo sellerInfo = sellerInfoService.findSellerInfoByUsername(username);
        if (sellerInfo==null || sellerInfo.getPassword()==null || !sellerInfo.getPassword().equals(password)) {
            map.put("msg","登录失败");
            map.put("url","/sell/seller/login");
            return new ModelAndView("common/error",map);
        }
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(prefix + token, username, expire, TimeUnit.SECONDS);
        CookieUtil.saveCookie(response,prefix, prefix + token);
        return new ModelAndView("redirect:/seller/order/list");
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpServletRequest request,
                               HttpServletResponse response,
                               Map<String,Object> map) {
        String cookie = CookieUtil.removeCookie(request, response,prefix);
        if (cookie!=null) {
            redisTemplate.opsForValue().getOperations().delete(cookie);
        }
        map.put("msg","登出成功");
        map.put("url","#");
        return new ModelAndView("common/success",map);
    }
}
