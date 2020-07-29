package com.soetek.helper;
import java.io.Serializable;
import java.util.List;

public interface IExcelHelperBean extends Serializable {
    public List<String>tryParseThisToList();
    public void tryParseListToThis(List<String> row);
}
