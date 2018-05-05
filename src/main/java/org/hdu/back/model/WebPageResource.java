package org.hdu.back.model;

import java.util.Date;

public class WebPageResource {
    private Long id;

    private Long urlDetailId;

    private String url;

    private String resourceUrl;

    private Short resourceType;

    private Date crawlTime;

    private Date updateTime;

    public WebPageResource() {
    }

    public WebPageResource(Long urlDetailId, String url, String resourceUrl, Short resourceType, Date crawlTime) {
        this.urlDetailId = urlDetailId;
        this.url = url;
        this.resourceUrl = resourceUrl;
        this.resourceType = resourceType;
        this.crawlTime = crawlTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUrlDetailId() {
        return urlDetailId;
    }

    public void setUrlDetailId(Long urlDetailId) {
        this.urlDetailId = urlDetailId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl == null ? null : resourceUrl.trim();
    }

    public Short getResourceType() {
        return resourceType;
    }

    public void setResourceType(Short resourceType) {
        this.resourceType = resourceType;
    }

    public Date getCrawlTime() {
        return crawlTime;
    }

    public void setCrawlTime(Date crawlTime) {
        this.crawlTime = crawlTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}