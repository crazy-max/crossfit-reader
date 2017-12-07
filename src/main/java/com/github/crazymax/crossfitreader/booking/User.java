package com.github.crazymax.crossfitreader.booking;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User model
 * @author CrazyMax
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

    private int id;
    private String title;
    private String firstName;
    private String lastName;
    private String nickName;
    private String address;
    private int zipCode;
    private String city;
    private String telephonNumber;
    private String cardUuid;
    private String email;
    private boolean locked;
    private boolean enabled;
    private String langKey;
    private List<String> roles;
    private List<String> subscriptions;
    private List<String> rules;
    private String login;
    private List<Booking> bookings;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTelephonNumber() {
        return telephonNumber;
    }

    public void setTelephonNumber(String telephonNumber) {
        this.telephonNumber = telephonNumber;
    }

    public String getCardUuid() {
        return cardUuid;
    }

    public void setCardUuid(String cardUuid) {
        this.cardUuid = cardUuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", title=" + title + ", firstName=" + firstName + ", lastName=" + lastName
                + ", nickName=" + nickName + ", address=" + address + ", zipCode=" + zipCode + ", city=" + city
                + ", telephonNumber=" + telephonNumber + ", cardUuid=" + cardUuid + ", email=" + email + ", locked="
                + locked + ", enabled=" + enabled + ", langKey=" + langKey + ", roles=" + roles + ", subscriptions="
                + subscriptions + ", rules=" + rules + ", login=" + login + ", bookings=" + bookings + "]";
    }
}
