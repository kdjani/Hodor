package com.app.hodor;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ViewFlipper;

public class SwipeDetector implements View.OnTouchListener {

    public static enum Action {
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        Start,
        Stop,
        None // when no action was detected
    }

    private static final float HORIZONTAL_MIN_DISTANCE = 150;
    private float downX, upX;
    private Action mSwipeDetected = Action.None;

    SwipeDetector() {
    	
    }
    
    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN: {
	            downX = event.getX();
	            mSwipeDetected = Action.Start;
	            return true; 
	        }
	        case MotionEvent.ACTION_MOVE: {
	            upX = event.getX();
	            
	            float deltaX = downX - upX;
	
	            // horizontal swipe detection
	            if (Math.abs(deltaX) > HORIZONTAL_MIN_DISTANCE) {
	                // left or right
	                if (deltaX <= 0) {
	                    mSwipeDetected = Action.LR;
	                    return true;
	                }
	                if (deltaX > 0) { 
	                    mSwipeDetected = Action.RL;
	                    return true;
	                }
	            } 
	            return false;
	        }
	        case MotionEvent.ACTION_UP:
	        {
	       		 ListView lv = (ListView)(v);
	             int position = lv.pointToPosition((int) event.getX(), (int) event.getY());
	             View item = lv.getChildAt(position - lv.getFirstVisiblePosition());
	        	 if( item != null && (mSwipeDetected == Action.LR  || mSwipeDetected == Action.RL)) {
                     ViewFlipper viewFlipper = (ViewFlipper) item.findViewById(R.id.view_flipper);
                	 viewFlipper.showNext();
	                 return true;
	        	 } else {
	        		 v.performClick();
	        	 }
	        		 
	             mSwipeDetected = Action.None;
	             return false;
	        }
        }
        return false;
    }
}
