package cn.mcfun.utils;

import cn.mcfun.Main;
import cn.mcfun.entity.UserInfo;
import cn.mcfun.request.PostRequest;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 引继码登录流程
 * 该登录流程中出现任何异常都可能会导致账号丢失！请自行处理异常或者及时存档，避免异常终止导致数据丢失！
 */
public class ContinueKeyLogin {
    //引继码
    static String continueKey = "4k3Yp9ndcJ";
    //引继密码（生成新的引继码后默认也使用该密码）
    static String continuePass = "123456";
    public static void main(String[] args) {
        UserInfo userInfo = new UserInfo();
        userInfo.setCookie(new BasicCookieStore());
        regist(userInfo);
    }
    //注册一个新账号
    public static void regist(UserInfo userInfo) {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("appVer", Main.appVer));
        params.add(new BasicNameValuePair("dateVer", Main.dateVer));
        params.add(new BasicNameValuePair("dataVer", Main.dataVer));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "e92c481b51ff8203344cf768b2f5bc84b14409bc2ad7084292c9370ea97621de"));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = sendPost(userInfo.getCookie(), "https://game.fate-go.jp/account/regist", params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            String authKey = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("authKey");
            String secretKey = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("secretKey");
            String userId = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("userId");
            userInfo.setAuthKey(authKey);
            userInfo.setSecretKey(secretKey);
            userInfo.setUserId(userId);
            decide(userInfo);
        } else {
            //处理错误
        }

    }
    //使用引继码替换新账号
    public static void decide(UserInfo userInfo) {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("continueKey", continueKey));
        params.add(new BasicNameValuePair("continuePass", continuePass));
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", Main.appVer));
        params.add(new BasicNameValuePair("dateVer", Main.dateVer));
        params.add(new BasicNameValuePair("dataVer", Main.dataVer));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("continueType", "1"));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "e92c481b51ff8203344cf768b2f5bc84b14409bc2ad7084292c9370ea97621de"));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = sendPost(userInfo.getCookie(), "https://game.fate-go.jp/continue/decide?_userId="+userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            String authKey = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("authKey");
            String secretKey = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("secretKey");
            String userId = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("userId");
            userInfo.setAuthKey(authKey);
            userInfo.setSecretKey(secretKey);
            userInfo.setUserId(userId);
            //及时保存存档！避免以外情况导致数据丢失！存档：（authKey、secretKey、userId）只要存档在，就可以使用存档登录生成新的引继码！
            topLogin(userInfo);
        } else {
            //处理错误
        }

    }
    //登录账号第一步
    public static void topLogin(UserInfo userInfo) {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        String userState = String.valueOf(-Long.parseLong(lastAccessTime) >> 2 ^ Long.parseLong(userInfo.getUserId()) & Long.parseLong(Main.dataServerFolderCrc));
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", Main.appVer));
        params.add(new BasicNameValuePair("dateVer", Main.dateVer));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("verCode", "723d93a599b6f10ef3085ff1131fa5679a91da924246b8ca40dded18eccaf3da"));
        params.add(new BasicNameValuePair("userState", userState));
        params.add(new BasicNameValuePair("assetbundleFolder", Main.assetbundleFolder));
        params.add(new BasicNameValuePair("dataVer", Main.dataVer));
        params.add(new BasicNameValuePair("isTerminalLogin", "1"));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = (new PostRequest()).sendPost(userInfo.getCookie(), "https://game.fate-go.jp/login/top?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            //登录成功（及时保存存档！）
            topHome(userInfo);
        } else {
            //处理错误
        }

    }
    //登录账号第二步
    public static void topHome(UserInfo userInfo) {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", Main.appVer));
        params.add(new BasicNameValuePair("dateVer", Main.dateVer));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "723d93a599b6f10ef3085ff1131fa5679a91da924246b8ca40dded18eccaf3da"));
        params.add(new BasicNameValuePair("dataVer", Main.dataVer));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = (new PostRequest()).sendPost(userInfo.getCookie(), "https://game.fate-go.jp/home/top?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            //登录成功（及时保存存档！）
            prepare(userInfo);
        } else {
            //处理错误
        }

    }
    //生成新的引继码
    public static void prepare(UserInfo userInfo) {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("continuePass", continuePass));
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", Main.appVer));
        params.add(new BasicNameValuePair("dateVer", Main.dateVer));
        params.add(new BasicNameValuePair("dataVer", Main.dataVer));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "e92c481b51ff8203344cf768b2f5bc84b14409bc2ad7084292c9370ea97621de"));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = sendPost(userInfo.getCookie(), "https://game.fate-go.jp/continue/prepare?_userId="+userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            String continueKey = jsonObject.getJSONObject("cache").getJSONObject("updated").getJSONArray("userContinue").getJSONObject(0).getString("continueKey");
            //打印新的引继码（密码为“continuePass”的值）
            System.out.println(continueKey);
        } else {
            //处理错误
        }

    }

    public static String sendPost(BasicCookieStore cookie, String url, List<BasicNameValuePair> params) {
        CloseableHttpClient httpClient;
        httpClient = HttpClients.custom()
                    .setDefaultCookieStore((CookieStore)cookie)
                    .build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("Accept", "*/*");
        httpPost.addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 11; Pixel 5 Build/RD1A.201105.003.A1)");
        CloseableHttpResponse response = null;
        String result = null;
        try {
            httpPost.setEntity((HttpEntity)new UrlEncodedFormEntity(params, "utf-8"));
            response = httpClient.execute((HttpUriRequest)httpPost);
            result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
