package com.neroyang.distributedsystem.raft.client.handler;

import com.neroyang.distributedsystem.raft.client.Client;
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

import static com.neroyang.distributedsystem.raft.constant.ELECTION.HEARTBEAT_RSP;
import static com.neroyang.distributedsystem.raft.constant.ELECTION.JOIN_NOTIFICATION;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/13
 * Time   下午2:58
 */
public class ClientHandler implements Runnable {
    final SocketChannel socketChannel;
    final SelectionKey selectionKey;
    ByteBuffer input = ByteBuffer.allocate(1024);
    static final int READING = 0,SENDING = 1;

    byte[] receivedBytes;
    Request  request;

    int state = READING;
    AtomicInteger notifyTimes= new AtomicInteger();
    Timer tickTimer;
    Timer tickCheckTimer;


    Long lastHeartBeatTime = System.currentTimeMillis();
    Long leaderDelay =0L;



    public ClientHandler(SocketChannel socketChannel, Selector selector) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        this.selectionKey = socketChannel.register(selector,0);

        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
        notifyTimes.set(0);
    }

    class TickTask extends TimerTask{

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
    class TickCheckTask extends TimerTask{

        public void run() {
            System.out.println("检查Leader超时.");
            if(System.currentTimeMillis()-lastHeartBeatTime>Client.timeOutTime*1000){
                System.out.println("Leader无响应.选举开始!");
                state = SENDING;
                selectionKey.interestOps(SelectionKey.OP_WRITE);
                this.cancel();
            }
        }
    }

    public void run() {
        try{
            if(state == READING){
                read();
            }else if(state==SENDING){
                write();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void read() throws IOException{
        int readCount = socketChannel.read(input);
        if(readCount>0){
            readProcess(readCount);
        }
        state = SENDING;
        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    synchronized void readProcess(int readCount) throws IOException {
        input.flip();
        receivedBytes = new byte[readCount];
        byte[] array = input.array();
        System.arraycopy(array,0,receivedBytes,0,readCount);
        request = ProtoStuffUtils.deserializer(receivedBytes,Request.class);

        // server --> client
        switch (request.getRequestCode()){
            case JOIN_NOTIFICATION:
                System.out.println("收到：节点列表更新通知！");
                Request<List<Node>> nodeAllResponse = ProtoStuffUtils.deserializer(receivedBytes,Request.class);
                System.out.println(nodeAllResponse.toString());
                break;
            case HEARTBEAT_RSP:
                System.out.println("收到：心跳响应！");
                Request<HeartBeat> heartBeatRequest = ProtoStuffUtils.deserializer(receivedBytes,Request.class);

                lastHeartBeatTime = System.currentTimeMillis();
                leaderDelay = System.currentTimeMillis()-heartBeatRequest.getData().getTimestamp();

                System.out.println(heartBeatRequest.toString());
                break;
            default:
                break;
        }
        input.clear();

    }

    //client->server
    void write() throws IOException {
        switch (request.getRequestCode()) {
            case JOIN_NOTIFICATION: //客户端收到通知
               if(notifyTimes.get()==0) {

                    //心跳任务
                   tickTimer = new Timer();
                   tickTimer.schedule(new TickTask(), Client.tickInterval * 1000);

                   //timeout检测
                   tickCheckTimer = new Timer();
                   tickCheckTimer.schedule(new TickCheckTask(), 0,Client.tickCheckInterval*1000);
               }
               notifyTimes.incrementAndGet();
                selectionKey.interestOps(SelectionKey.OP_READ);
                state = READING;
                break;

            case HEARTBEAT_RSP: //客户端收到心跳响应
                tickTimer.cancel();
                tickTimer = null;
                tickTimer = new Timer();
                tickTimer.schedule(new TickTask(),Client.tickInterval*1000);

                selectionKey.interestOps(SelectionKey.OP_READ);
                state = READING;
            default:
                break;
        }
    }
}
