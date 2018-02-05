package cn.itcast.erp.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

public class ErpAuthorizationFilter extends AuthorizationFilter {

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		//获取主题
		Subject subject = getSubject(request, response);
		//得到配置文件中的权限列表
		String [] perms = (String[])mappedValue;
		//如果为空或长度为0，则放行
		if(null==perms||perms.length==0){
			return true;
		}
		//权限检查
		for(String p : perms){
			//只要有一个符合就放行
			if(subject.isPermitted(p)){
				return true;
			}
		}
		return false;
	}

}
