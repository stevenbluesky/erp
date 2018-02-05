package cn.itcast.erp.action;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionContext;

import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;

/**
 * 菜单Action 
 * @author Administrator
 *
 */
public class MenuAction extends BaseAction<Menu> {

	private IMenuBiz menuBiz;

	public void setMenuBiz(IMenuBiz menuBiz) {
		this.menuBiz = menuBiz;
		super.setBaseBiz(this.menuBiz);
	}
	public void getMenuTree(){
		Emp emp = getLoginUser();
		if(null!=emp){
			Menu root = menuBiz.getMenuByEmpuuid(emp.getUuid());
			write(JSON.toJSONString(root));
		}
		
	}
}
