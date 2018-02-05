package cn.itcast.erp.biz;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.itcast.erp.entity.Storealert;

public class MailJob {
	private IStoredetailBiz storedetailBiz;
	private MailUtil mailUtil;
	private String to;
	private String subject;
	private String text;
	public void setStoredetailBiz(IStoredetailBiz storedetailBiz) {
		this.storedetailBiz = storedetailBiz;
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
	public void doJob(){
		List<Storealert> list = storedetailBiz.getStorealert();
		if(null!=list&&list.size()>0){
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			try {
				mailUtil.sendMail(to, subject.replace("[time]",df.format(new Date())),
						text.replace("[count]",list.size()+""));
				System.out.println("邮件发送成功 "+df.format(new Date()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
