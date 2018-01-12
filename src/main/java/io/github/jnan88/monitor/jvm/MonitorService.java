/**
 * 
 */
package io.github.jnan88.monitor.jvm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * 
 * 描述： 请求详细处理
 * 
 * @author qizai
 * @version: 0.0.1 2018年1月12日-下午2:34:16
 *
 */
public class MonitorService {
	private static String error() {
		Map<String, Object> ret = new HashMap<>();
		ret.put("requestTime", new Date());
		ret.put("requestStatus", "FAIL");
		ret.put("error", "place use " + MonitorServer.LOCAL_URL);
		String jsonString = JSON.toJSONString(ret);
		return jsonString;
	}

	@SuppressWarnings("unchecked")
	public static String getResponse(HttpRequest req) {
		// 处理get请求
		if (req.method() != HttpMethod.GET) {
			return error();
		}
		QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
		if (!MonitorServer.BASE_PATH.equals(decoder.path())) {
			return error();
		}
		Map<String, List<String>> parame = decoder.parameters();
		// 获取监控的类型
		List<String> typeString = parame.get("type");
		Map<String, Object> ret = new HashMap<>();
		ret.put("requestType", typeString);
		if (null == typeString || typeString.isEmpty()) {
			return error();
		}
		ret.put("requestTime", new Date());
		boolean isPretty = parame.containsKey(MonitorServer.KEY_PRETTY);
		boolean isFormat = parame.containsKey(MonitorServer.KEY_FORMAT);
		String types = typeString.get(0);
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
		String jsonString = isPretty ? JSON.toJSONString(ret, SerializerFeature.PrettyFormat) : JSON.toJSONString(ret);
		return jsonString;
	}
}
