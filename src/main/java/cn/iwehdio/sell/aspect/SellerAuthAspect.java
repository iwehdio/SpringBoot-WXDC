package cn.iwehdio.sell.aspect;

import cn.iwehdio.sell.exception.SellerAuthException;
import cn.iwehdio.sell.utils.CookieUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * AOP实现卖家端权限
 */
@Aspect
@Component
public class SellerAuthAspect {
    private final String prefix = "token_";
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Pointcut("execution(public * cn.iwehdio.sell.controller.Seller*.*(..))" +
            "&& !execution(public * cn.iwehdio.sell.controller.SellerLogController.*(..))")
    public void verify(){}

    @Before("verify()")
    public void doVerify(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Cookie cookie = CookieUtil.findCookie(request, prefix);
        if (cookie==null){
            throw new SellerAuthException();
        }
        String s = redisTemplate.opsForValue().get(cookie.getValue());
        if (s==null){
            throw new SellerAuthException();
        }
    }

}
