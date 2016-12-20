package com.config;

import com.projects.restaurantfinder.R;

/**
 * Created by mg on 20/10/16.
 */
public class UIConfig {

    public static final int IMAGE_PLACEHOLDER_PROFILE_THUMB = R.mipmap.bg_image_thumb_placeholder;

    public static final int IMAGE_PLACEHOLDER = R.mipmap.bg_image_placeholder;

    public static int BORDER_RADIUS = R.dimen.border_radius;

    public static int BORDER_WIDTH = R.dimen.border_width;

    public static int THEME_COLOR = R.color.colorAccent;

    public static int[] INNER_TAB_TITLE = {
            R.string.sub_tab_details,
            R.string.sub_tab_map,
            R.string.sub_tab_gallery,
    };

    public static int[] SELECTED_INNER_TAB_BG = {
            R.mipmap.inner_tab_left_selected,
            R.mipmap.inner_tab_left_selected,
            R.mipmap.inner_tab_left_selected
    };

    public static int[] UNSELECTED_INNER_TAB_BG = {
            R.mipmap.inner_tab_left,
            R.mipmap.inner_tab_left,
            R.mipmap.inner_tab_left
    };
}

