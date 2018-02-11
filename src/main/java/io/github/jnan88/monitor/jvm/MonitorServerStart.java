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
public class MonitorServerStart {
	private EventLoopGroup	bossGroup	= null;
	private EventLoopGroup	workerGroup	= null;

	public void start() {
		MonitorServer.init();
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
			Channel ch = b.bind(MonitorServer.LOCAL_PORT).sync().channel();
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
		System.out.println(String.format("MonitorServer listen on %s", MonitorServer.getLocalUrl()));
		System.out.println("##############################################");
	}

	public void stop() {
		if (null != bossGroup) {
			bossGroup.shutdownGracefully();
		}
		if (null != workerGroup) {
			workerGroup.shutdownGracefully();
		}
		System.out.println("MonitorServer stop :" + MonitorServer.LOCAL_PORT);
	}

	public static void main(String[] args) {

		new MonitorServerStart().start();
	}
}
