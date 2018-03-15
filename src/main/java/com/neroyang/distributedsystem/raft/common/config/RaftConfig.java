package com.neroyang.distributedsystem.raft.common.config;

import com.neroyang.distributedsystem.raft.common.entity.Node;

import java.util.List;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/15
 * Time   上午11:08
 */
public class RaftConfig {
    public static final int tickInterval = 5;
    public static final int tickCheckInterval = 5;
    public static final int timeOutTime =10;

    List<Node> getNodeListFromConfigFile(String filePath){
        return null;
    }

}
