package com.soetek;

import com.soetek.helper.IExcelHelperBean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class T001SetPreOrderBean implements IExcelHelperBean {

    public String No;      //序號
    public Date ApplyDate;   //申請日期
    public String StoreCode;  //經銷商ID
    public String StoreSite; //經銷點ID
    public String MaterialCode; //預購肥料代碼
    public Float Package;       //重量(公斤/包)
    public String PickupStatus;  //取貨狀態
    public Float OrderTonnes;      //預購重量(頓)
    public String ErrorMessage;  //錯誤訊息

    // Columns for merging PreOrder
    public String ReceiverPartnerCode;  //收貨人
    public String CreatedBy;            //訂單建立人
    public Float PreOrderWeight;        //預購重量，合併後成為OrderTonnes
    public Integer InStorePickUpQty;      //已取包裹數量

    public T001SetPreOrderBean() {
    }

    public T001SetPreOrderBean(String no, Date applyDate, String storeCode, String storeSite, String materialCode, Float aPackage, String pickupStatus, Float orderTonnes, String errorMessage, String receiverPartnerCode, String createdBy, Float preOrderWeight, Integer inStorePickUpQty) {
        No = no;
        ApplyDate = applyDate;
        StoreCode = storeCode;
        StoreSite = storeSite;
        MaterialCode = materialCode;
        Package = aPackage;
        PickupStatus = pickupStatus;
        OrderTonnes = orderTonnes;
        ErrorMessage = errorMessage;
        ReceiverPartnerCode = receiverPartnerCode;
        CreatedBy = createdBy;
        PreOrderWeight = preOrderWeight;
        InStorePickUpQty = inStorePickUpQty;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public Date getApplyDate() {
        return ApplyDate;
    }

    public void setApplyDate(Date applyDate) {
        ApplyDate = applyDate;
    }

    public String getStoreCode() {
        return StoreCode;
    }

    public void setStoreCode(String storeCode) {
        StoreCode = storeCode;
    }

    public String getStoreSite() {
        return StoreSite;
    }

    public void setStoreSite(String storeSite) {
        StoreSite = storeSite;
    }

    public String getMaterialCode() {
        return MaterialCode;
    }

    public void setMaterialCode(String materialCode) {
        MaterialCode = materialCode;
    }

    public Float getPackage() {
        return Package;
    }

    public void setPackage(Float aPackage) {
        Package = aPackage;
    }

    public String getPickupStatus() {
        return PickupStatus;
    }

    public void setPickupStatus(String pickupStatus) {
        PickupStatus = pickupStatus;
    }

    public Float getOrderTonnes() {
        return OrderTonnes;
    }

    public void setOrderTonnes(Float orderTonnes) {
        OrderTonnes = orderTonnes;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public String getReceiverPartnerCode() {
        return ReceiverPartnerCode;
    }

    public void setReceiverPartnerCode(String receiverPartnerCode) {
        ReceiverPartnerCode = receiverPartnerCode;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public Float getPreOrderWeight() {
        return PreOrderWeight;
    }

    public void setPreOrderWeight(Float preOrderWeight) {
        PreOrderWeight = preOrderWeight;
    }

    public Integer getInStorePickUpQty() {
        return InStorePickUpQty;
    }

    public void setInStorePickUpQty(Integer inStorePickUpQty) {
        InStorePickUpQty = inStorePickUpQty;
    }

    @Override
    public List<String> tryParseThisToList() {
        List<String> result = new ArrayList<>();
        result.add(String.valueOf(this.No));
        result.add(String.valueOf(this.ApplyDate));
        result.add(this.StoreCode);
        result.add(this.StoreSite);
        result.add(this.MaterialCode);
        result.add(String.valueOf(this.Package));
        result.add(this.PickupStatus);
        result.add(String.valueOf(this.OrderTonnes));
        result.add(this.ErrorMessage);
        return result;
    }

    @Override
    public void tryParseListToThis(List<String> row) {
        this.setErrorMessage("");

        for (int i = 0; i < row.size(); i++) {
            switch (i) {
                case 0:
                    if (row.get(i).isEmpty()) {
                        this.setErrorMessage("序號不得是空格");
                        break;
                    }
                    this.No = row.get(i);
                    break;
                case 1:
                    String date = "YYYY/MM/dd";
                    DateFormat df = new SimpleDateFormat(date);
                    if (row.get(i).isEmpty()) {
                        break;
                    }
                    try {
                        this.ApplyDate = df.parse(row.get(i));
                    } catch (ParseException e) {
                        this.setErrorMessage("日期格式不正確. ");
                    }
                    break;
                case 5:
                    this.StoreCode = row.get(i);
                    if (row.get(i).isEmpty()) {
                        this.setErrorMessage("經銷點必須是文字，且不得為空格");
                    }
                    if (this.StoreCode.endsWith(".0")) {
                        this.StoreCode.substring(0, this.StoreCode.length() - 2);
                    }
                    break;
                case 7:
                    this.StoreSite = row.get(i);
                    if (row.get(i).isEmpty()) {
                        this.setErrorMessage("經銷點id必須是文字，且不得為空格");
                    }
                    if (this.StoreSite.endsWith(".0")) {
                        this.StoreSite.substring(0, this.StoreSite.length() - 2);
                    }
                    break;
                case 10:
                    this.MaterialCode = row.get(i);
                    if (row.get(i).isEmpty()) {
                        this.setErrorMessage("預購肥料ID必須是文字，且不得為空格");
                    }
                    if (this.MaterialCode.endsWith(".0")) {
                        this.MaterialCode.substring(0, this.MaterialCode.length() - 2);
                    }
                    break;
                case 11:
                    try {
                        this.Package = Float.valueOf(row.get(i));
                    } catch (NumberFormatException e) {
                        this.setErrorMessage("每包重量必須為數字");
                    }
                    break;
                case 12:
                    this.PickupStatus = row.get(i);
                    if (row.get(i).isEmpty()) {
                        this.setErrorMessage("取貨狀態必填，且必須是文字");
                    }
                    break;
                case 14:
                    if (row.get(i).isEmpty()) {
                        break;
                    }
                    try {
                        this.OrderTonnes = Float.valueOf(row.get(i));
                    } catch (NumberFormatException e) {

                        this.setErrorMessage("預購重量必須為數字");

                    }
                    break;
                default:
                    //row.add("");
                    //this.setErrorMessage("欄位不能空白");
                    break;
            }
        }


    }
}
