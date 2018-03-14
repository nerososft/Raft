package com.neroyang.distributedsystem.raft.client;

import com.neroyang.distributedsystem.raft.common.entity.Node;
import com.neroyang.distributedsystem.raft.common.entity.Request;
import com.neroyang.distributedsystem.raft.constant.ELECTION;
import com.neroyang.distributedsystem.raft.utils.ProtoStuffUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/14
 * Time   上午12:30
 */
public class ClientDemo {
    public static void main(String[] args) {
        try {
            String clientName="test_node_01";
            String hostIp="localhost";
            int hostPort = 8000;
            final Client client = new Client(hostIp, hostPort);
            Node node = new Node(clientName,hostIp,hostPort,0L);
            Request<Node> nodeRequest = new Request<Node>(
                    UUID.randomUUID(),
                    ELECTION.JOIN_REQ,
                    node);
            byte[] bytes = ProtoStuffUtils.serializer(nodeRequest);
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            new Thread(new Runnable() {
                public void run() {
                    client.run();
                }
            }).start();

            client.sendMsg(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
