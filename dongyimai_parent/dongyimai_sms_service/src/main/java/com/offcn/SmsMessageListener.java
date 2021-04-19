package com.offcn;

import com.offcn.utils.SmsUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.IOException;

@Component
public class SmsMessageListener implements MessageListener {

    @Autowired
    private SmsUtils smsUtils;

    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            try {
                MapMessage mapMessage = (MapMessage) message;
                String mobile = mapMessage.getString("mobile");
                String param = mapMessage.getString("param");
                System.out.println("注册成功！！！！！！！！！！happy");
                //HttpResponse response = smsUtils.sendSms(mobile, param);
                //System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
