package com.neroyang.distributedsystem.raft.server;

import com.neroyang.distributedsystem.raft.common.entity.Node;

import java.util.List;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/13
 * Time   下午2:11
 */
public interface IRaftServer {
    void run();
    void stop();
    void setNodeList(List<Node> nodeList);
}
