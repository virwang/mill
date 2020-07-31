package com.soetek;

import com.soetek.helper.IExcelHelperBean;

import java.util.ArrayList;
import java.util.List;

public class T001PreOrderDeleteBean implements IExcelHelperBean {
    public String PreOrderNo;
    public int PreOrderStatusCode;

    public T001PreOrderDeleteBean() {
    }

    public T001PreOrderDeleteBean(String preOrderNo) {
        PreOrderNo = preOrderNo;
    }
//
//    public T001PreOrderDeleteBean(String preOrderNo, int preOrderStatusCode) {
//        PreOrderNo = preOrderNo;
//        PreOrderStatusCode = preOrderStatusCode;
//    }

    public String getPreOrderNo() {
        return PreOrderNo;
    }

    public void setPreOrderNo(String preOrderNo) {
        PreOrderNo = preOrderNo;
    }

    public int getPreOrderStatusCode() {
        return PreOrderStatusCode;
    }

    public void setPreOrderStatusCode(int preOrderStatusCode) {
        PreOrderStatusCode = preOrderStatusCode;
    }

    @Override
    public List<String> tryParseThisToList() {
        List<String> result = new ArrayList<>();
        result.add(this.PreOrderNo);
        return result;
    }

    @Override
    public void tryParseListToThis(List<String> row) {
        for (int i = 0; i < row.size(); i++) {
            switch (i) {
                case 1:
                    this.PreOrderNo = row.get(i);
                    break;
                default:
                    break;
            }
        }
    }
}
