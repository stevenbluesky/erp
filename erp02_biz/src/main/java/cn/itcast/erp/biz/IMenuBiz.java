package cn.itcast.erp.biz;
import java.util.List;

import cn.itcast.erp.entity.Menu;
/**
 * 菜单业务逻辑层接口
 * @author Administrator
 *
 */
public interface IMenuBiz extends IBaseBiz<Menu>{
	/**
	 * 通过员工编号获取菜单列表
	 * @param uuid
	 * @return
	 */
	List<Menu> readMenusByEmpuuid(Long uuid);
	
	/**
	 * 通过员工编号获取菜单列表,页面展示的菜单
	 * @param uuid
	 * @return
	 */
	Menu getMenuByEmpuuid(Long uuid);
}

