package com.aventuracctv.ordermangement.interfaces;

import android.widget.ImageButton;

import com.aventuracctv.ordermangement.data.BuildItem;

/**
 * Created by jburmeister on 5/2/2016.
 */
public interface OpenPDFCallback {
        void openPDF(ImageButton btn, BuildItem item);
}
