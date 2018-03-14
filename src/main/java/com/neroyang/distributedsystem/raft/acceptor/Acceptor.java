package com.neroyang.distributedsystem.raft.acceptor;

import com.neroyang.distributedsystem.raft.client.handler.ClientHandler;
import com.neroyang.distributedsystem.raft.constant.WhoseAcceptor;
import com.neroyang.distributedsystem.raft.server.RaftServer;
import com.neroyang.distributedsystem.raft.server.handler.RaftServerHandler;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/13
 * Time   下午6:30
 */
public class Acceptor implements Runnable
{
    ServerSocketChannel serverSocketChannel;
    SocketChannel socketChannel;
    Selector selector;
    WhoseAcceptor whoseAcceptor;

    public Acceptor(WhoseAcceptor whoseAcceptor,ServerSocketChannel socketChannel, Selector selector) {
        this.whoseAcceptor = whoseAcceptor;
        this.serverSocketChannel = socketChannel;
        this.selector = selector;
    }

    public Acceptor(WhoseAcceptor whoseAcceptor,SocketChannel socketChannel, Selector selector) {
        this.whoseAcceptor = whoseAcceptor;
        this.socketChannel = socketChannel;
        this.selector = selector;
    }

    public void run() {
        try{
            if(whoseAcceptor==WhoseAcceptor.SERVER) {
                if (serverSocketChannel != null) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    new RaftServerHandler(socketChannel, selector);
                }
            }else if(whoseAcceptor==WhoseAcceptor.CLIENT){
                new ClientHandler(socketChannel, selector);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}