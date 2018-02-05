package cn.itcast.erp.biz.impl;
import java.util.ArrayList;
import java.util.List;

import cn.itcast.erp.biz.IRoleBiz;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.dao.IRoleDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
import redis.clients.jedis.Jedis;
/**
 * 角色业务逻辑类
 * @author Administrator
 *
 */
public class RoleBiz extends BaseBiz<Role> implements IRoleBiz {
	private IMenuDao menuDao;
	private IRoleDao roleDao;
	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
	}
	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
		super.setBaseDao(this.roleDao);
	}
	private Jedis jedis;
	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

	
	/**
	 * 把菜单转成树的节点
	 * @param menu
	 * @return
	 */
	private Tree createTree(Menu menu){
		Tree t = new Tree();
		t.setText(menu.getMenuname());//菜单名称
		t.setId(menu.getMenuid());//菜单编号
		t.setChildren(new ArrayList<Tree>());//子节点
		return t;
	}

	@Override
	public void updateRoleMenus(Long uuid, String ids) {
		// 获取角色信息，进入持久化状态
		Role role = roleDao.get(uuid);
		//清除角色下的菜单权限，删除原有的配置(关系)
		// delete from ROLE_MENU where roleuuid=?
		role.setMenus(new ArrayList<Menu>());
		
		//建立新的关系
		String[] menuIds = ids.split(",");
		for(String menuid : menuIds){
			// menu进入持久化
			Menu menu = menuDao.get(menuid);
			// 建立角色与菜单的关系
			// insert into ROLE_MENU
			role.getMenus().add(menu);
		}
		//** 清除缓存菜单 */
		//拥有该角色的所有用户
		List<Emp> emps = role.getEmpList();
		String key = "";
		for (Emp emp : emps) {
			//构建redis中的key值
			key = "stevenMenuList_" + emp.getUuid();
			//从 redis缓存中删除
			if(jedis!=null){
				jedis.del(key);
			}
		}
	}
	@Override
	public List<Tree> readRoleMenus(Long uuid) {
		//获取角色信息, 进入持久化
		Role role = roleDao.get(uuid);
		//这个角色下的菜单权限
		List<Menu> roleMenus = role.getMenus();
		
		//返回的值
		List<Tree> result = new ArrayList<Tree>();
		// 从根菜单来获取
		Menu root = menuDao.get("0");
		//循环一级菜单
		for(Menu l1menu : root.getMenus()){
			//把一级菜单转成tree的节点
			Tree t1 = createTree(l1menu);
			//一级菜单下的二级菜单
			for(Menu l2menu : l1menu.getMenus()){
				//把二级菜单转成tree的节点
				Tree t2 = createTree(l2menu);
				
				//如果这个角色下菜单权限有这个菜单(所有的)
				if(roleMenus.contains(l2menu)){
					//让它选中
					t2.setChecked(true);
				}
				//一级菜单下加入子节点
				t1.getChildren().add(t2);
			}
			//加上一级节点
			result.add(t1);
		}
		return result;
	}
	
}
