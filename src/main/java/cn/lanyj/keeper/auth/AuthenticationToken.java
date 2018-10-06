package cn.lanyj.keeper.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreType;

@JsonIgnoreType
public class AuthenticationToken {
	
	private Map<String, Object> attributes = new HashMap<>();

	static final long TIME_TO_LIVE_IN_MILLIS = 15 * 1000 * 60;
	
	private String userId;
	
	private long expiredIn;
	
	private String salt = UUID.randomUUID().toString();
	
	public AuthenticationToken(String userId) {
		this(userId, TIME_TO_LIVE_IN_MILLIS);
	}
	
	public AuthenticationToken(String userId, long liveTime) {
		this.userId = userId;
		this.expiredIn = System.currentTimeMillis() + liveTime;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public AuthenticationToken put(String key, Object value) {
		attributes.put(key, value);
		return this;
	}
	
	public AuthenticationToken clear() {
		attributes.clear();
		return this;
	}
	
	public Object get(String key) {
		return attributes.get(key);
	}
	
	public boolean containsKey(String key) {
		return attributes.containsKey(key);
	}
	
	public boolean containsValue(String value) {
		return attributes.containsValue(value);
	}
	
	public Set<String> keySet() {
		return attributes.keySet();
	}
	
	public Set<Entry<String, Object>> entrySet() {
		return attributes.entrySet();
	}
	
	public void refresh() {
		this.refresh(TIME_TO_LIVE_IN_MILLIS);
	}
	
	public void refresh(long liveTime) {
		this.expiredIn = System.currentTimeMillis() + liveTime;
	}
	
	public boolean isExpired() {
		return expiredIn <= System.currentTimeMillis();
	}
	
	public String getSalt() {
		return salt;
	}
	
	@Override
	public String toString() {
		throw new IllegalAccessError("Authentication token cannot call toString method.");
	}
	
}