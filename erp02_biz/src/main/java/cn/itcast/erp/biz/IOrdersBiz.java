package cn.itcast.erp.biz;
import java.io.OutputStream;

import cn.itcast.erp.entity.Orders;
/**
 * 订单业务逻辑层接口
 * @author Administrator
 *
 */
public interface IOrdersBiz extends IBaseBiz<Orders>{

	void doCheck(long id, Long uuid);
	void doStart(long id,Long uuid);
	public void exportById(OutputStream os,Long uuid)throws Exception;
}

