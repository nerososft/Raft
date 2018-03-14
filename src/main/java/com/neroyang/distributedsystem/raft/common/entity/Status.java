package com.neroyang.distributedsystem.raft.common.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/3/13
 * Time   下午4:32
 */
public class Status implements Serializable {
    Role myRole;
    List<Node> leader;
    List<Node> followers;


    public Status(Role myRole, List<Node> leader, List<Node> followers) {
        this.myRole = myRole;
        this.leader = leader;
        this.followers = followers;
    }

    public Role getMyRole() {
        return myRole;
    }

    public void setMyRole(Role myRole) {
        this.myRole = myRole;
    }

    public List<Node> getLeader() {
        return leader;
    }

    public void setLeader(List<Node> leader) {
        this.leader = leader;
    }

    public List<Node> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Node> followers) {
        this.followers = followers;
    }

    @Override
    public String toString() {
        return "Status{" +
                "myRole=" + myRole +
                ", leader=" + leader +
                ", followers=" + followers +
                '}';
    }
}
