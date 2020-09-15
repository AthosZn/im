package ai.yunxi.im.common.pojo;

import ai.yunxi.im.common.constant.MessageConstant;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * IM请求消息
 */
public class ImRouterRequestMessage implements Serializable {

    private static final long serialVersionUID = 4836768833746970531L;

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    /**
     * 消息id
     */
    private String messageId;
    /**
     * 目标用户列表
     */
    private List<Long> uids = new ArrayList<Long>();

    /**
     * 消息类型，
     */
    private String type = MessageConstant.CHAT;

    /**
     * 消息数据
     */
    private String data = "{}";

    /**
     * 时间戳
     */
    private long timestamp = 0L;

    /**
     * 是否需要重发
     */
    private boolean resend = false;

    /**
     * 重发超时时间(毫秒数)
     * 超时不重发 毫秒数  10分钟后不重发即 10 * 60 * 1000l
     * 如果resend = false 即不用设置当前值
     */
    private long expiredTime;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public List<Long> getUids() {
        return uids;
    }

    public void setUids(List<Long> uids) {
        this.uids = uids;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isResend() {
        return resend;
    }

    public void setResend(boolean resend) {
        this.resend = resend;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }

    @Override
    public String toString() {
        return "ImRouterRequestMessage{" +
                "messageId='" + messageId + '\'' +
                ", uids=" + uids +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", timestamp=" + timestamp +
                ", resend=" + resend +
                ", expiredTime=" + expiredTime +
                '}';
    }
}
