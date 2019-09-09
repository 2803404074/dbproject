package com.dabangvr.model;

public class ActivityBean {
    private int id;
    private String name;
    private int deptId;
    private int enough;
    private int reduce;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public int getEnough() {
        return enough;
    }

    public void setEnough(int enough) {
        this.enough = enough;
    }

    public int getReduce() {
        return reduce;
    }

    public void setReduce(int reduce) {
        this.reduce = reduce;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ActivityBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deptId=" + deptId +
                ", enough=" + enough +
                ", reduce=" + reduce +
                ", status=" + status +
                '}';
    }
}
