package com.soetek;

import com.soetek.helper.ExcelHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class T001PreOrderDeleteExcel {
    protected int maxColumnIndex = 2;
    protected int maxRowIndex = 2000;
    protected int maxSheetIndex = 1;
    protected String excelPath = "C:\\temp\\";
    protected Logger myLog = LoggerFactory.getLogger("T001-JC4");
    protected List<T001PreOrderDeleteBean> deleteExcelList = new ArrayList<>();
    protected List<T001PreOrderDeleteBean> errorExcelList = new ArrayList<>();
    protected ExcelHelper helper;
    protected HttpServletResponse myResponse;
    public static final String jndiDB = "java:/comp/env/jdbc/MyDB";

    public T001PreOrderDeleteExcel(String uuid, HttpServletResponse response) {
        excelPath = excelPath + uuid + ".xlsx";
        myResponse = response;
        helper = new ExcelHelper(maxColumnIndex, maxRowIndex, maxSheetIndex, excelPath, myLog);
        helper.openFile();
        helper.read();
    }

    public void inputDeleteExcel() {
        List<List<String>> mySheet = helper.getBook().get(0);

        try {
            Context webContainer = new InitialContext();
            DataSource dbSource = (DataSource) webContainer.lookup(jndiDB);
            Connection connection = dbSource.getConnection();

            //判斷excel input值，空值或null則不寫入
            for (int i = 2; i < mySheet.size(); i++) {
                T001PreOrderDeleteBean deleteBean = new T001PreOrderDeleteBean();
                deleteBean.tryParseListToThis(mySheet.get(i));
                if (deleteBean.PreOrderNo != "" || deleteBean.PreOrderNo != null) {
                    deleteExcelList.add(deleteBean);
                }
            }

            //確認input是否為可刪除單號
            for (int i = 0; i < deleteExcelList.size(); i++) {
                T001PreOrderDeleteBean deleteBean = deleteExcelList.get(i);

                String selectStatement = "SELECT [PreOrderNo], [PreOrderStatusCode]"
                        + " FROM [dbo].[PreOrder] WITH (NOLOCK)"
                        + " WHERE PreOrderNo = ? AND PreOrderStatusCode NOT IN (1, 15)";


                PreparedStatement prepsSelectStatement = connection.prepareStatement(selectStatement);
                prepsSelectStatement.setString(1, deleteBean.PreOrderNo);
                ResultSet rsSelect = prepsSelectStatement.executeQuery();
                while (rsSelect.next()) {
                    errorExcelList.add(new T001PreOrderDeleteBean(rsSelect.getString(1)));
                }
            }

            if (errorExcelList.isEmpty()) {
                for (int i = 0; i < deleteExcelList.size(); i++) {
                    T001PreOrderDeleteBean deleteBean = deleteExcelList.get(i);
                    String updateStatement = "UPDATE [dbo].[PreOrder]" +
                            " SET DeleteFlag = '1', UpdatedTime = SYSDATETIME(), UpdatedBy = 'User1'" +
                            " WHERE PreOrderNo = ?";
                    PreparedStatement prepsUpdateStatement = connection.prepareStatement(updateStatement);
//                    prepsUpdateStatement.setDate(1, new java.sql.Date(new Date().getTime()));
                    prepsUpdateStatement.setString(1, deleteBean.PreOrderNo);
                    prepsUpdateStatement.executeQuery();
                }
            }
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
    }
}
