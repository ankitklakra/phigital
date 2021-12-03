package com.phigital.ai.Model;


public class ModelChat  {

     String sender,pId,receiver,msg,type,uri,timestamp;
     boolean hide,isSeen;

    public ModelChat(String sender,String pId,String timestamp, String receiver, String msg, String type, boolean hide,String uri, boolean isSeen) {
        this.sender = sender;
        this.pId = pId;
        this.timestamp = timestamp;
        this.receiver = receiver;
        this.msg = msg;
        this.uri = uri;
        this.type = type;
        this.hide = hide;
        this.isSeen = isSeen;
    }

    public ModelChat() {
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean getHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }


    public boolean isIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }
}
