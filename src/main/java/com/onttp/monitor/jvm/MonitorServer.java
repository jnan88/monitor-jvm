/**
 * 
 */
package com.onttp.monitor.jvm;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 
 * @Description: jvm监控http服务启动入口
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2018年1月11日-下午1:42:17
 *
 */
public class MonitorServer {
	private EventLoopGroup	bossGroup	= null;
	private EventLoopGroup	workerGroup	= null;
	private int				port		= 20000;

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
	 *            the {@link #port} to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	public void start() {
		String monitorSkip = System.getProperty("monitor.skip");
		if (Boolean.valueOf(monitorSkip)) {
			return;
		}
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();
		String envPortStr = System.getProperty("monitor.port");
		int envPort = toInt(envPortStr, 0);
		if (envPort > 0) {
			port = envPort;
		}
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					// .handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new MonitorServerInitializer());
			Channel ch = b.bind(port).sync().channel();
			info();
			ch.closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private void info() {
		System.out.println("##############################################");
		System.out.println(String.format("MonitorServer listen on http://127.0.0.1:%d/monitor", port));
		System.out.println("##############################################");
	}

	public void stop() {
		if (null != bossGroup) {
			bossGroup.shutdownGracefully();
		}
		if (null != workerGroup) {
			workerGroup.shutdownGracefully();
		}
		System.out.println("MonitorServer stop :" + port);
	}

	public static void main(String[] args) {
		new MonitorServer().start();
	}
}
