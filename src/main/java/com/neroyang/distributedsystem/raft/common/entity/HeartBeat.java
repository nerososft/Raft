package com.neroyang.distributedsystem.raft.common.entity;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/13
 * Time   下午11:59
 */
public class HeartBeat implements Serializable {
    Long timestamp;

    public HeartBeat() {
    }

    public HeartBeat(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "HeartBeat{" +
                "timestamp=" + timestamp +
                '}';
    }
}
