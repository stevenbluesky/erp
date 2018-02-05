package cn.itcast.erp.realm;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;

public class ErpRealm extends AuthorizingRealm {
	private IEmpBiz empBiz;
	private IMenuBiz menuBiz;
	public void setMenuBiz(IMenuBiz menuBiz) {
		this.menuBiz = menuBiz;
	}
	public void setEmpBiz(IEmpBiz empBiz) {
		this.empBiz = empBiz;
	}
	//授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		//得到当前登录的用户
		Emp emp = (Emp)principals.getPrimaryPrincipal();
		//获取登录用户所对应的菜单权限集合
		List<Menu> menus = menuBiz.readMenusByEmpuuid(emp.getUuid());
		SimpleAuthorizationInfo sai = new SimpleAuthorizationInfo();
		for (Menu menu : menus) {
			sai.addStringPermission(menu.getMenuname());
		}
		return sai;
	}
	//认证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		//转成实现类，获取用户名和密码
		UsernamePasswordToken upt = (UsernamePasswordToken)token;
		String pwd = new String(upt.getPassword());
		
		//根据用户名和密码查找用户
		Emp emp = empBiz.findByUsernameAndPwd(upt.getUsername(), pwd);
		if(null!=emp){
			return new SimpleAuthenticationInfo(emp,pwd,getName());
		}
		return null;
	}

}
