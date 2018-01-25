/**
 * 
 */
package io.github.jnan88.monitor.jvm;

import java.lang.management.ThreadInfo;

import lombok.Getter;

/**
 * 
 * 描述： 线程基本信息
 * 
 * @author qizai
 * @version: 0.0.1 2018年1月12日-上午10:46:17
 *
 */
@Getter
public class ThreadInfoPO {
	private String	threadName, threadState, lockName, lockOwnerName;
	private long	threadId, lockOwnerId, blockedCount, blockedTime, waitedCount, waitedTime;

	/**
	 * 
	 * @param tf
	 *            线程信息
	 */
	public ThreadInfoPO(ThreadInfo tf) {
		threadId = tf.getThreadId();
		threadName = tf.getThreadName();
		threadState = tf.getThreadState().name();
		lockName = tf.getLockName();
		lockOwnerId = tf.getLockOwnerId();
		lockOwnerName = tf.getLockOwnerName();
		blockedCount = tf.getBlockedCount();
		blockedTime = tf.getBlockedTime();
		waitedCount = tf.getWaitedCount();
		waitedTime = tf.getWaitedTime();
	}
}
