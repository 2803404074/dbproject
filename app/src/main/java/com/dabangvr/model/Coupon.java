package com.dabangvr.model;


/**
 * 优惠卷bean
 */
public class Coupon {
    //ID
    private Long id;
    //标题
    private String title;
    //优惠券类型：1新用户、2满减券、3代金券
    private Integer couponType;
    //发放开始时间
    private String sendStartDate;
    //发放结束时间
    private String sendEndDate;
    //可用范围：1平台、2指定店铺、3指定商品
    private Integer availableRange;
    //发放类型：1自动领取 、2平台发放、3指定给用户
    private Integer sendType;
    //使用开始时间
    private String useStartDate;
    //使用结束时间
    private String useEndDate;
    //优惠金额
    private double couponAmount;
    //消费金额
    private double consumptionAmount;
    //所属商家
    private Long deptId;
    //优惠券数量
    private Integer couponNumber;
    //剩余数量
    private Integer remainingNumber;
    //状态：1开启 0 关闭 -1 已失效 -2已删除
    private Integer state;
    //添加时间
    private String addTime;
    //商家名称
    private String deptName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCouponType() {
        return couponType;
    }

    public void setCouponType(Integer couponType) {
        this.couponType = couponType;
    }

    public String getSendStartDate() {
        return sendStartDate;
    }

    public void setSendStartDate(String sendStartDate) {
        this.sendStartDate = sendStartDate;
    }

    public String getSendEndDate() {
        return sendEndDate;
    }

    public void setSendEndDate(String sendEndDate) {
        this.sendEndDate = sendEndDate;
    }

    public Integer getAvailableRange() {
        return availableRange;
    }

    public void setAvailableRange(Integer availableRange) {
        this.availableRange = availableRange;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public String getUseStartDate() {
        return useStartDate;
    }

    public void setUseStartDate(String useStartDate) {
        this.useStartDate = useStartDate;
    }

    public String getUseEndDate() {
        return useEndDate;
    }

    public void setUseEndDate(String useEndDate) {
        this.useEndDate = useEndDate;
    }

    public double getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(double couponAmount) {
        this.couponAmount = couponAmount;
    }

    public double getConsumptionAmount() {
        return consumptionAmount;
    }

    public void setConsumptionAmount(double consumptionAmount) {
        this.consumptionAmount = consumptionAmount;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Integer getCouponNumber() {
        return couponNumber;
    }

    public void setCouponNumber(Integer couponNumber) {
        this.couponNumber = couponNumber;
    }

    public Integer getRemainingNumber() {
        return remainingNumber;
    }

    public void setRemainingNumber(Integer remainingNumber) {
        this.remainingNumber = remainingNumber;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}