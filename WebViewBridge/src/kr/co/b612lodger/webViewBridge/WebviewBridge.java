package kr.co.b612lodger.webViewBridge;

import kr.co.b612lodger.jsonRpc.AsyncServerStub;
import kr.co.b612lodger.jsonRpc.AsyncServerStub.OnResponseListener;
import kr.co.b612lodger.jsonRpc.core.MethodHandler;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Bitmap;

@SuppressLint("SetJavaScriptEnabled")
public class WebviewBridge implements OnResponseListener {
	
	private static final String TAG = WebviewBridge.class.getName();
	
	private static final String JSTAG = WebviewBridge.class.getName() + "_JS";
	
	private static final String webviewJsCode = "var rpcStub=function(){function d(a){void 0!==b[a.id]&&null!==b[a.id]&&(b[a.id].call(e[a.id],a.result),b[a.id]=void 0,e[a.id]=void 0)}var b={},e={};return{request:function(a,d,c,f){for(a={jsonrpc:\"2.0\",method:a,params:d,id:Math.floor(1E6*Math.random())+1};void 0!==b[a.id];)a.id=Math.floor(1E6*Math.random())+1;b[a.id]=c;e[a.id]=f;c=JSON.stringify(a);console.log(c);void 0!==window.jsonRpc?window.jsonRpc.request(c):document.location.href=\"jsonrpc://\"+c},response:function(a){a=\"string\"==typeof a||a instanceof String?JSON.parse(a):a;if(a instanceof Array)for(responseObj in a)d(responseObj);else d(a)},putTestResponse:function(a,b){}}}();";
	
	private WebView mWebView;
	
	private AsyncServerStub mServerStub;
	
	//request handler. it is supposed to be in main thread.
	private Handler mMainHandler;
	
	
	/**
	 * Constructor
	 */
	public WebviewBridge() {
		mServerStub = new AsyncServerStub();
		mServerStub.setOnResponseListener(this);
	}
	
	
	/**
	 * Bind Webview
	 * @param webview
	 */
	public void bindWebView(WebView webview) {
		if(webview == null) {
			Log.e(TAG, "webview can not be null");
			throw new RuntimeException("webview can not be null");
		}
		
		if(Looper.getMainLooper().getThread() != Thread.currentThread()) {
			Log.w(TAG, "bindWebView must me called on main thread.");
			throw new RuntimeException("bindWebView must me called on main thread.");
		}
		mMainHandler = new Handler();
		
		
		mWebView = webview;
		//TODO debug code. remove this.
		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onConsoleMessage(String message, int lineNumber, String sourceID) {
				Log.d(JSTAG, message + " -- From line "
                        + lineNumber + " of "
                        + sourceID);
			}
			
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Log.d(JSTAG, cm.message() + " -- From line "
						+ cm.lineNumber() + " of "
						+ cm.sourceId() );
				return true;
			}
		});
		mWebView.setWebViewClient(new WebViewClient(){
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.v(TAG, "pushing js code");
				mWebView.loadUrl("javascript:" + webviewJsCode);
			}
		});
		
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		mWebView.addJavascriptInterface(getJavascriptInterface(), "jsonRpc");
	}
	
	
	/**
	 * Register Method.
	 * @param methodName
	 * @param method
	 */
	public void registMethod(String methodName, MethodHandler<?, ?> method) {
		if(mServerStub != null) {
			mServerStub.registMethod(methodName, method);
		}
	}
	
	
	/**
	 * make JavaScript interface. which will be accessible as window.jsonRpc from in WebView DOM object.
	 *   
	 * @return
	 */
	private Object getJavascriptInterface() {
		return new Object() {
			
			@JavascriptInterface
			public void request(final String request) {
				mMainHandler.post(new Runnable() {
					@Override
					public void run() {
						Log.v(TAG, "Request : [" + request + "]");
						mServerStub.executeAsync(request);
					}
				});
			}
		};
	}
	
	
	@Override
	public void onResponse(String response) {
		Log.v(TAG, "Response : [" + response + "]");
		if(mWebView != null) {
			mWebView.loadUrl("javascript:rpcStub.response(" + response + ")");
		}
	}
	
	
}
