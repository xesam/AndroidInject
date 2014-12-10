package dev.xesam.android.demo.inject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import dev.xesam.android.less.inject.Injector;

/**
 * Created by xesam[416249980@qq.com] on 14-11-25.
 */
public class InjectRelativeLayout extends RelativeLayout {

    @Injector.View(
            id = R.id.button1,
            click = "onButtonClick"
    )
    private Button btn1;
    @Injector.View(id = R.id.button2,
            click = "onButtonClick")
    private Button btn2;

    private Button btn3;

    public InjectRelativeLayout(Context context) {
        super(context);
        init();
    }

    public InjectRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InjectRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.inject_relative_layout, this);
        Injector.inject(this);
    }

    @Injector.Click(R.id.button3)
    public void onButtonClick(View view) {
        Tip.tip(getContext(), ((Button) view).getText());
    }
}
