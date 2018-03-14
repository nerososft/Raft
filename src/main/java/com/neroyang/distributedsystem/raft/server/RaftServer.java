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

    public static Map<UUID, SocketChannel> clientsMap = new HashMap<UUID, SocketChannel>();
    public static Map<UUID, Long> clientsbeatMap = new HashMap<UUID, Long>();
    public static Map<UUID,Node> nodeList = new HashMap<UUID,Node>();
    Timer timer;

    Role role;
    public static Long timeOut = 10*1000L;
    public static int tickInterval = 5;

    RaftServer(int port,Role role) throws IOException{
        this.role = role;
        this.port = port;
        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.socket().bind(new InetSocketAddress(port));
        this.serverSocketChannel.configureBlocking(false);

        SelectionKey selectionKey = this.serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor(WhoseAcceptor.SERVER,serverSocketChannel,selector));

        timer = new Timer();
    }

    class BeatCheckTask extends TimerTask{

        public void run() {
            if(!clientsbeatMap.isEmpty()) {
                for (Map.Entry<UUID,Long> entry: clientsbeatMap.entrySet()) {
                    if (System.currentTimeMillis() - entry.getValue()>2*tickInterval*1000) {
                        System.out.println("客户端 "+entry.getKey()+"异常，掉线！");
                        nodeList.remove(entry.getKey());
                        clientsbeatMap.remove(entry.getKey());
                        clientsMap.remove(entry.getKey());
                    }
                }
            }
        }
    }

    public void run() {
        System.out.println("Server listening to port: "+serverSocketChannel.socket().getLocalPort());

        try{
            timer.schedule(new BeatCheckTask(),5*1000,5*1000);
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
}
