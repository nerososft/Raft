package com.neroyang.distributedsystem.raft.client;

import com.neroyang.distributedsystem.raft.common.entity.Node;
import com.neroyang.distributedsystem.raft.constant.ELECTION;
import com.neroyang.distributedsystem.raft.common.entity.Request;
import com.neroyang.distributedsystem.raft.constant.WhoseAcceptor;
import com.neroyang.distributedsystem.raft.acceptor.Acceptor;
import com.neroyang.distributedsystem.raft.utils.ProtoStuffUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/13
 * Time   下午5:08
 */
public class Client {

    Selector selector;
    SocketChannel socketChannel;
    String hostIP;
    int hostPort;
    public static int tickInterval = 5;

    public Client(String hostIP, int hostPort) throws IOException {
        this.hostIP = hostIP;
        this.hostPort = hostPort;
        initialize();
    }

    private void initialize() throws IOException {
        socketChannel = SocketChannel.open(new InetSocketAddress(hostIP, hostPort));
        socketChannel.configureBlocking(false);

        selector = Selector.open();
        SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(new Acceptor(WhoseAcceptor.CLIENT, socketChannel, selector));
    }


    public void sendMsg(ByteBuffer message) throws IOException {
        socketChannel.write(message);
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                int readySelectionKeyCount = selector.select();
                if (readySelectionKeyCount == 0) {
                    continue;
                }
                Set<SelectionKey> selected = selector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()) {
                    dispatch((SelectionKey) (it.next()));
                }

                selected.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void dispatch(SelectionKey k) {
        Runnable r = (Runnable) (k.attachment());
        if (r != null) {
            r.run();
        }
    }
}
