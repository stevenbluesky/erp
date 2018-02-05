package cn.itcast.erp.biz;
import java.io.InputStream;
import java.io.OutputStream;

import cn.itcast.erp.entity.Supplier;
/**
 * 供应商业务逻辑层接口
 * @author Administrator
 *
 */
public interface ISupplierBiz extends IBaseBiz<Supplier>{
	public void export(OutputStream os,Supplier t1);
	void doImport(InputStream is)throws Exception;
}

