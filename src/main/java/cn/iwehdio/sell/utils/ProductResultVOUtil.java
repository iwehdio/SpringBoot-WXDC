package cn.iwehdio.sell.utils;

import cn.iwehdio.sell.viewObject.ProductResultVO;

/**
 * 生成返回给前端的数据
 */
public class ProductResultVOUtil {
    public static ProductResultVO success(Object object) {
        ProductResultVO resultVO = new ProductResultVO();
        resultVO.setData(object);
        resultVO.setMsg("success");
        resultVO.setCode(0);
        return resultVO;
    }
    public static ProductResultVO success() {
        return success(null);
    }
    public static ProductResultVO error(Integer code, String msg) {
        ProductResultVO resultVO = new ProductResultVO();
        resultVO.setMsg(msg);
        resultVO.setCode(code);
        return resultVO;
    }
}
