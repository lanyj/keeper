package cn.lanyj.keeper.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.utils.Utils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.util.Base64Utils;

public class AESCrypto {
	SecretKeySpec key;

	final static String transform = "AES/CBC/PKCS5Padding";
	final static Properties properties = new Properties();
	final static IvParameterSpec iv = new IvParameterSpec("=AES-UKEEPER-IV=".getBytes());

	/**
	 * Converts String to UTF8 bytes
	 *
	 * @param input the input string
	 * @return UTF8 bytes
	 */
	private static byte[] getUTF8Bytes(String input) {
		return input.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Converts ByteBuffer to String
	 *
	 * @param buffer input byte buffer
	 * @return the converted string
	 */
	private static String asString(ByteBuffer buffer) {
		final ByteBuffer copy = buffer.duplicate();
		final byte[] bytes = new byte[copy.remaining()];
		copy.get(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public static byte[] toHash256Deal(String datastr) {
		try {
			MessageDigest digester = MessageDigest.getInstance("SHA-256");
			digester.update(datastr.getBytes());
			byte[] hex = digester.digest();
			return hex;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static String toHash256(String datastr) {
		try {
			MessageDigest digester = MessageDigest.getInstance("SHA-256");
			digester.update(datastr.getBytes());
			byte[] hex = digester.digest();
			return Base64.encodeBase64String(hex);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public AESCrypto(String password) {
		key = new SecretKeySpec(toHash256Deal(password), "AES");
	}


	/**
	 * 加密
	 * 
	 * @param text 需要加密的明文
	 * @return 经过base64加密后的密文
	 */
	public String encrypt(String text) {
		final ByteBuffer outBuffer;
		final int bufferSize = 1024;
		final int updateBytes;
		final int finalBytes;
		try {
			CryptoCipher encipher = Utils.getCipherInstance(transform, properties);
			ByteBuffer inBuffer = ByteBuffer.allocateDirect(bufferSize);
			outBuffer = ByteBuffer.allocateDirect(bufferSize);
			inBuffer.put(getUTF8Bytes(text));
			inBuffer.flip();
			encipher.init(Cipher.ENCRYPT_MODE, key, iv);
			updateBytes = encipher.update(inBuffer, outBuffer);
			finalBytes = encipher.doFinal(inBuffer, outBuffer);
			outBuffer.flip();
			byte[] encoded = new byte[updateBytes + finalBytes];
			outBuffer.duplicate().get(encoded);
			String encodedString = Base64Utils.encodeToUrlSafeString(encoded);
			return encodedString;
		} catch (Exception e) {
			throw new RuntimeException("Encrypt failed.", e);
		}
	}

	/**
	 * 解密
	 * 
	 * @param encodedString 经过base64加密后的密文
	 * @return 明文
	 */
	public String decrypt(String encodedString) {
		final ByteBuffer outBuffer;
		final int bufferSize = 1024;
		ByteBuffer decoded = ByteBuffer.allocateDirect(bufferSize);
		try {
			CryptoCipher decipher = Utils.getCipherInstance(transform, properties);
			decipher.init(Cipher.DECRYPT_MODE, key, iv);
			outBuffer = ByteBuffer.allocateDirect(bufferSize);
			outBuffer.put(Base64Utils.decodeFromUrlSafeString(encodedString));
			outBuffer.flip();
			decipher.update(outBuffer, decoded);
			decipher.doFinal(outBuffer, decoded);
			decoded.flip();
			return asString(decoded);
		} catch (Exception e) {
			throw new RuntimeException("Decrypt failed.", e);
		}
	}
	
}