package com.phigital.ai.Model;


public class ModelCalllist {

    String id,name,photo,pId,phone,userid;

    public ModelCalllist(String id, String name, String photo, String pId,String phone,String userid) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.pId = pId;
        this.phone = phone;
        this.userid = userid;
    }

    public ModelCalllist() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
