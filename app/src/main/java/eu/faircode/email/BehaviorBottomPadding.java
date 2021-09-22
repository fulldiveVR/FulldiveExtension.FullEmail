package eu.faircode.email;

/*
   
*/

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

public class BehaviorBottomPadding extends CoordinatorLayout.Behavior<View> {
    public BehaviorBottomPadding() {
        super();
    }

    public BehaviorBottomPadding(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return (dependency instanceof Snackbar.SnackbarLayout);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        setPadding(child, dependency.isAttachedToWindow() ? dependency.getHeight() : 0);
        return true;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
        setPadding(child, 0);
    }

    private static void setPadding(View child, int value) {
        child.setPadding(0, 0, 0, value);
    }
}
