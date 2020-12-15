package cn.iwehdio.sell.viewObject;

import java.io.Serializable;
import java.util.List;

/**
 * 返回给前端的JSON对应的对象
 * @param <T>
 */
public class ProductResultVO<T> implements Serializable {


    private static final long serialVersionUID = 2785362815743400432L;;

    private Integer code;   //状态码，0正常1错误
    private String msg;
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ProductResultVO{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
