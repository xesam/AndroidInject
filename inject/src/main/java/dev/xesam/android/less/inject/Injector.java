package dev.xesam.android.less.inject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Build;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xesam[416249980@qq.com] on 14-11-25.
 */
public class Injector {

    ///////////////////////////////////////////////////

    /**
     * provider view and resource
     */
    interface ViewFinder {
        android.view.View findViewById(int viewId);

        Object getObject();

        Resources getResources();
    }

    static class SimpleViewFinder implements ViewFinder {

        private android.view.View view;

        public SimpleViewFinder(android.view.View view) {
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static class FragmentViewFinder implements ViewFinder {

        private Fragment target;
        private android.view.View view;

        public FragmentViewFinder(Fragment fragment) {
            this.target = fragment;
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
            return target.getResources();
        }
    }

    static class SupportFragmentViewFinder implements ViewFinder {

        private android.support.v4.app.Fragment target;
        private android.view.View view;

        public SupportFragmentViewFinder(android.support.v4.app.Fragment fragment) {
            this.target = fragment;
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
            return target.getResources();
        }
    }

    static class ActivityViewFinder implements ViewFinder {

        private Activity target;

        public ActivityViewFinder(Activity activity) {
            this.target = activity;
        }

        @Override
        public android.view.View findViewById(int viewId) {
            return target.findViewById(viewId);
        }

        @Override
        public Object getObject() {
            return target;
        }

        @Override
        public Resources getResources() {
            return target.getResources();
        }
    }


    ///////////////////////////////////////////////////

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Res {
        int value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Click {
        int[] value() default {};
    }

    /**
     * warning android.view.View conflict
     */

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface View {
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


        static Map<String, Integer> mapper;

        static {
            mapper = new HashMap<>();
            mapper.put(TYPE_LAYOUT_DESC, TYPE_LAYOUT);
            mapper.put(TYPE_MENU_DESC, TYPE_MENU);
            mapper.put(TYPE_STYLE_DESC, TYPE_STYLE);
            mapper.put(TYPE_ANIM_DESC, TYPE_ANIM);
            mapper.put(TYPE_DRAWABLE_DESC, TYPE_DRAWABLE);
            mapper.put(TYPE_DIMEN_DESC, TYPE_DIMEN);
            mapper.put(TYPE_STRING_DESC, TYPE_STRING);
            mapper.put(TYPE_COLOR_DESC, TYPE_COLOR);
            mapper.put(TYPE_BOOL_DESC, TYPE_BOOL);
            mapper.put(TYPE_ID_DESC, TYPE_ID);
            mapper.put(TYPE_INTEGER_DESC, TYPE_INTEGER);
            mapper.put(TYPE_ARRAY_DESC, TYPE_ARRAY);
            mapper.put(TYPE_FRACTION_DESC, TYPE_FRACTION);
            mapper.put(TYPE_PLURALS_DESC, TYPE_PLURALS);
        }

        static int getResType(Resources res, int resId) {
            String typeName = res.getResourceTypeName(resId);
            Integer typeCode = mapper.get(typeName);
            if (typeCode == null) {
                return TYPE_UNKNOWN;
            }
            return typeCode;
        }
    }

    public static final int DEFAULT_INVALID_VIEW_ID = -1;
    public static final String DEFAULT_INVALID_METHOD = "";

    private static final Class<?>[] CLICK_TYPES = {android.view.View.class};
    private static final Class<?>[] ITEM_CLICK_TYPES = {AdapterView.class, android.view.View.class, int.class, long.class};

    static class InjectListener implements OnClickListener, android.view.View.OnLongClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

        /**
         * ref : Objects.requireNonNull(T)
         */

        public static <T> T requireNonNull(T o) {
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
            return (Boolean) InjectListener.requireNonNull(onInvoke(parent, view, position, id));
        }

        @Override
        public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
            onInvoke(parent, view, position, id);
        }

        @Override
        public boolean onLongClick(android.view.View v) {
            return (Boolean) InjectListener.requireNonNull(onInvoke(v));
        }

        @Override
        public void onClick(android.view.View v) {
            onInvoke(v);
        }

    }

    ///////////////////////////////////////////////////

    @SuppressLint("Recycle")
    private static void _injectArray(Field field, Object receiver, Resources res, int resId) throws IllegalAccessException, IllegalArgumentException, NotFoundException {
        Class<?> fieldType = field.getType();
        if (String[].class.isAssignableFrom(fieldType)) {
            field.set(receiver, res.getStringArray(resId));
        } else if (int[].class.isAssignableFrom(fieldType)) {
            field.set(receiver, res.getIntArray(resId));
        } else {
            field.set(receiver, res.obtainTypedArray(resId));
        }
    }

    private static void _injectColor(Field field, Object receiver, Resources res, int resId) throws IllegalAccessException, IllegalArgumentException, NotFoundException {
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
    private static final void _injectRes(ViewFinder viewFinder, Resources res, Field field) throws IllegalAccessException, IllegalArgumentException {
        Res _Res = field.getAnnotation(Res.class);
        if (null == _Res) {
            return;
        }
        int resId = _Res.value();
        int resType = ResType.getResType(res, resId);
        Object obj = viewFinder.getObject();

        switch (resType) {
            case ResType.TYPE_ARRAY:
                _injectArray(field, obj, res, resId);
                break;
            case ResType.TYPE_BOOL:
                field.set(obj, res.getBoolean(resId));
                break;
            case ResType.TYPE_COLOR:
                _injectColor(field, obj, res, resId);
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

    private static void _injectView(ViewFinder viewFinder, Field field) throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException {
        View _View = field.getAnnotation(View.class);
        if (null == _View) {
            return;
        }
        int viewId = _View.id();
        android.view.View view = viewFinder.findViewById(viewId);
        final Object receiver = viewFinder.getObject();
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

    private static void injectClick(ViewFinder viewFinder, final Method method) {
        Click _inClick = method.getAnnotation(Click.class);
        if (null == _inClick) {
            return;
        }

        int[] viewIds = _inClick.value();

        OnClickListener onClickListener = new InjectListener(viewFinder.getObject(), method);
        for (int _viewId : viewIds) {
            viewFinder.findViewById(_viewId).setOnClickListener(onClickListener);
        }
    }

    private static void inject(ViewFinder viewFinder) {
        Resources res = viewFinder.getResources();
        Field[] fields = viewFinder.getObject().getClass().getDeclaredFields();

        for (Field field : fields) {
            boolean f = field.isAccessible();
            field.setAccessible(true);
            try {
                _injectRes(viewFinder, res, field);
                _injectView(viewFinder, field);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } finally {
                field.setAccessible(f);
            }
        }

        Method[] methods = viewFinder.getObject().getClass().getDeclaredMethods();
        for (Method method : methods) {
            injectClick(viewFinder, method);
        }
    }

    public static void inject(android.view.View view) {
        inject(new SimpleViewFinder(view));
    }

    public static void inject(Activity activity) {
        inject(new ActivityViewFinder(activity));
    }

    public static void inject(Fragment fragment) {
        inject(new FragmentViewFinder(fragment));
    }

    public static void inject(android.support.v4.app.Fragment fragment) {
        inject(new SupportFragmentViewFinder(fragment));
    }
}
