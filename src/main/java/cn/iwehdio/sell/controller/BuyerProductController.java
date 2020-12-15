package cn.iwehdio.sell.controller;

import cn.iwehdio.sell.dataObject.ProductCategory;
import cn.iwehdio.sell.dataObject.ProductInfo;
import cn.iwehdio.sell.service.ProductCategoryService;
import cn.iwehdio.sell.service.ProductInfoService;
import cn.iwehdio.sell.utils.ProductResultVOUtil;
import cn.iwehdio.sell.viewObject.ProductCategoryVO;
import cn.iwehdio.sell.viewObject.ProductResultVO;
import cn.iwehdio.sell.viewObject.ProdutInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 买家端产品列表
 */
@RestController
@RequestMapping("/buyer/product")
public class BuyerProductController {
    @Autowired
    private ProductCategoryService categoryService;
    @Autowired
    private ProductInfoService infoService;

    @GetMapping("/list")
    @Cacheable(value = "product", key = "123")
    public ProductResultVO<ProductCategoryVO> list() {
        //1、查询所有上架商品
        List<ProductInfo> upAll = infoService.findUpAll();
        //2、查询类名
        List<Integer> categoryTypeList = new ArrayList<>();
        for (ProductInfo productInfo : upAll) {
            categoryTypeList.add(productInfo.getCategoryType());
        }
        List<ProductCategory> productCategoryList = categoryService.findByCategoryTypeIn(categoryTypeList);
        //3、数据拼装
        List<ProductCategoryVO> categoryVOList = new ArrayList<>();
        for (ProductCategory productCategory : productCategoryList) {
            ProductCategoryVO categoryVO = new ProductCategoryVO();
            categoryVO.setCategoryType(productCategory.getCategoryType());
            categoryVO.setCategoryName(productCategory.getCategoryName());
            List<ProdutInfoVO> infoVOList = new ArrayList<>();
            for (ProductInfo productInfo : upAll) {
                if (productInfo.getCategoryType().equals(productCategory.getCategoryType())){
                    ProdutInfoVO infoVO = new ProdutInfoVO();
                    BeanUtils.copyProperties(productInfo,infoVO);
                    infoVOList.add(infoVO);
                }
            }
            categoryVO.setFoods(infoVOList);
            categoryVOList.add(categoryVO);
        }

        return ProductResultVOUtil.success(categoryVOList);

//        ProductResultVO<ProductCategoryVO> resultVO = new ProductResultVO<>();
//        ProductCategoryVO categoryVO = new ProductCategoryVO();
//        ProdutInfoVO infoVO = new ProdutInfoVO();
//
//        categoryVO.setFoods(Arrays.asList(infoVO,infoVO));
//        resultVO.setData(Arrays.asList(categoryVO));

    }
}
