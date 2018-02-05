package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Returnorderdetail;
/**
 * 退货订单明细业务逻辑层接口
 * @author Administrator
 *
 */
public interface IReturnorderdetailBiz extends IBaseBiz<Returnorderdetail>{
	/**
	 * 
	 * @param id	订单明细id
	 * @param storeuuid	仓库id
	 * @param uuid	入库员id
	 */
	void doInStore(long id, long storeuuid, Long uuid);

}

