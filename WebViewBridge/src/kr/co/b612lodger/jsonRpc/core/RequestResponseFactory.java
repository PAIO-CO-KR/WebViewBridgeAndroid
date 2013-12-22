package kr.co.b612lodger.jsonRpc.core;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import kr.co.b612lodger.jsonRpc.ServerStub;
import kr.co.b612lodger.jsonRpc.model.Request;
import kr.co.b612lodger.jsonRpc.model.Response;

public class RequestResponseFactory {
	
	private ServerStub mServerStub;
	
	public RequestResponseFactory(ServerStub serverStub) {
		mServerStub = serverStub;
	}
	
	
	/**
	 * Parse JSON-RPC v2 string into Request object.
	 * covers single and batch request format.
	 * 
	 * @param request
	 * @return
	 */
	public Request<?>[] makeRequest(String request) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Request[].class, new JsonDeserializer<Request<?>[]>() {
			
			@Override
			public Request<?>[] deserialize(JsonElement json, Type typeOfT,
					JsonDeserializationContext context) throws JsonParseException {
				Request<?>[] requests = null;
				
				if(json.isJsonArray()) {
					JsonArray array = json.getAsJsonArray();
					requests = new Request[array.size()];
					for(int i = 0; i < array.size(); i++) {
						requests[i] = makeSingleRequest(array.get(i).toString());
					}
				} else {
					requests = new Request[1];
					requests[0] = makeSingleRequest(json.getAsJsonObject().toString());
				}
				
				return requests;
			}
		});
		
		return gson.create().fromJson(request, Request[].class);
	}
	
	
	
	
	/**
	 * Parse JSON-RPC v2 string into Request object.
	 * only covers single request format.
	 * 
	 * @param request string representation of JSON-RPC v2
	 * @return
	 */
	public Request<?> makeSingleRequest(String string) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Request.class, new JsonDeserializer<Request<?>>() {
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Request<?> deserialize(JsonElement json, Type typeOfT,
					JsonDeserializationContext context) throws JsonParseException {
				
				Request request = new Request();
				
				JsonObject jsonObject = json.getAsJsonObject();
				String methodName = jsonObject.get("method").getAsString();
				request.setMethod(methodName);
				request.setJsonrpc(jsonObject.get("jsonrpc").getAsString());
				request.setId(jsonObject.get("id").getAsInt());
				
				Object requestParams = null;
				Class<?> cls = mServerStub.findAvailableParamClass(methodName);
				if(cls != null) {
					Gson gson = new Gson();
					requestParams = gson.fromJson(jsonObject.get("params"), cls);
				}
				request.setParams(requestParams);
				
				return request;
			}
		});
		
		return gson.create().fromJson(string, Request.class);
	}
	
	
	
	/**
	 * Make JSON-RPC v2 string out of Request object.
	 * 
	 * @param request
	 * @return
	 */
	public String deserializeRequest(Request<?> request) {
		Type type = new TypeToken<Request<?>>() {}.getType();
		return new Gson().toJson(request, type);
	}
	
	
	
	/**
	 * Make JSON-RPC v2 string out of Request object.
	 * 
	 * @param request
	 * @return
	 */
	public String deserializeRequest(Request<?>[] requests) {
		if(requests != null && requests.length > 0) {
			if(requests.length == 1) {
				Type type = new TypeToken<Request<?>>() {}.getType();
				return new Gson().toJson(requests[0], type);
			} else {
				Type type = new TypeToken<Request<?>[]>() {}.getType();
				return new Gson().toJson(requests, type);
			}
		}
		return null;
	}
	
	
	
	/**
	 * Make JSON-RPC v2 string out of Response object.
	 * 
	 * @param response
	 * @return
	 */
	public String deserializeResponse(Response<?> response) {
		return new Gson().toJson(response, Response.class);
	}
	
	
	
	/**
	 * Make JSON-RPC v2 string out of Response object.
	 * 
	 * @param response
	 * @return
	 */
	public String deserializeResponse(Response<?>[] responses) {
		if(responses != null && responses.length > 0) {
			if(responses.length == 1) {
				return new Gson().toJson(responses[0], Response.class);
			} else {
				return new Gson().toJson(responses, Response[].class);
			}
		}
		return null;
	}
}
