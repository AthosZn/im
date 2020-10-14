package ai.yunxi.im.common.pojo;

import java.io.Serializable;

/**
 * @author zz
 * 2020/6/10
 */
public class ChatInfo implements Serializable {

    private static final long serialVersionUID = -8953937130022071877L;

    //聊天内容
    private String content;

    //消息发送者
    private long fromUid;

    //消息发送者头像
    private String fromAvatar;

    //消息发送者昵称
    private String fromNickName;

    //消息接收者
    private long toId;

    //消息类型
    private int command;

    //是否原图
    private int isOrigin;

    //当前时间
    private long currentTime;

    //是否发送apns
    private boolean sendApns = true;

    public ChatInfo() {
    }

    public ChatInfo( int command, long currentTime, long toId,String content) {
        this.content = content;
        this.toId = toId;
        this.command = command;
        this.currentTime = currentTime;
    }

    public ChatInfo(long fromUid, int command, long currentTime, long toId,String content) {
        this.fromUid=fromUid;
        this.content = content;
        this.toId = toId;
        this.command = command;
        this.currentTime = currentTime;
    }

    public String getContent() {
        return content==null?"":content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getFromUid() {
        return fromUid;
    }

    public void setFromUid(int fromUid) {
        this.fromUid = fromUid;
    }

    public String getFromAvatar() {
        return fromAvatar;
    }

    public void setFromAvatar(String fromAvatar) {
        this.fromAvatar = fromAvatar;
    }

    public String getFromNickName() {
        return fromNickName;
    }

    public void setFromNickName(String fromNickName) {
        this.fromNickName = fromNickName;
    }

    public long getToId() {
        return toId;
    }

    public void setToId(long toId) {
        this.toId = toId;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public int getIsOrigin() {
        return isOrigin;
    }

    public void setIsOrigin(int isOrigin) {
        this.isOrigin = isOrigin;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public boolean isSendApns() {
        return sendApns;
    }

    public void setSendApns(boolean sendApns) {
        this.sendApns = sendApns;
    }

    @Override
    public String toString() {
        return "ChatInfo{" +
                "content='" + content + '\'' +
                ", fromUid=" + fromUid +
                ", fromAvatar='" + fromAvatar + '\'' +
                ", fromNickName='" + fromNickName + '\'' +
                ", toId=" + toId +
                ", command=" + command +
                ", isOrigin=" + isOrigin +
                ", currentTime=" + currentTime +
                ", sendApns=" + sendApns +
                '}';
    }
}
