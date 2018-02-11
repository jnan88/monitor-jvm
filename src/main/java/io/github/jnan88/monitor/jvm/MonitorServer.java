/**
 * 
 */
package io.github.jnan88.monitor.jvm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * 描述： jvm监控http服务启动入口
 * 
 * @author qizai
 * @version: 0.0.1 2018年1月12日-上午10:45:57
 *
 */
public class MonitorServer {
	public static int			LOCAL_PORT	= 20000;
	public static String		BASE_PATH	= "/oper";
	public final static String	LOCAL_IP	= "127.0.0.1";
	public final static String	KEY_PRETTY	= "v";
	public final static String	KEY_FORMAT	= "f";
	public final static String	KEY_TYPE	= "t";

	public static String getLocalUrl() {
		return String.format("http://%s:%d%s?%s=[]|%s|%s", LOCAL_IP, LOCAL_PORT, BASE_PATH, KEY_TYPE, JvmInfo.ALL,
				KEY_PRETTY, KEY_FORMAT);
	}

	public static Map<String, Object> errorData() {
		Map<String, Object> ret = new HashMap<>();
		ret.put("time", new Date());
		ret.put("status", "FAIL");
		ret.put("url", MonitorServer.getLocalUrl());
		return ret;
	}

	public static int toInt(final String str, final int defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(str);
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}

	/**
	 * @param port
	 *            the {@link #LOCAL_PORT} to set
	 */
	public void setPort(int port) {
		LOCAL_PORT = port;
	}

	/**
	 * @param basepath
	 *            the {@link #BASE_PATH} to set
	 */
	public static void setBasepath(String basepath) {
		BASE_PATH = basepath;
	}

	public static void init() {
		String monitorSkip = System.getProperty("monitor.skip");
		if (Boolean.valueOf(monitorSkip)) {
			return;
		}
		String monitorPath = System.getProperty("monitor.path");
		if (null != monitorPath && !"".equals(monitorPath.trim())) {
			BASE_PATH = monitorPath.startsWith("/") ? monitorPath : "/" + monitorPath;
		}
		String envPortStr = System.getProperty("monitor.port");
		int envPort = toInt(envPortStr, 0);
		if (envPort > 0) {
			LOCAL_PORT = envPort;
		}
	}

	public void info() {
		System.out.println("##############################################");
		System.out.println(String.format("MonitorServer listen on %s", getLocalUrl()));
		System.out.println("##############################################");
	}

}
