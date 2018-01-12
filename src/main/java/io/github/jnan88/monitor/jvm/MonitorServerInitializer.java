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
 * @Description: TODO(添加描述)
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2018年1月11日-下午2:17:14
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
