package com.soetek;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class T001PreOrderMerge {
    public List<T001SetPreOrderBean> originalData;
    public Map<String, List<T001SetPreOrderBean>> groupByColumnName = new HashMap<>();
    public Map<String, T001SetPreOrderBean> sumByAllFiled = new TreeMap<>();
    public Map<String, ArrayList<T001SetPreOrderBean>> groupByHeadLabor = new HashMap<>();
    public static final String jndiDB = "java:/comp/env/jdbc/MyDB";
    T001SetPreOrderBean sumBean = new T001SetPreOrderBean();

    public T001PreOrderMerge(List<T001SetPreOrderBean> originalData) {
        this.originalData = originalData;
    }

    //step 1: List PreOrder to Bean
    public void GroupByAllLabor() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String defaultReceiverPartnerCode = "0000000000";
        String receiverPartnerCode = "";

        for (int i = 0; i < originalData.size(); i++) {
            if (originalData.get(i).getReceiverPartnerCode() == null) {
                receiverPartnerCode = defaultReceiverPartnerCode;
            } else if (originalData.get(i).getReceiverPartnerCode().isEmpty()) {
                receiverPartnerCode = defaultReceiverPartnerCode;
            } else {
                receiverPartnerCode = originalData.get(i).getReceiverPartnerCode();
            }

            String key = "" +
                    sdf.format(originalData.get(i).getApplyDate()) +
                    receiverPartnerCode +
                    originalData.get(i).getMaterialCode();

            if (groupByColumnName.containsKey(key)) {
                groupByColumnName.get(key).add(originalData.get(i));
            } else {
                ArrayList<T001SetPreOrderBean> value = new ArrayList<>();
                value.add(originalData.get(i));
                groupByColumnName.put(key, value);
            }
        }
    }

    //step 2: upload preOrder file "ApplyDate","CreatedBy", "ReceiverPartnerCode", "MaterialCode"merge to one sheet , and sum columns
    public void sumByAllLabor() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String defaultReceiverPartnerCode = "0000000000";
        String receiverPartnerCode = "";

        for (Map.Entry entry : groupByColumnName.entrySet()) {

            for (T001SetPreOrderBean bean : (ArrayList<T001SetPreOrderBean>) entry.getValue()) {
                sumBean.Package += bean.Package;
                sumBean.PreOrderWeight += bean.PreOrderWeight;

                String keys = sdf.format(bean.getApplyDate()) +
                        sumBean.getMaterialCode() +
                        sumBean.getReceiverPartnerCode();

//                String key = "" + originalData.get(((ArrayList) entry.getValue()).size()).getApplyDate().toString()+
//                        originalData.get(((ArrayList) entry.getValue()).size()).getCreatedBy() +
//                        originalData.get(((ArrayList) entry.getValue()).size()).getReceiverPartnerCode() +
//                        originalData.get(((ArrayList) entry.getValue()).size()).getMaterialCode();
                sumByAllFiled.put(keys, sumBean);
            }

            for (int i = 0; i < groupByColumnName.entrySet().size(); i++) {
                String key = "" + originalData.get(i).getApplyDate() +
                        originalData.get(i).getReceiverPartnerCode() +
                        originalData.get(i).getMaterialCode();

                ArrayList<T001SetPreOrderBean> list = new ArrayList<>();
                list.add(originalData.get(i));
                groupByColumnName.put(key, list);
            }
        }
    }

    // step 3: preOrder file merge by  "ApplyDate","CreatedBy", "ReceiverPartnerCode"
    public void sumByColumnName() {
        for (Map.Entry entry : groupByHeadLabor.entrySet()) {
            T001SetPreOrderBean byHead = new T001SetPreOrderBean();
            for (T001SetPreOrderBean beans : (ArrayList<T001SetPreOrderBean>) entry.getValue()) {

                //OrderTonnes or PreOrderWeight which one is correct?
                beans.OrderTonnes += beans.OrderTonnes;
                beans.Package += beans.Package;
            }
            for (int b = 0; b < groupByColumnName.entrySet().size(); b++) {
                String key = "" + originalData.get(b).getApplyDate() +
                        originalData.get(b).getMaterialCode() +
                        originalData.get(b).getReceiverPartnerCode();

                ArrayList<T001SetPreOrderBean> groupHead = new ArrayList<>();
                groupHead.add(originalData.get(b));
                groupByHeadLabor.put(key, groupHead);
            }

        }
    }

    public void insertInToDB() {
        T001SetPreOrderBean t001 = new T001SetPreOrderBean();
        //connect to sql server
        try {
            Context webContainer = new InitialContext();
            DataSource dbSource = (DataSource) webContainer.lookup(jndiDB);
            Connection conn = dbSource.getConnection();

            ResultSet resultMerge = null;
            ResultSet resultSet = null;
            ResultSet resultSetNo = null;
            ResultSet resultSetInsert = null;

            String mergeTables = "SELECT [dbo].[Customer].[CustomerNo] from [dbo].[Customer] C LEFT JOIN [dbo].[PreOrder] PO  on [RECEIVERPARTNERCODE] ";
            String sqlNo = "INSERT INTO [dbo].[PreOrder] ([PreOrderNo], [ApplyDate], [CustomerNo], [CustomerName], [ReceiverPartnerCode], [ReceiverPartnerName],[PlateNumber], [CarTonnes], [OrderTonnes], [PreOrderStatusCode], [SourceCode], [CreatedBy])VALUES (dbo.fn_GetPreOrderNo(convert(varchar(6),SYSDATETIME(),112)), ?, ?,(SELECT [CustomerName] FROM [dbo].[Customer] WITH(NOLOCK) WHERE [CustomerNo] = ?),?,(SELECT [PartnerName] FROM [dbo].[CustomerPartner] A WITH(NOLOCK) JOIN [dbo].[Customer] B WITH(NOLOCK) ON A.[CustomerNo] = B.[CustomerNo]JOIN [dbo].[PartnerType] C WITH(NOLOCK) ON A.[PartnerTypeCode] = C.[PartnerTypeCode] AND C.[PartnerTypeCode] = 'SH' WHERE B.[CustomerNo] = ? AND A.[PartnerCode] = ?), ? ,?, ?, ?, ?, ?)";
            String sqlInsert = "INSERT INTO [dbo].[PreOrderItem] ([PreOrderId], [MaterialCode], [MaterialName], [Quantity], [UnitCode], [UnitName], [Package], [Remark], [CreatedBy]) VALUES (?, ?,(SELECT [MaterialName] FROM [dbo].[Material] WITH(NOLOCK) WHERE [MaterialCode] = ?),?, ?, (SELECT [UnitName] FROM [dbo].[Unit] WITH(NOLOCK) WHERE [UnitCode] = ?), ?,?, ?)";

            PreparedStatement pstmtMerge = conn.prepareStatement(mergeTables);
            PreparedStatement pstmtNo = conn.prepareStatement(sqlNo);
            pstmtNo.setString(1, t001.No.toString());
            PreparedStatement pstmtSetInsert = conn.prepareStatement(sqlInsert);
            pstmtSetInsert.setString(1, t001.MaterialCode);

            resultMerge = pstmtMerge.executeQuery();
            resultSetNo = pstmtNo.executeQuery();
            resultSetInsert = pstmtSetInsert.executeQuery();

        } catch (SQLException | NamingException throwables) {
            throwables.printStackTrace();
        }
    }
}












