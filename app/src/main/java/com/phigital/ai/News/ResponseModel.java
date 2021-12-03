package com.phigital.ai.News;

import com.google.gson.annotations.SerializedName;

import java.util.List;
public class ResponseModel {
    @SerializedName("status")
    private String status;
    @SerializedName("totalResults")
    private int totalResults;
    @SerializedName("articles")
    private List<ArticleModel> articles = null;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getTotalResults() {
        return totalResults;
    }
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
    public List<ArticleModel> getArticles() {
        return articles;
    }
    public void setArticles(List<ArticleModel> articles) {
        this.articles = articles;
    }

}
