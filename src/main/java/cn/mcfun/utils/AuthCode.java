package cn.mcfun.utils;

import cn.mcfun.entity.UserInfo;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.TextUtils;

public class AuthCode {
    public AuthCode() {
    }

    public String getSign(UserInfo userInfo, List<BasicNameValuePair> pairs) {
        Map<String, String> unsortMap = new HashMap();

        for(int j = 0; j < pairs.size(); ++j) {
            BasicNameValuePair bbs = (BasicNameValuePair)pairs.get(j);
            unsortMap.put(bbs.getName(), bbs.getValue());
        }

        Map<String, String> treeMap = new TreeMap(unsortMap);
        StringBuffer sb = new StringBuffer();
        String key;
        String value;
        if (treeMap.size() > 0) {
            Iterator var6 = treeMap.keySet().iterator();

            while(var6.hasNext()) {
                key = (String)var6.next();
                sb.append(key + "=");
                if (TextUtils.isEmpty((CharSequence)treeMap.get(key))) {
                    sb.append("&");
                } else {
                    value = (String)treeMap.get(key);
                    sb.append(value + "&");
                }
            }
        }

        String ms = sb.toString();
        ms = ms.substring(0, ms.length() - 1);
        key = ":" + userInfo.getSecretKey();
        ms = ms + key;
        value = this.encrypt("SHA-1", ms);
        return value;
    }

    public String encrypt(String algorithm, String srcStr) {
        try {
            StringBuilder result = new StringBuilder();
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(srcStr.getBytes("utf-8"));
            byte[] var6 = bytes;
            int var7 = bytes.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                byte b = var6[var8];
                String hex = Integer.toHexString(b & 255);
                if (hex.length() == 1) {
                    result.append("0");
                }

                result.append(hex);
            }

            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception var11) {
            throw new RuntimeException(var11);
        }
    }
}
