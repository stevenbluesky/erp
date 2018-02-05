package cn.itcast.erp.biz.impl;
import java.util.Date;
import java.util.List;

import cn.itcast.erp.biz.ErpException;
import cn.itcast.erp.biz.IReturnorderdetailBiz;
import cn.itcast.erp.dao.IReturnorderdetailDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.entity.Returnorders;
import cn.itcast.erp.entity.Store;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.entity.Storeoper;
/**
 * 退货订单明细业务逻辑类
 * @author Administrator
 *
 */
public class ReturnorderdetailBiz extends BaseBiz<Returnorderdetail> implements IReturnorderdetailBiz {

	private IReturnorderdetailDao returnorderdetailDao;
	private IStoredetailDao storedetailDao;
	private IStoreoperDao storeoperDao;
	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
	}
	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}
	public void setReturnorderdetailDao(IReturnorderdetailDao returnorderdetailDao) {
		this.returnorderdetailDao = returnorderdetailDao;
		super.setBaseDao(this.returnorderdetailDao);
	}

	@Override
	public void doInStore(long id, long storeuuid, Long uuid) {
		Returnorderdetail rdl = returnorderdetailDao.get(id);
		if(rdl.getState().equals(Returnorderdetail.STATE_IN)){
			throw new ErpException("该明细已经入库了");
		}
		//往订单明细里存数据
		rdl.setEnder(uuid);
		rdl.setEndtime(new Date());
		rdl.setState(Returnorderdetail.STATE_IN);
		rdl.setStoreuuid(storeuuid);
		//往库存明细中存数据
		 Storedetail storedetail = new Storedetail();
		 storedetail.setStoreuuid(storeuuid);
		 storedetail.setGoodsuuid(rdl.getGoodsuuid());
		 List<Storedetail> list = storedetailDao.getList(storedetail, null, null);
		 if(list.size()>0){
			 //仓库中已经存在该类商品
			 storedetail = list.get(0);
			 storedetail.setNum(storedetail.getNum()+rdl.getNum());
		 }else{
			 storedetail.setNum(rdl.getNum());
			 storedetailDao.add(storedetail);
		 }
		 //往操作记录里面存数据
		 Storeoper storeoper = new Storeoper();
		 storeoper.setEmpuuid(uuid);
		 storeoper.setOpertime(rdl.getEndtime());
		 storeoper.setStoreuuid(storeuuid);
		 storeoper.setGoodsuuid(rdl.getGoodsuuid());
		 storeoper.setNum(rdl.getNum());
		 storeoper.setType(Storeoper.TYPE_IN);
		 storeoperDao.add(storeoper);
		 
		 //订单表
		 Returnorderdetail querydetail = new Returnorderdetail();
		 querydetail.setState(Returnorderdetail.STATE_ONTIN);//订单明细结束的个数
		 querydetail.setReturnorders(rdl.getReturnorders());
		 long count = returnorderdetailDao.getCount(querydetail, null, null);
		 System.out.println("明细已完成的个数"+count);
		 if(count==0){
			 Returnorders returnorders = rdl.getReturnorders();
			 returnorders.setEnder(uuid);
			 returnorders.setEndtime(rdl.getEndtime());
			 returnorders.setState(Returnorders.STATE_OVER);
		 }
	}
	
}
