package cn.itcast.erp.biz.impl;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.crypto.hash.Md5Hash;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IRoleDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
import redis.clients.jedis.Jedis;
/**
 * 员工业务逻辑类
 * @author Administrator
 *
 */
public class EmpBiz extends BaseBiz<Emp> implements IEmpBiz {
	private IRoleDao roleDao;
	private IEmpDao empDao;
	private Jedis jedis;
	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}
	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
		super.setBaseDao(this.empDao);
	}
	@Override
	public Emp findByUsernameAndPwd(String username, String pwd) {
		Md5Hash md5 = new Md5Hash(pwd,username,2);
		pwd = md5.toString();
		return empDao.findByUsernameAndPwd(username, pwd);
	}
	@Override
	public void add(Emp emp){
		String pwd = emp.getUsername();
		Md5Hash md5 = new Md5Hash(pwd,emp.getName(),2);
		pwd = md5.toString();
		emp.setPwd(pwd);	
	}
	@Override
	public boolean updatePwd(Long uuid,String pwd, String newPwd) {
		Emp emp = empDao.get(uuid);
		String name = emp.getUsername();
		Emp findByUsernameAndPwd = findByUsernameAndPwd(name,pwd);
		if(findByUsernameAndPwd == null){
			System.out.println("旧密码错误");
			return false;
		}
		Md5Hash md5 = new Md5Hash(newPwd,name,2);
		pwd = md5.toString();
		empDao.updatePwd(uuid,pwd);
		return true;
	}
	@Override
	public boolean resetPwd(Long uuid, String newPwd) {
		Emp emp = empDao.get(uuid);
		String name = emp.getUsername();
		newPwd = name;
		Md5Hash md5 = new Md5Hash(newPwd,name,2);
		String pwd = md5.toString();
		empDao.updatePwd(uuid,pwd);
		return true;
	}
	@Override
	public List<Tree> readEmpRoles(Long uuid) {
		//获取员工信息, 进入持久
		Emp emp = empDao.get(uuid);
		//员工下的角色
		List<Role> empRoles = emp.getRoles();
		
		List<Tree> result = new ArrayList<Tree>();
		// 获取所有的角色列表,进入持久化
		List<Role> roleList = roleDao.getList(null, null, null);
		for (Role role : roleList) {
			//把角色转树的节点
			Tree t = createTree(role);
			//员工是否拥有这个角色, 由于对象进入持久，同一个类同一个ID，只有一个对象
			if(empRoles.contains(role)){
				//员工下拥有这个角色，就让它选中
				t.setChecked(true);
			}
			result.add(t);
		}
		return result;
	}
	
	private Tree createTree(Role role){
		Tree t = new Tree();
		t.setId(role.getUuid() + "");
		t.setText(role.getName());
		return t;
	}

	@Override
	public void updateEmpRoles(Long uuid, String ids) {
		//获取员工信息, 进入持久
		Emp emp = empDao.get(uuid);
		//删除旧的关系 
		emp.setRoles(new ArrayList<Role>());
		
		//建立新的关系
		String[] roleIds = ids.split(",");
		for (String roleuuid : roleIds) {
			//此时这个角色进入持久化
			Role role = roleDao.get(Long.valueOf(roleuuid));
			//建立员工与角色的关系
			emp.getRoles().add(role);
		}
		//用户角色更新时，清除缓存
		jedis.del("stevenMenuList_"+uuid);
		
	}
	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
	}
}
