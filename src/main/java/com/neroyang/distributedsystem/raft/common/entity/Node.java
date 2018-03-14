package com.neroyang.distributedsystem.raft.common.entity;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetSocketAddress;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/13
 * Time   下午2:13
 */
public class Node implements Serializable {

    String nodeName;
    String hostIP;
    Integer hostPort;
    Long delay;

    public Node() {
    }

    public Node(String nodeName, String hostIP, Integer hostPort, Long delay) {
        this.nodeName = nodeName;
        this.hostIP = hostIP;
        this.hostPort = hostPort;
        this.delay = delay;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getHostIP() {
        return hostIP;
    }

    public void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }

    public Integer getHostPort() {
        return hostPort;
    }

    public void setHostPort(Integer hostPort) {
        this.hostPort = hostPort;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeName='" + nodeName + '\'' +
                ", hostIP='" + hostIP + '\'' +
                ", hostPort=" + hostPort +
                ", delay=" + delay +
                '}';
    }
}
