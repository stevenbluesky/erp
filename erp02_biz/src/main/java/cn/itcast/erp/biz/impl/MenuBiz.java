package cn.itcast.erp.biz.impl;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.entity.Menu;
import redis.clients.jedis.Jedis;
/**
 * 菜单业务逻辑类
 * @author Administrator
 *
 */
public class MenuBiz extends BaseBiz<Menu> implements IMenuBiz {

	private IMenuDao menuDao;
	private Jedis jedis;
	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}
	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
		super.setBaseDao(this.menuDao);
	}

	@Override
	public List<Menu> readMenusByEmpuuid(Long uuid) {
		String menuListJson = jedis.get("stevenMenuList_"+uuid);
		List<Menu> menuList = null;
		if(menuListJson==null){
			//没有缓存过
			menuList = menuDao.readMenusByEmpuuid(uuid);
			menuListJson = JSON.toJSONString(menuList);
			jedis.set("stevenMenuList_"+uuid, menuListJson);
			System.out.println("从数据库中读取");
		}else{
			menuList = JSON.parseArray(menuListJson,Menu.class);
			System.out.println("从缓存中读取");
		}
		return menuList;
				
	}
	@Override
	public Menu getMenuByEmpuuid(Long empuuid) {
		//获取员工的菜单信息,进入持久化
		List<Menu> empMenus = readMenusByEmpuuid(empuuid);
		
		// 要有层级结构菜单，进入持久化
		Menu root = menuDao.get("0");
		
		//构建返回的菜单，复制后的菜单
		Menu _root = cloneMenu(root);
		// 循环模板菜单
		// 一级菜单
		for(Menu l1menu : root.getMenus()){
			//复制一级菜单
			Menu _l1 = cloneMenu(l1menu);
			//循环二级菜单
			for(Menu l2menu : l1menu.getMenus()){
				//用户下有这个菜单
				if(empMenus.contains(l2menu)){
					//有这个菜单就要复制
					Menu _l2 = cloneMenu(l2menu);
					//加入到复制的一级菜单下
					_l1.getMenus().add(_l2);
				}
			}
			//复制后有二级菜单，就要把一级菜单加进来
			if(_l1.getMenus().size() > 0){
				_root.getMenus().add(_l1);
			}
		}
		return _root;
	}
	
	/**
	 * 菜单复制
	 * @param src
	 * @return
	 */
	private Menu cloneMenu(Menu src){
		Menu target = new Menu();
		target.setIcon(src.getIcon());
		target.setMenuid(src.getMenuid());
		target.setMenuname(src.getMenuname());
		target.setUrl(src.getUrl());
		target.setMenus(new ArrayList<Menu>());
		return target;
	}
}
