package cn.lanyj.keeper;

import cn.lanyj.keeper.util.AESCrypto;

import static org.springframework.util.Base64Utils.decodeFromUrlSafeString;
import static org.springframework.util.Base64Utils.encodeToUrlSafeString;

import static org.springframework.util.Base64Utils.decodeFromString;
import static org.springframework.util.Base64Utils.encodeToString;

public class AESCryptoTest {

	public static void main(String[] args) throws Exception {
		String content = "abcdefghijklmn";
		AESCrypto crypto = new AESCrypto(content);
		System.out.println(crypto.encrypt(content));
		
		String test = "rPE3I3fCoGnyzD84D_9TDQ==";
		byte[] dus = decodeFromUrlSafeString(test);
		byte[] du = decodeFromString(test);
		
		System.out.println(encodeToString(dus));
		System.out.println(encodeToUrlSafeString(dus));
		
		System.out.println(encodeToString(du));
		System.out.println(encodeToUrlSafeString(du));
	}
	
	static String encodeUrlSafe(byte[] buf) {
		return encodeToUrlSafeString(buf);
	}
	
	static byte[] decodeUrlSafe(String url) {
		return decodeFromUrlSafeString(url);
	}

	static String encode(byte[] buf) {
		return encodeToString(buf);
	}
	
	static byte[] decode(String url) {
		return decodeFromString(url);
	}
	
}