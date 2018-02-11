/**
 * 
 */
package jvm;

import java.lang.reflect.Method;

/**
 * 描述： 运行时方法区溢出
 * 
 * @author qizai
 * @version: 0.0.1 2018年1月25日-上午11:21:37
 *
 */
public class MethodAreaOOMTest {
	// -Xms30M -Xmx30M -Xmn10M -XX:+PrintGCDetails -XX:PermSize=10m
	// -XX:MaxPermSize=10m -XX:+UseConcMarkSweepGC
	// -XX:+HeapDumpOnOutOfMemoryError

	public static void main(String[] args) {

//		while (true) {
//			Enhancer enhancer = new Enhancer();
//			enhancer.setSuperclass(OOMObject.class);
//			enhancer.setUseCache(false);
//			enhancer.setCallback(new MethodInterceptor() {
//				public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
//					return proxy.invokeSuper(obj, args);
//				}
//			});
//			enhancer.create();
//		}
	}
	static class OOMObject {

	}

}
