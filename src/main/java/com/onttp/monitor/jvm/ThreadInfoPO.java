/**
 * 
 */
package com.onttp.monitor.jvm;

import java.lang.management.ThreadInfo;

import lombok.Getter;

/**
 * 
 * @Description: TODO(添加描述)
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2018年1月11日-下午2:04:38
 *
 */
@Getter
public class ThreadInfoPO {
	private String	threadName, threadState, lockName, lockOwnerName;
	private long	threadId, lockOwnerId, blockedCount, blockedTime, waitedCount, waitedTime;

	/**
	 * 
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
