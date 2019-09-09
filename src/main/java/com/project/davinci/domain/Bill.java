package com.project.davinci.domain;


import java.io.Serializable;
import java.math.BigDecimal;

public class Bill implements Serializable {
    private int id;
    private int user_id;
    private int goods_id;
    private String goods_name;
    private String brief;
    private BigDecimal total_amount;
    private int stage_months;
    private int repayment_months;
    private int is_payment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public int getStage_months() {
        return stage_months;
    }

    public void setStage_months(int stage_months) {
        this.stage_months = stage_months;
    }

    public int getRepayment_months() {
        return repayment_months;
    }

    public void setRepayment_months(int repayment_months) {
        this.repayment_months = repayment_months;
    }

    public int getIs_payment() {
        return is_payment;
    }

    public void setIs_payment(int is_payment) {
        this.is_payment = is_payment;
    }

    public BigDecimal getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(BigDecimal total_amount) {
        this.total_amount = total_amount;
    }
}
