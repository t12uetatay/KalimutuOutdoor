package id.t12ue.kalimutu.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class UserEntity {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "userId")
    long userId;
    @ColumnInfo(name = "userName")
    String userName;
    @ColumnInfo(name = "email")
    String email;
    @ColumnInfo(name = "phone")
    String phone;
    @ColumnInfo(name = "token")
    String token;
    @ColumnInfo(name = "role")
    String role;

    public UserEntity(long userId, String userName, String email, String phone, String token, String role){
        this.userId=userId;
        this.userName=userName;
        this.email=email;
        this.phone=phone;
        this.token=token;
        this.role=role;
    }

    public UserEntity(){}

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
