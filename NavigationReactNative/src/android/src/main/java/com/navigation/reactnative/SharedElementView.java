package com.navigation.reactnative;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;

import androidx.transition.ChangeBounds;
import androidx.transition.ChangeClipBounds;
import androidx.transition.ChangeImageTransform;
import androidx.transition.ChangeTransform;
import androidx.transition.Transition;
import androidx.transition.TransitionSet;

import com.google.android.material.transition.MaterialContainerTransform;

public class SharedElementView extends ViewGroup {
    long duration = -1;
    int fadeMode = MaterialContainerTransform.FADE_MODE_IN;

    public SharedElementView(Context context) {
        super(context);
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        if (index == 0) child.setTransitionName("element__" + this.getTransitionName());
    }

    @Override
    public void endViewTransition(View view) {
        super.endViewTransition(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            view.setTransitionAlpha(1);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final SceneView scene = getScene();
        if (scene == null) return;
        scene.sharedElements.add(this);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                if (scene.sharedElementMotion != null)
                    scene.sharedElementMotion.load(SharedElementView.this);
                return true;
            }
        });
    }

    Transition getTransition(boolean containerTransform, boolean enter) {
        if (!containerTransform) {
            TransitionSet transition = new TransitionSet();
            transition.addTransition(new ChangeBounds());
            transition.addTransition(new ChangeTransform());
            transition.addTransition(new ChangeClipBounds());
            transition.addTransition(new ChangeImageTransform());
            return transition;
        } else {
            MaterialContainerTransform transition = new MaterialContainerTransform();
            transition.setDuration(duration);
            transition.setFadeMode(fadeMode);
            transition.setTransitionDirection(enter ? MaterialContainerTransform.TRANSITION_DIRECTION_ENTER : MaterialContainerTransform.TRANSITION_DIRECTION_RETURN);
            transition.setScrimColor(Color.TRANSPARENT);
            return transition;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        SceneView scene = getScene();
        if (scene != null) scene.sharedElements.remove(this);
    }

    private SceneView getScene() {
        ViewParent ancestor = getParent();
        while (ancestor != null && !(ancestor instanceof SceneView))
            ancestor = ancestor.getParent();
        if (ancestor == null)
            return null;
        return (SceneView) ancestor;
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
    }
}
