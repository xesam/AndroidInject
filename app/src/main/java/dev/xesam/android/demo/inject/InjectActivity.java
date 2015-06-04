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

    @Injector.View(R.id.btn_1)
    Button btn1;

    @Injector.View(R.id.btn_4)
    Button btn4;

    @Injector.View(R.id.demo_lv)
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
    @Injector.Res(R.color.color_1)
    int color_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inject_activity);
        Injector.inject(this);
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"a", "b", "c", "d", "e"}));

        Tip.log(string_1);
        Tip.log(sa.length);
        Tip.log(int_1);
        Tip.log(ia.length);
        Tip.log(bool_1);

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn4.setText(int_1 + "");
                btn4.setTextColor(color_1);
            }
        });
    }

    @Injector.Click(R.id.btn_1)
    private void btnClickA(View view) {
        int vid = view.getId();
        if (vid == R.id.btn_1) {
            Tip.tip(this, "click " + ((Button) view).getText());
        }
    }

    @Injector.LongClick(R.id.btn_1)
    private boolean longClickA(Button view) {
        int vid = view.getId();
        if (vid == R.id.btn_1) {
            Tip.tip(this, "long click " + view.getText());
        }
        return true;
    }

    @Injector.Click({R.id.btn_2, R.id.btn_3})
    private void btnClickB(Button view) {
        view.setText(string_1);
    }

    @Injector.ItemClick(R.id.demo_lv)
    public void itemClick(ListView parent, View view, int position, long id) {
        Tip.tip(this, "lv item click " + position);
    }

    @Injector.ItemLongClick(R.id.demo_lv)
    public boolean itemLongClick(ListView parent, View view, int position, long id) {
        Tip.tip(this, "lv item long click " + position);
        return true;
    }
}
