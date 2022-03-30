package cn.mcfun.api;

import cn.mcfun.Main;
import cn.mcfun.entity.UserInfo;
import cn.mcfun.name.SellSvtName;
import cn.mcfun.name.SvtName;
import cn.mcfun.request.PostRequest;
import cn.mcfun.utils.AuthCode;
import cn.mcfun.utils.Hikari;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import org.apache.http.message.BasicNameValuePair;

public class JPLogin {
    public JPLogin() {
    }

    public void topLogin(UserInfo userInfo) throws SQLException {
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
            JSONArray userQuest = jsonObject.getJSONObject("cache").getJSONObject("replaced").getJSONArray("userQuest");
            Map<String, String> quest = new HashMap();
            if (userQuest != null) {
                for(int i = 0; i < userQuest.size(); ++i) {
                    quest.put(userQuest.getJSONObject(i).getString("questId"), userQuest.getJSONObject(i).getString("questPhase"));
                }
            }


            JSONArray userShop;
            if (jsonObject.getJSONObject("cache").getJSONObject("replaced").getString("userItem") != null) {
                userShop = jsonObject.getJSONObject("cache").getJSONObject("replaced").getJSONArray("userItem");

                for(int i = 0; i < userShop.size(); ++i) {
                    quest.put("w" + userShop.getJSONObject(i).getString("itemId"), userShop.getJSONObject(i).getString("num"));
                }
            }

            userShop = jsonObject.getJSONObject("cache").getJSONObject("updated").getJSONArray("userShop");
            if (userShop != null) {
                for(int i = 0; i < userShop.size(); ++i) {
                    quest.put(userShop.getJSONObject(i).getString("shopId"), userShop.getJSONObject(i).getString("num"));
                }
            }

            String stone = "0";
            String userId = "0";
            if (jsonObject.getJSONObject("cache").getJSONObject("replaced").getJSONArray("userGame") != null) {
                stone = jsonObject.getJSONObject("cache").getJSONObject("replaced").getJSONArray("userGame").getJSONObject(0).getString("stone");
                userId = jsonObject.getJSONObject("cache").getJSONObject("replaced").getJSONArray("userGame").getJSONObject(0).getString("userId");
            }

            quest.put("stone", stone);
            quest.put("userId", userId);
            String qp = "0";
            if (jsonObject.getJSONObject("cache").getJSONObject("replaced").getJSONArray("userGame") != null) {
                qp = jsonObject.getJSONObject("cache").getJSONObject("replaced").getJSONArray("userGame").getJSONObject(0).getString("qp");
            }
            quest.put("qp", qp);

            JSONArray userDeck = jsonObject.getJSONObject("cache").getJSONObject("replaced").getJSONArray("userDeck");
            String activeDeckId = jsonObject.getJSONObject("cache").getJSONObject("replaced").getJSONArray("userGame").getJSONObject(0).getString("activeDeckId");
            String userEquipId = jsonObject.getJSONObject("cache").getJSONObject("replaced").getJSONArray("userGame").getJSONObject(0).getString("userEquipId");
            userInfo.setActiveDeckId(activeDeckId);
            userInfo.setUserEquipId(userEquipId);
            JSONObject activeuserDeck = null;

            for(int i = 0; i < userDeck.size(); ++i) {
                if (userDeck.getJSONObject(i).getString("id").equals(activeDeckId)) {
                    activeuserDeck = userDeck.getJSONObject(i);
                }
            }
            activeuserDeck.replace("userId", "0");
            activeuserDeck.remove("updatedAt");
            activeuserDeck.remove("createdAt");
            Map<String, String> userDecksvt = new HashMap();
            if (userDeck != null) {
                for(int i = 0; i < userDeck.size(); ++i) {
                    for(int j = 0; j < userDeck.getJSONObject(i).getJSONObject("deckInfo").getJSONArray("svts").size(); ++j) {
                        userDecksvt.put(userDeck.getJSONObject(i).getJSONObject("deckInfo").getJSONArray("svts").getJSONObject(j).getString("userSvtId"), "");
                    }
                }
            }
            JSONArray userSvt = jsonObject.getJSONObject("cache").getJSONObject("updated").getJSONArray("userSvt");
            Map<String, Integer> svt = new HashMap();
            SellSvtName sell = new SellSvtName();
            JSONArray sellData = new JSONArray();
            if (userSvt != null) {
                for(int i = 0; i < userSvt.size(); ++i) {
                    if (!userSvt.getJSONObject(i).getString("status").equals("6") && !userSvt.getJSONObject(i).getString("status").equals("7")) {
                        if (sell.getSellSvtName(userSvt.getJSONObject(i).getString("svtId")).equals("1") && userSvt.getJSONObject(i).getString("status").equals("0") && !userDecksvt.containsKey(userSvt.getJSONObject(i).getString("id"))) {
                            sellData.add(JSONObject.parse("{\"id\":" + userSvt.getJSONObject(i).getString("id") + ",\"num\":1}"));
                        }

                        if (userSvt.getJSONObject(i).getString("svtId").length() == 6 || userSvt.getJSONObject(i).getString("svtId").length() == 7 && userSvt.getJSONObject(i).getString("svtId").startsWith("10") || userSvt.getJSONObject(i).getString("svtId").length() == 7 && userSvt.getJSONObject(i).getString("svtId").startsWith("11") || userSvt.getJSONObject(i).getString("svtId").length() == 7 && userSvt.getJSONObject(i).getString("svtId").startsWith("23") || userSvt.getJSONObject(i).getString("svtId").length() == 7 && userSvt.getJSONObject(i).getString("svtId").startsWith("25")) {
                            if (svt.containsKey(userSvt.getJSONObject(i).getString("svtId")) || !sell.getSellSvtName(userSvt.getJSONObject(i).getString("svtId")).equals("0") && !userSvt.getJSONObject(i).getString("status").equals("1") && !userDecksvt.containsKey(userSvt.getJSONObject(i).getString("id"))) {
                                if (sell.getSellSvtName(userSvt.getJSONObject(i).getString("svtId")).equals("0") || userSvt.getJSONObject(i).getString("status").equals("1") || userDecksvt.containsKey(userSvt.getJSONObject(i).getString("id"))) {
                                    svt.put(userSvt.getJSONObject(i).getString("svtId"), (Integer)svt.get(userSvt.getJSONObject(i).getString("svtId")) + 1);
                                }
                            } else {
                                svt.put(userSvt.getJSONObject(i).getString("svtId"), 1);
                                if (userSvt.getJSONObject(i).getString("svtId").equals("800100")) {
                                    quest.put("svt1", userSvt.getJSONObject(i).getString("id"));
                                } else if (!quest.containsKey("svt2")) {
                                    quest.put("svt2", userSvt.getJSONObject(i).getString("id"));
                                }
                            }
                        }
                    }
                }
            }

            Set set = svt.keySet();
            String svtId = null;
            SvtName svt2 = new SvtName();
            Iterator iterator = set.iterator();

            String sql2;
            while(iterator.hasNext()) {
                sql2 = (String)iterator.next();
                Object value = svt.get(sql2);
                if (svtId == null) {
                    svtId = svt2.getSvtName(sql2) + "x" + value;
                } else {
                    svtId = svtId + "," + svt2.getSvtName(sql2) + "x" + value;
                }
            }

            quest.put("sellData", sellData.toJSONString());
            userInfo.setQuest(quest);

            try {
                Thread.currentThread();
                Thread.sleep(2000L);
            } catch (InterruptedException var30) {
                var30.printStackTrace();
            }
            String continueKey = jsonObject.getJSONObject("cache").getJSONObject("updated").getJSONArray("userContinue").getJSONObject(0).getString("continueKey");
            Connection conn2 = Hikari.getConnection();
            sql2 = "update `order` set user=?,svts=?,message='登录成功' where `order`=? and status='执行中'";
            PreparedStatement ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, continueKey);
            ps2.setString(2, svtId);
            ps2.setString(3, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();
            this.topHome(userInfo);
        } else {
            Connection conn2 = Hikari.getConnection();
            String sql2 = "update `order` set message=?,status='订单错误' where `order`=? and status='执行中'";
            PreparedStatement ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail"));
            ps2.setString(2, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();
            Thread.currentThread().stop();
        }

    }

    public void topHome(UserInfo userInfo) throws SQLException {
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
        Connection conn2;
        String sql2;
        PreparedStatement ps2;
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            conn2 = Hikari.getConnection();
            sql2 = "update `order` set message='数据初始化完成' where `order`=? and status='执行中'";
            ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();
        } else {
            conn2 = Hikari.getConnection();
            sql2 = "update `order` set message=?,status='订单错误' where `order`=? and status='执行中'";
            ps2 = conn2.prepareStatement(sql2);
            ps2.setString(1, jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail"));
            ps2.setString(2, userInfo.getOrder());
            ps2.executeUpdate();
            conn2.close();
            ps2.close();
            Thread.currentThread().stop();
        }

    }
}
