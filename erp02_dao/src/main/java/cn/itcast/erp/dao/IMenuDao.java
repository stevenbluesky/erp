package cn.itcast.erp.dao;

import java.util.List;

import cn.itcast.erp.entity.Menu;
/**
 * 菜单数据访问接口
 * @author Administrator
 *
 */
public interface IMenuDao extends IBaseDao<Menu>{
	/**
	 * 通过员工编号获取菜单列表
	 * @param uuid
	 * @return
	 */
	List<Menu> readMenusByEmpuuid(Long uuid);
}
