/**
 * This is for PC web browser can handle rpcStub.
 *
 */
var rpcStub = (function(){
	var request = function(method, params, callback) {
		var rpcObj = {
			jsonrpc: '2.0',
			method: method,
			params: params,
			id: randomId()
		};
		
		callback(null);
	};
	
	//make it sure this won't override rpcStub.
	if(window.jsonRpc) {
		return rpcStub;
	} else {
		return {
			request: request,
			response: null
		};
	}
}());