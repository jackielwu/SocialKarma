package cs407.socialkarmaapp;

public class meetup_item{
    String name,title, context;

    public meetup_item(String title, String name, String context) {
        this.name = name;
        this.context = context;
        this.title = title;
    }

   public String getTitle() {
        return title;
   }

    public String getName() {
        return name;
    }

    public String getContext() {
        return context;
    }


}
