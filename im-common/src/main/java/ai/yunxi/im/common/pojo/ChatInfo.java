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


}
