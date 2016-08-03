package com.example.shreyaprabhu.locationdetectlibrary.ToastMaker;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Shreya Prabhu on 8/3/2016.
 */
public class ToastMaker {

    public void ToastMakerLong(Context context, String toastText){
        Toast.makeText(context,toastText,Toast.LENGTH_SHORT).show();
    }

    public void ToastMakerShort(Context context, String toastText){
        Toast.makeText(context,toastText,Toast.LENGTH_SHORT).show();
    }
}
