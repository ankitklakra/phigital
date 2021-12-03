package com.phigital.ai.Notifications;

public class Sender2 {
    private Data2 data;
    private String to;

    public Sender2() {
    }

    public Sender2(Data2 data, String to) {
        this.data = data;
        this.to = to;
    }

    public Data2 getData2() {
        return data;
    }

    public void setData2(Data2 data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
