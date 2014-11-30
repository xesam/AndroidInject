package dev.xesam.android.demo.inject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import dev.xesam.android.less.inject.Injector;

/**
 * Created by xesam[416249980@qq.com] on 14-11-25.
 */
public class InjectActivity extends Activity {

    @Injector.View(
            id = R.id.btn_1,
            click = "btnClickA",
            longClick = "longClickA"
    )
    Button btn1;

    @Injector.View(
            id = R.id.demo_lv,
            itemClick = "itemClick",
            itemLongClick = "itemLongClick"
    )
    ListView lv;

    @Injector.Res(R.string.string_1)
    String string_1;
    @Injector.Res(R.array.sa_1)
    String[] sa;
    @Injector.Res(R.integer.int_1)
    int int_1;
    @Injector.Res(R.array.ia_1)
    int[] ia;
    @Injector.Res(R.bool.bool_1)
    boolean bool_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inject_activity);
        Injector.inject(this);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"a", "b", "c", "d", "e"}));

        Tip.log(string_1);
        Tip.log(sa.length);
        Tip.log(int_1);
        Tip.log(ia.length);
        Tip.log(bool_1);
    }

    private void btnClickA(View view) {
        int vid = view.getId();
        if (vid == R.id.btn_1) {
            Tip.tip(this, "click " + ((Button) view).getText());
        }
    }

    private boolean longClickA(View view) {
        int vid = view.getId();
        if (vid == R.id.btn_1) {
            Tip.tip(this, "long click " + ((Button) view).getText());
        }
        return true;
    }

    @Injector.Click(ones = {R.id.btn_2, R.id.btn_3, R.id.btn_4})
    private void btnClickB(View view) {
        Tip.tip(this, ((Button) view).getText());
    }

    public void itemClick(AdapterView<?> parent, View view, int position, long id) {
        Tip.tip(this, "lv item click " + position);
    }

    public boolean itemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Tip.tip(this, "lv item long click " + position);
        return true;
    }
}
