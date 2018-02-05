package cn.itcast.erp.action;
import cn.itcast.erp.biz.IReturnorderdetailBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Returnorderdetail;

/**
 * 退货订单明细Action 
 * @author Administrator
 *
 */
public class ReturnorderdetailAction extends BaseAction<Returnorderdetail> {
	private long storeuuid;
	public void setStoreuuid(long storeuuid) {
		this.storeuuid = storeuuid;
	}
	private IReturnorderdetailBiz returnorderdetailBiz;

	public void setReturnorderdetailBiz(IReturnorderdetailBiz returnorderdetailBiz) {
		this.returnorderdetailBiz = returnorderdetailBiz;
		super.setBaseBiz(this.returnorderdetailBiz);
	}
	public void doInStore(){
		Emp loginUser = getLoginUser();
		if(null==loginUser){
			ajaxReturn(false, "请先登录~");
			return ;
		}
		try {
			returnorderdetailBiz.doInStore(getId(), storeuuid,loginUser.getUuid());
			ajaxReturn(true, "入库成功~");
		} catch (Exception e) {
			ajaxReturn(false, "入库失败~");
			e.printStackTrace();
		}
	}
}
