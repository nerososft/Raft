package com.neroyang.distributedsystem.raft.server.handler;

import com.neroyang.distributedsystem.raft.client.Client;
import com.neroyang.distributedsystem.raft.client.handler.ClientHandler;
import com.neroyang.distributedsystem.raft.common.entity.HeartBeat;
import com.neroyang.distributedsystem.raft.common.entity.Node;
import com.neroyang.distributedsystem.raft.common.entity.Request;
import com.neroyang.distributedsystem.raft.constant.ELECTION;
import com.neroyang.distributedsystem.raft.server.RaftServer;
import com.neroyang.distributedsystem.raft.utils.ProtoStuffUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/13
 * Time   下午2:58
 */
public class ServerHandler implements Runnable {
    final SocketChannel socketChannel;
    final SelectionKey selectionKey;
    ByteBuffer input = ByteBuffer.allocate(1024);
    byte[] receivedBytes;
    static final int READING = 0, SENDING = 1;

    Request request;
    int state = READING;

    public ServerHandler(SocketChannel socketChannel, Selector selector) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        this.selectionKey = socketChannel.register(selector, 0);

        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }


    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                write();
            }
        } catch (IOException e) {
            System.out.println("客户端异常或离线。");
//            synchronized(RaftServer.clientsMap) {
//                for (Map.Entry entry : RaftServer.clientsMap.entrySet()) {
//                    if (socketChannel.equals(entry.getValue())) {
//                        RaftServer.nodeList.remove(entry.getKey());
//                        RaftServer.clientsMap.remove(entry.getKey());
//                    }
//                }
//            }
        }
    }

    public void read() throws IOException {
        int readCount = socketChannel.read(input);
        if (readCount > 0) {
            readProcess(readCount);
        }
        state = SENDING;
        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    synchronized void readProcess(int readCount) throws IOException {
        input.flip();
        receivedBytes = new byte[readCount];
        byte[] array = input.array();
        System.arraycopy(array, 0, receivedBytes, 0, readCount);
        request = ProtoStuffUtils.deserializer(receivedBytes, Request.class);

        // client --> server
        switch (request.getRequestCode()) {
            case JOIN_REQ:
                System.out.println("收到：节点请求加入集群！");
                Request<Node> nodeRequest = ProtoStuffUtils.deserializer(receivedBytes, Request.class);
                System.out.println(nodeRequest.toString());
                break;
            case HEARTBEAT_REQ:
                System.out.println("收到：心跳！");
                Request<HeartBeat> heartBeatRequest = ProtoStuffUtils.deserializer(receivedBytes, Request.class);

                //记录节点延迟
                Node node = RaftServer.nodeList.get(request.getRequestID());
                node.setDelay(System.currentTimeMillis()-heartBeatRequest.getData().getTimestamp());
                RaftServer.nodeList.put(request.getRequestID(),node);

                //更新上一次心跳时间
                RaftServer.clientsbeatMap.put(heartBeatRequest.getRequestID(),System.currentTimeMillis()); //更新tick时间

                System.out.println(heartBeatRequest.toString());
                break;
            default:
                break;
        }
        input.clear();

    }

    //server->client
    void write() throws IOException {
        switch (request.getRequestCode()) {
            case JOIN_REQ://服务端收到请求
                //加入Leader节点列表
                Request<Node> nodeRequest = ProtoStuffUtils.deserializer(receivedBytes, Request.class);
                RaftServer.nodeList.put(request.getRequestID(),nodeRequest.getData());

                RaftServer.clientsMap.put(nodeRequest.getRequestID(), socketChannel);
                System.out.println("状态：当前Follower数：" + RaftServer.clientsMap.size());
                //通知其节点更新节点列表
                if (!RaftServer.clientsMap.isEmpty()) {
                    Set<UUID> keySet = RaftServer.clientsMap.keySet();
                    Iterator<UUID> keys = keySet.iterator();
                    while (keys.hasNext()) {
                        UUID uuid = keys.next();
                        SocketChannel tempChannel = RaftServer.clientsMap.get(uuid);
                        Request<Map<UUID,Node>> nodeAllResponse = new Request<Map<UUID,Node>>(
                                uuid,
                                ELECTION.JOIN_NOTIFICATION,
                                RaftServer.nodeList);
                        byte[] listbytes = ProtoStuffUtils.serializer(nodeAllResponse);
                        System.out.println("发送：通知节点更新列表！");
                        tempChannel.write(ByteBuffer.wrap(listbytes));
                    }
                }

                selectionKey.interestOps(SelectionKey.OP_READ);
                state = READING;
                break;

            case HEARTBEAT_REQ:
                Request<HeartBeat> heartBeatRequest = new Request<HeartBeat>(
                        request.getRequestID(),
                        ELECTION.HEARTBEAT_RSP,
                        new HeartBeat(System.currentTimeMillis())
                );
                ByteBuffer byteBuffer = ByteBuffer.wrap(ProtoStuffUtils.serializer(heartBeatRequest));
                socketChannel.write(byteBuffer);

                selectionKey.interestOps(SelectionKey.OP_READ);
                state = READING;
                break;
            default:
                break;
        }
    }
}
