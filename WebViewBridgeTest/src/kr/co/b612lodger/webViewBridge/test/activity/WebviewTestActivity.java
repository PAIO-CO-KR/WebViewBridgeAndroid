package kr.co.b612lodger.webViewBridge.test.activity;

import kr.co.b612lodger.jsonRpc.AsyncServerStub;
import kr.co.b612lodger.jsonRpc.core.MethodHandler;
import kr.co.b612lodger.webViewBridge.WebviewBridge;
import kr.co.b612lodger.webViewBridge.test.R;
import kr.co.b612lodger.webViewBridge.test.SimpleTest.TestParam;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class WebviewTestActivity extends Activity {
	
	protected static final String TAG = WebviewTestActivity.class.getName();
	
	AsyncServerStub mServerStub;
	
	WebviewBridge mWebviewBridge;
	
	WebView mWebView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initServerStub();
	}
	
	private void initServerStub() {
		
		mWebView = (WebView) findViewById(R.id.webview);
		mServerStub = new AsyncServerStub();
		mServerStub.registMethod("subtract", new MethodHandler<TestParam, Integer>() {
			
			@Override
			public Integer execute(TestParam requestParam) {
				Log.d(TAG, "on execute subtract");
				return requestParam.getSubtrahend() - requestParam.getMinuend();
			}
		});
		
		mWebviewBridge = new WebviewBridge(mWebView, mServerStub);
		
		mWebView.loadUrl("file:///android_asset/www/index.html");
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
}
