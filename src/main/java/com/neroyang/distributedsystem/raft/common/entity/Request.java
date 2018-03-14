package com.neroyang.distributedsystem.raft.common.entity;

import com.neroyang.distributedsystem.raft.constant.ELECTION;

import java.io.Serializable;
import java.util.UUID;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/13
 * Time   下午4:55
 */
public class Request<T> implements Serializable{
    UUID requestID;
    ELECTION requestCode;
    T data;


    public Request() {
    }

    public Request(UUID requestID, ELECTION requestCode, T data) {
        this.requestID = requestID;
        this.requestCode = requestCode;
        this.data = data;
    }

    public UUID getRequestID() {
        return requestID;
    }

    public void setRequestID(UUID requestID) {
        this.requestID = requestID;
    }

    public ELECTION getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(ELECTION requestCode) {
        this.requestCode = requestCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestID=" + requestID +
                ", requestCode=" + requestCode +
                ", data=" + data +
                '}';
    }
}
