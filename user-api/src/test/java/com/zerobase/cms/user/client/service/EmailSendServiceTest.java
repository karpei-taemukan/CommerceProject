package com.zerobase.cms.user.client.service;


import com.zerobase.cms.user.client.MailgunClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class EmailSendServiceTest {
    @Autowired
    private EmailSendService emailSendService;
    @Autowired
    private MailgunClient mailgunClient;

    @Test
    public void EmailTest(){
        String response = emailSendService.sendEmail();
       // mailgunClient.sendEmail(null);
        System.out.println(response);
    }
}