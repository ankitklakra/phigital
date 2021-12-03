package com.phigital.ai.Model;

public class ModelUser {
    String  name, email, username, bio, location, link, photo, phone, id,status,typingTo,city,birthdate,job,verified,
            messageseen,education,gender,privacy,language,notify,relation,password,talent,likes,hometown,joined,bloodgroup,
            fambers,notificationseen;


    boolean isBlocked = false;

    public ModelUser() {
    }

    public ModelUser(String name, String email, String username, String bio, String location, String link, String photo, String phone, String id, String status, String typingTo, String city, String birthdate, String job, String verified, String messageseen, String education, String gender, String privacy, String language, String notify, String relation, String password, String talent, String likes, String hometown, String joined, String bloodgroup, String fambers, String notificationseen, boolean isBlocked) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.bio = bio;
        this.location = location;
        this.link = link;
        this.photo = photo;
        this.phone = phone;
        this.id = id;
        this.status = status;
        this.typingTo = typingTo;
        this.city = city;
        this.birthdate = birthdate;
        this.job = job;
        this.verified = verified;
        this.messageseen = messageseen;
        this.education = education;
        this.gender = gender;
        this.privacy = privacy;
        this.language = language;
        this.notify = notify;
        this.relation = relation;
        this.password = password;
        this.talent = talent;
        this.likes = likes;
        this.hometown = hometown;
        this.joined = joined;
        this.bloodgroup = bloodgroup;
        this.fambers = fambers;
        this.notificationseen = notificationseen;
        this.isBlocked = isBlocked;
    }

    public String getMessageseen() {
        return messageseen;
    }

    public void setMessageseen(String messageseen) {
        this.messageseen = messageseen;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTalent() {
        return talent;
    }

    public void setTalent(String talent) {
        this.talent = talent;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public String getFambers() {
        return fambers;
    }

    public void setFambers(String fambers) {
        this.fambers = fambers;
    }

    public String getNotificationseen() {
        return notificationseen;
    }

    public void setNotificationseen(String notificationseen) {
        this.notificationseen = notificationseen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
