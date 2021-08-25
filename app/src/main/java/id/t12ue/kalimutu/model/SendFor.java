package id.t12ue.kalimutu.model;

public class SendFor {
    long userId;
    String token;

    public SendFor(long userId, String token){
        this.userId=userId;
        this.token=token;
    }

    public long getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}
