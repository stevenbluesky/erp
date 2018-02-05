package cn.itcast.erp.action;
import com.opensymphony.xwork2.ActionContext;

import cn.itcast.erp.biz.IStoreBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Store;

/**
 * 仓库Action 
 * @author Administrator
 *
 */
public class StoreAction extends BaseAction<Store> {

	private IStoreBiz storeBiz;

	public void setStoreBiz(IStoreBiz storeBiz) {
		this.storeBiz = storeBiz;
		super.setBaseBiz(this.storeBiz);
	}
	public void myList(){
		if(null==getT1()){
			setT1(new Store());
		}
		Emp loginUser = getLoginUser();
		getT1().setEmpuuid(loginUser.getUuid());
		super.list();
	}
}
