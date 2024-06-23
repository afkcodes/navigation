package com.navigation.reactnative;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.view.ReactViewGroup;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class BottomSheetView extends ReactViewGroup {
    BottomSheetBehavior<BottomSheetView> bottomSheetBehavior;
    BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    float defaultHalfExpandedRatio;
    int pendingDetent;
    int detent;
    int nativeEventCount;
    int mostRecentEventCount;
    protected String stackId;
    protected ReadableArray ancestorStackIds;
    Fragment fragment;

    public BottomSheetView(Context context) {
        super(context);
        bottomSheetBehavior = new BottomSheetBehavior<>(context, null);
        detent = bottomSheetBehavior.getState();
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setBehavior(bottomSheetBehavior);
        setLayoutParams(params);
        defaultHalfExpandedRatio = bottomSheetBehavior.getHalfExpandedRatio();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (bottomSheetCallback != null)
            bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback);
        bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                nativeEventCount++;
                detent = i;
                ReactContext reactContext = (ReactContext) getContext();
                EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, getId());
                eventDispatcher.dispatchEvent(new BottomSheetView.DetentChangedEvent(getId(), detent, nativeEventCount));
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        };
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
        FragmentManager fragmentManager = fragment.getParentFragmentManager();
        if (fragmentManager.getPrimaryNavigationFragment() != fragment) {
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction()
                    .setPrimaryNavigationFragment(fragment);
            try {
                transaction.commitNowAllowingStateLoss();
            } catch(IllegalStateException ignored) {
                transaction.commit();
            }
        }
    }

    void onAfterUpdateTransaction() {
        nativeEventCount = Math.max(nativeEventCount, mostRecentEventCount);
        int eventLag = nativeEventCount - mostRecentEventCount;
        if (eventLag == 0) {
            detent = pendingDetent;
        }
        if (fragment == null) {
            FragmentActivity activity = (FragmentActivity) ((ReactContext) getContext()).getCurrentActivity();
            assert activity != null : "Activity is null";
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            for (int i = 0; i < ancestorStackIds.size(); i++) {
                Fragment ancestorFragment = fragmentManager.findFragmentByTag(ancestorStackIds.getString(i));
                if (ancestorFragment == null) return;
                fragmentManager = ancestorFragment.getChildFragmentManager();
            }
            fragment = new BottomSheetView.BottomSheetFragment(this);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction
                .add(fragment, stackId)
                .setPrimaryNavigationFragment(fragment)
                .commitNowAllowingStateLoss();
        }
        if (bottomSheetBehavior.getState() != detent)
            bottomSheetBehavior.setState(detent);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        FragmentManager fragmentManager = fragment.getParentFragmentManager();
        if (fragmentManager.getPrimaryNavigationFragment() == fragment) {
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction()
                    .setPrimaryNavigationFragment(null);
            try {
                transaction.commitNowAllowingStateLoss();
            } catch(IllegalStateException ignored) {
            }
        }
    }

    public static class BottomSheetFragment extends Fragment {
        private BottomSheetView bottomSheetView;

        public BottomSheetFragment() {
            super();
        }

        BottomSheetFragment(BottomSheetView bottomSheetView) {
            super();
            this.bottomSheetView = bottomSheetView;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return bottomSheetView != null ? bottomSheetView : new View(getContext());
        }
    }

    static class DetentChangedEvent extends Event<BottomSheetView.DetentChangedEvent> {
        private final int detent;
        private final int eventCount;

        public DetentChangedEvent(int viewId, int detent, int eventCount) {
            super(viewId);
            this.detent = detent;
            this.eventCount = eventCount;
        }

        @Override
        public String getEventName() {
            return "topDetentChanged";
        }

        @Override
        public void dispatch(RCTEventEmitter rctEventEmitter) {
            WritableMap event = Arguments.createMap();
            event.putInt("detent", this.detent);
            event.putInt("eventCount", this.eventCount);
            rctEventEmitter.receiveEvent(getViewTag(), getEventName(), event);
        }
    }
}
