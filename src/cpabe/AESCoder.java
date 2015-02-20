package cpabe;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESCoder {

	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG","Crypto");
		sr.setSeed(seed);
		kgen.init(128, sr); // 192 and 256 bits may not be available
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}

	public static byte[] encrypt(byte[] seed, byte[] plaintext)
			throws Exception {
		byte[] raw = getRawKey(seed);
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(plaintext);
		return encrypted;
	}

	public static byte[] decrypt(byte[] seed, byte[] ciphertext)
			throws Exception {
		byte[] raw = getRawKey(seed);
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(ciphertext);
		
		return decrypted;
	}
	
	/*
	static void decrypt() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
	    FileInputStream fis = new FileInputStream("data/encrypted");

	    FileOutputStream fos = new FileOutputStream("data/decrypted");
	    SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
	    Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.DECRYPT_MODE, sks);
	    CipherInputStream cis = new CipherInputStream(fis, cipher);
	    int b;
	    byte[] d = new byte[8];
	    while((b = cis.read(d)) != -1) {
	        fos.write(d, 0, b);
	    }
	    fos.flush();
	    fos.close();
	    cis.close();
	}
	*/

}