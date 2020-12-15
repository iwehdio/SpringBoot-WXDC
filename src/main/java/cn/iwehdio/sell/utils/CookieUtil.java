package cn.iwehdio.sell.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * Cookie工具类
 */
public class CookieUtil {
    public static void saveCookie(HttpServletResponse response,String key,String value) {
        Cookie cookie = new Cookie(key, value);
        response.addCookie(cookie);
    }
    public static Cookie findCookie(HttpServletRequest request,String key){
        Cookie[] cookies = request.getCookies();
        if (cookies!=null) {
            for (Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static String removeCookie(HttpServletRequest request,HttpServletResponse response, String key){
        Cookie cookie = findCookie(request, key);
        if (cookie!=null){
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return cookie.getValue();
        }
        return null;
    }
}
