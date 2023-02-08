package kr.wadiz.platform.api.nicepay.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final DateTimeFormatter YY_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final String ALG = "AES/ECB/PKCS5Padding";

    public static String getTid(String mid, String method, String type) {
        StringBuffer sb = new StringBuffer();
        sb.append(mid);
        sb.append(method);
        sb.append(type);
        sb.append(LocalDateTime.now().format(YY_DATE_TIME_FORMATTER));
        DecimalFormat df = new DecimalFormat("0000");
        sb.append(df.format((int)(Math.random() * 10000)));
        return sb.toString();
    }

    public static String getDateTime() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    public static String getDate() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    public static String encData(String strData, String key) {
        String encData = null;

        try {
            Cipher cipher = Cipher.getInstance(ALG);
            SecretKeySpec keySpec = new SecretKeySpec(key.substring(0, 16).getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] encrypted = cipher.doFinal(strData.getBytes());
            encData = encodeHex(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encData;
    }

    public static String signData(String strData){
        String passACL = null;

        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.reset();
            md.update(strData.getBytes());
            byte[] raw = md.digest();
            passACL = encodeHex(raw);
        } catch(Exception e) {
            System.out.print("암호화 에러" + e.toString());
        }

        return passACL;
    }

    private static String encodeHex(byte[] b){
        char [] c = Hex.encodeHex(b);
        return new String(c);
    }
}
