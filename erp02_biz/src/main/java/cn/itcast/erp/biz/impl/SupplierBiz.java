package cn.itcast.erp.biz.impl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import cn.itcast.erp.biz.ISupplierBiz;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Supplier;
/**
 * 供应商业务逻辑类
 * @author Administrator
 *
 */
public class SupplierBiz extends BaseBiz<Supplier> implements ISupplierBiz {

	private ISupplierDao supplierDao;
	
	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
		super.setBaseDao(this.supplierDao);
	}

	@Override
	public void export(OutputStream os, Supplier t1) {
		// 根据查询条件获取供应商/客户列表
	    List<Supplier> supplierList = super.getList(t1, null, null);
	    // 创建excel工作簿
	   HSSFWorkbook wk = new HSSFWorkbook();
	    HSSFSheet sheet = null;
	    // 根据查询条件中的类型来创建相应名称的工作表
	    System.err.println(t1.getType());
	    if("1".equals(t1.getType())){
	        sheet = wk.createSheet("供应商");
	    }
	    if("2".equals(t1.getType())){
	        sheet = wk.createSheet("客户");
	    }

	    // 写入表头
	    HSSFRow row = sheet.createRow(0);
	    // 定义好每一列的标题
	    String[] headerNames = {"名称","地址","联系人","电话","Email"};
	    // 指定每一列的宽度
	    int[] columnWidths = {4000,8000,2000,3000,8000};
	    HSSFCell cell = null;
	    for(int i = 0; i < headerNames.length; i++){
	        cell = row.createCell(i);
	        cell.setCellValue(headerNames[i]);
	        // 设置每列的宽度
	        sheet.setColumnWidth(i, columnWidths[i]);
	    }

	    // 写入内容
	    int i = 1;
	    for(Supplier supplier : supplierList){
	        row = sheet.createRow(i);
	        //必须按照hderarNames的顺序来
	        row.createCell(0).setCellValue(supplier.getName());//名称
	        row.createCell(1).setCellValue(supplier.getAddress());//地址
	        row.createCell(2).setCellValue(supplier.getContact());//联系人
	        row.createCell(3).setCellValue(supplier.getTele());//联系电话
	        row.createCell(4).setCellValue(supplier.getEmail());//邮件地址
	        i++;
	    }
	    try {
	    	// 写入到输出流中
	        wk.write(os);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }finally{
	        try {
	        	// 关闭工作簿
	            wk.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
		
	}

	@Override
	public void doImport(InputStream is) throws Exception {
		//读取excel内容
		HSSFWorkbook wk = null;
		try{
			wk = new HSSFWorkbook(is);
			//第一工作表
			HSSFSheet sht = wk.getSheetAt(0);
			//工作表名称
			String shtName = sht.getSheetName();
			//类型
			String type = "";
			if("供应商".equals(shtName)){
				type = "1";
			}
			if("客户".equals(shtName)){
				type = "2";
			}
			
			//最后一行的行号，下标从0开始
			int lastRowNum = sht.getLastRowNum();
			for(int i = 1; i <= lastRowNum; i++){
				Row row = sht.getRow(i);
				String name = row.getCell(0).getStringCellValue();//得到名称
				//判断是否存在,构建查询条件
				Supplier supplier = new Supplier();
				//按名称精确查询
				supplier.setName(name);
				List<Supplier> list = supplierDao.getList(null, supplier, null);
				if(list.size() > 0){
					//存在, 进入持久化状态
					supplier = list.get(0);
				}
				supplier.setAddress(row.getCell(1).getStringCellValue());//地址
				supplier.setContact(row.getCell(2).getStringCellValue());
				//电话号码：字符串, 如果是数值 getNumbericCellValue
				//单元格的数据类型：row.getCell(3).getCellType();
				supplier.setTele(row.getCell(3).getStringCellValue());
				supplier.setEmail(row.getCell(4).getStringCellValue());
				if(list.size() == 0){
					//不存在
					//类型
					supplier.setType(type);
					//添加
					supplierDao.add(supplier);
				}
			}
		}finally{
			if(null != wk){
				try {
					wk.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
