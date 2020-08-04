package com.aventuracctv.ordermangement.interfaces;

import android.widget.Button;
import android.widget.CheckBox;

import com.aventuracctv.ordermangement.data.BuildItem;

/**
 * Created by jburmeister on 4/26/2016.
 */
public interface AssignBuildCallback {
    void assignBuildItem(Button btn, BuildItem item);
}
