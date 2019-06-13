package com.jericbarcelona.recipeapp.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RecipeDetails {

    @Id
    private Long id;

    @NotNull
    private String uuid;
    private String typeUuid;

    private String name;
    private String description;
    private String country;
    private String imageLocation;

    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    @Generated(hash = 1101001037)
    public RecipeDetails(Long id, @NotNull String uuid, String typeUuid,
            String name, String description, String country, String imageLocation,
            Date createdAt, Date updatedAt, Date deletedAt) {
        this.id = id;
        this.uuid = uuid;
        this.typeUuid = typeUuid;
        this.name = name;
        this.description = description;
        this.country = country;
        this.imageLocation = imageLocation;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
    @Generated(hash = 460060166)
    public RecipeDetails() {
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
    public String getTypeUuid() {
        return this.typeUuid;
    }
    public void setTypeUuid(String typeUuid) {
        this.typeUuid = typeUuid;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
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
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCountry() {
        return this.country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getImageLocation() {
        return this.imageLocation;
    }
    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }


}
