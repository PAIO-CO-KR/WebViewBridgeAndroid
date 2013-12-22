package kr.co.b612lodger.webViewBridge.test;
import junit.framework.TestCase;
import kr.co.b612lodger.jsonRpc.AsyncServerStub;
import kr.co.b612lodger.jsonRpc.AsyncServerStub.OnResponseListener;
import kr.co.b612lodger.jsonRpc.ServerStub;
import kr.co.b612lodger.jsonRpc.core.MethodHandler;
import kr.co.b612lodger.jsonRpc.core.RequestResponseFactory;
import kr.co.b612lodger.jsonRpc.model.Request;
import android.test.suitebuilder.annotation.MediumTest;


public class SimpleTest extends TestCase {
	
	private final String singleRequest = "{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": {\"minuend\": 42, \"subtrahend\": 23}, \"id\": 1}";
	
	private final String batchRequest = "[{\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": {\"minuend\": 42, \"subtrahend\": 23}, \"id\": 1}, {\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": {\"minuend\": 42, \"subtrahend\": 23}, \"id\": 1}]";
	
	@MediumTest
	public void serializeDeserializeTest() {
		ServerStub serverStub = new ServerStub();
		serverStub.registMethod("subtract", new MethodHandler<TestParam, TestResult>() {
			
			@Override
			public TestResult execute(TestParam requestParam) {
				return null;
			}
		});
		
		RequestResponseFactory rf = new RequestResponseFactory(serverStub);
		@SuppressWarnings("unchecked")
		Request<TestParam> r = (Request<TestParam>) rf.makeSingleRequest(singleRequest);
		assertNotNull(r);
		
		Request<?>[] rr = (Request<?>[])rf.makeRequest(batchRequest);
		assertNotNull(rr);
		assertTrue(rr.length == 2);
		
		String s = rf.deserializeRequest(r);
		assertNotNull(s);
		
		String ss = rf.deserializeRequest(rr);
		assertNotNull(ss);
	}
	
	@MediumTest
	public void executeTest() {
		ServerStub serverStub = new ServerStub();
		serverStub.registMethod("subtract", new MethodHandler<TestParam, Integer>() {
			
			@Override
			public Integer execute(TestParam requestParam) {
				assertNotNull(requestParam);
				return requestParam.getSubtrahend() - requestParam.getMinuend();
			}
		});
		String response = serverStub.execute(singleRequest);
		assertNotNull(response);
		
		String responses = serverStub.execute(batchRequest);
		assertNotNull(responses);
	}
	
	@MediumTest
	public void executeAsyncTest() {
		AsyncServerStub serverStub = new AsyncServerStub();
		serverStub.registMethod("subtract", new MethodHandler<TestParam, Integer>() {
			
			@Override
			public Integer execute(TestParam requestParam) {
				assertNotNull(requestParam);
				return requestParam.getSubtrahend() - requestParam.getMinuend();
			}
		});
		
		serverStub.setOnResponseListener(new OnResponseListener() {
			
			@Override
			public void onResponse(String response) {
				assertNotNull(response);
			}
		});;
		serverStub.executeAsync(singleRequest);
	}
	
	
	
	public static class TestResult {
		private Integer result;

		public Integer getResult() {
			return result;
		}

		public void setResult(Integer result) {
			this.result = result;
		}
		
		
	}
	
	public static class TestParam {
		
		private Integer minuend;
		
		private Integer subtrahend;

		public Integer getMinuend() {
			return minuend;
		}

		public void setMinuend(Integer minuend) {
			this.minuend = minuend;
		}

		public Integer getSubtrahend() {
			return subtrahend;
		}

		public void setSubtrahend(Integer subtrahend) {
			this.subtrahend = subtrahend;
		}
		
		
	}
}
