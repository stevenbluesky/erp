package cn.itcast.erp.dao.impl;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import cn.itcast.erp.dao.IReturnordersDao;
import cn.itcast.erp.entity.Goods;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Returnorders;
/**
 * 退货订单数据访问类
 * @author Administrator
 *
 */
public class ReturnordersDao extends BaseDao<Returnorders> implements IReturnordersDao {

	/**
	 * 构建查询条件
	 * @param dep1
	 * @param dep2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Returnorders returnorders1,Returnorders returnorders2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Returnorders.class);
		if(returnorders1!=null){
			if(null != returnorders1.getType() && returnorders1.getType().trim().length()>0){
				dc.add(Restrictions.like("type", returnorders1.getType(), MatchMode.ANYWHERE));
			}
			if(null != returnorders1.getState() && returnorders1.getState().trim().length()>0){
				dc.add(Restrictions.eq("state", returnorders1.getState()));
			}
			if(null != returnorders1.getOrdersuuid() ){
				dc.add(Restrictions.eq("ordersuuid", returnorders1.getOrdersuuid()));
			}
			if(null != returnorders1.getUuid() ){
				dc.add(Restrictions.eq("uuid", returnorders1.getUuid()));
			}
			if(null != returnorders1.getEnder()){
				dc.add(Restrictions.eq("ender", returnorders1.getEnder()));
			}
			if(null != returnorders1.getEndtime()){
				dc.add(Restrictions.le("endtime", returnorders1.getEndtime()));
			}
		}
		dc.addOrder(Order.desc("createtime"));
		return dc;
	}
	@Override
	public String findifmore(Long ordersuuid, Long goodsuuid, Long num) {
		Long returncount = 0L;//退货商品算上这次的总数量
		Long sailcount = 0L;//退货商品总共卖了的数量
		/*String hql = "select nvl(sum(rdd.num),0)+? as total from returnorders re,returnorderdetail rdd "
						+"where re.ordersuuid = ? and re.uuid= rdd.ordersuuid and rdd.goodsuuid=?";*/
		String hql = "select nvl(sum(rdd.num),0) from Returnorders re,Returnorderdetail rdd "
				+"where re.ordersuuid = ? and re= rdd.returnorders and rdd.goodsuuid=?";
		List<Long> findlist = (List<Long>) getHibernateTemplate().find(hql,ordersuuid,goodsuuid);
		if(findlist==null||findlist.get(0)==null){
			returncount = num;
		}else{
			returncount = findlist.get(0)+num;
		}
		System.out.println("退货数量为"+returncount);
		String hql2 = "select sum(odt.num) from Orders od ,Orderdetail odt where od=odt.orders and od.uuid=? and odt.goodsuuid = ?";
		List<Long> findlist2 = (List<Long>) getHibernateTemplate().find(hql2,ordersuuid,goodsuuid);
		if(findlist2.get(0)==null){
			return "商品未买过，不能退货";//商品未买过，不能退货
		}
		
		sailcount = findlist2.get(0);
		System.out.println("购买数量为："+sailcount);
		if(sailcount < returncount){
			return "此次能退货的数量为"+(sailcount-findlist.get(0));//售出的商品数量小于退货数量，不能退货
		}
		return "可以退货";
	}

}
