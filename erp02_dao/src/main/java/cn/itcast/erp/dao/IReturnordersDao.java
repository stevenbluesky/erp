package cn.itcast.erp.dao;

import java.util.List;

import cn.itcast.erp.entity.Goods;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Returnorders;
/**
 * 退货订单数据访问接口
 * @author Administrator
 *
 */
public interface IReturnordersDao extends IBaseDao<Returnorders>{
	 /**
	  * 
	  * @param ordersuuid	原订单编号
	  * @param goodsuuid	退货商品id
	  * @param num			退货商品数量
	  * @return
	  */
	String findifmore(Long ordersuuid, Long goodsuuid, Long num);

}
