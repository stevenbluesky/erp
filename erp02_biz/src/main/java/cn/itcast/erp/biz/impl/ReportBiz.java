package cn.itcast.erp.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.itcast.erp.biz.IReportBiz;
import cn.itcast.erp.dao.IReportDao;

public class ReportBiz implements IReportBiz {
	private IReportDao reportDao;
	public void setReportDao(IReportDao reportDao) {
		this.reportDao = reportDao;
	}
	@Override
	public List<?> orderReport(Date startDate, Date endDate) {
		return reportDao.orderReport(startDate, endDate);
	}

	@Override
	public List<?> trendReport(int year) {
		List <Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		for(int i =1 ;i <=12;i++){
			List<Map<String,Object>> list = (List<Map<String, Object>>) reportDao.trendReport(year, i);
			if(list!=null&&list.size()>0){
				result.add(list.get(0));
			}else{
				HashMap<String, Object> data = new HashMap<String,Object>();
				data.put("name", i+"月");
				data.put("y", 0);
				result.add(data);
			}
		}
		
		return result;
	}

}
