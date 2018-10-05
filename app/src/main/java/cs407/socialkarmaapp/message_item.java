package cs407.socialkarmaapp;

public class message_item {
    String name, context, time;

    public message_item(String time, String name, String context) {
        this.name = name;
        this.context = context;
        this.time = time;
    }


    public String getTime() {
        return time;
    }
    public String getName() {
        return name;
    }

    public String getContext() {
        return context;
    }
}
