package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.service.ShoppingCartService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sky.constant.MessageConstant.ADDRESS_BOOK_IS_NULL;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;



    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        // 查询用户ID
        Long userId = BaseContext.getCurrentId();

        // 从地址簿中查询地址信息
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());

        //判断地址是否存在是否存在
        if (addressBook == null) {
            throw new OrderBusinessException(ADDRESS_BOOK_IS_NULL);
        }

        // 查找用户购物车中的商品
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> cartList = shoppingCartMapper.list(shoppingCart);

        if (cartList != null && cartList.isEmpty()) {
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 插入订单
        Orders orders = new Orders();

        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setUserId(userId);
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getProvinceName()
                + addressBook.getCityName()
                + addressBook.getDistrictName()
                + addressBook.getDetail());

        ordersMapper.insert(orders);

        // 插入订单细节
        for (ShoppingCart cart : cartList) {
            OrderDetail ordersDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, ordersDetail);
            ordersDetail.setOrderId(orders.getId());
            orderDetailMapper.insert(ordersDetail);
        }

        // 清空购物车
        shoppingCartMapper.delete(shoppingCart);

        // 封装订单提交VO
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();



        return orderSubmitVO;
    }

    /**
     * 订单支付（跳过微信支付，直接更新订单状态）
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("模拟支付，订单号：{}", ordersPaymentDTO.getOrderNumber());

        // 直接执行支付成功逻辑，更新订单状态
        paySuccess(ordersPaymentDTO.getOrderNumber());

        // 返回一个空的支付VO（前端不会真正调起微信支付了）
        return OrderPaymentVO.builder()
                .nonceStr("")
                .paySign("")
                .timeStamp("")
                .signType("RSA")
                .packageStr("")
                .build();
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = ordersMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        ordersMapper.update(orders);
    }

    /**
     * 历史订单查询
     * @param ordersPageQueryDTO 订单查询参数
     * @return 订单分页结果集
     */
    public PageResult historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {

        Long userId = BaseContext.getCurrentId();
        ordersPageQueryDTO.setUserId(userId);

        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = ordersMapper.selectPage(ordersPageQueryDTO);

        List<Orders> ordersList = page.getResult();
        List<OrderVO> orderVOList = new ArrayList<>();

        log.info("历史订单查询参数：{}", page.getResult());

        for (Orders orders : ordersList) {
            Long ordersId = orders.getId();
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            List<OrderDetail> orderDetailList = orderDetailMapper.listByOrderId(ordersId);
            orderVO.setOrderDetailList(orderDetailList);
            orderVOList.add(orderVO);
        }

        log.info("历史订单查询结果：{}", orderVOList.get(0).getUserId());


        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(orderVOList);

        log.info("历史订单查询结果：{}", pageResult);

        return pageResult;
    }

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单详情
     */
    public OrderVO detail(Long id) {
        Orders orders = ordersMapper.selectById(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        List<OrderDetail> orderDetailList = orderDetailMapper.listByOrderId(id);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 取消订单
     * @param id 订单id
     */
    public void cancel(Long id) {
        Orders orders = ordersMapper.selectById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (orders.getStatus() != Orders.TO_BE_CONFIRMED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.setStatus(Orders.CANCELLED);
        ordersMapper.update(orders);
    }

    /**
     * 再来一单
     * @param id 订单id
     */
    public void repetition(Long id) {

        // 清空购物车
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        shoppingCartMapper.delete(shoppingCart);

        // 根据订单id查询订单，判断订单是否存在且状态是否为已确认
        Orders orders = ordersMapper.selectById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (orders.getStatus() != Orders.COMPLETED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        // 查询旧订单菜品信息
        List<OrderDetail> orderDetailList = orderDetailMapper.listByOrderId(id);

        // 写入购物车
        for (OrderDetail orderDetail : orderDetailList) {
            BeanUtils.copyProperties(orderDetail, shoppingCart);
            shoppingCart.setUserId(userId);
            shoppingCartMapper.insert(shoppingCart);
        }
    }
}