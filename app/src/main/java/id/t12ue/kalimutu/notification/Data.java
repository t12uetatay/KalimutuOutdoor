package id.t12ue.kalimutu.notification;

public class Data {
    private String Id;
    private String Title;
    private String Message;

    public Data(String id, String title, String message) {
        Id=id;
        Title = title;
        Message = message;
    }

    public Data() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

}
