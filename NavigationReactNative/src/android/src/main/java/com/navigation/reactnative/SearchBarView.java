package com.navigation.reactnative;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.views.view.ReactViewGroup;
import com.google.android.material.appbar.AppBarLayout;

public class SearchBarView extends ReactViewGroup {
    final SearchView searchView;
    private MenuItem menuItem;
    boolean bottomBar = false;
    String pendingText;
    int nativeEventCount;
    int mostRecentEventCount;
    int nativeActiveEventCount;
    int mostRecentActiveEventCount;

    public SearchBarView(Context context) {
        super(context);
        searchView = new SearchView(context);
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        AppBarLayout.ScrollingViewBehavior behavior = new AppBarLayout.ScrollingViewBehavior();
        params.setBehavior(behavior);
        setLayoutParams(params);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ReactContext reactContext = (ReactContext) getContext();
                EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, getId());
                eventDispatcher.dispatchEvent(new QueryEvent(getId(), searchView.getQuery().toString()));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                nativeEventCount++;
                ReactContext reactContext = (ReactContext) getContext();
                EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, getId());
                eventDispatcher.dispatchEvent(new ChangeTextEvent(getId(), newText, nativeEventCount));
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ActionView actionView = (ActionView) searchView.getParent();
                    if (actionView.getChildAt(1) instanceof ImageButton)
                        actionView.setCollapseButton((ImageButton) actionView.getChildAt(1));
                }
            }
        });
    }

    void setQuery(String query) {
        pendingText = query;
    }

    void setActive(boolean active) {
        int eventLag = nativeActiveEventCount - mostRecentActiveEventCount;
        if (eventLag == 0 && menuItem != null && menuItem.isActionViewExpanded() != active)
            if (active)
                menuItem.expandActionView();
            else
                menuItem.collapseActionView();
    }

    void setBarTintColor(Integer barTintColor) {
        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (barTintColor != null) {
            searchAutoComplete.setBackgroundColor(barTintColor);
        } else {
            searchAutoComplete.setBackground(null);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(focusAndKeyboard);
        ActionView actionView = null;
        ViewGroup view = (ViewGroup) getParent();
        for(int i = 0; i < view.getChildCount() && actionView == null; i++) {
            if (!bottomBar && view.getChildAt(i) instanceof NavigationBarView) {
                NavigationBarView navigationBarView = (NavigationBarView) view.getChildAt(i);
                for (int j = 0; j < navigationBarView.getChildCount(); j++) {
                    if (navigationBarView.getChildAt(j) instanceof ActionView)
                        actionView = (ActionView) navigationBarView.getChildAt(j);
                }
            }
            if (bottomBar && view.getChildAt(i) instanceof ActionView)
                actionView = (ActionView) view.getChildAt(i);
        }
        if (actionView != null) {
            actionView.setOnSearchListener(new ActionView.OnSearchListener() {
                @Override
                public void onSearchAdd(MenuItem searchMenuItem) {
                    menuItem = searchMenuItem;
                    searchMenuItem.setActionView(searchView);
                }

                @Override
                public void onSearchExpand() {
                    nativeActiveEventCount++;
                    ReactContext reactContext = (ReactContext) getContext();
                    EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, getId());
                    eventDispatcher.dispatchEvent(new ChangeActiveEvent(getId(), true, nativeActiveEventCount));
                }

                @Override
                public void onSearchCollapse() {
                    nativeActiveEventCount++;
                    ReactContext reactContext = (ReactContext) getContext();
                    EventDispatcher eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, getId());
                    eventDispatcher.dispatchEvent(new ChangeActiveEvent(getId(), false, nativeActiveEventCount));
                }
            });
        }
    }

    void onAfterUpdateTransaction() {
        int eventLag = nativeEventCount - mostRecentEventCount;
        if (eventLag == 0 && !searchView.getQuery().toString().equals(pendingText))
            searchView.setQuery(pendingText, true);
    }

    private final Runnable focusAndKeyboard = new Runnable() {
        @Override
        public void run() {
            if (searchView.requestFocus()) {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null)
                    inputMethodManager.showSoftInput(searchView.findFocus(), 0);
            }
        }
    };

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    static class ChangeTextEvent extends Event<ChangeTextEvent> {
        private final String text;
        private final int eventCount;

        public ChangeTextEvent(int viewId, String text, int eventCount) {
            super(viewId);
            this.text = text;
            this.eventCount = eventCount;
        }

        @Override
        public String getEventName() {
            return "topOnChangeText";
        }

        @Override
        public void dispatch(RCTEventEmitter rctEventEmitter) {
            WritableMap event = Arguments.createMap();
            event.putString("text", this.text);
            event.putInt("eventCount", this.eventCount);
            rctEventEmitter.receiveEvent(getViewTag(), getEventName(), event);
        }
    }

    static class ChangeActiveEvent extends Event<SearchBarView.ChangeActiveEvent> {
        private final boolean active;
        private final int eventCount;
        public ChangeActiveEvent(int viewId, boolean active, int eventCount) {
            super(viewId);
            this.active = active;
            this.eventCount = eventCount;
        }

        @Override
        public String getEventName() {
            return "topOnChangeActive";
        }

        @Override
        public void dispatch(RCTEventEmitter rctEventEmitter) {
            WritableMap event = Arguments.createMap();
            event.putBoolean("active", this.active);
            event.putInt("eventCount", this.eventCount);
            rctEventEmitter.receiveEvent(getViewTag(), getEventName(), event);
        }
    }

    static class QueryEvent extends Event<SearchBarView.ChangeTextEvent> {
        private final String text;

        public QueryEvent(int viewId, String text) {
            super(viewId);
            this.text = text;
        }

        @Override
        public String getEventName() {
            return "topOnQuery";
        }

        @Override
        public void dispatch(RCTEventEmitter rctEventEmitter) {
            WritableMap event = Arguments.createMap();
            event.putString("text", this.text);
            rctEventEmitter.receiveEvent(getViewTag(), getEventName(), event);
        }
    }
}
