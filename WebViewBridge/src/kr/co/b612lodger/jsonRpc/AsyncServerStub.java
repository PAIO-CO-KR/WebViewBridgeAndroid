package kr.co.b612lodger.jsonRpc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.HandlerThread;

public class AsyncServerStub extends ServerStub {
	
	protected Handler mMethodHandler;
	
	protected OnResponseListener mOnResponseListener;
	
	public AsyncServerStub() {
		super();
		HandlerThread methodHandlerThread = new HandlerThread("MethodHandlerThread");
		methodHandlerThread.start();
		mMethodHandler = new Handler(methodHandlerThread.getLooper());
	}
	
	
	public String execute(final String request, int timeoutMillies) throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<String> future = executor.submit(new Callable<String>() {
			@Override
	        public String call() throws Exception {
				Thread.sleep(1); //To be interrupted.
				return AsyncServerStub.super.execute(request);
	        }
		});
		
		String response;
		try {
			response = future.get(10000, TimeUnit.MILLISECONDS);
			
		} catch (Exception e) {
			future.cancel(true);
			throw e;
		}
		return response;
	}
	
	
	public void executeAsync(final String request) {
		final Handler callerThreadHandler = new Handler();
		mMethodHandler.post(new Runnable() {
			
			@Override
			public void run() {
				final String[] response = new String[1];
				try {
					response[0] = AsyncServerStub.this.execute(request, 10000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				callerThreadHandler.post(new Runnable() {
					@Override
					public void run() {
						if(mOnResponseListener != null) {
							mOnResponseListener.onResponse(response[0]);
						}
					}
				});
			}
		});
		
		return;
	}
	
	public void setOnResponseListener(OnResponseListener onResponseListener) {
		mOnResponseListener = onResponseListener;
	}
	
	public static interface OnResponseListener {
		public void onResponse(String response);
	}
}
