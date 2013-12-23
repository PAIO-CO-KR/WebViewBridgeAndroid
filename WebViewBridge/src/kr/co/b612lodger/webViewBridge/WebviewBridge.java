package kr.co.b612lodger.webViewBridge;

import kr.co.b612lodger.jsonRpc.ServerStub;
import android.annotation.SuppressLint;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

@SuppressLint("SetJavaScriptEnabled")
public class WebviewBridge {
	
	private static final String TAG = WebviewBridge.class.getName();
	
	private WebView mWebView;
	
	private ServerStub mServerStub;
	
	public WebviewBridge(WebView webview, ServerStub serverStub) {
		if(webview == null) {
			Log.e(TAG, "webview can not be null");
			return;
		}
		if(serverStub == null) {
			Log.e(TAG, "serverStub can not be null");
			return;
		}
		
		mWebView = webview;
		mServerStub = serverStub;
		
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		mWebView.addJavascriptInterface(getJavascriptInterface(), "jsonRpc");
	}
	
	
	/**
	 * make JavaScript interface. which will be accessible as window.jsonRpc from in WebView DOM object.
	 *   
	 * @return
	 */
	private Object getJavascriptInterface() {
		return new Object() {
			@SuppressWarnings("unused")
			@JavascriptInterface
			public void request(String request) {
				mServerStub.execute(request);
			}
		};
	}
}
