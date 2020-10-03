package ai.yunxi.im.route.service.impl;

import java.io.IOException;
import java.util.List;

import ai.yunxi.im.route.zk.ZKUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import ai.yunxi.im.common.pojo.ImMessage;
import ai.yunxi.im.route.service.RouteService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Athos
 * @createTime 2019年3月7日 下午8:40:34
 */
@Service
public class RouteServiceImpl implements RouteService {

    private MediaType mediaType = MediaType.parse("application/json");
    @Autowired
    private OkHttpClient okHttpClient;
    @Autowired
    private ZKUtil zk;

    @Override
    public void sendMessage(String url, ImMessage chat) throws IOException {
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("command",chat.getType());
//		jsonObject.put("time",chat.getCurrentTime());
//		jsonObject.put("userId",chat.getUserId());
//		jsonObject.put("content",chat.getContent());
        String json = JSONObject.toJSONString(chat);
//		RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());
        RequestBody requestBody = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
    }

    @Override
    public void zkSend(ImMessage chat) {
        try {
            //从ZK拿到所有节点，分发消息
            List<String> all = zk.getAllNode();
            for (String server : all) {
                String[] serv = server.split("-");
                String ip = serv[0];
                int httpPort = Integer.parseInt(serv[2]);
                String url = "http://" + ip + ":" + httpPort + "/pushMessage";
                sendMessage(url, chat);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
