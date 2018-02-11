/**
 * 
 */
package jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： TODO(添加描述)
 * 
 * @author qizai
 * @version: 0.0.1 2018年1月25日-上午11:14:05
 *
 */
public class OOMTest {
	// JVM设置
	// -Xms30M -Xmx30M -Xmn20M -XX:+PrintGCDetails -XX:MetaspaceSize=30m -XX:MaxMetaspaceSize=30m -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError

	static class OOMObject {

	}

	public static void main(String[] args) {

		List<OOMObject> list = new ArrayList<OOMObject>();

		while (true) {

			list.add(new OOMObject());

		}

	}
}
