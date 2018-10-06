package cn.lanyj.keeper.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import cn.lanyj.keeper.models.User;
import cn.lanyj.keeper.util.AESCrypto;

public class AuthenticationService {
	
	private static final String AUTHENTICATION_PASSWORD = UUID.randomUUID() + "$-$";
	public static final String AUTHENTICATION_PART = "__token";
	
	private static final Gson gson = new Gson();
	private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
	
	static {
		log.info("Using password prefix: [" + AUTHENTICATION_PASSWORD + "]");
	}
	
	public static boolean isAuthentication(String token) {
		return getAuthenticationToken(token) != null;
	}
	
	public static AuthenticationToken generateAuthenticationToken(User user) {
		AuthenticationToken authenticationToken = new AuthenticationToken(user.getId());
		return authenticationToken;
	}
	
	public static String authenticationToString(AuthenticationToken authenticationToken) {
		AuxAuthen auxAuthen = new AuxAuthen();
		auxAuthen.setToken(aesEncrypt(gson.toJson(authenticationToken), AUTHENTICATION_PASSWORD + auxAuthen.getSalt()));
		String token = "";
		try {
			token = URLEncoder.encode(gson.toJson(auxAuthen), "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		return token;
	}
	
	public static AuthenticationToken getAuthenticationToken(String token) {
		try {
			token = URLDecoder.decode(token, "utf-8");
			AuxAuthen auxAuthen = gson.fromJson(token, AuxAuthen.class);
			AuthenticationToken authenticationToken = 
					gson.fromJson(aesDecrypt(auxAuthen.getToken(),
							AUTHENTICATION_PASSWORD + auxAuthen.getSalt()), 
							AuthenticationToken.class);
			if(authenticationToken.isExpired()) {
				throw new AuthenticationFailedException("Authentication failed.");
			}
			return authenticationToken;
		} catch (Exception e) {
			throw new AuthenticationFailedException("Authentication failed.");
		}
	}
	
	public static String aesEncrypt(String content, String password) {
		try {
			AESCrypto crypto = new AESCrypto(password);
			String ret = crypto.encrypt(content);
			return ret;
		} catch (Exception e) {
			throw new RuntimeException("Encrypt failed.", e);
		}
	}

	public static String aesDecrypt(String cryptograph, String password) {
		try {
			AESCrypto crypto = new AESCrypto(password);
			String ret = crypto.decrypt(cryptograph);
			return ret;
		} catch (Exception e) {
			throw new RuntimeException("Decrypt failed.", e);
		}
	}	
}
class AuxAuthen {
	protected String salt = UUID.randomUUID().toString();
	protected String token;

	public AuxAuthen() {
	}
	
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public String getSalt() {
		return salt;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
}
