/**
 * 
 */
package io.github.jnan88.monitor.jvm;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * 
 * 
 * 描述： 请求处理
 * 
 * @author qizai
 * @version: 0.0.1 2018年1月12日-上午10:46:05
 *
 */
public class MonitorServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;
			if (HttpUtil.is100ContinueExpected(req)) {
				ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
			}
			String responseString = getResponse(req);
			ByteBuf byteBuf = Unpooled.wrappedBuffer(responseString.getBytes());
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, byteBuf);
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

			boolean keepAlive = HttpUtil.isKeepAlive(req);
			if (!keepAlive) {
				ctx.write(response).addListener(ChannelFutureListener.CLOSE);
			} else {
				response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
				ctx.write(response);
			}
		}
	}

	private static String error() {
		Map<String, Object> ret = new HashMap<>();
		ret.put("requestTime", new Date());
		ret.put("requestStatus", "FAIL");
		ret.put("error", "place use " + MonitorServer.getLocalUrl());
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

	/**
	 * 
	 * 描述： 监控类型
	 * 
	 * @author qizai
	 * @version: 0.0.1 2018年1月25日-下午12:08:43
	 *
	 */
	enum MonitorType {
		gc, os, sys, runtime, thread, threads, compilation, memory, memoryAll
	}
}
