package com.soetek;

import sun.java2d.pipe.SpanShapeRenderer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class T001PreOrderMerge {
    protected List<T001SetPreOrderBean> originalData;
    protected Map<String, ArrayList<T001SetPreOrderBean>> groupByColumnName;
    protected Map<String, T001SetPreOrderBean> sumByAllFiled;
    protected Map<String, ArrayList<T001SetPreOrderBean>> groupByHeadLabor;
    public static final String jndiDB = "java:/comp/env/jdbc/MyDB";

    public T001PreOrderMerge(List<T001SetPreOrderBean> originalData) {
        this.originalData = originalData;
    }

    //step 1: 將訂單每列成為一個Bean
    public void GroupByAllLabor() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String defaultReceiverPartnerCode = "0000000000";
        String receiverPartnerCOde = "";

        for (int i = 0; i < originalData.size(); i++) {
            if (originalData.get(i).getReceiverPartnerCode() == null) {
                receiverPartnerCOde = defaultReceiverPartnerCode;
            }else if (originalData.get(i).getReceiverPartnerCode().isEmpty()) {
                receiverPartnerCOde = defaultReceiverPartnerCode;
            }else{
                receiverPartnerCOde = originalData.get(i).getReceiverPartnerCode();
            }
            String key = "" +
                    sdf.format(originalData.get(i).getApplyDate()) +
                    receiverPartnerCOde +
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

    //step 2: 上傳預購訂單依照 "ApplyDate","CreatedBy", "ReceiverPartnerCode", "MaterialCode"合併成一張預購單，並加總需要的欄位
    public void sumByAllLabor() {
        for (Map.Entry entry : groupByColumnName.entrySet()) {
            T001SetPreOrderBean sumBean = new T001SetPreOrderBean();
            for (T001SetPreOrderBean bean : (ArrayList<T001SetPreOrderBean>) entry.getValue()) {
                sumBean.Package += bean.Package;
                sumBean.PreOrderWeight += bean.PreOrderWeight;

                String key = "" + originalData.get(((ArrayList) entry.getValue()).size()).getApplyDate().toString() +
                        originalData.get(((ArrayList) entry.getValue()).size()).getCreatedBy() +
                        originalData.get(((ArrayList) entry.getValue()).size()).getReceiverPartnerCode() +
                        originalData.get(((ArrayList) entry.getValue()).size()).getMaterialCode();

                sumByAllFiled.put(key, sumBean);

            }
            for (int i = 0; i < groupByColumnName.entrySet().size(); i++) {
                String key = "" + originalData.get(i).getApplyDate() +
                        originalData.get(i).getCreatedBy() +
                        originalData.get(i).getReceiverPartnerCode() +
                        originalData.get(i).getMaterialCode();

                ArrayList<T001SetPreOrderBean> list = new ArrayList<>();
                list.add(originalData.get(i));
                groupByColumnName.put(key, list);
            }
        }
    }

    // step 3: 預購訂單依照 "ApplyDate","CreatedBy", "ReceiverPartnerCode" 各自合併
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
                        originalData.get(b).getCreatedBy() +
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

            ResultSet resultSetNo = null;
            ResultSet resultSetInsert = null;

            String sqlNo = "INSERT INTO [dbo].[PreOrder] ([PreOrderNo], [ApplyDate], [CustomerNo], [CustomerName], [ReceiverPartnerCode], [ReceiverPartnerName],[PlateNumber], [CarTonnes], [OrderTonnes], [PreOrderStatusCode], [SourceCode], [CreatedBy])VALUES (dbo.fn_GetPreOrderNo(convert(varchar(6),SYSDATETIME(),112)), ?, ?,(SELECT [CustomerName] FROM [dbo].[Customer] WITH(NOLOCK) WHERE [CustomerNo] = ?),?,(SELECT [PartnerName] FROM [dbo].[CustomerPartner] A WITH(NOLOCK) JOIN [dbo].[Customer] B WITH(NOLOCK) ON A.[CustomerNo] = B.[CustomerNo]JOIN [dbo].[PartnerType] C WITH(NOLOCK) ON A.[PartnerTypeCode] = C.[PartnerTypeCode] AND C.[PartnerTypeCode] = 'SH' WHERE B.[CustomerNo] = ? AND A.[PartnerCode] = ?), ? ,?, ?, ?, ?, ?)";
            String sqlInsert = "INSERT INTO [dbo].[PreOrderItem] ([PreOrderId], [MaterialCode], [MaterialName], [Quantity], [UnitCode], [UnitName], [Package], [Remark], [CreatedBy]) VALUES (?, ?,(SELECT [MaterialName] FROM [dbo].[Material] WITH(NOLOCK) WHERE [MaterialCode] = ?),?, ?, (SELECT [UnitName] FROM [dbo].[Unit] WITH(NOLOCK) WHERE [UnitCode] = ?), ?,?, ?)";


            PreparedStatement pstmtNo = conn.prepareStatement(sqlNo);
            pstmtNo.setString(1, t001.No.toString());
            PreparedStatement pstmtSetInsert = conn.prepareStatement(sqlInsert);
            pstmtSetInsert.setString(1, t001.MaterialCode);


            resultSetNo = pstmtNo.executeQuery();
            resultSetInsert = pstmtSetInsert.executeQuery();

        } catch (SQLException | NamingException throwables) {
            throwables.printStackTrace();
        }
    }
}












