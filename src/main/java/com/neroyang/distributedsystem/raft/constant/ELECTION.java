package com.neroyang.distributedsystem.raft.constant;

import java.io.Serializable;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/13
 * Time   下午4:52
 */

public enum ELECTION implements Serializable {
    JOIN_REQ,
    JOIN_NOTIFICATION,
    JOIN_NOTIFICATION_RSP,
    JOIN_RSP,
    VOTE_REQ,
    VOTE_RSP,
    HEARTBEAT_REQ,
    HEARTBEAT_RSP
}
