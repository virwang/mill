package com.soetek;

import com.helper.IExcelHelperBean;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

public class T001SetPreOrderBean implements IExcelHelperBean {

    public String No;      //序號
    public LocalDateTime ApplyDate;   //申請日期
    public String StoreCode;  //經銷商ID
    public String StoreSite; //經銷點ID
    public String MaterialCode; //預購肥料代碼
    public float Package;       //重量(公斤/包)
    public String PickupStatus;  //取貨狀態
    public float Quantity;      //預購重量(頓)
    public float AdjustmentQuantity; //已取貨重量(頓)
    public String ErrorMessage;  //錯誤訊息

    // Columns for merging & setting PreOrder
    public String ReceiverPartnerCode;  //收貨人
    public String ReceiverPartnerName;  //收貨人
    public String CreatedBy;            //訂單建立人
//    public Integer InStorePickUpQty;      //已取包裹數量 錯誤
    public String CustomerNo;
    public String CustomerName;
    public String MaterialName;
    public String UnitCode;
    public String UnitName;
    public String Remark;
    public String PlateNumber;
    public float CarTonnes;
    public int PreOrderStatusCode;
    public int SourceCode;
    public String CustomerType;
    public String ProductID;   //Material Table 的 ProductID



    public T001SetPreOrderBean() {
        this.Package = 0;
        this.Quantity = 0;
        this.AdjustmentQuantity = 0;
        this.CarTonnes = 0;
        this.PreOrderStatusCode = 0;
        this.SourceCode = 0;
    }

    public T001SetPreOrderBean(String no, LocalDateTime applyDate, String storeCode, String storeSite, String materialCode, float aPackage, String pickupStatus, float quantity, String errorMessage, String receiverPartnerCode, String createdBy, Integer inStorePickUpQty, String customerNo, String materialName, String unitCode, String unitName, String remark, String plateNumber, float carTonnes, int preOrderStatusCode, int sourceCode,String productID) {
        this.No = no;
        this.ApplyDate = applyDate;
        this.StoreCode = storeCode;
        this.StoreSite = storeSite;
        this.MaterialCode = materialCode;
        this.Package = aPackage;
        this.PickupStatus = pickupStatus;
        this.Quantity = quantity;
        this.ErrorMessage = errorMessage;
        this.ReceiverPartnerCode = receiverPartnerCode;
        this.CreatedBy = createdBy;

        this.CustomerNo = customerNo;
        this.MaterialName = materialName;
        this.UnitCode = unitCode;
        this.UnitName = unitName;
        this.Remark = remark;
        this.PlateNumber = plateNumber;
        this.CarTonnes = carTonnes;
        this.PreOrderStatusCode = preOrderStatusCode;
        this.SourceCode = sourceCode;
        this.ProductID = productID;
    }

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }

    public String getMaterialName() {
        return MaterialName;
    }

    public void setMaterialName(String materialName) {
        MaterialName = materialName;
    }

    public String getUnitCode() {
        return UnitCode;
    }

    public void setUnitCode(String unitCode) {
        UnitCode = unitCode;
    }

    public String getUnitName() {
        return UnitName;
    }

    public void setUnitName(String unitName) {
        UnitName = unitName;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getPlateNumber() {
        return PlateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        PlateNumber = plateNumber;
    }

    public float getCarTonnes() {
        return CarTonnes;
    }

    public void setCarTonnes(float carTonnes) {
        CarTonnes = carTonnes;
    }

    public int getPreOrderStatusCode() {
        return PreOrderStatusCode;
    }

    public void setPreOrderStatusCode(int preOrderStatusCode) {
        PreOrderStatusCode = preOrderStatusCode;
    }

    public int getSourceCode() {
        return SourceCode;
    }

    public void setSourceCode(int sourceCode) {
        SourceCode = sourceCode;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public LocalDateTime getApplyDate() {
        return ApplyDate;
    }

    public void setApplyDate(LocalDateTime applyDate) {
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

    public float getPackage() {
        return Package;
    }

    public void setPackage(float aPackage) {
        Package = aPackage;
    }

    public String getPickupStatus() {
        return PickupStatus;
    }

    public void setPickupStatus(String pickupStatus) {
        PickupStatus = pickupStatus;
    }

    public Float getQuantity() {
        return Quantity;
    }

    public void setQuantity(float quantity) {
        Quantity = quantity;
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

    public String getReceiverPartnerName() {
        return ReceiverPartnerName;
    }

    public void setReceiverPartnerName(String receiverPartnerName) {
        ReceiverPartnerName = receiverPartnerName;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getCustomerType() {
        return CustomerType;
    }

    public void setCustomerType(String customerType) {
        CustomerType = customerType;
    }

    public float getAdjustmentQuantity() {
        return AdjustmentQuantity;
    }

    public void setAdjustmentQuantity(float adjustmentQuantity) {
        AdjustmentQuantity = adjustmentQuantity;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
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
        result.add(String.valueOf(this.Quantity));
        result.add(this.ErrorMessage);
        return result;
    }

    @Override
    public void tryParseListToThis(List<String> row) {
        this.setErrorMessage("");

        for (int i = 0; i < row.size(); i++) {
            switch (i) {
                case 0:
                    this.No = row.get(i);
                    if (row.get(i).isEmpty()) {
                        this.setErrorMessage("序號不得是空格");
                        break;
                    }
                    if (this.No.endsWith(".0")){
                       this.No = row.get(i).substring(0,this.No.length() - 2);
                    }
                    break;
                case 1:
//                    String str = "yyyy/MM/dd";
//                    LocalDate local = this.ApplyDate;
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(str);

//                   SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
//                   formatter.
//                    LocalDate localDate = new LocalDate();
//                    LocalDate localday = LocalDate.this.

//                    LocalDate localDate = LocalDate.parse(str,formatter);

//                    str = localDate.format(formatter);

//                    Date date = Date.from();

                    if (row.get(i).isEmpty()) {
                        break;
                    }
                    try {
//                        LocalDateTime local = LocalDateTime.parse(row.get(i));

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//                        String time = formatter.format(local);
//                        System.out.println(time);
//                        String day = local.format(formatter);
//                        LocalDate localDate = LocalDate.parse(this.ApplyDate.format());
//                        String day = local.format(formatter);
                        this.ApplyDate = LocalDateTime.parse(row.get(i),formatter);
                        System.out.println(this.ApplyDate);

                    } catch (Exception e){
                        this.setErrorMessage("日期格式錯誤");
                    }
                    break;
                case 5:
                    this.StoreCode = row.get(i);
                    if (row.get(i).isEmpty()) {
                        this.setErrorMessage("經銷點必須是文字，且不得為空格");
                    }
                    if (this.StoreCode.endsWith(".0")) {
                        this.StoreCode = this.StoreCode.substring(0, this.StoreCode.length() - 2);
                    }
                    break;
                case 7:
                    this.StoreSite = row.get(i);
                    if (row.get(i).isEmpty()) {
                        this.setErrorMessage("經銷點id必須是文字，且不得為空格");
                    }
                    if (this.StoreSite.endsWith(".0")) {
                        this.StoreSite = this.StoreSite.substring(0, this.StoreSite.length() - 2);
                    }
                    break;
                case 10:
                    this.ProductID = row.get(i) ;
                    if (row.get(i).isEmpty()) {
                        this.setErrorMessage("預購肥料ID必須是文字，且不得為空格");
                    }
                    if (this.ProductID.endsWith(".0")) {
                        this.ProductID = this.ProductID.substring(0, this.ProductID.length() - 2);
                    }
                    break;
                case 11:
                    try {
                        this.Package = Float.parseFloat(row.get(i));
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
                        this.Quantity = Float.parseFloat(row.get(i));
                        this.Quantity = this.Quantity / (float)1000;
                        if (this.PickupStatus != null) {
                            if (this.PickupStatus.trim().equals("已取貨")) {
                                this.AdjustmentQuantity = this.Quantity;
                            }
                        }
                    } catch (NumberFormatException e) {
                        this.setErrorMessage("預購重量必須為數字");
                    }
                    break;
                default:
                    break;
            }
        }


    }
}
