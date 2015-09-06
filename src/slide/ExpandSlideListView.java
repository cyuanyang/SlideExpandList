package slide;


import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

/**
 * 继承 ExpandAbleListView 的 item 可向左滑动的listView
 * 缺少 打开和关闭的毁掉方法
 * @author chenyuanyang
 *@version 1.0
 */
public class ExpandSlideListView extends ExpandableListView{

	
	private static final String TAG = "ExpandSlideListView";
	private ExpandSlideListener expandSlideListener;
	
	public ExpandSlideListView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init();
	}

	public ExpandSlideListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub\
		init();
	}

	public ExpandSlideListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		expandSlideListener = new ExpandSlideListener(this);
		this.setOnTouchListener(expandSlideListener);
	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			Log.d(TAG, "down>>>>>>>>>>>>>>>>>>>>>>>>.");
//			break;
//
//		default:
//			break;
//		}
//		
//		return super.onTouchEvent(ev);
//	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
		if(isEnabled()){
			int action = MotionEventCompat.getActionMasked(ev);
			if(action == MotionEvent.ACTION_DOWN){
				int downPosition = pointToPosition((int)ev.getX(), (int)ev.getY());
				Log.d(TAG, downPosition+"<<<<<<<<<<<按下的位置 down");
				int opendPosition = expandSlideListener.getOpendPosition();
				
				if (opendPosition != INVALID_POSITION) {
					if (expandSlideListener.isInSliding()) {
						return false;
					}
					// if down position not equals the opend position,drop this
					// motion event and close the opend item
					if (downPosition != opendPosition) {
						expandSlideListener.closeOpenedItem();
						return false;
					}
				}
			}
			if(action == MotionEvent.ACTION_MOVE){
			}
		}
		
		return super.dispatchTouchEvent(ev);
	}
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Log.d(TAG, "on interceptTouchEvent");
		//return true;
		if(isEnabled()){
			if(expandSlideListener.onInterceptTouchEvent(ev)){
				return true;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	private class InnerDataSetObserver extends DataSetObserver{
		@Override
		public void onChanged() {
			super.onChanged();
			closeDirect();
		}

		
	}
	
	private void closeDirect() {
		if (expandSlideListener.isOpend()) {
			postDelayed(new Runnable() {
				@Override
				public void run() {
					expandSlideListener.closeOpenedItem();
				}
			}, 100);
		}else{
			expandSlideListener.reset();
		}
	}
	
	private InnerDataSetObserver mInnerDataSetObserver;
	
	@Override
	public void setAdapter(ExpandableListAdapter adapter) {
		// TODO Auto-generated method stub
		super.setAdapter(adapter);
		if (adapter!=null) {
			mInnerDataSetObserver = new InnerDataSetObserver();
			adapter.registerDataSetObserver(mInnerDataSetObserver);
		}
	}
}
