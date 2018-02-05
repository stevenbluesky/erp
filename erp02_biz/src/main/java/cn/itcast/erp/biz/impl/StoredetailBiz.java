package cn.itcast.erp.biz.impl;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.itcast.erp.biz.ErpException;
import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.biz.MailUtil;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IStoreDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;
/**
 * 仓库库存业务逻辑类
 * @author Administrator
 *
 */
public class StoredetailBiz extends BaseBiz<Storedetail> implements IStoredetailBiz {
	private MailUtil mailUtil;
	private String to;
	private String subject;
	private String text;
	private IStoredetailDao storedetailDao;
	private IGoodsDao goodsDao;
	private IStoreDao storeDao;
	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}
	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}
	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
		super.setBaseDao(this.storedetailDao);
	}
	
	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setText(String text) {
		this.text = text;
	}
	/*	private String getGoodsName(Long uuid,Map<Long,String> goodsNameMap){
		if(null==uuid){
			return null;
		}
		String goodsName = goodsNameMap.get(uuid);
		if(null == goodsName){
			goodsName = goodsDao.get(uuid).getName();
			goodsNameMap.put(uuid, goodsName);
		}
		return goodsName;
	}
	private String getStoreName(Long uuid,Map<Long,String> storeNameMap){
		if(null==uuid){
			return null;
		}
		String storeName = storeNameMap.get(uuid);
		if(null == storeName){
			storeName = storeDao.get(uuid).getName();
			storeNameMap.put(uuid, storeName);
		}
		return storeName;
	}*/
	@Override
	public List<Storedetail> getListByPage(Storedetail t1, Storedetail t2, Object param, int firstResult,int maxResults) {
		List<Storedetail> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		for (Storedetail sd : list) {
			//设置商品名称
			sd.setGoodsName(goodsDao.get(sd.getGoodsuuid()).getName());
			//设置仓库名称
			sd.setStoreName(storeDao.get(sd.getStoreuuid()).getName());
		}
		return list;
	}
	@Override
	public List<Storealert> getStorealert() {
		return storedetailDao.getStorealert();
	}
	@Override
	public void sendStoreAlertMail() throws Exception {
		List<Storealert> storealert = storedetailDao.getStorealert();
		int cnt = storealert==null?0:storealert.size();
		if(cnt > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			mailUtil.sendMail(to, subject.replace("[time]",sdf.format(new Date())),
					text.replace("[count]",String.valueOf(cnt)));
		}else{
			throw new ErpException("没有需要预警的商品~");
		}
	}
}
