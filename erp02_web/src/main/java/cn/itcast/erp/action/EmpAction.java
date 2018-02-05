package cn.itcast.erp.action;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionContext;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Tree;

/**
 * 员工Action 
 * @author Administrator
 *
 */
public class EmpAction extends BaseAction<Emp> {
	private String newPwd;
	private String oldPwd;
	private IEmpBiz empBiz;
	
	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	public String getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	public void setEmpBiz(IEmpBiz empBiz) {
		this.empBiz = empBiz;
		super.setBaseBiz(this.empBiz);
	}
	public void updatePwd(){
		//Emp emp = (Emp) ActionContext.getContext().getSession().get("loginUser");
		Emp emp = getLoginUser();
		if(emp==null){
			ajaxReturn(false, "请先登录~");
			return ;
		}
		try {
			boolean flag = empBiz.updatePwd(emp.getUuid(), oldPwd, newPwd);
			if(flag){
				
				ajaxReturn(true, "密码修改成功");
			}else{
				ajaxReturn(false, "旧密码错误");
			}
		} catch (Exception e) {
			ajaxReturn(false, "密码修改失败~");
			e.printStackTrace();
		}
	}
	public void updatePwd_reset(){
		try {
			boolean flag = empBiz.resetPwd(getId(), newPwd);
			ajaxReturn(true, "密码已重置为登录名");
		} catch (Exception e) {
			ajaxReturn(false, "密码重置失败~");
			e.printStackTrace();
		}
	}

	/**
	 * 读用户角色列表
	 */
	public void readEmpRoles(){
		List<Tree> list = empBiz.readEmpRoles(getId());
		write(JSON.toJSONString(list));
	}
	
	private String ids;//前端传过来的角色编号，多个以逗号分割
	
	public void setIds(String ids) {
		this.ids = ids;
	}

	/**
	 * 更新用户角色权限
	 */
	public void updateEmpRoles(){
		try {
			empBiz.updateEmpRoles(getId(), ids);
			ajaxReturn(true, "更新成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ajaxReturn(false, "更新失败");
		}
	}
}
