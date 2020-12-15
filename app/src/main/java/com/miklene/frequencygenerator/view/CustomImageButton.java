package com.miklene.frequencygenerator.view;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

public class CustomImageButton extends AppCompatImageButton {

    private static final long REPEAT_INTERVAL = 150L; // интервал повтора в миллисекундах
    private long lastAction = 0L;

    public CustomImageButton(@NonNull Context context) {
        super(context);
    }

    public CustomImageButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        long currTime = SystemClock.uptimeMillis();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastAction = currTime;
                // код короткого нажатия
                break;
            case MotionEvent.ACTION_MOVE:
                if (currTime - lastAction >= REPEAT_INTERVAL) {
                    lastAction = currTime;
                    // код действия при удержании
                }
                break;
        }
        return true;
    }

    // Because we call this from onTouchEvent, this code will be executed for both
    // normal touch events and for when the system calls this using Accessibility
    @Override
    public boolean performClick() {
        super.performClick();
        doSomething();
        return true;
    }

    private void doSomething() {

    }
}
