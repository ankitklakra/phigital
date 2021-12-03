package com.phigital.ai.Model;


public class ModelCustomerOrder {
    String id,pId,invoicedate,orderdate;

    public ModelCustomerOrder() {
    }

    public ModelCustomerOrder(String id, String pId, String invoicedate, String orderdate) {
        this.id = id;
        this.pId = pId;
        this.invoicedate = invoicedate;
        this.orderdate = orderdate;
    }

    public String getInvoicedate() {
        return invoicedate;
    }

    public void setInvoicedate(String invoicedate) {
        this.invoicedate = invoicedate;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
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

}
