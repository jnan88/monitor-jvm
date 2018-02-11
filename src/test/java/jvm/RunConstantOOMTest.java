/**
 * 
 */
package jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 运行时常量池内存溢出,Perm区引发的Full Gc效果很不明显，并且打印出来的内存映射快照，不包括Perm区的内存
 * 
 * @author qizai
 * @version: 0.0.1 2018年1月25日-上午11:19:05
 *
 */
public class RunConstantOOMTest {
	//-Xms30M -Xmx30M -Xmn10M -XX:+PrintGCDetails -XX:MetaspaceSize=3m -XX:MaxMetaspaceSize=3m -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError

    public static void main(String[] args) {

        // 使用List保持着常量池引用，避免Full GC回收常量池行为

        List<String> list = new ArrayList<String>();

        int i = 0;

        while (true) {

            list.add(String.valueOf(i++).intern());

        }

    }

}
