package cn.lanyj.keeper.services;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.sun.mail.util.MailSSLSocketFactory;

import cn.lanyj.keeper.models.Email;
import cn.lanyj.keeper.repositories.EmailRepository;


@Service
@PropertySource({"classpath:app.properties"})
public class EmailService {
	
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	private final static int WORK_THREAD_COUNT = 1;
	private final static int MAX_SENT_POOL_SIZE = 3;
	
	private List<Email> sent = new ArrayList<>(MAX_SENT_POOL_SIZE);
	private PriorityQueue<Email> emails = new PriorityQueue<>();
	
	Environment env;
	
	@Autowired
	EmailRepository repository;
	
	private Session session;
	private Transport transport;
	
	Runnable sender = new Runnable() {
		
		@Override
		public void run() {
			Email email = null;
			synchronized (emails) {
				email = emails.poll();
			}
			if(email != null) {
				send(email);
			}
		}
	};
	Runnable checker = new Runnable() {
		
		@Override
		public void run() {
			if(!transport.isConnected()) {
				try {
					transport.connect();
				} catch (MessagingException e) {
					log.error("Connect to mail server failed.", e);
				}
			}
		}
	};
	
	public void postSend(Email email) {
		synchronized (emails) {
			emails.add(email);
		}
	}
	
	@Autowired
	public EmailService(Environment env, SchedulerService schedulerService) {
		this.env = env;
		
		for(int i = 0; i < WORK_THREAD_COUNT; i++) {
			schedulerService.getScheduledExecutorService().scheduleAtFixedRate(sender, 0, 500, TimeUnit.MILLISECONDS);
		}
		schedulerService.getScheduledExecutorService().scheduleAtFixedRate(checker, 0, 5000, TimeUnit.MILLISECONDS);
		
		consrtuctEmailEngine();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				log.info("Mail service closing...");
				try {
					transport.close();
				} catch (MessagingException e) {
					log.error("Shutdown mail service failed.", e);
				}
			}
		}));
	}
	
	private void consrtuctEmailEngine() {
		Properties props = new Properties();
        props.setProperty("mail.debug", env.getProperty("mail.debug", "false"));
        props.setProperty("mail.smtp.auth", env.getProperty("mail.smtp.auth", "true"));
        props.setProperty("mail.host", env.getProperty("mail.host", "smtp.qq.com"));
        props.setProperty("mail.transport.protocol", env.getProperty("mail.transport.protocol", "smtp"));
		try {
	        MailSSLSocketFactory sf = new MailSSLSocketFactory();
	        sf.setTrustAllHosts(true);
	        props.put("mail.smtp.ssl.enable", env.getProperty("mail.smtp.ssl.enable", "true"));
	        props.put("mail.smtp.ssl.socketFactory", sf);
	        session = Session.getInstance(props);
	        transport = session.getTransport();
	        transport.connect(env.getProperty("mail.host", "smtp.qq.com"), env.getProperty("mail.user"), env.getProperty("mail.password"));
		} catch (GeneralSecurityException | MessagingException e) {
			log.error("Mail sender start failed", e);
		}
	}
	
	private void send(Email email) {
        try {
			Message msg = new MimeMessage(session);
			msg.setSubject(email.getSubject());
			msg.setText(email.getContent());
			msg.setFrom(new InternetAddress(email.getFrom()));
	        transport.sendMessage(msg, new Address[] { new InternetAddress(email.getTo()) });
			email.setSent(true);
			
			synchronized (sent) {
				sent.add(email);
				if(sent.size() >= MAX_SENT_POOL_SIZE) {
					repository.saveAll(sent);
					repository.flush();
					sent.clear();
				}
			}
		} catch (Exception e) {
			log.error("Email send failed, id=" + email.getId() + ", failedTimes=" + email.getFailTimes() +", to=" + email.getTo() + ", content=" + email.getContent(), e);
			synchronized (emails) {
				if((e instanceof IllegalStateException) && ("Not connected".equals(e.getMessage()))) {
					checker.run();
				}
				email.fail();
				if(!email.isFailAtLast()) {
					emails.add(email);
				}
			}
		}
	}
	
}
