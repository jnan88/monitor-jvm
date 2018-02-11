/**
 * 
 */
package jvm;

/**
 * 描述： 栈深度溢出
 * 
 * @author qizai
 * @version: 0.0.1 2018年1月25日-上午11:17:17
 *
 */
public class JavaStackOverFlowTest {
	// JVM设置
	// -Xms30M -Xmx30M -Xmn20M -XX:+PrintGCDetails -XX:MetaspaceSize=30m -XX:MaxMetaspaceSize=30m -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError

	private int stackLength = 1;

	public void stackLeak() {
		stackLength++;
		stackLeak();

	}

	public static void main(String[] args) throws Throwable {
		JavaStackOverFlowTest oom = new JavaStackOverFlowTest();
		try {
			oom.stackLeak();
		} catch (Throwable e) {
			System.out.println("stack length:" + oom.stackLength);
			throw e;
		}

	}
}
