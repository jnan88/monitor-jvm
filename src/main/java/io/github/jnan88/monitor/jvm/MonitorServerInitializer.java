/**
 * 
 */
package io.github.jnan88.monitor.jvm;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 
 * 
 * @author qizai
 * @version: 0.0.1 2018年1月12日-上午10:46:13
 *
 */
public class MonitorServerInitializer extends ChannelInitializer<SocketChannel> {

 
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new MonitorServerHandler());
    }
}
