package com.aventuracctv.ordermangement.interfaces;

import com.aventuracctv.ordermangement.data.User;

/**
 * Created by jburmeister on 5/3/2016.
 */
public interface LoginCallback {
    void login(User user);
}
