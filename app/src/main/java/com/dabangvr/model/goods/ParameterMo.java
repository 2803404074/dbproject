package com.dabangvr.model.goods;

/**
 * key  value  的列表，如商品参数、服务信息等
 */
public class ParameterMo {
    private String key;
    private String value;

    public ParameterMo() {
    }

    public ParameterMo(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
