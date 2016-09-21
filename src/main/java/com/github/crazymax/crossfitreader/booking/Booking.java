package com.github.crazymax.crossfitreader.booking;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Booking model
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
@XmlRootElement(name = "booking")
@XmlAccessorType(XmlAccessType.FIELD)
public class Booking {
    
    private int id;
    private Date date;
    private float startAt;
    private String title;
    private int timeslotId;
    private int subscriptionId;
    private float createdAt;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public float getStartAt() {
        return startAt;
    }
    
    public void setStartAt(float startAt) {
        this.startAt = startAt;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public int getTimeslotId() {
        return timeslotId;
    }
    
    public void setTimeslotId(int timeslotId) {
        this.timeslotId = timeslotId;
    }
    
    public int getSubscriptionId() {
        return subscriptionId;
    }
    
    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
    
    public float getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(float createdAt) {
        this.createdAt = createdAt;
    }
}
