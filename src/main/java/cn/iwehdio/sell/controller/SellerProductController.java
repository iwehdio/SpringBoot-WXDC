package cn.iwehdio.sell.controller;

import cn.iwehdio.sell.dataObject.ProductCategory;
import cn.iwehdio.sell.dataObject.ProductInfo;
import cn.iwehdio.sell.dto.OrderDTO;
import cn.iwehdio.sell.enums.ProductStatusEnum;
import cn.iwehdio.sell.exception.SellException;
import cn.iwehdio.sell.form.ProductForm;
import cn.iwehdio.sell.service.OrderMasterService;
import cn.iwehdio.sell.service.ProductCategoryService;
import cn.iwehdio.sell.service.ProductInfoService;
import cn.iwehdio.sell.utils.KeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 卖家端商品操作
 */
@Controller
@RequestMapping("/seller/product")
public class SellerProductController {
    @Autowired
    private ProductInfoService productService;
    @Autowired
    private ProductCategoryService categoryService;
    @GetMapping("/list")
    public ModelAndView list(@RequestParam(value = "page",defaultValue = "1") Integer page,
                             @RequestParam(value = "size",defaultValue = "10") Integer size,
                             Map<String,Object> map){
        PageRequest request = new PageRequest(page-1, size);
        Page<ProductInfo> productInfoPage = productService.findAll(request);
        map.put("productInfoPage",productInfoPage);
        map.put("currentPage",page);
        map.put("size",size);
        return new ModelAndView("sellerProduct/list",map);
    }

    private String returnUrl = "/sell/seller/product/list";
    @GetMapping("/onSale")
    public ModelAndView onSale(@RequestParam("productId") String productId,
                               Map<String,Object> map) {
        try {
            productService.onSale(productId);
        } catch (SellException e) {
            map.put("msg", e.getMessage());
            map.put("url",returnUrl);
            return new ModelAndView("common/error",map);
        }
        map.put("msg", "SUCCESS");
        map.put("url",returnUrl);
        return new ModelAndView("common/success",map);
    }

    @GetMapping("/offSale")
    public ModelAndView offSale(@RequestParam("productId") String productId,
                               Map<String,Object> map) {
        try {
            productService.offSale(productId);
        } catch (SellException e) {
            map.put("msg", e.getMessage());
            map.put("url",returnUrl);
            return new ModelAndView("common/error",map);
        }
        map.put("msg", "SUCCESS");
        map.put("url",returnUrl);
        return new ModelAndView("common/success",map);
    }

    @GetMapping("/index")
    public ModelAndView index(@RequestParam(value = "productId",required = false) String productId,
                              Map<String,Object> map) {
        if (productId!=null && !productId.isEmpty()) {
            ProductInfo productInfo = productService.findOne(productId);
            map.put("productInfo",productInfo);
        }
        List<ProductCategory> categoryList = categoryService.findAll();
        map.put("categoryList", categoryList);
        return new ModelAndView("sellerProduct/index",map);

    }


    @PostMapping("/save")
    public ModelAndView save(@Valid ProductForm form,
                             BindingResult bindingResult,
                             Map<String,Object> map){
        if (bindingResult.hasErrors()) {
            map.put("msg", bindingResult.getFieldError().getDefaultMessage());
            map.put("url","/sell/seller/product/index");
            return new ModelAndView("common/error",map);
        }
        ProductInfo productInfo = new ProductInfo();
        try {
            if (form.getProductId()!=null && !form.getProductId().isEmpty()) {
                productInfo = productService.findOne(form.getProductId());
            }else {
                form.setProductId(KeyUtil.genUniqueKey());
                productInfo.setProductStatus(ProductStatusEnum.DOWN.getCode());
            }
            BeanUtils.copyProperties(form,productInfo);
            productService.save(productInfo);
        } catch (Exception e) {
            map.put("msg", e.getMessage());
            map.put("url","/sell/seller/product/index");
            return new ModelAndView("common/error",map);
        }
        map.put("msg", "SUCCESS");
        map.put("url",returnUrl);
        return new ModelAndView("common/success",map);
    }
}
