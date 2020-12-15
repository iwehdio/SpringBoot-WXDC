package cn.iwehdio.sell.service.impl;

import cn.iwehdio.sell.converter.OrderMaster2OrderDTOConverter;
import cn.iwehdio.sell.dao.OrderDetailDao;
import cn.iwehdio.sell.dao.OrderMasterDao;
import cn.iwehdio.sell.dataObject.OrderDetail;
import cn.iwehdio.sell.dataObject.OrderMaster;
import cn.iwehdio.sell.dataObject.ProductInfo;
import cn.iwehdio.sell.dto.CartDTO;
import cn.iwehdio.sell.dto.OrderDTO;
import cn.iwehdio.sell.enums.OrderStatusEnum;
import cn.iwehdio.sell.enums.PayStatusEnum;
import cn.iwehdio.sell.enums.ResultEnum;
import cn.iwehdio.sell.exception.SellException;
import cn.iwehdio.sell.service.OrderMasterService;
import cn.iwehdio.sell.service.PayService;
import cn.iwehdio.sell.service.ProductInfoService;
import cn.iwehdio.sell.service.WebSocket;
import cn.iwehdio.sell.utils.KeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单核心操作
 */
@Service
public class OrderMasterServiceImpl implements OrderMasterService {
    @Autowired
    private PayService payService;
    @Autowired
    private ProductInfoService productInfoService;
    @Autowired
    private OrderMasterDao masterDao;
    @Autowired
    private OrderDetailDao detailDao;
    @Autowired
    private WebSocket webSocket;
    private final Logger logger = LoggerFactory.getLogger(OrderMasterServiceImpl.class);
    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {
        //开始生成订单号，初始化总价和购物车列表
        String orderId = KeyUtil.genUniqueKey();
        BigDecimal orderAmount = new BigDecimal("0");
        List<CartDTO> cartDTOList = new ArrayList<>();
        //1、查询商品数量和价格
        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()) {
            ProductInfo productInfo = productInfoService.findOne(orderDetail.getProductId());
            if(productInfo == null) {
                throw new SellException(ResultEnum.PRODUCT_NOT_EXIST);
            }
            //2、计算总价
            orderAmount = productInfo.getProductPrice()
                    .multiply(new BigDecimal(orderDetail.getProductQuantity()))
                    .add(orderAmount);
            //3、写入订单数据库detail，注意先拷贝再赋新值
            BeanUtils.copyProperties(productInfo,orderDetail);
            orderDetail.setDetailId(KeyUtil.genUniqueKey());
            orderDetail.setOrderId(orderId);
            detailDao.save(orderDetail);

            CartDTO cartDTO = new CartDTO(orderDetail.getProductId(), orderDetail.getProductQuantity());
            cartDTOList.add(cartDTO);

        }
        //3、写入订单数据库master
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        orderDTO.setOrderAmount(orderAmount);
        orderDTO.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderDTO.setPayStatus(PayStatusEnum.WAIT.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        masterDao.save(orderMaster);
        //4、扣库存
        productInfoService.decreaseStock(cartDTOList);

        webSocket.sendMessage(orderId);
        return orderDTO;
    }

    @Override
    public OrderDTO findOne(String orderId) {
        OrderMaster orderMaster = masterDao.findOne(orderId);
        if (orderMaster == null) {
            throw new SellException(ResultEnum.ORDER_NOT_EXIST);
        }
        List<OrderDetail> orderDetailList = detailDao.findByOrderId(orderId);
        if (orderDetailList.isEmpty()){
            throw new SellException(ResultEnum.ORDERDETAIL_NOT_EXIST);
        }
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderMaster,orderDTO);
        orderDTO.setOrderDetailList(orderDetailList);
        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findList(String buyerOpenid, Pageable pageable) {
        Page<OrderMaster> orderMasterPage = masterDao.findByBuyerOpenid(buyerOpenid, pageable);
        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());
        return new PageImpl<OrderDTO>(orderDTOList,pageable,orderMasterPage.getTotalElements());
    }

    @Override
    @Transactional
    public OrderDTO cancel(OrderDTO orderDTO) {
        OrderMaster orderMaster = new OrderMaster();
        //1、判断订单状态，指定状态下才能被取消
        if (!OrderStatusEnum.NEW.getCode().equals(orderDTO.getOrderStatus())){
            logger.error("【取消订单】订单状态不正确，orderId={},orderStatus={}",
                    orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //2、修改订单状态
        orderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster save = masterDao.save(orderMaster);
        if (save ==null){
            logger.error("【取消订单】更新失败,orderMaster={}",orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }
        //3、返还库存
        if(orderDTO.getOrderDetailList().isEmpty()) {
            logger.error("【取消订单】详情为空,orderDTO={}",orderDTO);
            throw new SellException(ResultEnum.ORDER_DETAIL_EMPTY);
        }
        List<CartDTO> cartDTOList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()) {
            cartDTOList.add(new CartDTO(orderDetail.getProductId(),orderDetail.getProductQuantity()));
        }
        productInfoService.increaseStock(cartDTOList);
        //4、如果已支付需要退款
        if (PayStatusEnum.SUCCESS.getCode().equals(orderDTO.getPayStatus())){
            payService.refund(orderDTO.getOrderId());
        }
        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO finish(OrderDTO orderDTO) {
        OrderMaster orderMaster = new OrderMaster();
        //1、判断订单状态，指定状态下才能被完结
        if (!OrderStatusEnum.NEW.getCode().equals(orderDTO.getOrderStatus())){
            logger.error("【完结订单】订单状态不正确，orderId={},orderStatus={}",
                    orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //2、修改订单状态
        orderDTO.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster save = masterDao.save(orderMaster);
        if (save ==null){
            logger.error("【完结订单】更新失败,orderMaster={}",orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }
        return orderDTO;
    }

    @Override
    @Transactional
    public OrderDTO paid(OrderDTO orderDTO){
        OrderMaster orderMaster = new OrderMaster();
        //1、判断订单状态，指定状态下才能被支付
        if (!OrderStatusEnum.NEW.getCode().equals(orderDTO.getOrderStatus())){
            logger.error("【支付订单】订单状态不正确，orderId={},orderStatus={}",
                    orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //2、判断订单支付状态
        if (!PayStatusEnum.WAIT.getCode().equals(orderDTO.getPayStatus())){
            logger.error("【支付订单】更新失败,orderDTO={}",orderDTO);
            throw new SellException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }
        //3、修改订单状态
        orderDTO.setPayStatus(PayStatusEnum.SUCCESS.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster save = masterDao.save(orderMaster);
        if (save ==null){
            logger.error("【支付订单】更新失败,orderMaster={}",orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }
        logger.info("【支付订单】支付成功,orderMaster={}",orderMaster);
        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findAll(Pageable pageable) {
        Page<OrderMaster> masterPage = masterDao.findAll(pageable);
        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(masterPage.getContent());
        return new PageImpl<OrderDTO>(orderDTOList, pageable, masterPage.getTotalElements());
    }

}
