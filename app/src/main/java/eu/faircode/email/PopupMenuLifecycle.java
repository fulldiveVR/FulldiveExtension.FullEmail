package eu.faircode.email;

/*
   
*/

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

public class PopupMenuLifecycle extends PopupMenu implements LifecycleObserver {

    public PopupMenuLifecycle(@NonNull Context context, LifecycleOwner owner, @NonNull View anchor) {
        super(new ContextThemeWrapper(context, R.style.popupMenuStyle), anchor);
        Log.i("Instantiate " + this);

        owner.getLifecycle().addObserver(this);
    }

    public void insertIcons(Context context) {
        insertIcons(new ContextThemeWrapper(context, R.style.popupMenuStyle), getMenu());
    }

    @Override
    public void show() {
        Log.i("Show " + this);
        try {
            super.show();
        } catch (Throwable ex) {
            Log.e(ex);
        }
    }

    public void showWithIcons(Context context, View anchor) {
        MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) getMenu(), anchor);
        menuHelper.setForceShowIcon(true);
        menuHelper.setGravity(Gravity.END);
        menuHelper.show();
    }

    @Override
    public void setOnMenuItemClickListener(@Nullable OnMenuItemClickListener listener) {
        super.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    // Handle click just before destroy
                    return listener.onMenuItemClick(item);
                } catch (Throwable ex) {
                    Log.w(ex);
                    return false;
                }
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Log.i("Destroy " + this);
        this.dismiss();
    }

    static void insertIcons(Context context, Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            CharSequence title = item.getTitle();
            insertIcon(context, item);
            if (item.hasSubMenu()) {
                SubMenu sub = item.getSubMenu();
                boolean has = false;
                for (int j = 0; j < sub.size(); j++)
                    if (sub.getItem(j).getIcon() != null) {
                        has = true;
                        insertIcons(context, sub);
                        break;
                    }
                if (has)
                    sub.setHeaderTitle(title);
            }
        }
    }

    private static void insertIcon(Context context, MenuItem menuItem) {
        Drawable icon = menuItem.getIcon();

        if (icon == null)
            icon = new ColorDrawable(Color.TRANSPARENT);
        else {
            icon = icon.getConstantState().newDrawable().mutate();
            int color = Helper.resolveColor(context, R.attr.colorAccent);
            icon.setTint(color);
            if (!menuItem.isEnabled())
                icon.setAlpha(Math.round(Helper.LOW_LIGHT * 255));
        }

        int iconSize = context.getResources().getDimensionPixelSize(R.dimen.menu_item_icon_size);
        icon.setBounds(0, 0, iconSize, iconSize);
        ImageSpan imageSpan = new CenteredImageSpan(icon);

        SpannableStringBuilder ssb = new SpannableStringBuilderEx(menuItem.getTitle());
        ssb.insert(0, "\uFFFC\u2002"); // object replacement character, en space
        ssb.setSpan(imageSpan, 0, 1, 0);
        menuItem.setTitle(ssb);
        menuItem.setIcon(null);
    }
}
