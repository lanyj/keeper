package cn.lanyj.keeper.models;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="emails")
public class Email extends BaseModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3298626943115496946L;

	private static final int MAX_FAIL_TIMES = 3;
	
	@Column(name="_from")
	private String from = "";
	
	@Column(name="_to")
	private String to = "";
	
	private String subject = "";
	
	@Column(length=4096)
	private String content = "";
	
	private int failTimes = 0;
	
	private boolean sent = false;

	public Email() {
		this.setCreatedAt(ZonedDateTime.now());
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public boolean isSent() {
		return sent;
	}
	
	public void setSent(boolean sent) {
		this.sent = sent;
	}
	
	public void fail() {
		this.failTimes++;
	}
	
	public boolean isFailAtLast() {
		return getFailTimes() >= MAX_FAIL_TIMES;
	}
	
	public void setFailTimes(int failTimes) {
		this.failTimes = failTimes;
	}
	
	public int getFailTimes() {
		return failTimes;
	}
	
	@Override
	public int compareTo(BaseModel o) {
		if(!(o instanceof Email)) {
			return -1;
		}
		Email mail = (Email) o;
		return this.getCreatedAt().compareTo(mail.getCreatedAt());
	}
	
}
