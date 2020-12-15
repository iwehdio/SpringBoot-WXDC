package cn.iwehdio.sell.viewObject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 返回给前端的JSON对应的对象
 */
public class ProductCategoryVO implements Serializable {
    private static final long serialVersionUID = -3395223637557822060L;
    @JsonProperty("name")
    private String categoryName;
    @JsonProperty("type")
    private Integer categoryType;
    private List<ProdutInfoVO> foods;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
        this.categoryType = categoryType;
    }

    public List<ProdutInfoVO> getFoods() {
        return foods;
    }

    public void setFoods(List<ProdutInfoVO> foods) {
        this.foods = foods;
    }

    @Override
    public String toString() {
        return "ProductCategoryVO{" +
                "categoryName='" + categoryName + '\'' +
                ", categoryType=" + categoryType +
                ", foods=" + foods +
                '}';
    }
}
