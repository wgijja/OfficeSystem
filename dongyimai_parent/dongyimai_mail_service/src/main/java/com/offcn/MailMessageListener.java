package com.offcn;

import com.offcn.utils.SendMail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class MailMessageListener implements MessageListener {

    @Autowired
    private SendMail sendMail;

    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            MapMessage mapMessage = (MapMessage) message;
            try {
                String toMail = mapMessage.getString("toMail");
                String text = mapMessage.getString("text");
                sendMail.sendTextMail(toMail,text);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
