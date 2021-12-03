package com.phigital.ai.Model;


public class ModelPost {
    String id,pId,text,pViews,rViews,type,video,image,reTweet,reId,privacy,pTime,location,content,link,dp,name;

    public ModelPost() {
    }

    public ModelPost(String id, String pId, String text, String pViews, String rViews, String type, String video, String image, String reTweet, String reId, String privacy, String pTime, String location, String content, String link, String dp, String name) {
        this.id = id;
        this.pId = pId;
        this.text = text;
        this.pViews = pViews;
        this.rViews = rViews;
        this.type = type;
        this.video = video;
        this.image = image;
        this.reTweet = reTweet;
        this.reId = reId;
        this.privacy = privacy;
        this.pTime = pTime;
        this.location = location;
        this.content = content;
        this.link = link;
        this.dp = dp;
        this.name = name;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getpViews() {
        return pViews;
    }

    public void setpViews(String pViews) {
        this.pViews = pViews;
    }

    public String getrViews() {
        return rViews;
    }

    public void setrViews(String rViews) {
        this.rViews = rViews;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReTweet() {
        return reTweet;
    }

    public void setReTweet(String reTweet) {
        this.reTweet = reTweet;
    }

    public String getReId() {
        return reId;
    }

    public void setReId(String reId) {
        this.reId = reId;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


}
