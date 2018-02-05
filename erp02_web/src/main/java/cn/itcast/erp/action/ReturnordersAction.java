package cn.itcast.erp.action;
import java.util.List;

import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.ErpException;
import cn.itcast.erp.biz.IReturnordersBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.entity.Returnorders;

/**
 * 退货订单Action 
 * @author Administrator
 *
 */
public class ReturnordersAction extends BaseAction<Returnorders> {
	private String jsonString;
	private IReturnordersBiz returnordersBiz;
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	public void setReturnordersBiz(IReturnordersBiz returnordersBiz) {
		this.returnordersBiz = returnordersBiz;
		super.setBaseBiz(this.returnordersBiz);
	}
	@Override
	public void add(){
		Emp loginUser = getLoginUser();
		if(loginUser==null){
			ajaxReturn(false, "请先登录~");
			return;
		}
		try {
			Returnorders orders = getT();
			orders.setCreater(loginUser.getUuid());
			List<Returnorderdetail> orderDetailList = JSON.parseArray(jsonString, Returnorderdetail.class);
			orders.setReturnorderdetails(orderDetailList);
			returnordersBiz.add(orders);
			ajaxReturn(true, "销售退货登记成功~");
		} catch(ErpException e){
			ajaxReturn(false, e.getMessage());
		}catch (Exception e) {
			ajaxReturn(false, "销售退货登记失败~");
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
			returnordersBiz.doCheck(getId(),loginUser.getUuid());
			ajaxReturn(true, "审核成功");
		} catch (Exception e) {
			ajaxReturn(false, "审核失败");
			e.printStackTrace();
		}
	}
	public void doBack(){
		Emp loginUser = getLoginUser();
		if(loginUser==null){
			ajaxReturn(false, "请先登录~");
			return;
		}
		try {
			returnordersBiz.doBack(getId(),loginUser.getUuid());
			ajaxReturn(true, "驳回成功");
		} catch (Exception e) {
			ajaxReturn(false, "驳回失败");
			e.printStackTrace();
		}
	}
}
