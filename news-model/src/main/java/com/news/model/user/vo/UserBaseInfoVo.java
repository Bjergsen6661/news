package com.news.model.user.vo;


/**
 * @author Bjergsen  Email:915681203@qq.com
 * @Description 封装用户基本信息给前端
 * @create 2022-06-17-23:13
 */
public class UserBaseInfoVo {

    private String id;
    private String nickname;
    private String face;
    private Integer activeStatus; //激活状态

    private Integer myFansCounts;       // 我的粉丝数
    private Integer myFollowCounts;     // 我关注的人数

    private boolean isFault; //是否进过降级


    public UserBaseInfoVo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public Integer getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Integer activeStatus) {
        this.activeStatus = activeStatus;
    }

    public Integer getMyFansCounts() {
        return myFansCounts;
    }

    public void setMyFansCounts(Integer myFansCounts) {
        this.myFansCounts = myFansCounts;
    }

    public Integer getMyFollowCounts() {
        return myFollowCounts;
    }

    public void setMyFollowCounts(Integer myFollowCounts) {
        this.myFollowCounts = myFollowCounts;
    }

    public boolean isFault() {
        return isFault;
    }

    public void setFault(boolean fault) {
        isFault = fault;
    }
}
