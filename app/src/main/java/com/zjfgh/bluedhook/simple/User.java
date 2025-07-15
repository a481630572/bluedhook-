package com.zjfgh.bluedhook.simple;

public class User {
    private int id;
    private String name;
    private String avatar;
    private String live;
    private String uid;
    private String union_uid;
    private String enc_uid;
    private boolean strongRemind;
    private boolean voiceReminded;
    private boolean joinedLive;
    private boolean avatarDownloaded;
    private boolean voiceRemind;
    private boolean joinLive;

    // 构造函数
    public User() {}

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLive() {
        return live;
    }

    public void setLive(String live) {
        this.live = live;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUnion_uid() {
        return union_uid;
    }

    public void setUnion_uid(String union_uid) {
        this.union_uid = union_uid;
    }

    public String getEnc_uid() {
        return enc_uid;
    }

    public void setEnc_uid(String enc_uid) {
        this.enc_uid = enc_uid;
    }

    public boolean isStrongRemind() {
        return strongRemind;
    }

    public void setStrongRemind(boolean strongRemind) {
        this.strongRemind = strongRemind;
    }

    public boolean isVoiceReminded() {
        return voiceReminded;
    }

    public void setVoiceReminded(boolean voiceReminded) {
        this.voiceReminded = voiceReminded;
    }

    public boolean isJoinedLive() {
        return joinedLive;
    }

    public void setJoinedLive(boolean joinedLive) {
        this.joinedLive = joinedLive;
    }

    public boolean isAvatarDownloaded() {
        return avatarDownloaded;
    }

    public void setAvatarDownloaded(boolean avatarDownloaded) {
        this.avatarDownloaded = avatarDownloaded;
    }

    public boolean isVoiceRemind() {
        return voiceRemind;
    }

    public void setVoiceRemind(boolean voiceRemind) {
        this.voiceRemind = voiceRemind;
    }

    public boolean isJoinLive() {
        return joinLive;
    }

    public void setJoinLive(boolean joinLive) {
        this.joinLive = joinLive;
    }

    public boolean isAvatarDownload() {
        return avatarDownloaded;
    }

    public void setAvatarDownload(boolean avatarDownload) {
        this.avatarDownloaded = avatarDownload;
    }
}
