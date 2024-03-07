package com.zerobase.cms.user.client.service;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.client.mailgun.SendMailForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {
    private final MailgunClient mailgunClient;


    public String sendEmail(){

        SendMailForm form = SendMailForm.builder()
                            .from("zerobase-test@email.com")
                            .to("l07wkdgustn07l@gmail.com")
                            .subject("Test email from zerobase")
                            .text("EMAIL TEST")
                            .build();

      return mailgunClient.sendEmail(form).getBody();
    }
}
