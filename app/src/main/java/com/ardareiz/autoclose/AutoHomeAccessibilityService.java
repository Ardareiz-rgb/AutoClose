package com.ardareiz.autoclose;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class AutoHomeAccessibilityService extends AccessibilityService {
    public static AutoHomeAccessibilityService instance;

    @Override protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) { }

    @Override public void onInterrupt() { }

    public void goHome() {
        performGlobalAction(GLOBAL_ACTION_HOME);
    }

    @Override public void onDestroy() {
        if (instance == this) instance = null;
        super.onDestroy();
    }
}
