package com.soetek;

import com.soetek.helper.ExcelHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")
public class T001PreOrderExcelCheck {// extends HttpServlet {
    protected int maxColumnIndex = 26;
    protected int maxRowIndex = 2000;
    protected int maxSheetIndex = 1;
    protected String excelPath = "C:\\temp\\";
    protected Logger myLog = LoggerFactory.getLogger("T001-JC2");
    protected List<T001SetPreOrderBean> t001s = new ArrayList<>();
    protected ExcelHelper helper;
    protected HttpServletResponse myResponse;
    public static final String jndiDB = "java:/comp/env/jdbc/MyDB";

    public T001PreOrderExcelCheck(String uuid, HttpServletResponse response) {
//        String uri = (String) request.getAttribute("javax.servlet.forward.request_uri");
        excelPath = excelPath + uuid + ".xlsx";
        myResponse = response;
        helper = new ExcelHelper(maxColumnIndex, maxRowIndex, maxSheetIndex, excelPath, myLog);
        helper.openFile();
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
                if (t001.No != null) {
                    t001s.add(t001);
                }
            }

            for (int i = 0; i < t001s.size(); i++) {
                T001SetPreOrderBean t001 = t001s.get(i);
                if (t001.getErrorMessage().isEmpty()) {
                    continue;
                }

                //sql logic
                String selectStoreCode = "SELECT COUNT(*) FROM Customer WITH(NOLOCK) WHERE StoreCode = ?";
                String selectSotoreSite = "SELECT COUNT(*) FROM CustomerMapping WITH(NOLOCK) WHERE StoreSite = ?";

                PreparedStatement pstmtSotreCode = conn.prepareStatement(selectStoreCode);
                pstmtSotreCode.setString(1, t001.getStoreCode());
                PreparedStatement pstmtSotreSite = conn.prepareStatement(selectSotoreSite);
                pstmtSotreSite.setString(1, t001.getStoreSite());

                resultSetStoreCode = pstmtSotreCode.executeQuery();
                resultSotreSite = pstmtSotreSite.executeQuery();

                //sql check && Business Logic
                if (resultSetStoreCode.equals(0) || resultSotreSite.equals(0)) {
                    t001.ErrorMessage += " 查無資料經銷商ID或經銷點ID資料，請確認後再重新上傳";
                } else if (t001.StoreCode.isEmpty()) {
                    t001.ErrorMessage += " 經銷商ID不能空白";
                } else if (t001.StoreSite.isEmpty()) {
                    t001.ErrorMessage += " 經銷點ID不能空白";
                } else if (t001.MaterialCode.isEmpty()) {
                    t001.ErrorMessage += " 肥料不能空白";
                } else if (t001.Package <= 0) {
                    t001.ErrorMessage += " 每包重量必須大於零";
                } else if (t001.PickupStatus.isEmpty()) {
                    t001.ErrorMessage += " 取貨狀態不能空白";
                } else if (t001.OrderTonnes <= 0) {
                    t001.ErrorMessage += " 預購重量必須大於零";
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
                error.postError();
            } else {
                T001PreOrderMerge create = new T001PreOrderMerge(t001s);
                create.GroupByAllLabor();
                create.sumByAllLabor();
                create.sumByColumnName();
                create.insertInToDB();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
