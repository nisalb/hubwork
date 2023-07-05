package dev.nisalb.hubwork.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MailService {

    private final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final ExecutorService sendPool;

    @Autowired
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.sendPool = Executors.newCachedThreadPool(new ThreadFactory() {
            private static final AtomicInteger ids = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("snd-th-" + ids.getAndIncrement());

                return t;
            }
        });
    }


    public void sendMail(String to, String subject, List<String> bodyLines) {
        var mail = new SimpleMailMessage();

        mail.setFrom("no-reply@hubwork.dev");
        mail.setTo(to);
        mail.setSubject(subject);

        Optional<String> builtText = bodyLines.stream().reduce((body, line) -> body + "\n" + line);
        String mailText = builtText.orElse("");
        mail.setText(mailText);

        doSend(mail);
    }

    private void doSend(final SimpleMailMessage message) {
        sendPool.submit(() -> {
            try {
                mailSender.send(message);
            } catch (Exception ex) {
                logger.error("Exception occurred while sending mail: (to = " + to + ", subject = " + subject + ")");
            }
        });
    }
}
