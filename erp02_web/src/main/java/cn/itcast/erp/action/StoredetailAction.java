package cn.itcast.erp.action;
import java.util.List;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;

/**
 * 仓库库存Action 
 * @author Administrator
 *
 */
public class StoredetailAction extends BaseAction<Storedetail> {

	private IStoredetailBiz storedetailBiz;

	public void setStoredetailBiz(IStoredetailBiz storedetailBiz) {
		this.storedetailBiz = storedetailBiz;
		super.setBaseBiz(this.storedetailBiz);
	}
	public void storealertList(){
		List<Storealert> storealertList = storedetailBiz.getStorealert();
		write(JSON.toJSONString(storealertList));
	}
	public void sendStorealertMail(){
		try {
			storedetailBiz.sendStoreAlertMail();
			ajaxReturn(true,"发送预警邮件成功！");
		} catch (Exception e) {
			ajaxReturn(false,"邮件发送失败~");
			e.printStackTrace();
		}
	}
}
