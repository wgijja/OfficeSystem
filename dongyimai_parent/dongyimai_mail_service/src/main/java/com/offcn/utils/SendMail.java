package com.offcn.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class SendMail {

    @Autowired
    private JavaMailSenderImpl mailSender;

    private String from = "wgijja@163.com";
    private String subject = "Welcome to dongyimai";

    public void sendTextMail(String toMail, String text) {
        //创建一个简单的发送邮件对象
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        //发送邮件的账号
        simpleMailMessage.setFrom(from);
        //接收邮件的账号
        simpleMailMessage.setTo(toMail);
        //标题
        simpleMailMessage.setSubject(subject);
        //正文
        simpleMailMessage.setText(text);

        //发送邮件
        mailSender.send(simpleMailMessage);
        System.out.println("发送邮件成功");
    }
}
