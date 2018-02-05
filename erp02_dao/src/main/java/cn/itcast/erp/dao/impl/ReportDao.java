package cn.itcast.erp.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import cn.itcast.erp.dao.IReportDao;

public class ReportDao extends HibernateDaoSupport implements IReportDao {

	@Override
	public List<?> orderReport(Date startDate, Date endDate) {
		String hql = "select new Map( gt.name as name,sum(od.money) as y) from "
					+"Orders o,Orderdetail od,Goods g ,Goodstype gt "
					+"where gt=g.goodstype and g.uuid=od.goodsuuid and o=od.orders "
					+"and o.type='2' ";
		List<Date>params = new ArrayList<>();
		if(startDate!=null){
			hql += "and o.createtime>=? ";
			params.add(startDate);
		}
		if(endDate!=null){
			hql += "and o.createtime<=? ";
			params.add(endDate);
		}
		hql += "group by gt.name ";
		return getHibernateTemplate().find(hql, params.toArray());
	}

	@Override
	public List<?> trendReport(int year, int month) {
		String hql ="select new Map(month(o.createtime) || '月' as name,sum(od.money) as y) "
				+ "from Orders o, Orderdetail od "
				+ "where year(o.createtime)=? and month(o.createtime)=? and o.type='2' and o=od.orders "
				+ "group by  month(o.createtime)";
		System.out.println(getHibernateTemplate().find(hql, year,month));
		return getHibernateTemplate().find(hql, year,month);
	}

}
