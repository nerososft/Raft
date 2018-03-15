package com.neroyang.distributedsystem.raft.server;

import com.neroyang.distributedsystem.raft.acceptor.Acceptor;
import com.neroyang.distributedsystem.raft.common.entity.Node;
import com.neroyang.distributedsystem.raft.common.entity.Role;
import com.neroyang.distributedsystem.raft.constant.WhoseAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/13
 * Time   下午2:39
 */
public class RaftServer implements IRaftServer {

    int port;

    final Selector selector;
    final ServerSocketChannel serverSocketChannel;

    public static Map<UUID, SocketChannel> clientsMap = Collections.synchronizedMap(new HashMap<UUID, SocketChannel>());
    public static Map<UUID, Long> clientsBeatMap = Collections.synchronizedMap(new HashMap<UUID, Long>());
    public static Map<UUID,Node> nodeList = Collections.synchronizedMap(new HashMap<UUID,Node>());

    Role role;

    RaftServer(int port,Role role) throws IOException{
        this.role = role;
        this.port = port;
        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.socket().bind(new InetSocketAddress(port));
        this.serverSocketChannel.configureBlocking(false);

        SelectionKey selectionKey = this.serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor(WhoseAcceptor.SERVER,serverSocketChannel,selector));
    }

    public void run() {
        System.out.println("Server listening to port: "+serverSocketChannel.socket().getLocalPort());

        try{
            while(!Thread.interrupted()){
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
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void dispatch(SelectionKey k) {
        Runnable r = (Runnable) (k.attachment());
        if (r != null) {
            r.run();
        }
    }

    public void stop() {

    }

    public void setNodeList(List<Node> nodeList) {

    }

    public synchronized static Set<Map.Entry<UUID, SocketChannel>> getClientMapEntry(){
        return clientsMap.entrySet();
    }
}

