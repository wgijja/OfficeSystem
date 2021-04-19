package com.offcn.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.offcn.pojo.TbItem;
import com.offcn.serarch.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class ItemSearchMessageListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            //1、消息类型转换
            TextMessage textMessage = (TextMessage) message;
            try {
                String listStr = textMessage.getText();
                List<TbItem> itemList = JSON.parseArray(listStr, TbItem.class);
                //2、调用导入solr方法
                itemSearchService.importItem(itemList);
                System.out.println("消息队列接收消息成功，导入solr成功");
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
