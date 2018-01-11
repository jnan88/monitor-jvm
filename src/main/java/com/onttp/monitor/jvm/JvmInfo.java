/**
 * 
 */
package com.onttp.monitor.jvm;

import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @Description: jvm运行信息获取
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2017年7月7日-上午10:44:21
 *
 */
public class JvmInfo {
	public static String	ALL	= "os,sys,gc,thread,threads,memory,memoryAll,compilation,runtime";

	private static int		MB	= 1024 * 1024;

	private static void putVmMemoryInfo(Map<String, Object> vminfos, String type, MemoryUsage heapMemory) {
		vminfos.put(type + "Init", heapMemory.getInit());
		vminfos.put(type + "Used", heapMemory.getUsed());
		vminfos.put(type + "Committed", heapMemory.getCommitted());
		vminfos.put(type + "Max", heapMemory.getMax());
		//
		vminfos.put(type + "InitMb", heapMemory.getInit() / MB);
		vminfos.put(type + "UsedMb", heapMemory.getUsed() / MB);
		vminfos.put(type + "CommittedMb", heapMemory.getCommitted() / MB);
		vminfos.put(type + "MaxMb", heapMemory.getMax() / MB);
	}

	private static void putVmMemoryAllInfo(Map<String, Object> vminfos, String type, MemoryUsage heapMemory) {
		// vminfos.put("memoryAllDesc",
		// "init,used,max,committed-保证java虚拟机能使用的内存量>=used");
		vminfos.put(type + "AllMb", heapMemory.getInit() / MB + "," + heapMemory.getUsed() / MB + ","
				+ heapMemory.getCommitted() / MB + "," + heapMemory.getMax() / MB);
	}

	public static Map<String, Object> compilation() {
		Map<String, Object> vmInfo = new HashMap<>();
		// 当前编译器情况
		CompilationMXBean gm = (CompilationMXBean) ManagementFactory.getCompilationMXBean();
		vmInfo.put("compilationName", gm.getName());
		vmInfo.put("compilationTotalTime", gm.getTotalCompilationTime());
		return vmInfo;
	}

	public static Map<String, Object> memory() {
		Map<String, Object> vminfos = new HashMap<>();
		// 获取整个虚拟机内存使用情况
		MemoryMXBean memorymbean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heapMemory = memorymbean.getHeapMemoryUsage();
		MemoryUsage nonHeapMemory = memorymbean.getNonHeapMemoryUsage();
		putVmMemoryInfo(vminfos, "memoryHeap", heapMemory);
		putVmMemoryInfo(vminfos, "memoryNonHeap", nonHeapMemory);
		// 获取多个内存池的使用情况
		List<MemoryPoolMXBean> mpmList = ManagementFactory.getMemoryPoolMXBeans();
		for (MemoryPoolMXBean mpm : mpmList) {
			String typeName = mpm.getType() == MemoryType.HEAP ? "memoryHeap" : "memoryNonHeap";
			putVmMemoryInfo(vminfos, (typeName + mpm.getName()).replaceAll(" ", ""), mpm.getUsage());
		}
		return vminfos;
	}

	public static Map<String, Object> memoryAll() {
		Map<String, Object> vminfos = new HashMap<>();
		// 获取整个虚拟机内存使用情况
		MemoryMXBean memorymbean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heapMemory = memorymbean.getHeapMemoryUsage();
		MemoryUsage nonHeapMemory = memorymbean.getNonHeapMemoryUsage();
		putVmMemoryAllInfo(vminfos, "memoryHeap", heapMemory);
		putVmMemoryAllInfo(vminfos, "memoryNonHeap", nonHeapMemory);
		// 获取多个内存池的使用情况
		List<MemoryPoolMXBean> mpmList = ManagementFactory.getMemoryPoolMXBeans();
		for (MemoryPoolMXBean mpm : mpmList) {
			String typeName = mpm.getType() == MemoryType.HEAP ? "memoryHeap" : "memoryNonHeap";
			putVmMemoryAllInfo(vminfos, (typeName + mpm.getName()).replaceAll(" ", ""), mpm.getUsage());
		}
		return vminfos;
	}

	public static Map<String, Object> thread() {
		Map<String, Object> vminfos = new HashMap<>();
		// 获取各个线程的各种状态，CPU 占用情况，以及整个系统中的线程状况
		ThreadMXBean tm = (ThreadMXBean) ManagementFactory.getThreadMXBean();
		vminfos.put("threadPeakCount", tm.getPeakThreadCount());
		vminfos.put("threadCurrentCount", tm.getThreadCount());
		vminfos.put("threadDaemonCount", tm.getDaemonThreadCount());
		vminfos.put("threadTotalStartedCount", tm.getTotalStartedThreadCount());
		vminfos.put("threadCurrentCpuTime", tm.getCurrentThreadCpuTime());
		vminfos.put("threadCurrentUserTime", tm.getCurrentThreadUserTime());
		vminfos.put("threadDeadlockedIds", tm.findDeadlockedThreads());
		vminfos.put("threadMonitorDeadlockedIds", tm.findMonitorDeadlockedThreads());
		return vminfos;
	}

	public static Map<String, Object> threads() {
		Map<String, Object> vminfos = new HashMap<>();
		// 获取各个线程的各种状态，CPU 占用情况，以及整个系统中的线程状况
		ThreadMXBean tm = (ThreadMXBean) ManagementFactory.getThreadMXBean();
		long[] tids = tm.getAllThreadIds();
		List<ThreadInfoPO> tsList = new ArrayList<>();
		for (long id : tids) {
			ThreadInfo tf = tm.getThreadInfo(id);
			if (null != tf) {
				tsList.add(new ThreadInfoPO(tf));
			}
		}
		vminfos.put("threads", tsList);
		vminfos.put("threadsCount", tsList.size());
		return vminfos;
	}

	public static Map<String, Object> getAllInfo() {
		Map<String, Object> vminfos = new HashMap<>();
		vminfos.putAll(compilation());
		vminfos.putAll(runtime());
		vminfos.putAll(memory());
		vminfos.putAll(gc());
		vminfos.putAll(os());
		vminfos.putAll(thread());
		vminfos.putAll(threads());
		vminfos.putAll(sys());
		return vminfos;
	}

	public static Map<String, Object> runtime() {
		Map<String, Object> vminfos = new HashMap<>();
		// 获取启动参数
		List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
		vminfos.put("javaOpts", inputArguments);
		// 获取Jvm系统状态
		Runtime rt = Runtime.getRuntime();
		vminfos.put("runtimeMemoryTotal", rt.totalMemory());
		vminfos.put("runtimeMemoryFree", rt.freeMemory());
		vminfos.put("runtimeMemoryMax", rt.maxMemory());
		vminfos.put("runtimeMemoryTotalMb", rt.totalMemory() / MB);
		vminfos.put("runtimeMemoryFreeMb", rt.freeMemory() / MB);
		vminfos.put("runtimeMemoryMaxMb", rt.maxMemory() / MB);
		return vminfos;
	}

	public static Map<String, Object> os() {
		Map<String, Object> vminfos = new HashMap<>();
		// 获取操作系统相关信息
		OperatingSystemMXBean osm = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		vminfos.put("osArch", osm.getArch());
		vminfos.put("osName", osm.getName());
		vminfos.put("osVersion", osm.getVersion());
		vminfos.put("osSystemLoadAverage", osm.getSystemLoadAverage());
		vminfos.put("osAvailableProcessors", osm.getAvailableProcessors());
		return vminfos;
	}

	public static Map<String, Object> gc() {
		Map<String, Object> vminfos = new HashMap<>();
		// 获取GC的次数以及花费时间之类的信息
		List<GarbageCollectorMXBean> gcmList = ManagementFactory.getGarbageCollectorMXBeans();
		for (GarbageCollectorMXBean gcm : gcmList) {
			String _name = gcm.getName().replaceAll(" ", "");
			vminfos.put("gc" + _name + "Count", gcm.getCollectionCount());
			vminfos.put("gc" + _name + "Time", gcm.getCollectionTime());
		}
		return vminfos;
	}

	public static Map<String, Object> sys() {
		Map<String, Object> vminfos = new HashMap<>();
		// 获取运行时信息
		RuntimeMXBean rmb = (RuntimeMXBean) ManagementFactory.getRuntimeMXBean();
		vminfos.put("sysProperties", rmb.getSystemProperties());
		vminfos.put("sysClassPath", rmb.getClassPath());
		vminfos.put("sysLibraryPath", rmb.getLibraryPath());
		vminfos.put("sysBootClassPath", rmb.getBootClassPath());
		vminfos.put("sysStartTime", rmb.getStartTime());
		vminfos.put("sysUptime", rmb.getUptime());
		vminfos.put("sysVmInfo", rmb.getVmVendor() + " " + rmb.getVmName() + " " + rmb.getVmVersion());
		return vminfos;
	}

}
