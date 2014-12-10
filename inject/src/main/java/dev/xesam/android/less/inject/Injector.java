package dev.xesam.android.less.inject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by xesam[416249980@qq.com] on 14-11-25.
 */
public class Injector {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface Res {
        int value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface Click {
        int[] value() default {};
    }

    /**
     * 注意与android.view.View冲突
     */

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface View {
        int id() default Injector.DEFAULT_INVALID_VIEW_ID;

        String click() default Injector.DEFAULT_INVALID_METHOD;

        String longClick() default Injector.DEFAULT_INVALID_METHOD;

        String itemClick() default Injector.DEFAULT_INVALID_METHOD;

        String itemLongClick() default Injector.DEFAULT_INVALID_METHOD;
    }

    static final class ResType {

        public static final int TYPE_LAYOUT = 1;
        public static final int TYPE_MENU = 2;
        public static final int TYPE_STYLE = 3;
        public static final int TYPE_ANIM = 4;
        public static final int TYPE_DRAWABLE = 5;
        public static final int TYPE_DIMEN = 6;
        public static final int TYPE_STRING = 7;
        public static final int TYPE_COLOR = 8;
        public static final int TYPE_BOOL = 9;
        public static final int TYPE_ID = 10;
        public static final int TYPE_INTEGER = 11;
        public static final int TYPE_ARRAY = 12;
        public static final int TYPE_FRACTION = 13;
        public static final int TYPE_PLURALS = 14;
        public static final int TYPE_UNKNOWN = -1;

        private static final String TYPE_LAYOUT_DESC = "layout";
        private static final String TYPE_MENU_DESC = "menu";
        private static final String TYPE_STYLE_DESC = "style";
        private static final String TYPE_ANIM_DESC = "anim";
        private static final String TYPE_DRAWABLE_DESC = "drawable";
        private static final String TYPE_DIMEN_DESC = "dimen";
        private static final String TYPE_STRING_DESC = "string";
        private static final String TYPE_COLOR_DESC = "color";
        private static final String TYPE_BOOL_DESC = "bool";
        private static final String TYPE_ID_DESC = "id";
        private static final String TYPE_INTEGER_DESC = "integer";
        private static final String TYPE_ARRAY_DESC = "array";
        private static final String TYPE_FRACTION_DESC = "fraction";
        private static final String TYPE_PLURALS_DESC = "plurals";

        public static final int getResType(Resources res, int resId) {
            String typeName = res.getResourceTypeName(resId);
            int typeCode = TYPE_UNKNOWN;
            if (TYPE_LAYOUT_DESC.equals(typeName)) {
                typeCode = TYPE_LAYOUT;
            } else if (TYPE_MENU_DESC.equals(typeName)) {
                typeCode = TYPE_MENU;
            } else if (TYPE_STYLE_DESC.equals(typeName)) {
                typeCode = TYPE_STYLE;
            } else if (TYPE_ANIM_DESC.equals(typeName)) {
                typeCode = TYPE_ANIM;
            } else if (TYPE_DRAWABLE_DESC.equals(typeName)) {
                typeCode = TYPE_DRAWABLE;
            } else if (TYPE_DIMEN_DESC.equals(typeName)) {
                typeCode = TYPE_DIMEN;
            } else if (TYPE_STRING_DESC.equals(typeName)) {
                typeCode = TYPE_STRING;
            } else if (TYPE_COLOR_DESC.equals(typeName)) {
                typeCode = TYPE_COLOR;
            } else if (TYPE_BOOL_DESC.equals(typeName)) {
                typeCode = TYPE_BOOL;
            } else if (TYPE_ID_DESC.equals(typeName)) {
                typeCode = TYPE_ID;
            } else if (TYPE_INTEGER_DESC.equals(typeName)) {
                typeCode = TYPE_INTEGER;
            } else if (TYPE_ARRAY_DESC.equals(typeName)) {
                typeCode = TYPE_ARRAY;
            } else if (TYPE_FRACTION_DESC.equals(typeName)) {
                typeCode = TYPE_FRACTION;
            } else if (TYPE_PLURALS_DESC.equals(typeName)) {
                typeCode = TYPE_PLURALS;
            }

            return typeCode;
        }
    }

    static class InjectListener implements OnClickListener, android.view.View.OnLongClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

        public static final <T> T requireNonNull(T o) {
            if (o == null) {
                throw new NullPointerException();
            }
            return o;
        }

        private Object receiver;
        private Method method;

        public InjectListener(Object receiver, Method method) {
            this.receiver = receiver;
            this.method = method;
        }

        private Object onInvoke(Object... objs) {
            boolean isAccessible = method.isAccessible();
            try {
                if (!isAccessible) {
                    method.setAccessible(true);
                }
                return method.invoke(receiver, objs);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } finally {
                method.setAccessible(isAccessible);
            }
            return null;
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, android.view.View view, int position, long id) {
            return (Boolean) InjectListener.<Object>requireNonNull(onInvoke(parent, view, position, id));
        }

        @Override
        public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
            onInvoke(parent, view, position, id);
        }

        @Override
        public boolean onLongClick(android.view.View v) {
            return (Boolean) InjectListener.<Object>requireNonNull(onInvoke(v));
        }

        @Override
        public void onClick(android.view.View v) {
            onInvoke(v);
        }

    }

    static interface FindableView {
        android.view.View findViewById(int viewId);

        Object getObject();

        Resources getResources();
    }

    static class InnerFindView implements FindableView {

        private android.view.View view;

        public InnerFindView(android.view.View view) {
            this.view = view;
        }

        @Override
        public android.view.View findViewById(int viewId) {
            return view.findViewById(viewId);
        }

        @Override
        public Object getObject() {
            return view;
        }

        @Override
        public Resources getResources() {
            return view.getResources();
        }
    }

    @SuppressLint("NewApi")
    static class InnerFindFragment implements FindableView {

        private Fragment fragment;
        private android.view.View view;

        public InnerFindFragment(Fragment fragment) {
            this.fragment = fragment;
            this.view = fragment.getView();
        }

        @Override
        public android.view.View findViewById(int viewId) {
            return view.findViewById(viewId);
        }

        @Override
        public Object getObject() {
            return view;
        }

        @Override
        public Resources getResources() {
            return fragment.getResources();
        }
    }

    static class InnerFindSupportFragment implements FindableView {

        private android.support.v4.app.Fragment fragment;
        private android.view.View view;

        public InnerFindSupportFragment(android.support.v4.app.Fragment fragment) {
            this.fragment = fragment;
            this.view = fragment.getView();
        }

        @Override
        public android.view.View findViewById(int viewId) {
            return view.findViewById(viewId);
        }

        @Override
        public Object getObject() {
            return view;
        }

        @Override
        public Resources getResources() {
            return fragment.getResources();
        }
    }

    static class InnerFindActivity implements FindableView {

        private Activity activity;

        public InnerFindActivity(Activity activity) {
            this.activity = activity;
        }

        @Override
        public android.view.View findViewById(int viewId) {
            return activity.findViewById(viewId);
        }

        @Override
        public Object getObject() {
            return activity;
        }

        @Override
        public Resources getResources() {
            return activity.getResources();
        }
    }

    public static final int DEFAULT_INVALID_VIEW_ID = -1;
    public static final String DEFAULT_INVALID_METHOD = "";

    private static final Class<?>[] CLICK_TYPES = {android.view.View.class};
    private static final Class<?>[] ITEM_CLICK_TYPES = {AdapterView.class, android.view.View.class, int.class, long.class};

    @SuppressLint("Recycle")
    private static void injectArray(Field field, Object receiver, Resources res, int resId) throws IllegalAccessException, IllegalArgumentException, NotFoundException {
        Class<?> fieldType = field.getType();
        if (String[].class.isAssignableFrom(fieldType)) {
            field.set(receiver, res.getStringArray(resId));
        } else if (int[].class.isAssignableFrom(fieldType)) {
            field.set(receiver, res.getIntArray(resId));
        } else {
            field.set(receiver, res.obtainTypedArray(resId));
        }
    }

    private static void injectColor(Field field, Object receiver, Resources res, int resId) throws IllegalAccessException, IllegalArgumentException, NotFoundException {
        Class<?> fieldType = field.getType();
        if (ColorStateList.class.isAssignableFrom(fieldType)) {
            field.set(receiver, res.getColorStateList(resId));
        } else {
            field.set(receiver, res.getColor(resId));
        }
    }

    /*
     * not used:
     * case ResType.TYPE_ANIM:
     * case ResType.TYPE_ID:
     * case ResType.TYPE_LAYOUT:
     * case ResType.TYPE_MENU:
     * case ResType.TYPE_STYLE:
     *
     * */
    private static final void injectRes(FindableView findableView, Resources res, Field field) throws IllegalAccessException, IllegalArgumentException {
        Res _Res = field.getAnnotation(Res.class);
        if (null == _Res) {
            return;
        }
        int resId = _Res.value();
        int resType = ResType.getResType(res, resId);
        Object obj = findableView.getObject();

        switch (resType) {
            case ResType.TYPE_ARRAY:
                injectArray(field, obj, res, resId);
                break;
            case ResType.TYPE_BOOL:
                field.set(obj, res.getBoolean(resId));
                break;
            case ResType.TYPE_COLOR:
                injectColor(field, obj, res, resId);
                break;
            case ResType.TYPE_DIMEN:
                field.set(obj, res.getDimension(resId));
                break;
            case ResType.TYPE_DRAWABLE:
                field.set(obj, res.getDrawable(resId));
                break;
            case ResType.TYPE_INTEGER:
                field.set(obj, res.getInteger(resId));
                break;
            case ResType.TYPE_STRING:
                field.set(obj, res.getString(resId));
                break;
            case ResType.TYPE_UNKNOWN:
                break;
            default:
                break;
        }
    }

    private static final void injectView(FindableView findableView, Field field) throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException {
        View _View = field.getAnnotation(View.class);
        if (null == _View) {
            return;
        }
        int viewId = _View.id();
        android.view.View view = findableView.findViewById(viewId);
        final Object receiver = findableView.getObject();
        field.set(receiver, view);

        if (!_View.click().equals(DEFAULT_INVALID_METHOD)) {
            System.out.println(_View.click());
            Method method = receiver.getClass().getDeclaredMethod(_View.click(), CLICK_TYPES);
            view.setOnClickListener(new InjectListener(receiver, method));
        }

        if (!_View.longClick().equals(DEFAULT_INVALID_METHOD)) {
            Method method = receiver.getClass().getDeclaredMethod(_View.longClick(), CLICK_TYPES);
            view.setOnLongClickListener(new InjectListener(receiver, method));
        }

        if (!_View.itemClick().equals(DEFAULT_INVALID_METHOD)) {
            final Method method = receiver.getClass().getDeclaredMethod(_View.itemClick(), ITEM_CLICK_TYPES);
            ((AdapterView<?>) view).setOnItemClickListener(new InjectListener(receiver, method));
        }

        if (!_View.itemLongClick().equals(DEFAULT_INVALID_METHOD)) {
            final Method method = receiver.getClass().getDeclaredMethod(_View.itemLongClick(), ITEM_CLICK_TYPES);
            ((AdapterView<?>) view).setOnItemLongClickListener(new InjectListener(receiver, method));
        }
    }

    private static void injectClick(FindableView findableView, final Method method) {
        Click _inClick = method.getAnnotation(Click.class);
        if (null == _inClick) {
            return;
        }

        int[] viewIds = _inClick.value();

        OnClickListener onClickListener = new InjectListener(findableView.getObject(), method);
        for (int _viewId : viewIds) {
            findableView.findViewById(_viewId).setOnClickListener(onClickListener);
        }
    }

    private static final void inject(FindableView findableView) {
        Resources res = findableView.getResources();
        Field[] fields = findableView.getObject().getClass().getDeclaredFields();

        for (Field field : fields) {
            boolean f = field.isAccessible();
            field.setAccessible(true);
            try {
                injectRes(findableView, res, field);
                injectView(findableView, field);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } finally {
                field.setAccessible(f);
            }
        }

        Method[] methods = findableView.getObject().getClass().getDeclaredMethods();
        for (Method method : methods) {
            injectClick(findableView, method);
        }
    }

    public static final void inject(android.view.View view) {
        inject(new InnerFindView(view));
    }

    public static final void inject(Activity activity) {
        inject(new InnerFindActivity(activity));
    }

    public static final void inject(Fragment fragment) {
        inject(new InnerFindFragment(fragment));
    }

    public static final void inject(android.support.v4.app.Fragment fragment) {
        inject(new InnerFindSupportFragment(fragment));
    }
}
