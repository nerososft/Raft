package com.neroyang.distributedsystem.raft.server.handler;

import com.neroyang.distributedsystem.raft.server.handler.ServerHandler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/13
 * Time   下午2:56
 */
public class RaftServerHandler extends ServerHandler {

    static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    static final int PROCESSING = 2;

    public RaftServerHandler(SocketChannel socketChannel, Selector selector) throws IOException {
        super(socketChannel, selector);
    }

    public void read() throws IOException {
        int readCount = socketChannel.read(input);
        if (readCount > 0) {
            state = PROCESSING;
            executorService.execute(new Processer(readCount));
        }
        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    synchronized void processAndHandle(int readCount) throws IOException {
        readProcess(readCount);
        state = SENDING;
    }

    class Processer implements Runnable {
        int readCount;

        public Processer(int readCount) {
            this.readCount = readCount;
        }

        public void run() {
            try {
                processAndHandle(readCount);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
