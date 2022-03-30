package cn.mcfun.entity;

import com.alibaba.fastjson.JSONObject;
import java.util.Map;
import org.apache.http.impl.client.BasicCookieStore;

public class UserInfo {
    BasicCookieStore cookie;
    String userId;
    String authKey;
    String secretKey;
    String activeDeckId;
    String userEquipId;
    String data;
    String questId;
    String questPhase;
    String type;
    String recover;
    String order;
    JSONObject activeuserDeck;
    Map<String, String> quest;

    public UserInfo() {
    }

    public String getRecover() {
        return this.recover;
    }

    public void setRecover(String recover) {
        this.recover = recover;
    }

    public String getOrder() {
        return this.order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getQuestId() {
        return this.questId;
    }

    public void setQuestId(String questId) {
        this.questId = questId;
    }

    public String getQuestPhase() {
        return this.questPhase;
    }

    public void setQuestPhase(String questPhase) {
        this.questPhase = questPhase;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getActiveDeckId() {
        return this.activeDeckId;
    }

    public void setActiveDeckId(String activeDeckId) {
        this.activeDeckId = activeDeckId;
    }

    public String getUserEquipId() {
        return this.userEquipId;
    }

    public void setUserEquipId(String userEquipId) {
        this.userEquipId = userEquipId;
    }

    public JSONObject getActiveuserDeck() {
        return this.activeuserDeck;
    }

    public void setActiveuserDeck(JSONObject activeuserDeck) {
        this.activeuserDeck = activeuserDeck;
    }

    public Map<String, String> getQuest() {
        return this.quest;
    }

    public void setQuest(Map<String, String> quest) {
        this.quest = quest;
    }

    public BasicCookieStore getCookie() {
        return this.cookie;
    }

    public void setCookie(BasicCookieStore cookie) {
        this.cookie = cookie;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthKey() {
        return this.authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
