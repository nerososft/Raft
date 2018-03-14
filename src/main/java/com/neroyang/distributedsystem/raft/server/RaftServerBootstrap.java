package com.neroyang.distributedsystem.raft.server;

import com.neroyang.distributedsystem.raft.common.entity.Role;

import java.io.IOException;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/13
 * Time   下午3:29
 */
public class RaftServerBootstrap {
    public static void main(String[] args){
        try {
            RaftServer raftServer = new RaftServer(8000,Role.Leader);
            raftServer.run();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
