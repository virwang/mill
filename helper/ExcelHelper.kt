package com.helper

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
//import org.slf4j.Logger
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream

open class ExcelHelper {
    protected var maxColumnIndex : Int = 0
    protected var maxRowIndex : Int = 0
    protected var maxSheetIndex : Int = 0
    protected var excelPath : String = ""
    //protected var myLog : Logger? = null
    protected var workbook: Workbook = XSSFWorkbook()
    public var book : MutableList<MutableList<MutableList<String>>> = mutableListOf()
//    public var bookFontColor : MutableList<MutableList<MutableList<String?>>> = mutableListOf()

    open fun write(bookContent : MutableList<MutableList<MutableList<String>>>, outFilePath: String) {
        val wb = XSSFWorkbook()

        for( i in 0 until bookContent.count()) {
            val sheet = wb.createSheet("mySheet" + i )
            for(j in 0 until bookContent[i].count()) {
                val row = sheet.createRow(j)
                for(k in 0 until bookContent[i][j].count()) {
                    val cell = row.createCell(k)
                    cell.setCellValue(bookContent[i][j][k]);
                }
            }
        }

        //val myConfig: Properties = PropertyHelper().myProperty
        //val outPath = myConfig["WebPathRunOut"].toString()
        val fileOut: OutputStream = FileOutputStream(outFilePath)
        wb.write(fileOut)
    }

    open fun read() {
        for (i in 0 until workbook.numberOfSheets) {
            if(i > this.maxSheetIndex) {
                break
            }

            val currentSheet: Sheet = workbook.getSheetAt(i)

            //取得所有的合併區域
            val regionsList: MutableList<CellRangeAddress> = mutableListOf()
            for (i in 0 until currentSheet.numMergedRegions) {
                regionsList.add(currentSheet.getMergedRegion(i))
            }

            val itRow: Iterator<Row> = currentSheet.iterator()
            var sheet : MutableList<MutableList<String>> = mutableListOf()
//            var sheetFontColor : MutableList<MutableList<String?>> = mutableListOf()

            while (itRow.hasNext()) {
                val nextRow = itRow.next()

                if (nextRow.rowNum > this.maxRowIndex) {
                    break
                }

                val itCell = nextRow.cellIterator()
                var rowCurrent: MutableList<String> = mutableListOf()
//                var rowFontColor: MutableList<String?> = mutableListOf()

                while (itCell.hasNext()) {
                    val cell = itCell.next()

                    if (cell.columnIndex > this.maxColumnIndex) {
                        break
                    }

                    //將欄位值存入 rowCurrent
                    //檢查是否為合併儲存格, 若是合併儲存格, 則紀錄左上角的內容
                    var addFlag : Boolean = false
                    for(region in regionsList) {
                        if (region.isInRange(cell.rowIndex, cell.columnIndex))
                        {
                            val stringRegionValue : String = currentSheet.getRow(region.firstRow).getCell(region.firstColumn).stringCellValue
                            rowCurrent.add(stringRegionValue)
                            addFlag = true
                            break
                        }
                    }

                    if (addFlag == false) {
                        if(cell.cellType == CellType.NUMERIC) {
                            rowCurrent.add(cell.numericCellValue.toString())
                        }else if (cell.cellType == CellType.STRING) {
                            rowCurrent.add(cell.stringCellValue.trim())
                        }else {
                            rowCurrent.add(cell.stringCellValue.trim())
                        }
                    }

//                    val style: XSSFCellStyle = cell.cellStyle as XSSFCellStyle
//                    val font = style.font
//                    rowFontColor.add(style.font.xssfColor?.argbHex)
                }
                sheet.add(rowCurrent)
//                sheetFontColor.add(rowFontColor)
            }
            book.add(sheet)
//            bookFontColor.add(sheetFontColor)
        }

    }

    open fun openFile() {
        val excelFile = File(this.excelPath)
        val isExcel = FileInputStream(excelFile)
        this.workbook = XSSFWorkbook(isExcel)
    }

    constructor(maxColumnIndex: Int,
                maxRowIndex: Int,
                maxSheetIndex: Int,
                excelPath: String
                //myLog: Logger?
                ) {
        this.maxColumnIndex = maxColumnIndex
        this.maxRowIndex = maxRowIndex
        this.maxSheetIndex = maxSheetIndex
        this.excelPath = excelPath
        //this.myLog = myLog
    }
}