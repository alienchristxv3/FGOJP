package cn.mcfun.utils;

public class Order {
    private String orderId;
    private String username;
    private String password;
    private String content;
    private String current;
    private String gachaId;
    private String svtId;

    public Order() {
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCurrent() {
        return this.current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getGachaId() {
        return this.gachaId;
    }

    public void setGachaId(String gachaId) {
        this.gachaId = gachaId;
    }

    public String getSvtId() {
        return this.svtId;
    }

    public void setSvtId(String svtId) {
        this.svtId = svtId;
    }
}
