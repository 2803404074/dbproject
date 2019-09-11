package com.dabangvr.model;

/**
 * 优惠券数据
 */
public class CouponMo {
    private boolean isCheck;

    public CouponMo() {
    }

    public CouponMo(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
