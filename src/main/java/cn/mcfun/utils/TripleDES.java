package cn.mcfun.utils;

import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import static com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility.getBytes;

public class TripleDES {
    final static String key = "b5nHjsMrqaeNliSs3jyOzgpD";
    final static String keyiv = "wuD6keVr";
    public String decryptMode(String data) {
        byte[] keyB = getBytes(key);
        byte[] keyivB = getBytes(keyiv);
        byte[] dataByte = Base64.getDecoder().decode(data);
        String result = null;
        try {
            DESedeKeySpec spec = new DESedeKeySpec(keyB);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            Key deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(keyivB);
            cipher.init(2, deskey, ips);
            byte[] bOut = cipher.doFinal(dataByte);
            result = new String(bOut, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public String encryptMode(String data) {
        byte[] keyB = getBytes(key);
        byte[] keyivB = getBytes(keyiv);
        byte[] dataByte = getBytes(data);
        String result = null;
        try {
            DESedeKeySpec spec = new DESedeKeySpec(keyB);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
            Key deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(keyivB);
            cipher.init(1, deskey, ips);
            byte[] bOut = cipher.doFinal(dataByte);
            result = Base64.getEncoder().encodeToString(bOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
