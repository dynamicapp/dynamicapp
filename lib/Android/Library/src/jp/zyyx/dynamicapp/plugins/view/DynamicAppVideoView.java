package jp.zyyx.dynamicapp.plugins.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * @author Zyyx
 *
 */
public class DynamicAppVideoView extends VideoView {

    private int mVideoHeight = 0;
    private int mVideoWidth = 0;

	/**
	 * @param context	activity context
	 */
	public DynamicAppVideoView(Context context) {
		super(context);
	}

	/**
	 * @param context	activity context
	 * @param attrs		attributes from xml
	 */
	public DynamicAppVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context	activity context
	 * @param attrs		attributes from xml
	 * @param defStyle	default styles
	 */
	public DynamicAppVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// super should be called first
		// black screen if not called first
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mVideoWidth, mVideoHeight);
	}
	
    /**
     * @param width		width of the video view
     * @param height	height of the video view
     */
    public void changeVideoSize(int width, int height) {
        mVideoWidth = width;      
        mVideoHeight = height;

        // not sure whether it is useful or not but safe to do so
        getHolder().setFixedSize(width, height); 
        
        requestLayout();
        invalidate();     // very important, so that onMeasure will be triggered
    }
}
