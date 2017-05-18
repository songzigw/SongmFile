package cn.songm.file.entity;

import cn.songm.common.beans.Entity;

public class FileUrl extends Entity implements java.io.Serializable {

    private static final long serialVersionUID = -1778690045534504903L;

    private String server;

    private String path;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
