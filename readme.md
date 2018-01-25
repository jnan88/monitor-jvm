# jvm运行状态监控
## 项目介绍
本项目通过MXBean获取jvm运行的状态信息，并通过Netty提供http服务提取数据。
默认端口20000,
1. 可以通过monitor.port系统参数配置，如:
> -Dmonitor.port=10001

2. 可以通过monitor.skip=true配置不启动
3. 可以通过monitor.path=/monitor 配置请求路径

监控项目如下：
1. os:操作系统信息
2. sys：运行系统信息
3. gc：jvm发生GC的时间和次数
4. thread：线程信息（当前活动数，历史活动数等）
5. threads：线程详情
6. memory：内存基本信息
7. memoryAll：单行列出内存的信息
8. compilation：编译信息
9. runtime：运行参数信息（启动参数等）

## 使用方法
### maven添加依赖
```
<dependency>
	<groupId>io.github.jnan88</groupId>
	<artifactId>monitor-jvm</artifactId>
	<version>0.0.1</version>
</dependency>
```
### 在spring中使用配置：

```
<bean class="io.github.jnan88.monitor.jvm.MonitorServer" init-method="start" destroy-method="stop" lazy-init="true"></bean>

```
### 获取数据
请求方式：
> GET http://you_ip:you_port/monitor?type=x&pretty

请求参数:

|参数名|作用|描述|
|--|--|
|type|获取结果的内容|os,sys,gc,thread,threads,memory,compilation,runtime多个使用英文逗号间隔,all表示获取全部|
|pretty|是否对结果进行格式化，默认不格式化|任意值|
|format|是否对结果进行分组，分组则gc的参数在gc节点下，默认不分组|任意值|

响应参数：
memoryAll:为init,used,committed,max
```
{
	"memoryHeapInit":268435456, #堆初始大小
	"memoryHeapInitMb":256, 
	"memoryHeapMax":3817865216, #堆最大
	"memoryHeapMaxMb":3641,
	"memoryHeapUsed":32214272, #堆当前使用量
	"memoryHeapUsedMb":30,
	"memoryHeapCommitted":257425408,
	"memoryHeapCommittedMb":245,
	"memoryHeapPSSurvivorSpaceInit":11010048,
	"memoryHeapPSSurvivorSpaceInitMb":10,
	"memoryHeapPSSurvivorSpaceMax":11010048,
	"memoryHeapPSSurvivorSpaceMaxMb":10,
	"memoryHeapPSSurvivorSpaceUsed":0,
	"memoryHeapPSSurvivorSpaceUsedMb":0,
	"memoryHeapPSSurvivorSpaceCommitted":11010048,
	"memoryHeapPSSurvivorSpaceCommittedMb":10,
	"memoryHeapPSEdenSpaceInit":67108864,
	"memoryHeapPSEdenSpaceInitMb":64,
	"memoryHeapPSEdenSpaceMax":1409286144,
	"memoryHeapPSEdenSpaceMaxMb":1344,
	"memoryHeapPSEdenSpaceUsed":32214272,
	"memoryHeapPSEdenSpaceUsedMb":30,
	"memoryHeapPSEdenSpaceCommitted":67108864,
	"memoryHeapPSEdenSpaceCommittedMb":64,
	"memoryHeapPSOldGenInit":179306496,
	"memoryHeapPSOldGenInitMb":171,
	"memoryHeapPSOldGenMax":2863661056,
	"memoryHeapPSOldGenMaxMb":2731,
	"memoryHeapPSOldGenUsed":0,
	"memoryHeapPSOldGenUsedMb":0,
	"memoryHeapPSOldGenCommitted":179306496,
	"memoryHeapPSOldGenCommittedMb":171,
	"memoryNonHeapInit":2555904, #非堆
	"memoryNonHeapInitMb":2,
	"memoryNonHeapMax":-1,
	"memoryNonHeapMaxMb":0,
	"memoryNonHeapUsed":15804784,
	"memoryNonHeapUsedMb":15,
	"memoryNonHeapCommitted":16187392,
	"memoryNonHeapCommittedMb":15,
	"memoryNonHeapMetaspaceInit":0,
	"memoryNonHeapMetaspaceInitMb":0,
	"memoryNonHeapMetaspaceMax":-1,
	"memoryNonHeapMetaspaceMaxMb":0,
	"memoryNonHeapMetaspaceUsed":11436240,
	"memoryNonHeapMetaspaceUsedMb":10,
	"memoryNonHeapMetaspaceCommitted":11665408,
	"memoryNonHeapMetaspaceCommittedMb":11,
	"memoryNonHeapCodeCacheInit":2555904,
	"memoryNonHeapCodeCacheInitMb":2,
	"memoryNonHeapCodeCacheUsed":3019008,
	"memoryNonHeapCodeCacheUsedMb":2,
	"memoryNonHeapCodeCacheMax":251658240,
	"memoryNonHeapCodeCacheMaxMb":240,
	"memoryNonHeapCodeCacheCommitted":3080192,
	"memoryNonHeapCodeCacheCommittedMb":2,
	"memoryNonHeapCompressedClassSpaceInit":0,
	"memoryNonHeapCompressedClassSpaceInitMb":0,
	"memoryNonHeapCompressedClassSpaceUsed":1350080,
	"memoryNonHeapCompressedClassSpaceUsedMb":1,
	"memoryNonHeapCompressedClassSpaceMax":1073741824,
	"memoryNonHeapCompressedClassSpaceMaxMb":1024,
	"memoryNonHeapCompressedClassSpaceCommitted":1441792,
	"memoryNonHeapCompressedClassSpaceCommittedMb":1,
	"javaOpts":[ #服务启动参数
		"-Dfile.encoding=utf-8"
	],
	"gcPSScavengeCount":0, #吞吐量优先gc次数
	"gcPSScavengeTime":0,
	"gcPSMarkSweepCount":0, #标记清除gc次数
	"gcPSMarkSweepTime":0,
	"threadsCount":6,
	"threadPeakCount":6,
	"threadDaemonCount":3,
	"threadTotalStartedCount":6,
	"threadCurrentUserTime":205139000,
	"threadCurrentCpuTime":221739000,
	"compilationName":"HotSpot 64-Bit Tiered Compilers",
	"compilationTotalTime":517,
	"runtimeMemoryFree":225211136,
	"runtimeMemoryMax":3817865216,
	"runtimeMemoryMaxMb":3641,
	"runtimeMemoryTotalMb":245,
	"runtimeMemoryTotal":257425408,
	"runtimeMemoryFreeMb":214,
	"osAvailableProcessors":8,
	"osVersion":"10.12.5",
	"osName":"Mac OS X",
	"osArch":"x86_64",
	"osSystemLoadAverage":2.98486328125,
	"sysUptime":32863,
	"sysLibraryPath":"/usr",
	"sysStartTime":1515655242998,
	"sysBootClassPath":"/usr",
	"sysProperties":{ # 系统参数
		"user.home":"/Users/xxx",
		"user.dir":"/usr",
	},
	"sysClassPath":"/usr",
	"sysVmInfo":"Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 25.20-b23",
	"threadCurrentCount":1,
	"threads":[ # 当前全部线程信息
		{
			"blockedCount":0,
			"blockedTime":-1,
			"lockOwnerId":-1,
			"threadId":12,
			"threadName":"nioEventLoopGroup-3-1",
			"threadState":"RUNNABLE",
			"waitedCount":0,
			"waitedTime":-1
		}
	],
	"requestTime":"2018-01-11 15:21:15:811",
	"requestType":[
		"all"
	],
	"requestStatus":"SUCCESS"
}
```
## 更新记录
1. 20180111：新增通过系统配置monitor.skip=true不开启，避免修改配置文件
2. 20180112：新增请求参数format控制是否对结果进行分组，新增通过monitor.path=/monitor 配置请求路径，代码优化通过反射调用获取结果信息
3. 20180125：测试发现如果lazy-init=true时，会造成spring的启动被lock住，无法正常启动，所以需要在Spring启动中通过ApplicationContext获取一次对象进行初始化进行启动