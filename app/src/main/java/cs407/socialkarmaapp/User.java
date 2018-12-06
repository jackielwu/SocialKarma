package cs407.socialkarmaapp;

import java.io.Serializable;
import java.util.Map;

public class User implements Serializable {
    public String username;
    public int karma;
    public String uid;
    public Map<String, String> chatMembers;

    public User() {

    }
}
