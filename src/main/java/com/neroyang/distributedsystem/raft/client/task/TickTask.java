package com.neroyang.distributedsystem.raft.client.task;

import com.neroyang.distributedsystem.raft.common.entity.HeartBeat;
import com.neroyang.distributedsystem.raft.common.entity.Request;
import com.neroyang.distributedsystem.raft.constant.ELECTION;
import com.neroyang.distributedsystem.raft.utils.ProtoStuffUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.TimerTask;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/15
 * Time   上午11:37
 */
public class TickTask extends TimerTask {
    Request request;
    SocketChannel socketChannel;

    public TickTask(Request request, SocketChannel socketChannel) {
        this.request = request;
        this.socketChannel = socketChannel;
    }

    public void run() {
        Request<HeartBeat> heartBeatRequest = new Request<HeartBeat>(
                request.getRequestID(),
                ELECTION.HEARTBEAT_REQ,
                new HeartBeat(System.currentTimeMillis())
        );
        ByteBuffer byteBuffer = ByteBuffer.wrap(ProtoStuffUtils.serializer(heartBeatRequest));
        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            System.out.println("服务端掉线！"+e.getMessage());
        }
    }
}
