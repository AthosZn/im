package ai.yunxi.im.common.pojo;
/**
 * @author Athos
 * @createTime 2019年2月27日 下午3:29:38
 */

import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable {

    private static final long serialVersionUID = -4454737765850239378L;

    private Long id;

    private String account;

    /**
     * 头像
     */
    private String avatar;

    private String email;  // 邮箱

    private String nickname;

    private String mobilePhoneNumber;

    private Date createDate;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", mobilePhoneNumber='" + mobilePhoneNumber + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
