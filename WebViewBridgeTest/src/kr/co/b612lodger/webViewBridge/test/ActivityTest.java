package kr.co.b612lodger.webViewBridge.test;

import kr.co.b612lodger.webViewBridge.test.activity.WebviewTestActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

public class ActivityTest extends ActivityInstrumentationTestCase2<WebviewTestActivity> {
	
	WebviewTestActivity mActivity;

	public ActivityTest() {
		super(WebviewTestActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

	    setActivityInitialTouchMode(false);

	    mActivity = getActivity();
	    
	}
	
	@MediumTest
	public void showTest() throws InterruptedException {
		while(true) {
	    	Thread.sleep(1000);
	    }
	}

}
