package cn.iwehdio.sell.enums;

public enum ResultEnum {
    PRODUCT_NOT_EXIST(0,"商品不存在"),
    PRODUCT_STOCK_ERROR(1,"库存错误"),
    ORDER_NOT_EXIST(2,"订单不存在"),
    ORDERDETAIL_NOT_EXIST(3,"订单详情不存在"),
    ORDER_STATUS_ERROR(4,"订单状态不正确"),
    ORDER_UPDATE_FAIL(5,"订单更新失败"),
    ORDER_DETAIL_EMPTY(6,"订单详情为空"),
    ORDER_PAY_STATUS_ERROR(7,"支付状态错误"),
    PARAM_ERROR(8,"参数不正确"),
    CART_EMPTY(9,"购物车为空"),
    ORDER_OWNER_ERROR(10,"操作的不是你的订单"),
    WECHAT_MP_ERROR(11,"微信授权出错"),
    PRODUCT_STATUS_ERROR(12,"订单状态出错"),
    ACTIVITY_END(13,"库存不足"),
    CANNT_GET_LOCK(14,"请求下单失败"),
    ;
    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
