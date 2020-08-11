package com.soetek;

import com.helper.ExcelHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")
public class T001PreOrderExcelCheck {
    protected int maxColumnIndex = 26;
    protected int maxRowIndex = 2000;
    protected int maxSheetIndex = 1;
    protected String excelPath = "C:\\temp\\";
    protected Logger myLog = LoggerFactory.getLogger("T001-JC2");
    protected List<T001SetPreOrderBean> t001s = new ArrayList<>();
    protected ExcelHelper helper;
    protected HttpServletResponse myResponse;
    public static final String jndiDB = "java:/comp/env/jdbc/MyDB";
    protected String CreatedBy = "";
    protected T001SetPreOrderBean setBean = new T001SetPreOrderBean();

    public T001PreOrderExcelCheck(String myUuid, String createdBy, HttpServletResponse response) {
        this.CreatedBy = createdBy;
        excelPath = excelPath + myUuid + ".xlsx";
        myResponse = response;
        helper = new ExcelHelper(maxColumnIndex, maxRowIndex, maxSheetIndex, excelPath);
        helper.openFile();

//        if (row.getCell(0) !=null){
//            row.getCell(0).setCellType(Cell.CellType)
//        }
//
//        String data ="";
//        if (cell.getCellType()== CellType.STRING){
//             data.cell.getStringCellValue();
//        }else if (cell.getCellType()==CellType.NUMERIC){
//            data = String.valueOf(cell.getNumericCellValue);
//        }

        helper.read();
    }

    public void CheckLogic() {
        List<List<String>> mySheet = helper.getBook().get(0);

        //connect to sql server
        try {
            Context webContainer = new InitialContext();
            DataSource dbSource = (DataSource) webContainer.lookup(jndiDB);
            Connection conn = dbSource.getConnection();

            ResultSet resultSetStoreCode = null;
            ResultSet resultSotreSite = null;

            //read excel sheet with only one sheet
            for (int i = 2; i < mySheet.size(); i++) {
                T001SetPreOrderBean t001 = new T001SetPreOrderBean();
                t001.tryParseListToThis(mySheet.get(i));
//                if (mySheet.get(i).isEmpty() || mySheet.get(i) == null ){
//                    t001.ErrorMessage += " Excel空白，請填入資料";
//                    break;
//                }else if (t001.No == null || t001.No.isEmpty()){
//                    t001.ErrorMessage += " Excel 序號不得空白";
//                    break;
//                }

                if (t001.No != null && !t001.No.isEmpty()) {
                    t001s.add(t001);
                }
            }

            for (int i = 0; i < t001s.size(); i++) {
                T001SetPreOrderBean t001 = t001s.get(i);
                if (!t001.getErrorMessage().isEmpty()) {
                    continue;
                }

                //sql logic
                String selectStoreCode = "SELECT COUNT(*) FROM Customer WITH(NOLOCK) WHERE StoreCode = ?";
                String selectSotoreSite = "SELECT COUNT(*) FROM CustomerMapping WITH(NOLOCK) WHERE StoreSite = ?";

                //check if CustomerNo,CustomerName exits in [dbo].Customer
                String sql_getCustomer = "select CustomerTypeCode, CustomerNo, CustomerName " +
                        "from dbo.Customer " +
                        "where StoreCode = ?";
                //check if CustomerNo,ReceiverCode suite StoreSite on  Excel table
                String sql_getCustomerMapping = " select CustomerNo, ReceiverCode " +
                        " from dbo.CustomerMapping " +
                        " where StoreSite = ?";

                //check if CustomerName correct & exits in [dbo].Customer where CustomerNo =? from sql_getCustomerMapping
                String sql_getCustomerName = "select CustomerName from dbo.Customer where CustomerNo = ?";

                //check if MaterialCode exits in [dbo].Material where ProductID = ? from Excel
                String sql_getMaterial = "select MaterialCode from dbo.Material where ProductID = ?";

                //connection, implements sql String
                PreparedStatement pstmtSotreCode = conn.prepareStatement(selectStoreCode);
                pstmtSotreCode.setString(1, t001.getStoreCode());
                PreparedStatement pstmtSotreSite = conn.prepareStatement(selectSotoreSite);
                pstmtSotreSite.setString(1, t001.getStoreSite());

                resultSetStoreCode = pstmtSotreCode.executeQuery();

                int cnt = 0;
                while (resultSetStoreCode.next()) {
                    cnt += resultSetStoreCode.getInt(1);
                }

                resultSotreSite = pstmtSotreSite.executeQuery();
                while (resultSotreSite.next()) {
                    cnt += resultSotreSite.getInt(1);
                }

                //用經銷商ID抓取的客戶代碼數量
                int cnt_getCustomer = 0;

                //取得CustomerType
                PreparedStatement pstat_getCustomer = conn.prepareStatement(sql_getCustomer);
                pstat_getCustomer.setString(1, String.valueOf(t001.StoreCode));
                pstat_getCustomer.execute();
                ResultSet rs_getCustomer = pstat_getCustomer.getResultSet();

                while (rs_getCustomer.next()) {
                    t001.CustomerType = rs_getCustomer.getString(1);
                    t001.CustomerNo = rs_getCustomer.getString(2);
                    t001.CustomerName = rs_getCustomer.getString(3);
                    cnt_getCustomer++;
                }

                //農系01: 要額外用customerMapping抓客戶代碼, 同樣要檢查客戶代碼數量
                int cnt_getCustomerMapping = 0;
                if ("01".equals(t001.CustomerType)) {
                    PreparedStatement pstst_getCustomerMapping = conn.prepareStatement(sql_getCustomerMapping);
                    pstst_getCustomerMapping.setString(1, String.valueOf(t001.StoreSite));
                    pstst_getCustomerMapping.execute();
                    ResultSet rs_getCustomerMapping = pstst_getCustomerMapping.getResultSet();
                    while (rs_getCustomerMapping.next()) {
                        t001.CustomerNo = rs_getCustomerMapping.getString(1);
                        t001.ReceiverPartnerCode = rs_getCustomerMapping.getString(2);
                        cnt_getCustomerMapping++;
                    }
                    PreparedStatement pstst_getCustName = conn.prepareStatement(sql_getCustomerName);
                    pstst_getCustName.setString(1, t001.CustomerNo);
                    pstst_getCustName.execute();
                    ResultSet rs_getCustName = pstat_getCustomer.getResultSet();
                    while (rs_getCustName.next()) {
                        t001.CustomerName = rs_getCustName.getString(1);
                    }
                    PreparedStatement pstst_getCustName2 = conn.prepareStatement(sql_getCustomerName);
                    pstst_getCustName2.setString(1, t001.ReceiverPartnerCode);
                    pstst_getCustName2.execute();
                    ResultSet rs_getCustName2 = pstst_getCustName2.getResultSet();
                    while (rs_getCustName2.next()) {
                        t001.ReceiverPartnerName = rs_getCustName2.getString(1);
                    }
                } else {
                    t001.ReceiverPartnerCode = t001.CustomerNo;
                    t001.ReceiverPartnerName = t001.CustomerName;
                }

                //抓取物料號碼
                int cnt_getMaterial = 0;
                PreparedStatement pstst_getMaterial = conn.prepareStatement(sql_getMaterial);
                pstst_getMaterial.setString(1, t001.ProductID);
                pstst_getMaterial.execute();
                ResultSet rs_getMaterial = pstst_getMaterial.getResultSet();
                while (rs_getMaterial.next()) {
                    t001.MaterialCode = rs_getMaterial.getString(1);
                    cnt_getMaterial++;
                }

                //sql check && Business Logic
                if (cnt == 0) {
                    t001.ErrorMessage += " 查無資料經銷商ID或經銷點ID資料，請確認後再重新上傳";
                } else if (t001.StoreCode.isEmpty()) {
                    t001.ErrorMessage += " 經銷商ID不能空白";
                } else if (t001.StoreSite.isEmpty()) {
                    t001.ErrorMessage += " 經銷點ID不能空白";
                } else if (t001.MaterialCode == null || t001.MaterialCode.isEmpty()) {
                    t001.ErrorMessage += " 找不到肥料號碼";
                } else if (t001.Package <= 0) {
                    t001.ErrorMessage += " 每包重量必須大於零";
                } else if (t001.PickupStatus.isEmpty()) {
                    t001.ErrorMessage += " 取貨狀態不能空白";
                } else if (t001.Quantity <= 0) {
                    t001.ErrorMessage += " 預購重量必須大於零";
                } else if ("01".equals(t001.CustomerType) && cnt_getCustomerMapping != 1) {
                    t001.ErrorMessage += "農系經銷點對應主檔有誤, 客戶非唯一(" + t001.StoreSite + ")";
                } else if ((!"01".equals(t001.CustomerType)) && cnt_getCustomer != 1) {
                    t001.ErrorMessage += "商系經銷商對應客戶有誤, 客戶非唯一(" + t001.StoreCode + ")";
                } else if (t001.CustomerNo.isEmpty()) {
                    t001.ErrorMessage += "找不到客戶代碼";
                } else if (t001.ReceiverPartnerCode.isEmpty()) {
                    t001.ErrorMessage += "找不到收貨點";
                } else if (t001.ReceiverPartnerName == null || t001.ReceiverPartnerName.isEmpty()) {
                    t001.ErrorMessage += "收貨點沒有中文名稱. " + t001.ReceiverPartnerCode;
                } else if (cnt_getMaterial != 1) {
                    t001.ErrorMessage += "預購肥料ID非唯一";
                }
            }
            boolean all_ok = true;
            for (int i = 0; i < t001s.size(); i++) {
                if (!t001s.get(i).getErrorMessage().isEmpty()) {
                    all_ok = false;
                    break;
                }
            }

            if (!all_ok) {
                T001PreOrderPostError error = new T001PreOrderPostError(t001s, myResponse);
                error.ErrorMessage();
                error.postError();
            } else {
                T001PreOrderMerge create = new T001PreOrderMerge(t001s, this.CreatedBy);
//                create.CreateTempData(); // Insert Excel to DB
                create.GroupByAllLabor();
                create.sumByAllLabor();
                create.GroupByColumnHead();
                create.insertInToDB();
                T001PreOrderPostError ok = new T001PreOrderPostError(t001s, myResponse);
                ok.postSuccess();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
