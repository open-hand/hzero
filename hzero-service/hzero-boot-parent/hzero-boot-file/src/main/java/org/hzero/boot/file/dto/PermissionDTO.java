package org.hzero.boot.file.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;

/**
 * 权限VO
 *
 * @author xianzhi.chen@hand-china.com 2019年4月29日下午4:45:44
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("onlyOffice编辑权限")
public class PermissionDTO {

    /**
     * 编辑权限
     */
    private Boolean edit;
    /**
     * 审阅权限
     */
    private Boolean review;
    /**
     * 处理审阅记录权限
     */
    private Boolean dealWithReview;
    /**
     * 只处理审阅记录
     */
    private Boolean dealWithReviewOnly;
    /**
     * 切换审阅功能的权限
     */
    private Boolean changeReview;
    /**
     * 下载权限
     */
    private Boolean download;
    /**
     * 打印权限
     */
    private Boolean print;
    /**
     * 评论权限
     */
    private Boolean comment;

    public Boolean getEdit() {
        return edit;
    }

    public PermissionDTO setEdit(Boolean edit) {
        this.edit = edit;
        return this;
    }

    public Boolean getReview() {
        return review;
    }

    public PermissionDTO setReview(Boolean review) {
        this.review = review;
        return this;
    }

    public Boolean getDealWithReview() {
        return dealWithReview;
    }

    public PermissionDTO setDealWithReview(Boolean dealWithReview) {
        this.dealWithReview = dealWithReview;
        return this;
    }

    public Boolean getDealWithReviewOnly() {
        return dealWithReviewOnly;
    }

    public PermissionDTO setDealWithReviewOnly(Boolean dealWithReviewOnly) {
        this.dealWithReviewOnly = dealWithReviewOnly;
        return this;
    }

    public Boolean getChangeReview() {
        return changeReview;
    }

    public PermissionDTO setChangeReview(Boolean changeReview) {
        this.changeReview = changeReview;
        return this;
    }

    public Boolean getDownload() {
        return download;
    }

    public PermissionDTO setDownload(Boolean download) {
        this.download = download;
        return this;
    }

    public Boolean getPrint() {
        return print;
    }

    public PermissionDTO setPrint(Boolean print) {
        this.print = print;
        return this;
    }

    public Boolean getComment() {
        return comment;
    }

    public PermissionDTO setComment(Boolean comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "edit=" + edit +
                ", review=" + review +
                ", dealWithReview=" + dealWithReview +
                ", dealWithReviewOnly=" + dealWithReviewOnly +
                ", changeReview=" + changeReview +
                ", download=" + download +
                ", print=" + print +
                ", comment=" + comment +
                '}';
    }
}
