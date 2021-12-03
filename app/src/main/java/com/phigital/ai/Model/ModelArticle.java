package com.phigital.ai.Model;


public class ModelArticle {
    String id,pId,text,pViews,type,image,video,category,title,pTime;

    public ModelArticle() {
    }

    public ModelArticle(String id, String pId, String text, String pViews, String type, String image, String video, String category, String title, String pTime) {
        this.id = id;
        this.pId = pId;
        this.text = text;
        this.pViews = pViews;
        this.type = type;
        this.image = image;
        this.video = video;
        this.category = category;
        this.title = title;
        this.pTime = pTime;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }
}
