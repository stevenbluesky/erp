package cn.itcast.erp.biz.impl;
import java.util.Date;
import java.util.List;

import cn.itcast.erp.biz.ErpException;
import cn.itcast.erp.biz.IReturnordersBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IOrdersDao;
import cn.itcast.erp.dao.IReturnordersDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Goods;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.entity.Returnorders;
/**
 * 退货订单业务逻辑类
 * @author Administrator
 *
 */
public class ReturnordersBiz extends BaseBiz<Returnorders> implements IReturnordersBiz {
	private IEmpDao empDao;
	private ISupplierDao supplierDao;
	private IReturnordersDao returnordersDao;
	private IOrdersDao ordersDao;
	public void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
	}
	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}
	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}
	public void setReturnordersDao(IReturnordersDao returnordersDao) {
		this.returnordersDao = returnordersDao;
		super.setBaseDao(this.returnordersDao);
	}
	public void add(Returnorders neworder){
		//此处根据订单编号获取原订单相关信息
		Long ordersuuid = neworder.getOrdersuuid();
		Orders oldorder = ordersDao.get(ordersuuid);
		if(oldorder==null){
			throw new ErpException("您输入的订单编号不存在！");
		}
		//设置退货登记时间
		neworder.setCreatetime(new Date());
		//设置客户/供应商
		neworder.setSupplieruuid(oldorder.getSupplieruuid());
		//设置退货登记状态为0，未审核
		neworder.setState(Returnorders.STATE_UNCHECK);
		double total = 0;
		int i = 0;
		for (Returnorderdetail detail:neworder.getReturnorderdetails()) {
			//需要遍历查看退货商品总量是否大于下单数量
			 //List<Goods> goodslist = returnordersDao.findDetailCount(orders2,detail.getGoodsuuid());
			if(detail.getNum()==0){
				throw new ErpException(detail.getGoodsname()+"商品退货数量为0，请修改！");
			}
			 String message = returnordersDao.findifmore(ordersuuid,detail.getGoodsuuid(),detail.getNum());
			 if(!message.equals("可以退货")){
				 throw new ErpException(detail.getGoodsname()+message);
			 }
			total += detail.getMoney();
			if(Orders.TYPE_OUT.equals(neworder.getType())){
				//销售订单
				detail.setState(Returnorderdetail.STATE_ONTIN);
			}
			detail.setReturnorders(neworder);
			i++;
		}
		if(i==0){
			throw new ErpException("退货登记中无退货信息，请填写或取消！");
		}
		neworder.setTotalmoney(total);
		super.add(neworder);
	}
	@Override
	public List<Returnorders> getListByPage(Returnorders t1,Returnorders t2,Object param,int firstResult, int maxResults) {
		List<Returnorders> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		for(Returnorders orders : list){
			orders.setCreaterName(getEmpName(orders.getCreater()));
			orders.setCheckerName(getEmpName(orders.getChecker()));
			orders.setEnderName(getEmpName(orders.getEnder()));
			orders.setSupplierName(getSupplierName(orders.getSupplieruuid()));
		}
		
		return list;
	}
	/**
	 * 通过员工编号获取员工名称
	 * @param empuuid
	 * @return
	 */
	private String getEmpName(Long empuuid){
		if(null == empuuid){
			return null;
		}
		return empDao.get(empuuid).getName();
	}
	private String getSupplierName(Long supplieruuid){
		if(supplieruuid == null){
			return null;
		}
		return supplierDao.get(supplieruuid).getName();
	}
	@Override
	public void doCheck(long id, Long uuid) {
		Returnorders returnorders = returnordersDao.get(id);
		if(!returnorders.getState().equals(Returnorders.STATE_UNCHECK)){
			throw new ErpException("申请已审核，请勿重复审核！");
		}
		returnorders.setChecker(uuid);
		returnorders.setChecktime(new Date());
		returnorders.setState(Returnorders.STATE_CHECKED);
	}
	@Override
	public void doBack(long id, Long uuid) {
		Returnorders returnorders = returnordersDao.get(id);
		if(returnorders.getState().equals(Returnorders.STATE_BACK)){
			throw new ErpException("申请已驳回，请勿重复操作！");
		}
		returnorders.setChecker(uuid);
		returnorders.setChecktime(new Date());
		returnorders.setState(Returnorders.STATE_BACK);
		for (Returnorderdetail detail :returnorders.getReturnorderdetails()) {
			detail.setNum(0L);
		}
	}
}
