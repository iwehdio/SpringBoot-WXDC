package cn.iwehdio.sell.handler;

import cn.iwehdio.sell.exception.SellException;
import cn.iwehdio.sell.exception.SellerAuthException;
import cn.iwehdio.sell.utils.ProductResultVOUtil;
import cn.iwehdio.sell.viewObject.ProductResultVO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class SellerAuthExceptionHandler {
    @ExceptionHandler(SellerAuthException.class)
    public ModelAndView handlerSellerAuth(){
        return new ModelAndView("redirect:/seller/login");
    }

    @ExceptionHandler(SellException.class)
    @ResponseBody
    public ProductResultVO handlerSell(SellException e) {
        return ProductResultVOUtil.error(e.getCode(), e.getMessage());
    }
}
