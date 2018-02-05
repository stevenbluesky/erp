package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Returnorders;
/**
 * 退货订单业务逻辑层接口
 * @author Administrator
 *
 */
public interface IReturnordersBiz extends IBaseBiz<Returnorders>{

	void doCheck(long id, Long uuid);
	/**
	 * 
	 * @param id	订单编号
	 * @param uuid	审核人id
	 */
	void doBack(long id, Long uuid);

}

