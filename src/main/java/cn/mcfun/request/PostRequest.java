package cn.mcfun.request;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import static cn.mcfun.utils.Hikari.getConnection;

public class PostRequest {
    public String sendPost(BasicCookieStore cookie, String url, List<BasicNameValuePair> params) {
        CloseableHttpClient httpClient;
        String ip = null;
        try {
            Connection conn = getConnection();
            String sql = "select ip from `proxy` order by rand() LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ip = rs.getString("ip");
            }
            conn.close();
            rs.close();
            ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        HttpHost proxy;
        if(ip != null && !ip.equals("")){
            proxy = new HttpHost(ip.split(":")[0], Integer.parseInt(ip.split(":")[1]));
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(new AuthScope(proxy), new UsernamePasswordCredentials("fgo", "fgo"));
            httpClient = HttpClients.custom()
                    .setDefaultCookieStore((CookieStore)cookie)
                    .setDefaultCredentialsProvider(provider)
                    .setRoutePlanner(routePlanner)
                    .build();
        }else{
            httpClient = HttpClients.custom()
                    .setDefaultCookieStore((CookieStore)cookie)
                    .build();
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(RequestConfig.custom()
                .setConnectTimeout(20000).setConnectionRequestTimeout(20000)
                .setSocketTimeout(20000).build());
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
