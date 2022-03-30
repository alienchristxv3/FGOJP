package cn.mcfun.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;

public class Base64Utils {
    public Base64Utils() {
    }

    public static String encode(String s) {
        if (s == null) {
            return null;
        } else {
            byte[] b = Base64.getEncoder().encode(s.getBytes());

            try {
                return new String(b, "UTF-8");
            } catch (UnsupportedEncodingException var3) {
                var3.printStackTrace();
                return null;
            }
        }
    }

    public static String decode(String s) {
        if (s == null) {
            return null;
        } else {
            try {
                byte[] b = Base64.getDecoder().decode(URLDecoder.decode(s, "UTF-8"));
                return new String(b, "UTF-8");
            } catch (UnsupportedEncodingException var2) {
                var2.printStackTrace();
                return null;
            }
        }
    }
}
