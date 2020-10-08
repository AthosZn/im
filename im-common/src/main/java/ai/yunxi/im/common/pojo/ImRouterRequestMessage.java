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

    public ImRouterRequestMessage(List<Long> uids, Integer type, ChatInfo data) {
        this.uids = uids;
        this.type = type;
        this.data = data;
    }

    public ImRouterRequestMessage(Long uid, Integer type, ChatInfo data) {
        this.uids.add(uid);
        this.type = type;
        this.data = data;
    }

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
    private int type = MessageConstant.CHAT;

    /**
     * 消息数据
     */
    private ChatInfo data;

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
    private long expiredObjectime;

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

    public Integer getObjectype() {
        return type;
    }

    public void setObjectype(Integer type) {
        this.type = type;
    }

    public ChatInfo getData() {
        return data;
    }

    public void setData(ChatInfo data) {
        this.data = data;
    }

    public long getObjectimestamp() {
        return timestamp;
    }

    public void setObjectimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isResend() {
        return resend;
    }

    public void setResend(boolean resend) {
        this.resend = resend;
    }

    public long getExpiredObjectime() {
        return expiredObjectime;
    }

    public void setExpiredObjectime(long expiredObjectime) {
        this.expiredObjectime = expiredObjectime;
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
                ", expiredObjectime=" + expiredObjectime +
                '}';
    }
}
