package com.neroyang.distributedsystem.raft.server.task;

import com.neroyang.distributedsystem.raft.server.RaftServer;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;

import static com.neroyang.distributedsystem.raft.common.config.RaftConfig.timeOutTime;
import static com.neroyang.distributedsystem.raft.server.RaftServer.clientsMap;
import static com.neroyang.distributedsystem.raft.server.RaftServer.clientsbeatMap;
import static com.neroyang.distributedsystem.raft.server.RaftServer.nodeList;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/15
 * Time   上午11:35
 */
public class BeatCheckTask extends TimerTask {
    SocketChannel socketChannel;

    public BeatCheckTask(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void run() {
        if (!clientsMap.isEmpty() && !clientsbeatMap.isEmpty()) {
            for (Map.Entry<UUID, SocketChannel> entry : RaftServer.getClientMapEntry()) {
                if (socketChannel.equals(entry.getValue())) {
                    if (System.currentTimeMillis() - clientsbeatMap.get(entry.getKey()) > timeOutTime * 1000) {
                        System.out.println("客户端 " + entry.getKey() + "异常，掉线！");
                        nodeList.remove(entry.getKey());
                        clientsbeatMap.remove(entry.getKey());
                        clientsMap.remove(entry.getKey());


                        //通知其他客户端掉线信息
                    }
                }
            }
        }
    }
}
