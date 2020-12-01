package com.example.drifting.ui.login;

public class ExampleItem {


    final private int friend_image;
    final private String friend_name;
    final private String recent_text;
    final private String recent_text_sent_time; // TODO: backend might need to check

    public ExampleItem(int image_src, String name, String text, String time) {
        friend_image = image_src;
        friend_name = name;
        recent_text = text;
        recent_text_sent_time = time;
    }

    public int getFriendImage() {
        return friend_image;
    }

    public String getName() {
        return friend_name;
    }

    public String getText() {
        return recent_text;
    }

    public String getTime() {
        return recent_text_sent_time;
    }
}