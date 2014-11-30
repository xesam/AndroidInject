package dev.xesam.android.demo.inject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by xesam[416249980@qq.com] on 14-11-25.
 */
public class Tip {
    public static void tip(Context context, CharSequence s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static void log(Object s) {
        Log.e("Tip", s.toString());
    }
}
