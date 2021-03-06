package com.pinyougou.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbOrderItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojo.TbSellerExample;
import com.pinyougou.pojogroup.Order;
import com.pinyougou.user.service.OrderService;

import entity.PageResult;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	@Autowired
	private TbSellerMapper sellerMapper;
	
	@Override
	public PageResult findList(String status,String userId, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		TbOrderExample example=new TbOrderExample();
		example.setOrderByClause("create_time desc");
		Criteria criteria=example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		System.out.println(status);
		try {
			if (Integer.parseInt(status)>0&&Integer.parseInt(status)<6) {
				criteria.andStatusEqualTo(status);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}
		
		Page<TbOrder> orderList=(Page<TbOrder>) orderMapper.selectByExample(example);
		List<TbOrderItem> tbOrderItemList=new ArrayList<>();
		List<Order> orders=new ArrayList<>();//组合实体类的集合
		
		for(int i=0;i<orderList.size();i++){
			orderList.get(i).setOrderIdString(orderList.get(i).getOrderId().toString());
			System.out.println(orderList.get(i).getOrderId().toString());
			String sellerId=orderList.get(i).getSellerId();
			System.out.println(sellerId);
			String nickName=sellerMapper.selectByPrimaryKey(sellerId).getNickName();
			orderList.get(i).setSellerId(nickName);
			TbOrderItemExample example2=new TbOrderItemExample();
			com.pinyougou.pojo.TbOrderItemExample.Criteria criteria2=example2.createCriteria();
			criteria2.andOrderIdEqualTo(orderList.get(i).getOrderId());
			tbOrderItemList=orderItemMapper.selectByExample(example2);
			Order order=new Order();
			order.setTbOrder(orderList.get(i));
			order.setTbOrderItemList(tbOrderItemList);
			orders.add(order);
		}
		PageResult pageResult=new PageResult(orderList.getTotal(), orders);
		return pageResult;
	}
	
	@Override
	public void updateOrderStatus(String status, String orderId) {
		TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.valueOf(orderId));//获得order对象
		tbOrder.setStatus(status);//设置状态码
		orderMapper.updateByPrimaryKey(tbOrder);
	}

}
