package cn.iwehdio.sell.service.impl;

import cn.iwehdio.sell.dao.ProductCategoryDao;
import cn.iwehdio.sell.dataObject.ProductCategory;
import cn.iwehdio.sell.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品类目操作
 */
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    @Autowired
    private ProductCategoryDao dao;
    @Override
    public ProductCategory findOne(Integer categoryId) {
        return dao.findOne(categoryId);
    }
    @Override
    public List<ProductCategory> findAll() {
        return dao.findAll();
    }
    @Override
    public List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList) {
        return dao.findByCategoryTypeIn(categoryTypeList);
    }
    @Override
    public ProductCategory save(ProductCategory productCategory) {
        return dao.save(productCategory);
    }
}
