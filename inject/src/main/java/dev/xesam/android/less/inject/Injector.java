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

import java.lang.annotation.Annotation;
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

    public static final int DEFAULT_INVALID_VIEW_ID = -1;
    public static final String DEFAULT_INVALID_METHOD = "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Res {
        int value();
    }

    /**
     * warning android.view.View conflict
     */

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface View {
        int value() default Injector.DEFAULT_INVALID_VIEW_ID;
    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Click {
        int[] value() default {};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface LongClick {
        int[] value() default {};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ItemClick {
        int[] value() default {};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ItemLongClick {
        int[] value() default {};
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

        static Map<String, Integer> typeMapper;

        static {
            typeMapper = new HashMap<>();
            typeMapper.put("layout", TYPE_LAYOUT);
            typeMapper.put("menu", TYPE_MENU);
            typeMapper.put("style", TYPE_STYLE);
            typeMapper.put("anim", TYPE_ANIM);
            typeMapper.put("drawable", TYPE_DRAWABLE);
            typeMapper.put("dimen", TYPE_DIMEN);
            typeMapper.put("string", TYPE_STRING);
            typeMapper.put("color", TYPE_COLOR);
            typeMapper.put("bool", TYPE_BOOL);
            typeMapper.put("id", TYPE_ID);
            typeMapper.put("integer", TYPE_INTEGER);
            typeMapper.put("array", TYPE_ARRAY);
            typeMapper.put("fraction", TYPE_FRACTION);
            typeMapper.put("plurals", TYPE_PLURALS);
        }

        static int getResType(Resources res, int resId) {
            String typeName = res.getResourceTypeName(resId);
            Integer typeCode = typeMapper.get(typeName);
            if (typeCode == null) {
                return TYPE_UNKNOWN;
            }
            return typeCode;
        }
    }

    ///////////////////////////////////////////////////

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
    private static void _injectRes(ViewFinder viewFinder, Resources res, Field field) throws IllegalAccessException, IllegalArgumentException {
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
        View _view = field.getAnnotation(View.class);
        if (null == _view) {
            return;
        }
        int viewId = _view.value();
        android.view.View view = viewFinder.findViewById(viewId);
        Object receiver = viewFinder.getObject();
        field.set(receiver, view);
    }

    private static void _injectListener(ViewFinder viewFinder, final Method method) {

        Annotation[] annotations = method.getAnnotations();
        InjectListener injectListener = new InjectListener(viewFinder.getObject(), method);

        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Click.class) {
                int[] viewIds = ((Click) annotation).value();
                for (int _viewId : viewIds) {
                    viewFinder.findViewById(_viewId).setOnClickListener(injectListener);
                }
            } else if (annotation.annotationType() == LongClick.class) {
                int[] viewIds = ((LongClick) annotation).value();
                for (int _viewId : viewIds) {
                    viewFinder.findViewById(_viewId).setOnLongClickListener(injectListener);
                }
            } else if (annotation.annotationType() == ItemClick.class) {
                int[] viewIds = ((ItemClick) annotation).value();
                for (int _viewId : viewIds) {
                    ((AdapterView<?>) viewFinder.findViewById(_viewId)).setOnItemClickListener(injectListener);
                }
            } else if (annotation.annotationType() == ItemLongClick.class) {
                int[] viewIds = ((ItemLongClick) annotation).value();
                for (int _viewId : viewIds) {
                    ((AdapterView<?>) viewFinder.findViewById(_viewId)).setOnItemLongClickListener(injectListener);
                }
            }
        }
    }

    public static void inject(ViewFinder viewFinder) {
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
            _injectListener(viewFinder, method);
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
