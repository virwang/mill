package com.soetek;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class T001PreOrderPostError {

    protected List<T001SetPreOrderBean> t001s;
    protected HttpServletResponse myResponse;
    protected T001PreOrderMerge merge;

    public T001PreOrderPostError(List<T001SetPreOrderBean> t001s, HttpServletResponse myResponse) {
        this.t001s = t001s;
        this.myResponse = myResponse;
    }

    public void ErrorMessage() throws IOException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonbHead = new JSONObject();
        JSONObject jsonbItem;
        jsonbHead.put("status", 1);
        jsonbHead.put("error", "上傳失敗，請看錯誤訊息");

        for (T001SetPreOrderBean t001 : t001s) {
            String NoString = "";
            String ErrorMsg= "";
            jsonbItem = new JSONObject();

            if (t001.No != null) {
                NoString = t001.No;
            }
            jsonbItem.put("No", NoString);

            if (t001.ErrorMessage != null && !t001.ErrorMessage.isEmpty()){
                ErrorMsg = t001.ErrorMessage;
            }

            jsonbItem.put("ErrorMessage", ErrorMsg);
            jsonArray.put(jsonbItem);
        }
        jsonbHead.put("Items", jsonArray);
        myResponse.setContentType("text/json; charset=UTF-8");
        PrintWriter out = myResponse.getWriter();
        String outString = jsonbHead.toString();
        out.println(outString);
        out.flush();
        out.close();
        //out.write(jsonb.toString());
    }

    protected void postError() {
        try {
            ErrorMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void postSuccess() throws IOException {

        JSONObject jsonSuccess = new JSONObject();
        jsonSuccess.put("status", 0);
        jsonSuccess.put("error", "上傳成功");
        jsonSuccess.put("Items", "[ ]");

        myResponse.setContentType("text/json; charset=UTF-8");
        PrintWriter out = myResponse.getWriter();
        String outString = jsonSuccess.toString();
        out.println(outString);
        out.flush();
        out.close();
    }
}

