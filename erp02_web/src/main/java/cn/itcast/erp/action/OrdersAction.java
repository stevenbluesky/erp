package cn.itcast.erp.action;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionContext;
import com.redsun.bos.ws.Waybilldetail;
import com.redsun.bos.ws.impl.IWaybillWs;

import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;

/**
 * 订单Action 
 * @author Administrator
 *
 */
public class OrdersAction extends BaseAction<Orders> {
	private String jsonString;
	private IWaybillWs waybillWs;
	public String getJsonString() {
		return jsonString;
	}
	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}
	private Long waybillsn;
	public void setWaybillsn(Long waybillsn) {
		this.waybillsn = waybillsn;
	}
	public Long getWaybillsn() {
		return waybillsn;
	}
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	private IOrdersBiz ordersBiz;

	public void setOrdersBiz(IOrdersBiz ordersBiz) {
		this.ordersBiz = ordersBiz;
		super.setBaseBiz(this.ordersBiz);
	}
	public void waybilldetailList(){
		System.out.println("waybillsn是："+waybillsn);
		List<Waybilldetail> waybilldetailList = waybillWs.waybilldetailList(waybillsn);
		write(JSON.toJSONString(waybilldetailList));
	}
	@Override
	public void add(){
		Emp loginUser = getLoginUser();
		if(loginUser==null){
			ajaxReturn(false, "请先登录~");
			return;
		}
		try {
			Orders orders = getT();
			System.err.println(orders);
			orders.setCreater(loginUser.getUuid());
			List<Orderdetail> orderDetailList = JSON.parseArray(jsonString, Orderdetail.class);
			orders.setOrderDetails(orderDetailList);
			ordersBiz.add(orders);
			System.err.println(orders);
			ajaxReturn(true, "添加订单成功~");
		} catch (Exception e) {
			ajaxReturn(false, "添加订单失败~");
			e.printStackTrace();
		}
	}
	public void doCheck(){
		Emp loginUser = getLoginUser();
		if(loginUser==null){
			ajaxReturn(false, "请先登录~");
			return;
		}
		try {
			ordersBiz.doCheck(getId(),loginUser.getUuid());
			ajaxReturn(true, "审核成功");
		} catch (Exception e) {
			ajaxReturn(false, "审核失败");
			e.printStackTrace();
		}
	}
	public void doStart(){
		Emp loginUser = getLoginUser();
		if(loginUser==null){
			ajaxReturn(false, "请先登录~");
			return;
		}
		try {
			ordersBiz.doStart(getId(),loginUser.getUuid());
			ajaxReturn(true, "确认成功");
		} catch (Exception e) {
			ajaxReturn(false, "确认失败");
			e.printStackTrace();
		}
	}
	public void myListByPage(){
		if(null == getT1()){
			setT1(new Orders());
		}
		Emp loginUser = getLoginUser();
		getT1().setCreater(loginUser.getUuid());
		super.listByPage();
	}
	public void exportById(){
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Content-Disposition","attachment;filename=orders_"+getId()+".xls");
		try {
			ordersBiz.exportById(response.getOutputStream(), getId());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
