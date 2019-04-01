package com.pinyougou.user.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbOrderItemExample;
import com.pinyougou.pojogroup.Order;
import com.pinyougou.user.service.OrderService;

import entity.PageResult;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	@Override
	public PageResult findList(String status,String userId, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		TbOrderExample example=new TbOrderExample();
		Criteria criteria=example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		if (status!=null) {
			criteria.andStatusEqualTo(status);
		}
		Page<TbOrder> orderList=(Page<TbOrder>) orderMapper.selectByExample(example);
		List<TbOrderItem> tbOrderItemList=new ArrayList<>();
		List<Order> orders=new ArrayList<>();//组合实体类的集合
		
		for(TbOrder o:orderList){
			TbOrderItemExample example2=new TbOrderItemExample();
			com.pinyougou.pojo.TbOrderItemExample.Criteria criteria2=example2.createCriteria();
			criteria2.andOrderIdEqualTo(o.getOrderId());
			tbOrderItemList=orderItemMapper.selectByExample(example2);
			Order order=new Order();
			order.setTbOrder(o);
			order.setTebOrderItemList(tbOrderItemList);
			orders.add(order);
		}
		PageResult pageResult=new PageResult(orderList.getTotal(), orders);
		return pageResult;
	}

}
