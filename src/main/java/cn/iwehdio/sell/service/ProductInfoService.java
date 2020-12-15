package cn.iwehdio.sell.service;

import cn.iwehdio.sell.dataObject.ProductInfo;
import cn.iwehdio.sell.dto.CartDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductInfoService {
    ProductInfo findOne(String productId);
    List<ProductInfo> findUpAll();  //所有上架商品
    Page<ProductInfo> findAll(Pageable pageable);
    ProductInfo save(ProductInfo productInfo);
    //加减库存
    void increaseStock(List<CartDTO> cartDTOList);
    void decreaseStock(List<CartDTO> cartDTOList);

    //上下架
    ProductInfo onSale(String pruductId);
    ProductInfo offSale(String pruductId);
}
