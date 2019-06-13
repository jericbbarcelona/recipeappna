package com.jericbarcelona.recipeapp.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RecipeIngredients {

    @Id
    private Long id;

    @NotNull
    private String uuid;
    private String detailsUuid;

    private String value;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    @Generated(hash = 1255016904)
    public RecipeIngredients(Long id, @NotNull String uuid, String detailsUuid,
            String value, Date createdAt, Date updatedAt, Date deletedAt) {
        this.id = id;
        this.uuid = uuid;
        this.detailsUuid = detailsUuid;
        this.value = value;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
    @Generated(hash = 333700824)
    public RecipeIngredients() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUuid() {
        return this.uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public String getDetailsUuid() {
        return this.detailsUuid;
    }
    public void setDetailsUuid(String detailsUuid) {
        this.detailsUuid = detailsUuid;
    }
    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public Date getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Date getDeletedAt() {
        return this.deletedAt;
    }
    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }


}
