package cn.itcast.erp.biz;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailUtil {
	private JavaMailSender sender;
	private String from;
	public void sendMail(String to,String subject,String text)throws Exception{
		//创建邮件
		MimeMessage mimeMessage = sender.createMimeMessage();
		//邮件包装工具
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
		//发送人
		messageHelper.setFrom(from);
		//收件人
		messageHelper.setTo(to);
		//邮件标题
		messageHelper.setSubject(subject);
		//邮件正文
		messageHelper.setText(text);
		//发送邮件
		sender.send(mimeMessage);
	}
	public void setSender(JavaMailSender sender) {
		this.sender = sender;
	}
	public void setFrom(String from) {
		this.from = from;
	}
}
