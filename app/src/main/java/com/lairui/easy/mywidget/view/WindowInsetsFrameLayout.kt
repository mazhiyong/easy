package com.lairui.easy.mywidget.view

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.WindowInsets
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.lairui.easy.utils.tool.UtilTools.Companion.getNavigationBarHeight

class WindowInsetsFrameLayout : LinearLayout {
    private var mContenx: Context

    /* public WindowInsetsFrameLayout(Context context) {
        this(context, null);
    }

    public WindowInsetsFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }*/
    constructor(context: Context) : super(context) {
        mContenx = context
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContenx = context
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContenx = context
    }

    /* public WindowInsetsFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }*/
/*@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WindowInsetsFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                requestApplyInsets();
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++)
            getChildAt(index).dispatchApplyWindowInsets(insets);
        return insets;
    }*/
    override fun fitSystemWindows(insets: Rect): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            insets.left = 0
            insets.top = 0
            insets.right = 0
        }
        return super.fitSystemWindows(insets)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (insets.systemWindowInsetBottom > 0) { //键盘开启
                val hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME)
                if (!hasHomeKey) {
                    super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom - getNavigationBarHeight(mContenx)))
                } else {
                    super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom))
                }
                // return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, insets.getSystemWindowInsetBottom()-UtilTools.getNavigationBarHeight(mContenx)));
            } else { //键盘关闭
                super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom))
            }
        } else {
            insets
        }
    }
}