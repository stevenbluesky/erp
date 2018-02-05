package cn.itcast.erp.biz;
import java.util.List;

import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Tree;
/**
 * 员工业务逻辑层接口
 * @author Administrator
 *
 */
public interface IEmpBiz extends IBaseBiz<Emp>{
	Emp findByUsernameAndPwd(String username,String pwd);
	boolean updatePwd(Long uuid,String pwd,String newPwd);
	boolean resetPwd(Long uuid,String newPwd);
	/**
	 * 用户角色列表
	 * @param uuid
	 * @return
	 */
	List<Tree> readEmpRoles(Long uuid);
	
	/**
	 * 更新用户的角色
	 * @param uuid
	 * @param ids 角色编号，多个以逗号分割
	 */
	void updateEmpRoles(Long uuid,String ids);
}

