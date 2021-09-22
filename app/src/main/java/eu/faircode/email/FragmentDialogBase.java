package eu.faircode.email;

/*

*/

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import static android.app.Activity.RESULT_CANCELED;

public class FragmentDialogBase extends DialogFragment {
    private boolean once = false;
    private LifecycleOwner owner;
    private LifecycleRegistry registry;
    private String requestKey = null;
    private String targetRequestKey;
    private int targetRequestCode;

    private static int requestSequence = 0;

    public String getRequestKey() {
        if (requestKey == null)
            requestKey = getClass().getName() + "_" + (++requestSequence);
        return requestKey;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        owner = new LifecycleOwner() {
            @NonNull
            @Override
            public Lifecycle getLifecycle() {
                return registry;
            }
        };
        registry = new LifecycleRegistry(owner);
        registry.setCurrentState(Lifecycle.State.CREATED);

        if (savedInstanceState != null) {
            requestKey = savedInstanceState.getString("fair:request");
            targetRequestKey = savedInstanceState.getString("fair:key");
            targetRequestCode = savedInstanceState.getInt("fair:code");
        }

        getParentFragmentManager().setFragmentResultListener(getRequestKey(), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                try {
                    result.setClassLoader(ApplicationEx.class.getClassLoader());
                    int requestCode = result.getInt("requestCode");
                    int resultCode = result.getInt("resultCode");

                    Intent data = new Intent();
                    data.putExtra("args", result);
                    onActivityResult(requestCode, resultCode, data);
                } catch (Throwable ex) {
                    Log.w(ex);
                }
            }
        });

        Log.i("Create " + this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("fair:request", requestKey);
        outState.putString("fair:key", targetRequestKey);
        outState.putInt("fair:code", targetRequestCode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        registry.setCurrentState(Lifecycle.State.RESUMED);
        super.onResume();
        Log.d("Resume " + this);
    }

    @Override
    public void onPause() {
        registry.setCurrentState(Lifecycle.State.STARTED);
        super.onPause();
        Log.d("Pause " + this);
    }

    @Override
    public void onDestroy() {
        registry.setCurrentState(Lifecycle.State.DESTROYED);
        super.onDestroy();
        Log.i("Destroy " + this);
    }

    @Override
    public void onStart() {
        registry.setCurrentState(Lifecycle.State.STARTED);
        try {
            super.onStart();
        } catch (Throwable ex) {
            Log.e(ex);
        }
        Log.d("Start " + this);
    }

    @Override
    public void onStop() {
        registry.setCurrentState(Lifecycle.State.CREATED);
        super.onStop();
        Log.d("Stop " + this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        String action = (data == null ? null : data.getAction());
        Log.i("Result class=" + this.getClass().getSimpleName() +
                " action=" + action + " request=" + requestCode + " result=" + resultCode);
        Log.logExtras(data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @NonNull
    @Override
    public LifecycleOwner getViewLifecycleOwner() {
        return owner;
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            super.show(manager, tag);
        } catch (Throwable ex) {
            // IllegalStateException Can not perform this action after onSaveInstanceState
            // Should not happen, but still happened in AdapterMessage.onOpenLink
            Log.e(ex);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        sendResult(RESULT_CANCELED);
        super.onDismiss(dialog);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setTargetFragment(@Nullable Fragment fragment, int requestCode) {
        if (fragment instanceof FragmentBase)
            targetRequestKey = ((FragmentBase) fragment).getRequestKey();
        else if (fragment instanceof FragmentDialogBase)
            targetRequestKey = ((FragmentDialogBase) fragment).getRequestKey();
        else {
            Log.e("setTargetFragment=" + fragment.getClass().getName());
            throw new IllegalArgumentException();
        }
        targetRequestCode = requestCode;
        Log.i("Set target " + this + " " + fragment + " request=" + requestCode);
    }

    protected void sendResult(int resultCode) {
        if (!once) {
            once = true;
            Log.i("Dialog key=" + targetRequestKey + " result=" + resultCode);
            if (targetRequestKey != null) {
                Bundle args = getArguments();
                if (args == null) // onDismiss
                    args = new Bundle();
                args.putInt("requestCode", targetRequestCode);
                args.putInt("resultCode", resultCode);
                getParentFragmentManager().setFragmentResult(targetRequestKey, args);
            }
        }
    }

    @Override
    public void startActivity(Intent intent) {
        try {
            super.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Log.w(ex);
            Helper.reportNoViewer(getContext(), intent);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        try {
            super.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException ex) {
            Log.w(ex);
            Helper.reportNoViewer(getContext(), intent);
        }
    }
}
