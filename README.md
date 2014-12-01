#AndroidInject
一个简单的 androd 资源注入工具，只有一个类，方便拷贝，用来在平时学习中简化一些操作，减少重复代码量，因此，只适合学习工程

**如果需要在工作中使用，可以参考更严谨的视图注入框架 [butterknife](https://github.com/JakeWharton/butterknife)**

*暂不支持绑定继承而来的方法*

##使用方法

####绑定资源

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

####绑定事件
    //普通View
    @Injector.View(
            id = R.id.btn_1,
            click = "btnClickA",
            longClick = "longClickA"
    )
    Button btn1;

    //ListView
    @Injector.View(
            id = R.id.demo_lv,
            itemClick = "itemClick",
            itemLongClick = "itemLongClick"
    )
    ListView lv;

    //将一个click方法绑定到多个view
    @Injector.Click(ones = {R.id.btn_2, R.id.btn_3, R.id.btn_4})
    private void btnClickB(View view) {
        Tip.tip(this, ((Button) view).getText());
    }

####执行绑定

    Injector.inject(view);
    或者
    Injector.inject(activity);

##示例
参见app工程
