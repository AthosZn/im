package ai.yunxi.im.common.pojo;

import java.io.Serializable;

/**
 * @author zz
 * 2020/6/10
 */
public class ImMessage implements Serializable {

    private static final long serialVersionUID = 1360647504610967672L;
    private Integer command;
    private Long time;
    private Integer userId;
    private String content;

    public ImMessage(Integer command, Long time, Integer userId, String content) {
        this.command = command;
        this.time = time;
        this.userId = userId;
        this.content = content;
    }
    public ImMessage() {
    }
    public Integer getCommand() {
        return command;
    }
    public void setCommand(Integer command) {
        this.command = command;
    }
    public Long getTime() {
        return time;
    }
    public void setTime(Long time) {
        this.time = time;
    }
    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }


}
