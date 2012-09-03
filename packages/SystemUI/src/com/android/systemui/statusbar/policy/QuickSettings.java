/*
 * Copyright (C) 2012 Crossbones Software
 * Original author qberticus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.android.systemui.R;


public class QuickSettings {
	protected final View anchor;
	private final PopupWindow window;
	private View root;
	private Drawable background = null;
	private final WindowManager windowManager;

    /**
     * @param anchor
     *            the view that the QuickSettingsController will be displaying 'from'
     */
    public QuickSettings(View anchor) {
        this.anchor = anchor;
        this.window = new PopupWindow(anchor.getContext());

        // when a touch even happens outside of the window
        // make the window go away
        this.window.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    QuickSettings.this.window.dismiss();
                    return true;
                }
                return false;
            }
        });

        this.windowManager = (WindowManager) this.anchor.getContext().getSystemService(Context.WINDOW_SERVICE);
        onCreate();
    }

    /**
     * Anything you want to have happen when created. Probably should create a view and setup the event listeners on
     * child views.
     */
    protected void onCreate() {}

    /**
     * In case there is stuff to do right before displaying.
     */
    protected void onShow() {}

    private void preShow() {
        if(this.root == null) {
            throw new IllegalStateException("setContentView was not called with a view to display.");
        }
        onShow();

        if(this.background == null) {
            this.window.setBackgroundDrawable(new BitmapDrawable());
        } else {
            this.window.setBackgroundDrawable(this.background);
        }

        // if using PopupWindow#setBackgroundDrawable this is the only values of the width and hight that make it work
        // otherwise you need to set the background of the root viewgroup
        // and set the popupwindow background to an empty BitmapDrawable
        this.window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        this.window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        this.window.setTouchable(true);
        this.window.setFocusable(true);
        this.window.setOutsideTouchable(true);

        this.window.setContentView(this.root);
    }

    public void setBackgroundDrawable(Drawable background) {
        this.background = background;
    }

    /**
     * Sets the content view. Probably should be called from {@link onCreate}
     * 
     * @param root
     *            the view the popup will display
     */
    public void setContentView(View root) {
        this.root = root;
        this.window.setContentView(root);
    }

    /**
     * Will inflate and set the view from a resource id
     * 
     * @param layoutResID
     */
    public void setContentView(int layoutResID) {
        LayoutInflater inflator =
                (LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	this.setContentView(inflator.inflate(layoutResID, null));
    }

    /**
     * If you want to do anything when {@link dismiss} is called
     * 
     * @param listener
     */
    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        this.window.setOnDismissListener(listener);
    }

    /**
     * Displays like a popdown menu from the anchor view
     */
    public void showLikePopDownMenu() {
        this.showLikePopDownMenu(0, 0);
    }

    /**

     * Displays like a popdown menu from the anchor view.
     * 
     * @param buttonLeft
     *            location of QuickSettings button left border in relation to parent view
     * @param buttonWidth
     *            width of QuickSettings button
     */

	public void showLikePopDownMenu(int buttonLeft, int buttonWidth) {
        this.preShow();

        this.window.setAnimationStyle(R.style.Animation_StatusBar_QuickSettingsPopDownMenu);
        int xOffset = (0 - (buttonLeft - buttonWidth));
        this.window.showAsDropDown(this.anchor, xOffset, 0);
    }

    public void dismiss() {
        this.window.dismiss();
    }
}
