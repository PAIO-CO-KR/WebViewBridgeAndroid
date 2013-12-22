package kr.co.b612lodger.jsonRpc.core;

import java.lang.reflect.ParameterizedType;

public abstract class MethodHandler<T, U> {
	
	public abstract U execute(T request);
	
	@SuppressWarnings("unchecked")
	public Class<T> getParamClass() {
		Class<T> typeClass = null;
		try {
			typeClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
		} catch(Exception e) {}
		
		if(typeClass.getName().equals(Object.class.getName())) {
			typeClass = null;
		}
		
		return typeClass;
	}
	
	@SuppressWarnings("unchecked")
	public Class<T> getResultClass() {
		Class<T> typeClass = null;
		try {
			typeClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[1];
		} catch(Exception e) {}
		
		if(typeClass.getName().equals(Object.class.getName())) {
			typeClass = null;
		}
		
		return typeClass;
	}
}
