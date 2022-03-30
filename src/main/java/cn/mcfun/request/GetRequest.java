package cn.mcfun.request;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class GetRequest {
    public GetRequest() {
    }

    public String sendGet(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        String result = null;

        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        return result;
    }
}
