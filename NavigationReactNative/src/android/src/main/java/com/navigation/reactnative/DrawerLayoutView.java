package com.navigation.reactnative;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.NativeGestureUtil;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class DrawerLayoutView extends DrawerLayout implements ToolbarDrawerView {
    int nativeEventCount;
    int mostRecentEventCount;
    boolean pendingOpen;
    int gravity;
    boolean dragging;
    private boolean layoutRequested = false;
    ActionBarDrawerToggle toolbarDrawerToggle;

    public DrawerLayoutView(@NonNull Context context) {
        super(context);
        addDrawerListener(new SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                nativeEventCount++;
                ReactContext reactContext = (ReactContext) getContext();
                EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, getId());
                eventDispatcher.dispatchEvent(new DrawerLayoutView.ChangeOpenEvent(getId(), true, nativeEventCount));
                if (toolbarDrawerToggle != null)
                    toolbarDrawerToggle.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                nativeEventCount++;
                ReactContext reactContext = (ReactContext) getContext();
                EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, getId());
                eventDispatcher.dispatchEvent(new DrawerLayoutView.ChangeOpenEvent(getId(), false, nativeEventCount));
                if (toolbarDrawerToggle != null)
                    toolbarDrawerToggle.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (toolbarDrawerToggle != null)
                    toolbarDrawerToggle.onDrawerSlide(drawerView, slideOffset);

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (toolbarDrawerToggle != null)
                    toolbarDrawerToggle.onDrawerStateChanged(newState);
            }
        });
    }

    void onAfterUpdateTransaction() {
        setupDrawerToggle();
        int eventLag = nativeEventCount - mostRecentEventCount;
        if (eventLag == 0 && isOpen() != pendingOpen) {
            if (pendingOpen) openDrawer(gravity);
            else closeDrawer(gravity);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (toolbarDrawerToggle != null)
            toolbarDrawerToggle.syncState();
        setupDrawerToggle();
        requestLayout();
    }

    private void setupDrawerToggle() {
        ViewParent parent = this;
        while(parent != null) {
            parent = parent.getParent();
            if (parent instanceof SceneView sceneView) {
                sceneView.setDrawer(this);
                parent = null;
            }
        }
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        if (!layoutRequested) {
            layoutRequested = true;
            post(measureAndLayout);
        }
    }

    private final Runnable measureAndLayout = () -> {
        layoutRequested = false;
        measure(
            MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
        layout(getLeft(), getTop(), getRight(), getBottom());
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (super.onInterceptTouchEvent(ev)) {
            NativeGestureUtil.notifyNativeGestureStarted(this, ev);
            dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_UP && dragging) {
            NativeGestureUtil.notifyNativeGestureEnded(this, ev);
            dragging = false;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void handleToggle(ActionBarDrawerToggle toolbarDrawerToggle) {
        this.toolbarDrawerToggle = toolbarDrawerToggle;
    }

    static class ChangeOpenEvent extends Event<DrawerLayoutView.ChangeOpenEvent> {
        private final boolean open;
        private final int eventCount;
        public ChangeOpenEvent(int viewId, boolean open, int eventCount) {
            super(viewId);
            this.open = open;
            this.eventCount = eventCount;
        }

        @Override
        public String getEventName() {
            return "topChangeOpen";
        }

        @Override
        public void dispatch(RCTEventEmitter rctEventEmitter) {
            WritableMap event = Arguments.createMap();
            event.putBoolean("open", this.open);
            event.putInt("eventCount", this.eventCount);
            rctEventEmitter.receiveEvent(getViewTag(), getEventName(), event);
        }
    }
}
