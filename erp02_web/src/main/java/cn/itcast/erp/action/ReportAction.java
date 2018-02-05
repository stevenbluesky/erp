package cn.itcast.erp.action;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IReportBiz;

public class ReportAction {
	private IReportBiz reportBiz;
	private Date startDate;
	private Date endDate;
	private int year;
	public void setReportBiz(IReportBiz reportBiz) {
		this.reportBiz = reportBiz;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public void setYear(int year) {
		this.year = year;
	}
	/**
	 * 销售统计
	 */
	public void orderReport(){
		List<?> orderReport = reportBiz.orderReport(startDate, endDate);
		String jsonString = JSON.toJSONString(orderReport);
		write(jsonString);
	}
	/**
	 * 趋势走向
	 */
	public void trendReport(){
		List<?> trendReport = reportBiz.trendReport(year);
		write(JSON.toJSONString(trendReport));
		
	}
	/**
	 * 输出字符串到前端
	 * @param jsonString
	 */
	public void write(String jsonString){
		try {
			//响应对象
			HttpServletResponse response = ServletActionContext.getResponse();
			//设置编码
			response.setContentType("text/html;charset=utf-8"); 
			//输出给页面
			response.getWriter().write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
