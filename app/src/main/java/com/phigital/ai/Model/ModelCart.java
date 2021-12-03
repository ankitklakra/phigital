package com.phigital.ai.Model;


public class ModelCart {
    String id,category,productname,brandname,mrp,discount,price,saveamount,address,detail,pTime,available,quantity,pId,finalprice,finalmrp,finalsave,size;

    public ModelCart() {
    }

    public ModelCart(String id, String category, String productname, String brandname, String mrp, String discount, String price, String saveamount, String address, String detail, String pTime, String available, String quantity, String pId, String finalprice,String size) {
        this.id = id;
        this.category = category;
        this.productname = productname;
        this.brandname = brandname;
        this.mrp = mrp;
        this.discount = discount;
        this.price = price;
        this.saveamount = saveamount;
        this.address = address;
        this.detail = detail;
        this.pTime = pTime;
        this.available = available;
        this.quantity = quantity;
        this.pId = pId;
        this.finalprice = finalprice;
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSaveamount() {
        return saveamount;
    }

    public void setSaveamount(String saveamount) {
        this.saveamount = saveamount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getFinalprice() {
        return finalprice;
    }

    public void setFinalprice(String finalprice) {
        this.finalprice = finalprice;
    }

    public String getFinalmrp() {
        return finalmrp;
    }

    public void setFinalmrp(String finalmrp) {
        this.finalmrp = finalmrp;
    }

    public String getFinalsave() {
        return finalsave;
    }

    public void setFinalsave(String finalsave) {
        this.finalsave = finalsave;
    }
}
