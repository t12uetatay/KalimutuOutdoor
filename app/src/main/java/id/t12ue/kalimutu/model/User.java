package id.t12ue.kalimutu.model;

public class User {
    String id;
    String name;
    String role;

    public User(String id, String name, String role){
        this.id=id;
        this.name=name;
        this.role=role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
