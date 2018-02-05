package cn.itcast.erp.action;
import java.util.List;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IRoleBiz;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;

/**
 * 角色Action 
 * @author Administrator
 *
 */
public class RoleAction extends BaseAction<Role> {

	private IRoleBiz roleBiz;

	public void setRoleBiz(IRoleBiz roleBiz) {
		this.roleBiz = roleBiz;
		super.setBaseBiz(this.roleBiz);
	}
	/**
	 * 读取角色的菜单列表
	 */
	public void readRoleMenus(){
		List<Tree> list = roleBiz.readRoleMenus(getId());
		write(JSON.toJSONString(list));
		//TODO
	}
	
	private String ids;//前端传过来的菜单编号，多个以逗号分割
	
	public void setIds(String ids) {
		this.ids = ids;
	}

	/**
	 * 更新角色菜单权限
	 */
	public void updateRoleMenus(){
		try {
			roleBiz.updateRoleMenus(getId(), ids);
			ajaxReturn(true, "更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			ajaxReturn(false, "更新失败");
		}
	}
}
