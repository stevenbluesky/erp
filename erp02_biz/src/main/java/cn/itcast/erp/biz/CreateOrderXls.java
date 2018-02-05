package cn.itcast.erp.biz;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class CreateOrderXls {
	
	public static void main(String [] args)throws Exception{
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
		
		
		for(int i = 2; i < 12; i++){
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
		
		sht.getRow(3).getCell(2).setCellValue("经办人");
		sht.getRow(4).getCell(2).setCellValue("经办人");
		sht.getRow(5).getCell(2).setCellValue("经办人");
		sht.getRow(6).getCell(2).setCellValue("经办人");
		
		sht.getRow(7).getCell(0).setCellValue("订单明细");
		sht.getRow(8).getCell(0).setCellValue("商品名称");
		sht.getRow(8).getCell(1).setCellValue("数量");
		sht.getRow(8).getCell(2).setCellValue("价格");
		sht.getRow(8).getCell(3).setCellValue("金额");
		//alt + shift+a
		
		//设置日期值
		sht.getRow(3).getCell(1).setCellValue(new Date());
		
		//保存本地文件中
		wk.write(new FileOutputStream(new File("d:\\orders.xls")));
		wk.close();

	}
}
