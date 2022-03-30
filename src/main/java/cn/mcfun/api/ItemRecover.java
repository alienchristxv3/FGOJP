package cn.mcfun.api;

import cn.mcfun.Main;
import cn.mcfun.entity.UserInfo;
import cn.mcfun.request.PostRequest;
import cn.mcfun.utils.AuthCode;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.message.BasicNameValuePair;

public class ItemRecover {
    public ItemRecover() {
    }

    public void recover(UserInfo userInfo) {
        if (userInfo.getRecover().equals("0")) {
            Thread.currentThread().stop();
        } else if (userInfo.getRecover().equals("1")) {
            this.shopPurchaseByStone(userInfo);
        } else {
            this.itemRecover(userInfo);
        }

    }

    public void itemRecover(UserInfo userInfo) {
        String recoverId = userInfo.getRecover();
        String num = "1";
        if (recoverId.equals("4")) {
            num = "4";
        }

        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("recoverId", recoverId));
        params.add(new BasicNameValuePair("num", num));
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
        String result = (new PostRequest()).sendPost(userInfo.getCookie(), "https://game.fate-go.jp/item/recover?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (!jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00") && (!jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("99") || !jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail").equals("道具不足，无法使用。"))) {
            String message = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
            if (message.equals("AP已完全恢复。")) {
            }
        }

    }

    public void shopPurchaseByStone(UserInfo userInfo) {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("id", "2"));
        params.add(new BasicNameValuePair("num", "1"));
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
        String result = (new PostRequest()).sendPost(userInfo.getCookie(), "https://game.fate-go.jp/shop/purchasebystone?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (!jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00") && (!jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("99") || !jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail").equals("道具不足，无法使用。"))) {
            String message = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
            if (message.equals("AP已完全恢复。")) {
            }
        }

    }
}
