package cn.itcast.erp.biz.impl;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.redsun.bos.ws.impl.IWaybillWs;

import cn.itcast.erp.biz.ErpException;
import cn.itcast.erp.biz.IOrderdetailBiz;
import cn.itcast.erp.dao.IOrderdetailDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.entity.Storeoper;
import cn.itcast.erp.entity.Supplier;
/**
 * 订单明细业务逻辑类
 * @author Administrator
 *
 */
public class OrderdetailBiz extends BaseBiz<Orderdetail> implements IOrderdetailBiz {

	private IOrderdetailDao orderdetailDao;
	private IStoredetailDao storedetailDao;
	private IStoreoperDao storeoperDao;
	/*物流系统调用*/
	private IWaybillWs waybillWs;
	private ISupplierDao supplierDao;
	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}
	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}
	public void setOrderdetailDao(IOrderdetailDao orderdetailDao) {
		this.orderdetailDao = orderdetailDao;
		super.setBaseDao(this.orderdetailDao);
	}
	
	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}

	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
	}

	@Override
	public void doInStore(Long uuid, Long storeUuid, Long empUuid) {
		Orderdetail orderdetail = this.orderdetailDao.get(uuid);
		//先检查是否入库，此处懒得检查
		orderdetail.setState(Orderdetail.STATE_IN);
		orderdetail.setEnder(empUuid);
		orderdetail.setStoreuuid(storeUuid);
		orderdetail.setEndtime(Calendar.getInstance().getTime());
		
		Storedetail storeDetail = new Storedetail();
		storeDetail.setGoodsuuid(orderdetail.getGoodsuuid());
		storeDetail.setStoreuuid(storeUuid);
		List<Storedetail> list = storedetailDao.getList(storeDetail, null, null);
		if(list.size() > 0){
			//	2.4 存在：数量累加, 此时storeDetail是持久化状态
			storeDetail = list.get(0);
			storeDetail.setNum(storeDetail.getNum() + orderdetail.getNum());
		}else{
		//	2.5 不存在：新增记录
			storeDetail.setNum(orderdetail.getNum());//第一次入库的数量, 解bug
			storedetailDao.add(storeDetail);
		}
		
		//3. 操作日志：
		Storeoper log = new Storeoper();
		//	3.1 操作员工编号： -> 登录用户
		log.setEmpuuid(empUuid);
		//	3.2 操作日期 -> 系统时间, 时间要一致，这次的业务操作只有一个时间
		log.setOpertime(orderdetail.getEndtime());
		//	3.3 仓库编号 -> 前端下拉列表
		log.setStoreuuid(storeUuid);
		//	3.4 商品编号 -> 明细里有
		log.setGoodsuuid(orderdetail.getGoodsuuid());
		//	3.5 数量     -> 明细里有
		log.setNum(orderdetail.getNum());
		//	3.6 操作类型 -> 1 入库
		log.setType(Storeoper.TYPE_IN);
		//插入 日志
		storeoperDao.add(log);
		
		//4. 订单表
		//	4.1 判断(所有的明细是否都入库了, 查询状态为0的明细的个数)
		Orderdetail queryParam = new Orderdetail();
		queryParam.setState(Orderdetail.STATE_NOT_IN);
		queryParam.setOrders(orderdetail.getOrders());
		//状态为0的明细的个数
		long count = orderdetailDao.getCount(queryParam,null,null);
		if(count == 0){
		//	4.2 都入库了
		//		更新订单 , orders是持久化状态
			Orders orders = orderdetail.getOrders();
		//			入库时间： -> 系统时间
			orders.setEndtime(orderdetail.getEndtime());
		//			库管员     -> 登录用户
			orders.setEnder(empUuid);
		//			订单状态   -> 3 已入库
			orders.setState(Orders.STATE_END);
		}
	}
	/**
	 * 销售出库操作
	 */
	@Override
	public void doOutStore(Long uuid, Long storeUuid, Long empUuid) {
		Orderdetail orderdetail = orderdetailDao.get(uuid);
		System.out.println("---------"+orderdetail.getState()+"----------");
		if(!"0".equals(orderdetail.getState())){
			System.out.println("已经出库了~~~");
		}
		//更新订单明细
		orderdetail.setEnder(empUuid);
		orderdetail.setEndtime(new Date());
		orderdetail.setState("1");
		orderdetail.setStoreuuid(storeUuid);
		//查询库存
		Storedetail storedetail = new Storedetail();
		storedetail.setGoodsuuid(orderdetail.getGoodsuuid());
		storedetail.setStoreuuid(storeUuid);
		List<Storedetail> list = storedetailDao.getList(storedetail, null, null);
		//商品库存数量
		long num = -1;
		/*if(null != list && list.size()>0){
			storedetail = list.get(0);
			num = storedetail.getNum().longValue() - orderdetail.getNum().longValue();
		}
		//库存充足
		if(num>=0){
			storedetail.setNum(num);
		}else{
			System.out.println("库存不足！");
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		if(list.size()>0){
			storedetail=list.get(0);
			num = storedetail.getNum()-orderdetail.getNum();
			if(num<0){
				throw new ErpException("库存不足！");
			}else{
				storedetail.setNum(num);
			}
		}else{
			throw new ErpException("库存不足！");
		}
		//添加库存变更操作记录
		Storeoper log = new Storeoper();
		log.setEmpuuid(empUuid);
		 log.setGoodsuuid(orderdetail.getGoodsuuid());
		    log.setNum(orderdetail.getNum());
		    log.setOpertime(orderdetail.getEndtime());
		    log.setStoreuuid(storeUuid);
		    log.setType("2");
		    storeoperDao.add(log);

		    //检查是否订单下的所有明细都已经出库
		    Orderdetail queryParam = new Orderdetail();
		    Orders orders = orderdetail.getOrders();
		    queryParam.setOrders(orders);
		    queryParam.setState("0");
		    Long cnt = orderdetailDao.getCount(queryParam, null, null);
		    if(cnt == 0){
		    	//所有明细都已经出库，则更新订单状态为已出库
		        orders.setState("1");
		        orders.setEnder(empUuid);
		        orders.setEndtime(orderdetail.getEndtime());
		       // 销售出库自动提交物流清单
		      //在线预约下单, erp对应bos系统的用户编号为1,
				//获取客户信息
				Supplier supplier = supplierDao.get(orders.getSupplieruuid());
				//调用bos系统在线预约下单,拿到运单号
				Long waybillsn = waybillWs.add(1l, supplier.getAddress(), supplier.getContact(), supplier.getTele(), "--");
				//更新订单的运单号
				orders.setWaybillsn(waybillsn);
		    }
	}
	
}
