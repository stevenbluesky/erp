package cn.itcast.erp.entity;

import java.util.List;

/**
 * 退货订单实体类
 * @author Administrator *
 */
public class Returnorders {	
	private Long uuid;//编号
	private java.util.Date createtime;//生成日期
	private java.util.Date checktime;//检查日期
	private java.util.Date endtime;//结束日期
	private String type;//订单类型
	private Long creater;//下单员
	private String createrName;
	private Long checker;//审核员工编号
	private String checkerName;
	private Long ender;//库管员
	private String enderName;
	private Long supplieruuid;//供应商及客户编号
	private String supplierName;
	private Double totalmoney;//合计金额
	private String state;//订单状态
	private Long waybillsn;//运单号
	private Long ordersuuid;//原订单编号
	private List<Returnorderdetail> returnorderdetails;
	public final static String STATE_UNCHECK = "0";//未审核
	public final static String STATE_CHECKED = "1";//已审核
	public final static String STATE_OVER = "2";//已结束
	public final static String STATE_BACK = "5";//已驳回
	public List<Returnorderdetail> getReturnorderdetails() {
		return returnorderdetails;
	}
	public void setReturnorderdetails(List<Returnorderdetail> returnorderdetails) {
		this.returnorderdetails = returnorderdetails;
	}
	public Long getUuid() {		
		return uuid;
	}
	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}
	public java.util.Date getCreatetime() {		
		return createtime;
	}
	public void setCreatetime(java.util.Date createtime) {
		this.createtime = createtime;
	}
	public java.util.Date getChecktime() {		
		return checktime;
	}
	public void setChecktime(java.util.Date checktime) {
		this.checktime = checktime;
	}
	public java.util.Date getEndtime() {		
		return endtime;
	}
	public void setEndtime(java.util.Date endtime) {
		this.endtime = endtime;
	}
	public String getType() {		
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getCreater() {		
		return creater;
	}
	public void setCreater(Long creater) {
		this.creater = creater;
	}
	public Long getChecker() {		
		return checker;
	}
	public void setChecker(Long checker) {
		this.checker = checker;
	}
	public Long getEnder() {		
		return ender;
	}
	public void setEnder(Long ender) {
		this.ender = ender;
	}
	public Long getSupplieruuid() {		
		return supplieruuid;
	}
	public void setSupplieruuid(Long supplieruuid) {
		this.supplieruuid = supplieruuid;
	}
	public Double getTotalmoney() {		
		return totalmoney;
	}
	public void setTotalmoney(Double totalmoney) {
		this.totalmoney = totalmoney;
	}
	public String getState() {		
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Long getWaybillsn() {		
		return waybillsn;
	}
	public void setWaybillsn(Long waybillsn) {
		this.waybillsn = waybillsn;
	}
	public Long getOrdersuuid() {		
		return ordersuuid;
	}
	public void setOrdersuuid(Long ordersuuid) {
		this.ordersuuid = ordersuuid;
	}
	
	public String getCreaterName() {
		return createrName;
	}
	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}
	public String getCheckerName() {
		return checkerName;
	}
	public void setCheckerName(String checkerName) {
		this.checkerName = checkerName;
	}
	public String getEnderName() {
		return enderName;
	}
	public void setEnderName(String enderName) {
		this.enderName = enderName;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	@Override
	public String toString() {
		return "Returnorders [uuid=" + uuid + ", createtime=" + createtime + ", checktime=" + checktime + ", endtime="
				+ endtime + ", type=" + type + ", creater=" + creater + ", createrName=" + createrName + ", checker="
				+ checker + ", checkerName=" + checkerName + ", ender=" + ender + ", enderName=" + enderName
				+ ", supplieruuid=" + supplieruuid + ", supplierName=" + supplierName + ", totalmoney=" + totalmoney
				+ ", state=" + state + ", waybillsn=" + waybillsn + ", ordersuuid=" + ordersuuid
				+ ", returnorderdetails=" + returnorderdetails + "]";
	}
	
	
}
