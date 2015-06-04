#AndroidInject
一个简单的 androd 资源注入工具，只有一个类，方便拷贝，用来在平时学习中简化一些操作，减少重复代码量，因此，只适合学习工程

**如果需要在工作中使用，可以参考更严谨的视图注入框架 [butterknife](https://github.com/JakeWharton/butterknife)**

*不支持绑定继承而来的字段与方法*

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
    @Injector.View(R.id.btn_1)
    Button btn1;

    //ListView
    @Injector.View(R.id.demo_lv)
    ListView lv;

    //将click方法绑定到view
    @Injector.Click(R.id.btn_1)
    private void btnClickB(View view) {
        Tip.tip(this, ((Button) view).getText());
    }
    //将click方法绑定到多个view
    @Injector.Click({R.id.btn_2, R.id.btn_3, R.id.btn_4})
    private void btnClickB(View view) {
        Tip.tip(this, ((Button) view).getText());
    }

####执行绑定

    Injector.inject(view);
    或者
    Injector.inject(activity);
    或者
    Injector.inject(fragment);

###修改日志

####2015.16.05
1. 重构
2. 删掉原来的字符串绑定模式，不利于IDE的自动重构

####2014.12.10
1. 增加对fragment的支持，需要区分 Fragment 与 v4.Fragment
2. 修改注解，取消InjectClick 中 one与ones的显式区分

##示例
参见app工程

