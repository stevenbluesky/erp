package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Orderdetail;
/**
 * 订单明细业务逻辑层接口
 * @author Administrator
 *
 */
public interface IOrderdetailBiz extends IBaseBiz<Orderdetail>{
	void doInStore(Long uuid,Long storeUuid,Long empUuid);
	void doOutStore(Long uuid,Long storeUuid,Long empUuid);
}

