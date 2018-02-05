package cn.itcast.erp.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionContext;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.entity.Emp;

public class LoginAction {
	private String username;
	private String pwd;
	//private IEmpBiz empBiz;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	/*public void setEmpBiz(IEmpBiz empBiz) {
		this.empBiz = empBiz;
	}*/
	public void checkUser(){
		try {
			/*Emp emp = empBiz.findByUsernameAndPwd(username, pwd);
			if(emp==null){
				//数据库中未找到用户信息
				ajaxReturn(false, "用户名或密码不正确");
				return ;
			}
			ActionContext.getContext().getSession().put("loginUser", emp);*/
			//创建令牌
			UsernamePasswordToken upt = new UsernamePasswordToken(username,pwd);
			//获得主题subject
			Subject subject = SecurityUtils.getSubject();
			//执行login方法
			subject.login(upt);
			ajaxReturn(true,"");
		} catch (Exception e) {
			e.printStackTrace();
			ajaxReturn(false, "登录失败");
		}
	}
	public void showName(){
		//Emp loginUser = (Emp) ActionContext.getContext().getSession().get("loginUser");
		Emp loginUser = (Emp)SecurityUtils.getSubject().getPrincipal();
		if(loginUser!=null){
			ajaxReturn(true,loginUser.getName());
		}else{
			ajaxReturn(false,"");
		}
	}
	public void loginOut(){
		//ActionContext.getContext().getSession().remove("loginUser");
		SecurityUtils.getSubject().logout();
	}
	/**
	 * 返回前端操作结果
	 * @param success
	 * @param message
	 */
	public void ajaxReturn(boolean success, String message){
		//返回前端的JSON数据
		Map<String, Object> rtn = new HashMap<String, Object>();
		rtn.put("success",success);
		rtn.put("message",message);
		write(JSON.toJSONString(rtn));
	}
	
	/**
	 * 输出字符串到前端
	 * @param jsonString
	 */
	public void write(String jsonString){
		try {
			//响应对象
			HttpServletResponse response = ServletActionContext.getResponse();
			//设置编码
			response.setContentType("text/html;charset=utf-8"); 
			//输出给页面
			response.getWriter().write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
