package slide;

import static com.nineoldandroids.view.ViewHelper.setTranslationX;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
/**
 * listtener 
 * @author cyy
 *
 */
public class ExpandSlideListener implements OnTouchListener{

	private static final String TAG = "ExpandSlideListener";
	
	private static final int animateTime =  200; //动画时间
	
	private static final int INVALID_POINTER = -1;
	
	private static final int SLIDING_STATE_NONE = 0;// no sliding
	private static final int SLIDING_STATE_MANUAL = 1;// manual sliding
	private static final int SLIDING_STATE_AUTO = 2;// auto sliding

	private int mTouchSlop;
	private int mScrollState;
	private int mDownPosition ; //按下时的item位置
	private int mDownMotionX; 
	
	private ExpandSlideListView expandSlideListView;
	
	private int mActivePointerId;
	private int delatX = 0;
	private int downPositionX; //点击是的x位置
	
	private SlideItem mSlideItem;
	
	private VelocityTracker mVelocityTracker;
	
	public ExpandSlideListener(ExpandSlideListView expandSlideListView){
		this.expandSlideListView = expandSlideListView;
		ViewConfiguration configuration = ViewConfiguration.get(expandSlideListView.getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
	}
	
	//自动关闭打开的item
	public void closeOpenedItem() {
		if (isOpend()) {
			autoScroll(mSlideItem.offset, false);
		}
	}
	
	void reset() {
		mSlideItem = null;
		mScrollState = SLIDING_STATE_NONE;
	}
	
	//得到打开的item
	public int getOpendPosition() {
		if (isOpend()) {
			return mSlideItem.position;
		}
		return AbsListView.INVALID_POSITION;
	}
	//是否打开状态
	public boolean isOpend() {
		return mSlideItem != null && mSlideItem.isOpen();
	}
	
	/**
	 * 自动滚动  true> open  false > close
	 * @param offset
	 * @param toOpen
	 */
	private void autoScroll(final int offset, final boolean toOpen) {
		mScrollState = SLIDING_STATE_AUTO;
		
		int moveTo = 0;
		
		moveTo = toOpen ? mSlideItem.MaxOffsetX : 0;
		
		if(mSlideItem.leftView !=  null){
//			//左侧滚动
			 ViewPropertyAnimator.animate(mSlideItem.leftView).translationX(moveTo).setDuration(animateTime);
		}
		//前景滚动
		ViewPropertyAnimator.animate(mSlideItem.frontView).translationX(moveTo).setDuration(animateTime).setListener(new Animator.AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				if(mSlideItem == null){
					throw new NullPointerException("SlideItem === null");
				}
				
				if(toOpen){
					mSlideItem.offset = mSlideItem.MaxOffsetX;
				}else{
					mSlideItem.offset = 0;
				}
				
				finishScroll();
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	/**
	 * 完成滚动
	 */
	private void finishScroll(){
		mScrollState = SLIDING_STATE_NONE;
		if(mSlideItem.preOffsetX != mSlideItem.offset){
			if(mSlideItem.preOffsetX != 0){
				boolean left = mSlideItem.preOffsetX > 0 && mSlideItem.preOffsetX <= mSlideItem.MaxOffsetX;
				
				//关闭回调方法
				//expandSlideListView.onClosed(mSlideItem.position, left);
			}
			if (mSlideItem.offset != 0) {// Current sliding has open left or
				// right back view.So wo should
				// norify opend
				boolean left = mSlideItem.offset > 0 && mSlideItem.offset <= mSlideItem.MaxOffsetX;
				//打开回调
				//expandSlideListView.onOpend(mSlideItem.position, left);
			}
		}
		
		if (mSlideItem.offset != 0) {
			//mSlideItem.frontView.setOpend(true);
			mSlideItem.preOffsetX = mSlideItem.offset;
			mSlideItem.preDelatX = 0;
		} else {
			//mSlideItem.frontView.setOpend(false);
//			mSlideItem.child.setLeftBackViewShow(false);
//			mSlideItem.child.setRightBackViewShow(false);
			mSlideItem = null;
		}
		
	}
	
	boolean onInterceptTouchEvent(MotionEvent ev){
		int action = MotionEventCompat.getActionMasked(ev);
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if(isInSliding()){
				return true;
			}
			mDownPosition = AbsListView.INVALID_POSITION;
			mDownMotionX = 0;
			mActivePointerId = INVALID_POINTER;
			int position = expandSlideListView.pointToPosition((int) ev.getX(), (int) ev.getY());
			
			if (position == AbsListView.INVALID_POSITION) {
				break;
			}
			boolean allowSlide = expandSlideListView.getAdapter().isEnabled(position)
					&& expandSlideListView.getAdapter().getItemViewType(position) >= 0;
			if (allowSlide) {
				mDownPosition = position;
				mActivePointerId = ev.getPointerId(0);
				mDownMotionX = (int) ev.getX();
				initOrResetVelocityTracker();
				mVelocityTracker.addMovement(ev);
			}
			
			break;

		case MotionEvent.ACTION_MOVE:
			if (mDownPosition == AbsListView.INVALID_POSITION) {
				break;
			}
//			if (expandSlideListView.isInScroll()) {
//				break;
//			}
			int pointerIndex = getPointerIndex(ev);
			// get scroll speed
			initVelocityTrackerIfNotExists();
			mVelocityTracker.addMovement(ev);
			mVelocityTracker.computeCurrentVelocity(1000);
			float velocityX = Math.abs(mVelocityTracker.getXVelocity(mActivePointerId));
			float velocityY = Math.abs(mVelocityTracker.getYVelocity(mActivePointerId));
			// whether is scroll on x axis
			boolean isScrollX = velocityX > velocityY;
			
			int distance = Math.abs((int) ev.getX(pointerIndex) - mDownMotionX);

			if (isScrollX && distance > mTouchSlop) {
				ViewParent parent = expandSlideListView.getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
				}
				mScrollState = SLIDING_STATE_MANUAL;
				return true;
			}
			
			break;
		default:
			break;
		}
		return false;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		if (!expandSlideListView.isEnabled()) {
			return false;
		}
		// TODO Auto-generated method stub
		int action = MotionEventCompat.getActionMasked(event);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			
			if(isInSliding()){
				return true;
			}	
			
			break;
	
		case MotionEvent.ACTION_MOVE:
			Log.d(TAG, "ontouch move");
		//	if()
			Log.d(TAG, ">>>>position ====="+ mDownPosition);
			if(mDownPosition == AbsListView.INVALID_POSITION){
				break;
			}
			
			View view = expandSlideListView.getChildAt(mDownPosition - expandSlideListView.getFirstVisiblePosition());
			if  ( !(view instanceof CoustomerItemView)) {
				break;
			}
			
			int pointerIndex = getPointerIndex(event);
			if (mScrollState == SLIDING_STATE_MANUAL) {
				if (mSlideItem == null) {
					
					mSlideItem = new SlideItem(mDownPosition);
				}
				
				int deltaX = (int) event.getX(pointerIndex) - mDownMotionX;
				int nextOffset = deltaX - mSlideItem.preDelatX + mSlideItem.offset;
				mSlideItem.preDelatX = deltaX;
				if (nextOffset < mSlideItem.minOffsetX) {
					nextOffset = mSlideItem.minOffsetX;
				}
				if (nextOffset > mSlideItem.MaxOffsetX) {
					nextOffset = mSlideItem.MaxOffsetX;
				}
				if (mSlideItem.offset != nextOffset) {
					mSlideItem.offset = nextOffset;
					move(nextOffset);
				}
				return true;
				
			}else{
				initVelocityTrackerIfNotExists();
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				float velocityX = Math.abs(mVelocityTracker.getXVelocity(mActivePointerId));
				float velocityY = Math.abs(mVelocityTracker.getYVelocity(mActivePointerId));
				// whether is scroll on x axis
				boolean isScrollX = velocityX > velocityY;
				// get scroll distance
				int distance = Math.abs((int) event.getX(pointerIndex) - mDownMotionX);

				if (isScrollX && distance > mTouchSlop) {
					ViewParent parent = expandSlideListView.getParent();
					if (parent != null) {
						parent.requestDisallowInterceptTouchEvent(true);
					}
					mScrollState = SLIDING_STATE_MANUAL;
					return true;
				}
			}
			
			break;
			
		case MotionEvent.ACTION_UP:
			
			int offsetX = (int) (event.getX() - delatX);
			Log.d(TAG, "ontouch up"+ offsetX +" "+delatX);
			
			if (mDownPosition == AbsListView.INVALID_POSITION) {
				break;
			}
			if (mSlideItem == null) {
				break;
			}
			
			if (mScrollState == SLIDING_STATE_MANUAL) {
				int pointIndex = getPointerIndex(event);

				int deltaX = (int) event.getX(pointIndex) - mDownMotionX;
				if (deltaX == 0) {// sliding distance equals 0
					reset();
					return true;
				}
				
				if (mSlideItem.offset == 0 || mSlideItem.offset == mSlideItem.minOffsetX || mSlideItem.offset == mSlideItem.MaxOffsetX) {
					finishScroll();
					return true;
				}
				
				boolean doOpen = false;// 开还是关
				
				boolean distanceGreater = Math.abs(mSlideItem.offset - mSlideItem.preOffsetX) > Math.abs(mSlideItem.MaxOffsetX)
						/ (float) 4;
				if (mSlideItem.offset - mSlideItem.preOffsetX > 0) {
					doOpen = distanceGreater;
				} else {
					doOpen = !distanceGreater;
				}
					
				autoScroll(mSlideItem.offset, doOpen);
				return true;
			}else{
				//if (mSlideListView.isInScrolling()) {
				//	closeOpenedItem();
				//}
			}
			
			break;
			
			
		default:
			mScrollState = SLIDING_STATE_NONE;
			break;
		}
		
		return false;
	}
	
	// 位移制定的距离
	private void move(int offset){
		ViewHelper.setTranslationX(mSlideItem.frontView, offset);
		if (mSlideItem.leftView != null) {
			//mSlideItem.child.setLeftBackViewShow(true);
			//SlideAction leftAction = mSlideListView.getSlideLeftAction();
			//if (leftAction == SlideAction.SCROLL) {
				setTranslationX(mSlideItem.leftView,  offset);
		//	}
		}
//		if (mSlideItem.rightBackView != null) {
//			mSlideItem.child.setRightBackViewShow(false);
//		}
	}
	
	//判断是否在滚动
	boolean isInSliding() {
		return mScrollState != SLIDING_STATE_NONE;
	}
	
	private void initOrResetVelocityTracker() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		} else {
			mVelocityTracker.clear();
		}
	}

	private void initVelocityTrackerIfNotExists() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
	}
	
	private int getPointerIndex(MotionEvent event) {
		int pointerIndex = event.findPointerIndex(mActivePointerId);
		if (pointerIndex == INVALID_POINTER) {
			pointerIndex = 0;
			mActivePointerId = event.getPointerId(pointerIndex);
		}
		return pointerIndex;
	}

	
	private class SlideItem{
		
		private final int position; // 滑动item的位置
		
		private CoustomerItemView mSlideItem;
		
		private int preDelatX;
		
		private int offset; //记录最终的位移
		
		private int preOffsetX;//上一次的位移 
		
		private int nextOffsetX; //下一次的位移
		
		private final int minOffsetX;
		
		private final int MaxOffsetX;
		
		private View leftView;
		
		private View frontView;
		
		
		public SlideItem(int position){
			this.position = position;
			
			mSlideItem = (CoustomerItemView) expandSlideListView.getChildAt(position - expandSlideListView.getFirstVisiblePosition());
			if(mSlideItem == null){
				throw new NullPointerException("mSlideitem == null");
			}
			leftView = mSlideItem.leftView;
			frontView = mSlideItem.frontView;
			minOffsetX = 0;
			MaxOffsetX = mSlideItem.leftView.getWidth();
		}
		
		private Boolean isOpen(){
			return offset != 0;
		}
	}

}
