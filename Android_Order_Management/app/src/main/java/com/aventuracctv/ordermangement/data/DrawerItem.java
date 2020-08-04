package com.aventuracctv.ordermangement.data;

public class DrawerItem {
    private boolean showNotify;
    private String title;
    private int icon;


    public DrawerItem() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
