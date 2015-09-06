package slide;


import com.example.slideexpandlist.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CoustomerItemView extends RelativeLayout{

	private static final String TAG = "CoustomerItemView";
	private Context context;
	public LinearLayout frontView ,leftView;
	private int mOffset = 0;
	
	//默认 布局
	public CoustomerItemView(Context context) {
		super(context);
		this.context = context;
		init(R.layout.item);
	}
	 
	/**
	 * send layout id
	 * 布局根视图有两个子布局 ID 命名为front_view and left_view
	 * @param context
	 * @param layout
	 */
	public CoustomerItemView(Context context , int layout) {
		super(context);
		this.context = context;
		init(layout);
	}
	
	private void init(int layout) {
		View view = LayoutInflater.from(context).inflate(layout, this);		
		frontView = (LinearLayout) view.findViewById(R.id.front_view);
		leftView = (LinearLayout) view.findViewById(R.id.left_view);		
		Log.d(TAG, leftView+"");
		
	}

//	private void addLeftView(){
//		LinearLayout layout = new LinearLayout(context);
//		RelativeLayout.LayoutParams lp = new LayoutParams(
//				dip2px(context, 100) , dip2px(context, 50));
//		//lp.addRule(RelativeLayout.LEFT_OF ,);
//		
//		layout.setBackgroundColor(Color.RED);
//		addView(layout, lp);
//		
//		
//	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int parentWidthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
		int parentHeightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
	
		if(leftView != null){
			RelativeLayout.LayoutParams params = (LayoutParams) leftView.getLayoutParams();
			
			int widthSpec = ViewGroup.getChildMeasureSpec(parentWidthSpec, getPaddingLeft() + getPaddingRight() + params.leftMargin
					+ params.rightMargin, params.width);
			int heightSpec = ViewGroup.getChildMeasureSpec(parentHeightSpec, getPaddingTop() + getPaddingBottom() + params.topMargin
					+ params.bottomMargin, params.height);
			leftView.measure(widthSpec, heightSpec);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		
//		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
//			frontView.layout(frontView.getLeft() + mOffset, frontView.getTop(), frontView.getRight() + mOffset, frontView.getBottom());
//		}
		if(leftView != null){
			int top = (b - t - leftView.getMeasuredHeight()) / 2;
			
			int bo= leftView.getMeasuredHeight();
			int right = frontView.getLeft();
			leftView.layout(frontView.getLeft() - leftView.getMeasuredWidth(), top, right,
						top + bo);
		}
	}
	
	public void setOffset(int offset) {
		if (mOffset == offset) {
			return;
		}
		mOffset = offset;
		requestLayout();
	}
	
	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
}
