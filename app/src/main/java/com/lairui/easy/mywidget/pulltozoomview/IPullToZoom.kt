package com.lairui.easy.mywidget.pulltozoomview

/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      2014/11/7  14:21.
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2014/11/7        ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */

import android.content.res.TypedArray
import android.view.View

interface IPullToZoom<T : View> {
    /**
     * Get the Wrapped Zoom View. Anything returned here has already been
     * added to the content view.
     *
     * @return The View which is currently wrapped
     */
    val zoomView: View?

    val headerView: View?

    /**
     * Get the Wrapped root View.
     *
     * @return The View which is currently wrapped
     */
    var pullRootView: T?

    /**
     * Whether Pull-to-Refresh is enabled
     *
     * @return enabled
     */
    val isPullToZoomEnabled: Boolean

    /**
     * Returns whether the Widget is currently in the Zooming state
     *
     * @return true if the Widget is currently zooming
     */
    val isZooming: Boolean

    /**
     * Returns whether the Widget is currently in the Zooming anim type
     *
     * @return true if the anim is parallax
     */
    val isParallax: Boolean

    val isHideHeader: Boolean

    fun handleStyledAttributes(a: TypedArray)
}
