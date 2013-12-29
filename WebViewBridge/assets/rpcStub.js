/**
 * This js will be minified and injected to webview when you initialize WebviewBridge.
 *
 * you should generate minified code by compiling this with closure compilier and store it to WebviewBridge.java
 */
var rpcStub = (function(){
	var debugEnabled = true;
	var androidRpcBridge = window.jsonRpc;
	var isAndroid = androidRpcBridge !== undefined;

	var callbackMap = {};
	var contextMap = {};
	
	var putTestResponse = function(method, result) {
		return;
	}

	/**
	* Request
	*/
	var request = function(method, params, callback, context) {
		var rpcObj = {
			jsonrpc: '2.0',
			method: method,
			params: params,
			id: randomId()
		};

		//add callback to empty slot
		while(callbackMap[rpcObj.id] !== undefined) {
			rpcObj.id = randomId();
		}
		callbackMap[rpcObj.id] = callback;
		contextMap[rpcObj.id] = context;

		//build json string.
		var rpcString = JSON.stringify(rpcObj);
		log(rpcString);
		
		//android or ios
		if(isAndroid) {
			androidRpcBridge.request(rpcString);
		} else {
			document.location.href = 'jsonrpc://' + rpcString;
		}
	};

	/**
	* Response
	*/
	var response = function(responseString) {
		var responseObjs = (typeof responseString == 'string' || responseString instanceof String) ? 
				JSON.parse(responseString) : responseString;
				
		if(responseObjs instanceof Array) {
			for(responseObj in responseObjs) {
				handleSingleResponse(responseObj);
			}
		} else {
			handleSingleResponse(responseObjs);
		}
	}

	function handleSingleResponse(responseObj) {
		callbackMap[responseObj.id].call(contextMap[responseObj.id], responseObj.result);
		callbackMap[responseObj.id] = undefined;
		contextMap[responseObj.id] = undefined;
	}

	function log(message) {
		if(debugEnabled) {
			console.log(message);
		}
	}

	function randomId() {
		return Math.floor(Math.random() * 1E6) + 1;
	}

	return {
		request: request,
		response: response,
		putTestResponse: putTestResponse
	};
}());