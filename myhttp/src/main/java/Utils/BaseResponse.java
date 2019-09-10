package Utils;

import org.json.JSONObject;

public class BaseResponse{
    private int errno;
    private String errmsg;
    private String data;
    public BaseResponse() {
    }
    public int getErrno() {
        return errno;
    }
    public String getErrmsg() {
        return errmsg;
    }
    public String getData() {
        return data;
    }
}
