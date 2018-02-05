package cn.itcast.erp.action;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import cn.itcast.erp.biz.ISupplierBiz;
import cn.itcast.erp.entity.Supplier;

/**
 * 供应商Action 
 * @author Administrator
 *
 */
public class SupplierAction extends BaseAction<Supplier> {
	private File file;
	private String fileFileName;
	private String fileContentType;
	private ISupplierBiz supplierBiz;
	public void setFile(File file) {
		this.file = file;
	}
	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}
	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}
	public void setSupplierBiz(ISupplierBiz supplierBiz) {
		this.supplierBiz = supplierBiz;
		super.setBaseBiz(this.supplierBiz);
	}
	public void export(){
		String filename = "";
		if("1".equals(getT1().getType())){
			filename = "供应商.xls";
		}
		if("2".equals(getT1().getType())){
			filename = "客户.xls";
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			response.setHeader("Content-Disposition", "attachment;filename="+
			new String(filename.getBytes(),"ISO-8859-1"));
			supplierBiz.export(response.getOutputStream(), getT1());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void doImport(){
		if(!"application/vnd.ms-excel".equals(fileContentType)||(!fileFileName.endsWith(".xls")&&!fileFileName.endsWith(".xlsx"))){
			ajaxReturn(false, "上传文件必须为excel文件！");
			return;
		}
		try {
			supplierBiz.doImport(new FileInputStream(file));
			ajaxReturn(true,"文件上传成功！");
		} catch (Exception e) {
			ajaxReturn(false,"文件上传失败！");
			e.printStackTrace();
		}
	}
}
