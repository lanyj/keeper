package cn.lanyj.keeper.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import cn.lanyj.keeper.util.AESCrypto;

@Service
public class CryptoService {

	public String aesEncrypt(String content, String password) throws Exception {
		return new AESCrypto(password).encrypt(content);
	}

	public String aesDecrypt(String cryptograph, String password) throws Exception {
		return new AESCrypto(password).decrypt(cryptograph);
	}

	public byte[] toHash256Deal(String datastr) {
		try {
			MessageDigest digester = MessageDigest.getInstance("SHA-256");
			digester.update(datastr.getBytes());
			byte[] hex = digester.digest();
			return hex;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public String toHash256(String datastr) {
		try {
			MessageDigest digester = MessageDigest.getInstance("SHA-256");
			digester.update(datastr.getBytes());
			byte[] hex = digester.digest();
			return Base64.encodeBase64String(hex);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
}
