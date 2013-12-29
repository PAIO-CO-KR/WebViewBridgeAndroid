/**
 * This is for PC web browser can handle rpcStub.
 *
 */
var rpcStub = (function(){
	var testResult = {};
	
	var request = function(method, params, callback, context) {
		if(typeof(callback) == 'function') {
			callback.call(context, testResult[method]);
		}
	};
	
	var putTestResponse = function(method, result) {
		testResult[method] = result;
	}
	
	//make it sure this won't override rpcStub.
	if(window.jsonRpc) {
		return rpcStub;
	} else {
		return {
			request: request,
			response: null,
			putTestResponse: putTestResponse
		};
	}
}());