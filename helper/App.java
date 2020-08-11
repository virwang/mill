package com.helper;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String filepath = "";
        if (args.length > 0 && args[0] != null && !args[0].isEmpty()) {
            filepath = args[0];
        }else{
            filepath = "C:/temp/from_jean.xlsx";
        }
        ExcelHelper helper = new
                ExcelHelper(999,999,999,filepath);
        helper.openFile();
        helper.read();
        System.out.println( "Hello World!" );
    }
}
