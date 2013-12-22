package kr.co.b612lodger.jsonRpc.model;

public class Response<T> {
	
	protected String jsonrpc = "2.0";
	
	protected Error error;
	
	protected T result;
	
	protected Integer id;
	
	
	public String getJsonrpc() {
		return jsonrpc;
	}


	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}


	public Error getError() {
		return error;
	}


	public void setError(Error error) {
		this.error = error;
	}


	public T getResult() {
		return result;
	}


	public void setResult(T result) {
		this.result = result;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public static class Error {
		
		protected int code;
		
		protected String message;
		
		public Error(Integer code, String message) {
			this.code = code;
			this.message = message;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

}
