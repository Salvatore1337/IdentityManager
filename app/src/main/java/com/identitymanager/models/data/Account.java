package com.identitymanager.models.data;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Account {

    private String id;
    private String docId;
    private String fkIdUser;
    private String accountName;
    private String username;
    private String email;
    private String password;
    private String passwordStrength;
    private String twoFactorAuthentication;
    private String category;
    private Timestamp lastUpdate;

    public Account() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getdocId() {
        return docId;
    }

    public void setdocId(String id) {
        this.docId = docId;
    }

    public String getFkIdUser() { return fkIdUser; }

    public void setFkIdUser(String fkIdUser) {
        this.fkIdUser = fkIdUser;
    }

    public String getAccountName() { return accountName; }

    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String Email) { this.email = Email; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getPasswordStrength() { return passwordStrength; }

    public void setPasswordStrength(String passwordStrength) { this.passwordStrength = passwordStrength; }

    public String getTwoFactorAuthentication() { return twoFactorAuthentication; }

    public void setTwoFactorAuthentication(String twoFactorAuthentication) { this.twoFactorAuthentication = twoFactorAuthentication; }

    public String getcategory() { return category; }

    public void setcategory(String category) { this.category = category; }

    public Timestamp getLastUpdate() { return this.lastUpdate; }

    public void setLastUpdate(Timestamp lastUpdate) { this.lastUpdate = lastUpdate; }
}
