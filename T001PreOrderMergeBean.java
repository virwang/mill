package com.soetek;

import java.util.Date;

public class T001PreOrderMergeBean {

    public Date ApplyDate;
    public String CustomerNo;
    public String ReceiverPartnerCode;
    public String MaterialCode;
    public String CreatedBy;

    public T001PreOrderMergeBean(Date applyDate, String customerNo, String receiverPartnerCode, String materialCode, String createdBy) {
        ApplyDate = applyDate;
        CustomerNo = customerNo;
        ReceiverPartnerCode = receiverPartnerCode;
        MaterialCode = materialCode;
        CreatedBy = createdBy;
    }

    public T001PreOrderMergeBean() {

    }

    public Date getApplyDate() {
        return ApplyDate;
    }

    public void setApplyDate(Date applyDate) {
        ApplyDate = applyDate;
    }

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }

    public String getReceiverPartnerCode() {
        return ReceiverPartnerCode;
    }

    public void setReceiverPartnerCode(String receiverPartnerCode) {
        ReceiverPartnerCode = receiverPartnerCode;
    }

    public String getMaterialCode() {
        return MaterialCode;
    }

    public void setMaterialCode(String materialCode) {
        MaterialCode = materialCode;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }
}
