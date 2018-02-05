package cn.itcast.erp.biz.impl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import cn.itcast.erp.biz.ErpException;
import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IOrdersDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
/**
 * 订单业务逻辑类
 * @author Administrator
 *
 */
public class OrdersBiz extends BaseBiz<Orders> implements IOrdersBiz {

	private IOrdersDao ordersDao;
	private IEmpDao empDao;
	private ISupplierDao supplierDao;
	
	public IEmpDao getEmpDao() {
		return empDao;
	}
	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}
	public ISupplierDao getSupplierDao() {
		return supplierDao;
	}
	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}
	public void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
		super.setBaseDao(this.ordersDao);
	}
	public void add(Orders orders){
		Subject subject = SecurityUtils.getSubject();
		if(Orders.TYPE_IN.equals(orders.getType())){
			if(!subject.isPermitted("我的采购订单")){
				throw new ErpException("您没有采购申请权限");
			}
			//采购订单
			orders.setState(Orders.STATE_CREATE);
		}
		if(Orders.TYPE_OUT.equals(orders.getType())){
			if(!subject.isPermitted("我的销售订单")){
				throw new ErpException("您没有销售订单录入权限");
			}
			//销售订单
			orders.setState(Orders.STATE_NOT_OUT);
		}
		orders.setCreatetime(new Date());
		double total = 0;
		for (Orderdetail detail:orders.getOrderDetails()) {
			total += detail.getMoney();
			if(Orders.TYPE_IN.equals(orders.getType())){
				//采购订单
				detail.setState(Orderdetail.STATE_NOT_IN);
			}
			if(Orders.TYPE_OUT.equals(orders.getType())){
				//销售订单
				detail.setState(Orderdetail.STATE_NOT_OUT);
			}
			detail.setOrders(orders);
		}
		orders.setTotalmoney(total);
		super.add(orders);
	}
	@Override
	public List<Orders> getListByPage(Orders t1,Orders t2,Object param,int firstResult, int maxResults) {
		List<Orders> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		for(Orders orders : list){
			orders.setCreaterName(getEmpName(orders.getCreater()));
			orders.setCheckerName(getEmpName(orders.getChecker()));
			orders.setStarterName(getEmpName(orders.getStarter()));
			orders.setEnderName(getEmpName(orders.getEnder()));
			orders.setSupplierName(getSupplierName(orders.getSupplieruuid()));
		}
		
		return list;
	}
	/**
	 * 通过员工编号获取员工名称
	 * @param empuuid
	 * @return
	 */
	private String getEmpName(Long empuuid){
		if(null == empuuid){
			return null;
		}
		return empDao.get(empuuid).getName();
	}
	private String getSupplierName(Long supplieruuid){
		if(supplieruuid == null){
			return null;
		}
		return supplierDao.get(supplieruuid).getName();
	}
	@RequiresPermissions("采购审核")
	@Override
	public void doCheck(long id, Long uuid) {
		Orders orders = ordersDao.get(id);
		if(!Orders.STATE_CREATE.equals(orders.getState())){
			System.out.println("已经审核过了！");
			return ;
		}
		orders.setChecker(uuid);
		orders.setChecktime(new Date());
		orders.setState(Orders.STATE_CHECK);
	}
	@RequiresPermissions("采购确认")
	@Override
	public void doStart(long id, Long uuid) {
		Orders orders = ordersDao.get(id);
		if(!Orders.STATE_CHECK.equals(orders.getState())){
			System.out.println("已经确认过了！");
			return ;
		}
		orders.setStarter(uuid);
		orders.setStarttime(new Date());
		orders.setState(Orders.STATE_START);
	}
	@Override
	public void exportById(OutputStream os, Long uuid) throws Exception {
		//查询出订单
		Orders orders = ordersDao.get(uuid);
		//订单的明细
		List<Orderdetail> list = orders.getOrderDetails();
		//创建工作簿
		Workbook wk = new HSSFWorkbook();
		String title = "采 购 单";
		//创建工作表
		Sheet sht = wk.createSheet(title);
		//创建行,索引是从0开，列的从0开始
		Row row = sht.createRow(0);
		row.setHeight((short)1000);
		
		//创建字体
		Font font_content = wk.createFont();
		font_content.setFontName("宋体");
		font_content.setFontHeightInPoints((short)11);
		
		//创建标题的样式
		CellStyle style_title = wk.createCellStyle();
		//标题的字体
		Font font_title = wk.createFont();
		font_title.setFontName("黑体");
		font_title.setFontHeightInPoints((short)18);
		
		//创建单元格样式
		CellStyle style_content = wk.createCellStyle();
		//居中
		style_content.setAlignment(CellStyle.ALIGN_CENTER);//水平居中
		style_content.setVerticalAlignment(CellStyle.VERTICAL_CENTER);//垂直居中
		
		//复制样式
		style_title.cloneStyleFrom(style_content);
		//设置标题的字体
		style_title.setFont(font_title);
		//标题的单元格
		Cell cell_title = sht.getRow(0).createCell(0);
		//设置标题的样式
		cell_title.setCellStyle(style_title);
		
		style_content.setBorderBottom(CellStyle.BORDER_THIN);//底部边框为细边框
		style_content.setBorderLeft(CellStyle.BORDER_THIN);//
		style_content.setBorderTop(CellStyle.BORDER_THIN);
		style_content.setBorderRight(CellStyle.BORDER_THIN);
		
		//设置样式的字体
		style_content.setFont(font_content);
		
		//创建日期的样式
		CellStyle style_date = wk.createCellStyle();
		style_date.cloneStyleFrom(style_content);
		//格式化
		DataFormat dataFormat = wk.createDataFormat();
		style_date.setDataFormat(dataFormat.getFormat("yyyy-MM-dd"));
		
		//合并单元格
		//firstRow 开始的行, lastRow结束的行, firstCol开始的列, lastCol结束的列
		sht.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));//标题
		sht.addMergedRegion(new CellRangeAddress(2, 2, 1, 3));//供应商
		sht.addMergedRegion(new CellRangeAddress(7, 7, 0, 3));//明细
		
		int rowCnt = 9 + list.size();
		//创建行
		for(int i = 2; i <= rowCnt; i++){
			row = sht.createRow(i);
			//设置行高
			row.setHeight((short)500);
			for(int j = 0; j < 4; j++){
				//设置单元格的样式
				row.createCell(j).setCellStyle(style_content);
			}
		}
		//设置列宽
		for(int i = 0; i < 4; i++){
			sht.setColumnWidth(i, 5000);
		}
		
		//设置内容
		cell_title.setCellValue(title);//标题
		//供应商
		sht.getRow(2).getCell(0).setCellValue("供应商");
		//日期
		sht.getRow(3).getCell(0).setCellValue("下单日期");
		sht.getRow(4).getCell(0).setCellValue("审核日期");
		sht.getRow(5).getCell(0).setCellValue("采购日期");
		sht.getRow(6).getCell(0).setCellValue("入库日期");
		
		//设置日期格式
		sht.getRow(3).getCell(1).setCellStyle(style_date);
		sht.getRow(4).getCell(1).setCellStyle(style_date);
		sht.getRow(5).getCell(1).setCellStyle(style_date);
		sht.getRow(6).getCell(1).setCellStyle(style_date);
		
		sht.getRow(3).getCell(1).setCellValue(orders.getCreatetime());//下单日期
		setDateValue(sht.getRow(4).getCell(1),orders.getChecktime());//审核日期
		setDateValue(sht.getRow(5).getCell(1),orders.getStarttime());//确认日期
		setDateValue(sht.getRow(6).getCell(1),orders.getEndtime());//入库日期
		
		sht.getRow(3).getCell(2).setCellValue("经办人");
		sht.getRow(4).getCell(2).setCellValue("经办人");
		sht.getRow(5).getCell(2).setCellValue("经办人");
		sht.getRow(6).getCell(2).setCellValue("经办人");
		
		sht.getRow(3).getCell(3).setCellValue(getEmpName(orders.getCreater()));//下单人
		sht.getRow(4).getCell(3).setCellValue(getEmpName(orders.getChecker()));//审核人
		sht.getRow(5).getCell(3).setCellValue(getEmpName(orders.getStarter()));//确认人
		sht.getRow(6).getCell(3).setCellValue(getEmpName(orders.getEnder()));//库管员
		
		sht.getRow(7).getCell(0).setCellValue("订单明细");
		sht.getRow(8).getCell(0).setCellValue("商品名称");
		sht.getRow(8).getCell(1).setCellValue("数量");
		sht.getRow(8).getCell(2).setCellValue("价格");
		sht.getRow(8).getCell(3).setCellValue("金额");
		
		//设置供应商
		sht.getRow(2).getCell(1).setCellValue(getSupplierName(orders.getSupplieruuid()));
		
		int i = 9;
		for (Orderdetail od : list) {
			row = sht.getRow(i);
			row.getCell(0).setCellValue(od.getGoodsname());
			row.getCell(1).setCellValue(od.getNum());
			row.getCell(2).setCellValue(od.getPrice());
			row.getCell(3).setCellValue(od.getMoney());
			i++;
		}
		sht.getRow(i).getCell(0).setCellValue("合计");
		sht.getRow(i).getCell(3).setCellValue(orders.getTotalmoney());
		
		//把工作簿写到输出流中
		try {
			wk.write(os);
		} finally{
			try {
				wk.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	private void setDateValue(Cell cell, Date date){
		if(date != null){
			cell.setCellValue(date);
		}
	}
}
