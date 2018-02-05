package cn.itcast.erp.action;
import com.opensymphony.xwork2.ActionContext;

import cn.itcast.erp.biz.IOrderdetailBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;

/**
 * 订单明细Action 
 * @author Administrator
 *
 */
public class OrderdetailAction extends BaseAction<Orderdetail> {

	private IOrderdetailBiz orderdetailBiz;
	private Long storeuuid;
	
	public Long getStoreuuid() {
		return storeuuid;
	}

	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}

	public void setOrderdetailBiz(IOrderdetailBiz orderdetailBiz) {
		this.orderdetailBiz = orderdetailBiz;
		super.setBaseBiz(this.orderdetailBiz);
	}
	public void doInStore(){
		Emp loginUser = getLoginUser();
		if(null==loginUser){
			ajaxReturn(false, "请先登录~");
			return ;
		}
		try {
			orderdetailBiz.doInStore(getId(), storeuuid,loginUser.getUuid());
			ajaxReturn(true, "入库成功~");
		} catch (Exception e) {
			ajaxReturn(false, "入库失败~");
			e.printStackTrace();
		}
		
	}
	public void doOutStore(){
		Emp loginUser = getLoginUser();
		try {
			orderdetailBiz.doOutStore(getId(), storeuuid,loginUser.getUuid());
			ajaxReturn(true, "出库成功~");
		} catch (Exception e) {
			ajaxReturn(false, "出库失败~");
			e.printStackTrace();
		}
	}
}
