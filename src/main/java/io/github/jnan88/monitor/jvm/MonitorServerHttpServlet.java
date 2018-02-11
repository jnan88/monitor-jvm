/**
 * 
 */
package io.github.jnan88.monitor.jvm;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 描述： 通过servlet启动jvm监控
 * 
 * @author qizai
 * @version: 0.0.1 2018年2月8日-下午3:16:35
 *
 */
@SuppressWarnings("serial")
public class MonitorServerHttpServlet extends HttpServlet {
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		MonitorServer.BASE_PATH = request.getRequestURI();

		Map<String, String[]> params = request.getParameterMap();
		String[] tValues = params.getOrDefault(MonitorServer.KEY_TYPE, new String[] {});
		boolean isPretty = params.getOrDefault(MonitorServer.KEY_PRETTY, new String[] {}).length > 0;
		boolean isFormat = params.getOrDefault(MonitorServer.KEY_FORMAT, new String[] {}).length > 0;
		String types = tValues.length > 0 ? tValues[0] : null;
		if (null == types || types.isEmpty()) {
			outJson(response, MonitorServer.errorData(), isPretty);
			return;
		}
		Map<String, Object> ret = new HashMap<>();
		ret.put("url", MonitorServer.getLocalUrl());
		ret.put("type", types);
		ret.put("ts", new Date());
		types = "all".equalsIgnoreCase(types) ? JvmInfo.ALL : types;
		try {
			for (String type : types.split(",")) {
				Class<?> jvmInfoClass = JvmInfo.class;
				MonitorType monitorType = MonitorType.valueOf(type);
				Method m1 = jvmInfoClass.getMethod(monitorType.name());
				Object jvmInfoInstance = jvmInfoClass.newInstance();
				if (isFormat) {
					ret.put(monitorType.name(), m1.invoke(jvmInfoInstance));
					continue;
				}
				ret.putAll((Map<String, Object>) m1.invoke(jvmInfoInstance));
			}
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		outJson(response, ret, isPretty);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}

	private void outJson(HttpServletResponse response, Map<String, Object> ret, boolean isPretty) throws IOException {
		String jsonString = isPretty ? JSON.toJSONString(ret, SerializerFeature.PrettyFormat) : JSON.toJSONString(ret);
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.println(jsonString);
		out.close();
	}
}
