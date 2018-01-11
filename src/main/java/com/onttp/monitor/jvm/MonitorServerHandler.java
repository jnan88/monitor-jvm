/**
 * 
 */
package com.onttp.monitor.jvm;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

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
 * @Description: 请求处理
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2018年1月11日-下午2:18:27
 *
 */
public class MonitorServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	private void putInfo(Map<String, Object> ret, String typeString) {
		for (String type : typeString.split(",")) {
			switch (type) {
			case "gc":
				ret.putAll(JvmInfo.gc());
				break;
			case "compilation":
				ret.putAll(JvmInfo.compilation());
				break;
			case "memory":
				ret.putAll(JvmInfo.memory());
				break;
			case "memoryAll":
				ret.putAll(JvmInfo.memoryAll());
				break;
			case "thread":
				ret.putAll(JvmInfo.thread());
				break;
			case "threads":
				ret.putAll(JvmInfo.threads());
				break;
			case "runtime":
				ret.putAll(JvmInfo.runtime());
				break;
			case "os":
				ret.putAll(JvmInfo.os());
				break;
			case "sys":
				ret.putAll(JvmInfo.sys());
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;
			if (HttpUtil.is100ContinueExpected(req)) {
				ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
			}
			Map<String, Object> ret = new HashMap<>();
			ret.put("requestTime", new Date());
			boolean isPretty = false;
			// 处理get请求
			if (req.method() == HttpMethod.GET) {
				QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
				if ("/monitor".equals(decoder.path())) {
					Map<String, List<String>> parame = decoder.parameters();
					// 获取监控的类型
					List<String> typeString = parame.get("type");
					ret.put("requestType", typeString);
					if (null != typeString && !typeString.isEmpty()) {
						String types = typeString.get(0);
						putInfo(ret, types.equals("all") ? JvmInfo.ALL : types);
						ret.put("requestStatus", "SUCCESS");
					} else {
						ret.put("requestStatus", "FAIL");
						ret.put("error", "place use http:://127.0.0.1:20000/monitor?type=["+JvmInfo.ALL+"]|&pretty");

					}
					if (parame.containsKey("pretty")) {
						isPretty = true;
					}
				}
			}
			String jsonString = isPretty ? JSON.toJSONString(ret, SerializerFeature.PrettyFormat)
					: JSON.toJSONString(ret);
			ByteBuf byteBuf = Unpooled.wrappedBuffer(jsonString.getBytes());
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

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
