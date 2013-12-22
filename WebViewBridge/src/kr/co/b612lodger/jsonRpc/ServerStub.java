package kr.co.b612lodger.jsonRpc;

import java.util.HashMap;

import kr.co.b612lodger.jsonRpc.core.MethodHandler;
import kr.co.b612lodger.jsonRpc.core.RequestResponseFactory;
import kr.co.b612lodger.jsonRpc.model.Request;
import kr.co.b612lodger.jsonRpc.model.Response;

/**
 * Server-side(android native) RPC Stub.
 * It handles JSON-RPC v2 requests.
 * 
 * InjectMethodHandlers with specific RequestParams & method name.
 * injected method handlers will handle requests in json string format.
 *  
 * @author apple
 *
 */
@SuppressWarnings("rawtypes")
public class ServerStub {
	
	/**
	 * MethodHandler hash map.
	 * execute function of given methodHandler will be processed according to request's method name.
	 * 
	 */
	protected HashMap<String, MethodHandler> mMethodHandlerMap;
	
	protected RequestResponseFactory mRequestResponseFactory;
	
	
	/**
	 * Initializer
	 */
	public ServerStub() {
		mMethodHandlerMap = new HashMap<String, MethodHandler>();
		mRequestResponseFactory = new RequestResponseFactory(this);
	}
	
	
	/**
	 * Register method to handle request.
	 * the method's execute function with methodName will be invoked according to request's method name.
	 * 
	 * @param methodName same string value to request's method name you desire to handle.
	 * @param method 
	 */
	public void registMethod(String methodName, MethodHandler method) {
		if(method == null) {
			return;
		}
		mMethodHandlerMap.put(methodName, method);
	}
	
	
	/**
	 * Execute given string in JSON-RPC v2 format 
	 * 
	 * @param request string representation of JSON-RPC v2
	 * @return
	 */
	public String execute(String request) {
		
		Request<?>[] requests = mRequestResponseFactory.makeRequest(request);
		Response<?>[] responses = new Response[requests.length];
		for(int i = 0; i < requests.length; i++) {
			responses[i] = execute(requests[i]);
		}
		return mRequestResponseFactory.deserializeResponse(responses);
	}
	
	
	/**
	 * Execute single request
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Response<?> execute(Request request) {
		if(request == null) {
			Response res = new Response();
			res.setError(new Response.Error(-32600, "The JSON sent is not a valid Request object."));
			return res;
		}
		
		if(!mMethodHandlerMap.containsKey(request.getMethod())) {
			Response res = new Response();
			res.setError(new Response.Error(-32601, "The method does not exist / is not available."));
			return res;
		}
		
		MethodHandler methodHandler = mMethodHandlerMap.get(request.getMethod());
		Object result = methodHandler.execute(request.getParams());
		Response response = new Response();
		response.setId(request.getId());
		response.setResult(result);
		return response;
	}
	
	
	/**
	 * Utility class to get method's parameter type.
	 * 
	 * @param methodName
	 * @return
	 */
	public Class findAvailableParamClass(String methodName) {
		if(!mMethodHandlerMap.containsKey(methodName)) {
			return null;
		}
		
		MethodHandler methodHandler = mMethodHandlerMap.get(methodName);
		return methodHandler.getParamClass();
	}
	
	
	
}
