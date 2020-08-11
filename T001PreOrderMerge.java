package com.soetek;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class T001PreOrderMerge {
    public List<T001SetPreOrderBean> originalData;
    public Map<String, List<T001SetPreOrderBean>> groupByColumnName = new HashMap<>();
    public Map<String, T001SetPreOrderBean> sumByAllFiled = new TreeMap<>();
    public Map<String, ArrayList<T001SetPreOrderBean>> groupByTableHead = new HashMap<>();
    public static final String jndiDB = "java:/comp/env/jdbc/MyDB";
    public T001SetPreOrderBean sumBean = new T001SetPreOrderBean();
//
//    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//    String str = "yyyy/MM/dd";
//   LocalDateTime dateTime = LocalDateTime.parse(str,df);


   //    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    public String CreatedBy = "";

    public T001PreOrderMerge(List<T001SetPreOrderBean> originalData, String createdBy) {
        this.originalData = originalData;
        this.CreatedBy = createdBy;
    }

    public void CreateTempData() throws SQLException {
        //connect to sql server
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Connection conn = null;
        try {

            Context webContainer = new InitialContext();
            DataSource dbSource = (DataSource) webContainer.lookup(jndiDB);
            conn = dbSource.getConnection();
            conn.setAutoCommit(false);

            String tranId = UUID.randomUUID().toString();

            String sql = "INSERT INTO [dbo].[UploadExcelTemp] ([No], [ApplyDate], [StoreCode], [StoreSite], ";
            sql += "[ProductID], [Package], [Status], [Quantity], [TransactionId], [CreatedBy]) ";
            sql += "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

            for (int i = 0; i < originalData.size(); i++) {
                PreparedStatement pstmtNo = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmtNo.setString(1, originalData.get(i).getNo()); //1.No
                pstmtNo.setString(2, String.valueOf(originalData.get(i).getApplyDate()));  //2.ApplyDate
                pstmtNo.setString(3, originalData.get(i).getStoreCode());  //3.StoreCode
                pstmtNo.setString(4, originalData.get(i).getStoreSite());  //4.StoreSite
                pstmtNo.setString(5, originalData.get(i).getProductID());  //5.ProductID
                pstmtNo.setString(6, String.valueOf(originalData.get(i).getPackage()));  //6.Package
                pstmtNo.setString(7, originalData.get(i).getPickupStatus());  //7.PickupStatus
                pstmtNo.setString(8, String.valueOf(originalData.get(i).getQuantity()));  //6.Quantity
                pstmtNo.setString(9, tranId);  //6.Quantity
                pstmtNo.setString(10, this.CreatedBy);  //6.Quantity
                pstmtNo.execute();
            }


            String insertStatement = "DECLARE @status int" +
                    " EXECUTE @status = sp_ExcuteUploadPreOrder ?" +
                    " SELECT @status AS 'STATUS'";
            CallableStatement prepsInsertCustomer = conn.prepareCall(insertStatement);
            prepsInsertCustomer.setString(1, tranId);
            prepsInsertCustomer.executeQuery();

            conn.commit();

        } catch (SQLException | NamingException throwables) {
            conn.rollback();
            throwables.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }
        conn.setAutoCommit(true);
        conn.close();
    }

    //step 1: List PreOrder to Bean
    public void GroupByAllLabor() {
        String defaultReceiverPartnerCode = "0000000000";
        String receiverPartnerCode = "";
        String defaultStoreSite = "000000000";
        String storeSite = "";
//        String pickedup = "已取貨";
//        String notPicked = "尚未取貨";

        for (int i = 0; i < originalData.size(); i++) {

//            if (originalData.get(i).getReceiverPartnerCode() == null) {
//                receiverPartnerCode = defaultReceiverPartnerCode;
//            } else if (originalData.get(i).getReceiverPartnerCode().isEmpty()) {
//                receiverPartnerCode = defaultReceiverPartnerCode;
//            } else {
//                receiverPartnerCode = originalData.get(i).getReceiverPartnerCode();
//            }

//            if (defaultStoreSite == null) {
//                storeSite = defaultStoreSite;
//            } else if (originalData.get(i).getStoreSite().isEmpty()) {
//                storeSite = defaultStoreSite;
//            } else {
//                storeSite = originalData.get(i).getStoreSite();
//            }

//            if (originalData.get(i).getPickupStatus() == pickedup) {
//                pickedup = originalData.get(i).getPickupStatus();
//            } else if (originalData.get(i).getPickupStatus() == notPicked) {
//                notPicked = originalData.get(i).getPickupStatus();
//            }
            LocalDate localDate = originalData.get(i).ApplyDate;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String date =  localDate.format(formatter);

            String key = "" +
//                    dateTime.format(originalData.get(i).getApplyDate()) +
                    date +
                    originalData.get(i).getReceiverPartnerCode() +
                    originalData.get(i).getMaterialCode() +
                    originalData.get(i).getStoreSite() +
                    originalData.get(i).getPickupStatus();

            if (groupByColumnName.containsKey(key)) {
                groupByColumnName.get(key).add(originalData.get(i));
            } else {
                ArrayList<T001SetPreOrderBean> value = new ArrayList<>();
                value.add(originalData.get(i));
                groupByColumnName.put(key, value);
            }
        }
    }

    //step 2: upload preOrder file "ApplyDate","CreatedBy", "ReceiverPartnerCode", "MaterialCode","StoreSite" merge to one sheet , and sum columns
    public void sumByAllLabor() {
        String defaultReceiverPartnerCode = "0000000000";
        String receiverPartnerCode = "";
        String defaultStoreSite = "0000000000";
        String StoreSite = "";

        for (Map.Entry entry : groupByColumnName.entrySet()) {
            sumBean = new T001SetPreOrderBean();
            for (T001SetPreOrderBean bean : (ArrayList<T001SetPreOrderBean>) entry.getValue()) {

//                if (sumBean.getPickupStatus() == bean.PickupStatus && sumBean.MaterialCode == bean.MaterialCode){
//                    sumBean.Quantity += bean.Quantity;
//                }else {
//                    sumBean.Quantity = bean.Quantity;
//                }

                sumBean.Package = bean.Package;
                sumBean.Quantity += bean.Quantity;
                sumBean.AdjustmentQuantity += bean.AdjustmentQuantity;   //已取貨數量
                sumBean.ApplyDate = bean.ApplyDate;
                sumBean.MaterialCode = bean.MaterialCode;
                sumBean.ProductID = bean.ProductID;
                sumBean.StoreCode = bean.StoreCode;
                sumBean.StoreSite = bean.StoreSite;
                sumBean.CustomerNo = bean.CustomerNo;
                sumBean.CustomerName = bean.CustomerName;
                sumBean.ReceiverPartnerCode = bean.ReceiverPartnerCode;
                sumBean.ReceiverPartnerName = bean.ReceiverPartnerName;
                sumBean.PickupStatus = bean.PickupStatus;
//                sumBean.InStorePickUpQty = bean.InStorePickUpQty;
//                sumBean.CreatedBy = bean.CreatedBy;
            }
            sumByAllFiled.put((String) entry.getKey(), sumBean);
        }
    }

    // step 3: preOrder file merge by  "ApplyDate","CreatedBy", "ReceiverPartnerCode", "StoreSite"
    public void GroupByColumnHead() {
        for (Map.Entry entry : sumByAllFiled.entrySet()) {
            T001SetPreOrderBean t001 = (T001SetPreOrderBean) entry.getValue();
          LocalDate localDate = t001.ApplyDate;
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
          String date = localDate.format(formatter);
//            String defaultReceiverPartnerCode = "0000000000";
//            String receiverPartnerCode = "";
//            String storeSite = "";
//            storeSite = t001.StoreSite;
//            String pickedup = "已取貨";
//            String notPicked = "尚未取貨";

//            if (t001.ReceiverPartnerCode == null) {
//                receiverPartnerCode = defaultReceiverPartnerCode;
//            } else if (t001.ReceiverPartnerCode.isEmpty()) {
//                receiverPartnerCode = defaultReceiverPartnerCode;
//            } else {
//                receiverPartnerCode = t001.ReceiverPartnerCode;
//            }

//            if (t001.PickupStatus == pickedup) {
//                pickedup = t001.getPickupStatus();
//            } else if (t001.PickupStatus == notPicked) {
//                notPicked = t001.getPickupStatus();
//            }

            String keyHeadField =
//                    sdf.format(t001.ApplyDate) +
                   date+
                    t001.ReceiverPartnerCode
                    + t001.StoreSite;

            if (groupByTableHead.containsKey(keyHeadField)) {
                groupByTableHead.get(keyHeadField).add(t001);
            } else {
                ArrayList<T001SetPreOrderBean> valueHeadField = new ArrayList<>();
                valueHeadField.add(t001);
                groupByTableHead.put(keyHeadField, valueHeadField);
            }
        }
    }

    public void insertInToDB() throws SQLException {
        //connect to sql server
//        String str = "yyyy/MM/dd";
//        DateTimeFormatter df = DateTimeFormatter.ofPattern(str);
//        LocalDateTime dateTime = LocalDateTime.parse(str,df);
        Connection conn = null;
        try {

            Context webContainer = new InitialContext();
            DataSource dbSource = (DataSource) webContainer.lookup(jndiDB);
            conn = dbSource.getConnection();
            conn.setAutoCommit(false);

//            String sql_getCustomer = "select CustomerTypeCode, CustomerNo " +
//                    "from dbo.Customer " +
//                    "where StoreCode = ?";
//
//            String sql_getCustomerMapping = " select CustomerNo, ReceiverCode " +
//                    " from dbo.CustomerMapping " +
//                    " where StoreSite = ?";

            String sql_insertPreOrder = "INSERT INTO [dbo].[PreOrder] ([PreOrderNo], [ApplyDate], [CustomerNo], [CustomerName], [ReceiverPartnerCode], [ReceiverPartnerName]" +
                    ", [CarTonnes], [OrderTonnes], [PreOrderStatusCode], [SourceCode], [CreatedBy]) " +
                    "VALUES (dbo.fn_GetPreOrderNo(convert(varchar(6),SYSDATETIME(),112))" +
                    ", ? " +  //1. ApplyDate
                    ", ? " +  //2. CustomerNo
                    ", ? " + //3.CustomerName
                    ", ? " + //4.ReceiverPartnerCode
                    ", ? " + //5.ReceiverPartnerName
                    " ,0, ?, 10, 2,?)"; // 6.OrderTonnes 7.CreatedBy

            String sql_insertPreOrderItem =
                    "INSERT INTO [dbo].[PreOrderItem] ([PreOrderId], [MaterialCode], [MaterialName], [Quantity], " +
                            " [AdjustmentQuantity], [UnitCode], [UnitName], [Package], [CreatedBy]) " +
                            "VALUES (?, " +
                            " ?," + //MaterialCode
                            "(SELECT [MaterialName] FROM [dbo].[Material] WITH(NOLOCK) WHERE [MaterialCode] = ?)," +
                            "? , ? , 'MT' ,N'噸'," +
                            "(SELECT [Package] FROM [dbo].[Material] WITH(NOLOCK) WHERE [MaterialCode] = ?)" +
                            ",?)";

            //check if CustomerType is 01 else not 01, means 農系/非農系
            for (Map.Entry entry : this.groupByTableHead.entrySet()) {
                List<T001SetPreOrderBean> beanList = (List<T001SetPreOrderBean>) entry.getValue();
                T001SetPreOrderBean firstBean = beanList.get(0);
//                PreparedStatement pstat_getCustomer = conn.prepareStatement(sql_getCustomer);
//                pstat_getCustomer.setString(1, String.valueOf(firstBean.StoreCode));
//                pstat_getCustomer.execute();
//                ResultSet rs_getCustomer = pstat_getCustomer.getResultSet();
//                while (rs_getCustomer.next()) {
//                    firstBean.CustomerType = rs_getCustomer.getString(1);
//                    firstBean.CustomerNo = rs_getCustomer.getString(2);
//                }
//
//                if ("01".equals(firstBean.CustomerType)) {
//                    PreparedStatement pstst_getCustomerMapping = conn.prepareStatement(sql_getCustomerMapping);
//                    pstst_getCustomerMapping.setString(1, String.valueOf(firstBean.StoreSite));
//                    pstst_getCustomerMapping.execute();
//                    ResultSet rs_getCustomerMapping = pstst_getCustomerMapping.getResultSet();
//                    while (rs_getCustomerMapping.next()) {
//                        firstBean.CustomerNo = rs_getCustomerMapping.getString(1);
//                        firstBean.ReceiverPartnerCode = rs_getCustomerMapping.getString(2);
//                    }
//                } else {
//                    firstBean.ReceiverPartnerCode = firstBean.CustomerNo;
//                }

                //start def & insert PreOrder ColumnName
                PreparedStatement pstmtNo = conn.prepareStatement(sql_insertPreOrder, Statement.RETURN_GENERATED_KEYS);//no

                //formatter ApplyDate type to yyyy/MM/dd
                LocalDate localDate =  firstBean.ApplyDate;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                String date = localDate.format(formatter);

                pstmtNo.setString(1,date); //1.ApplyDate
                pstmtNo.setString(2, firstBean.CustomerNo);  //2.customerNo
                pstmtNo.setString(3, firstBean.CustomerName);  //3.customerName
                pstmtNo.setString(4, firstBean.ReceiverPartnerCode);  //4.receiverPartnerCode
                pstmtNo.setString(5, firstBean.ReceiverPartnerName);  //5.StoreSite

                float sumOfQty = 0;
                for (int i = 0; i < beanList.size(); i++) {
                    sumOfQty += beanList.get(i).Quantity;
                }
                pstmtNo.setString(6, String.valueOf(sumOfQty));  //6.OrderTonnes
                pstmtNo.setString(7, this.CreatedBy);  //7.StoreSite
//                pstmtNo.setString(8, String.valueOf(firstBean.CarTonnes));  //6.CarTonnes
//                pstmtNo.setString(9, String.valueOf(sumOfQty)); //7.OrderTonnes
//                pstmtNo.setString(10, this.CreatedBy); //8.CreatedBy
                pstmtNo.execute();

                //start insert PreOrderItem
                ResultSet result = pstmtNo.getGeneratedKeys();
                int id = 0;
                if (result.next() && !result.wasNull()) {
                    id = result.getInt(1);
                }

                for (int i = 0; i < beanList.size(); i++) {

                    T001SetPreOrderBean bean = beanList.get(i);
                    PreparedStatement pstmtSetInsert = conn.prepareStatement(sql_insertPreOrderItem);
                    pstmtSetInsert.setInt(1, id);// PreOrderId
                    pstmtSetInsert.setString(2, bean.MaterialCode);// ProductID
                    pstmtSetInsert.setString(3, bean.MaterialCode);// ProductID
                    pstmtSetInsert.setFloat(4, bean.Quantity);// Quantity= OrderTonnes/1000
                    pstmtSetInsert.setFloat(5, bean.AdjustmentQuantity);// Quantity= OrderTonnes/1000
                    pstmtSetInsert.setString(6, bean.MaterialCode);//MaterialCode
                    System.out.println(bean.MaterialCode);
                    pstmtSetInsert.setString(7, this.CreatedBy);// CreatedBy
                    pstmtSetInsert.execute();
                }
            }
            conn.commit();
        } catch (SQLException | NamingException throwables) {
            conn.rollback();
            throwables.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
        }
        conn.setAutoCommit(true);
        conn.close();

    }
}












