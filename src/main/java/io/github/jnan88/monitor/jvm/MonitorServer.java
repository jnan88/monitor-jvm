/**
 * 
 */
package io.github.jnan88.monitor.jvm;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

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
	private EventLoopGroup		bossGroup	= null;
	private EventLoopGroup		workerGroup	= null;
	public static int			LOCAL_PORT	= 20000;
	public static String		BASE_PATH	= "/monitor";
	public final static String	LOCAL_IP	= "127.0.0.1";
	public final static String	KEY_PRETTY	= "pretty";
	public final static String	KEY_FORMAT	= "format";

	public static String getLocalUrl() {
		return String.format("http://%s:%d%s?type=[]|%s|%s", LOCAL_IP, LOCAL_PORT, BASE_PATH, JvmInfo.ALL, KEY_PRETTY,
				KEY_FORMAT);
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

	public void start() {
		String monitorSkip = System.getProperty("monitor.skip");
		if (Boolean.valueOf(monitorSkip)) {
			return;
		}
		String monitorPath = System.getProperty("monitor.path");
		if (null != monitorPath && !"".equals(monitorPath.trim())) {
			BASE_PATH = monitorPath.startsWith("/") ? monitorPath : "/" + monitorPath;
		}
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();
		String envPortStr = System.getProperty("monitor.port");
		int envPort = toInt(envPortStr, 0);
		if (envPort > 0) {
			LOCAL_PORT = envPort;
		}
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
							p.addLast(new HttpServerCodec());
							p.addLast(new MonitorServerHandler());
						}
					});
			Channel ch = b.bind(LOCAL_PORT).sync().channel();
			info();
			ch.closeFuture().sync();
		} catch (InterruptedException e) {
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private void info() {
		System.out.println("##############################################");
		System.out.println(String.format("MonitorServer listen on %s", getLocalUrl()));
		System.out.println("##############################################");
	}

	public void stop() {
		if (null != bossGroup) {
			bossGroup.shutdownGracefully();
		}
		if (null != workerGroup) {
			workerGroup.shutdownGracefully();
		}
		System.out.println("MonitorServer stop :" + LOCAL_PORT);
	}

	public static void main(String[] args) {
		new MonitorServer().start();
	}
}
