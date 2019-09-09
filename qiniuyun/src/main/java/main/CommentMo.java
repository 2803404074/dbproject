package main;

import java.util.List;

public class CommentMo {
    private String type;
    private String name;
    private String mess;
    private String head;
    private String date;
    private List<ImgList> listUrl;



    public CommentMo(String type,String name, String mess) {
        this.type = type;
        this.name = name;
        this.mess = mess;
    }

    public CommentMo() {
    }

    public List<ImgList> getListUrl() {
        return listUrl;
    }

    public void setListUrl(List<ImgList> listUrl) {
        this.listUrl = listUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public static class ImgList{
        private String url;

        public ImgList(String url) {
            this.url = url;
        }

        public ImgList() {
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
