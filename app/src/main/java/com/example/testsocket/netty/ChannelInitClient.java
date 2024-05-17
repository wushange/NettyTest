package com.example.testsocket.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * 客户端数据收发线程
 *
 * @author liangc
 */
public class ChannelInitClient extends ChannelInitializer<Channel> {

    private DataHandlerAdapter adapter;

    ChannelInitClient(DataHandlerAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected void initChannel(Channel ch) {
        try {
            ChannelPipeline channelPipeline = ch.pipeline();
            channelPipeline.addLast(new LineBasedFrameDecoder(1024 * 1024));
            channelPipeline.addLast(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
