package cn.hejinyo;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2024/12/11 15:08
 */
public class PingAnExcelToCSV {
    // 定义日期格式化规则
    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    public static void main(String[] args) {
        for (String month : Arrays.asList("1")) {
            doProcess(month);
        }
    }

    private static void doProcess(String month) {
        String excelFilePath = "/Users/hejinyo/Downloads/平安储蓄卡/平安银行个人账户交易明细" + month + ".xlsx"; // 修改为你的文件路径
        String csvFilePath = "/Users/hejinyo/Downloads/平安储蓄卡/平安银行个人账户交易明细" + month + ".csv"; // 输出的 CSV 文件路径

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis);
             PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath))) {

            Sheet sheet = workbook.getSheetAt(0); // 读取第一个工作表
            Iterator<Row> rowIterator = sheet.iterator();

            // 写入 CSV 表头
//            writer.println("Date,Amount,Remark,Notes");
            // 写入表头
            writer.println("Date, Amount, Source Currency, Target Currency, Exchange Rate, Budget Book, Account, Folder, Category, Payee, Tags, Notes");
            // 写入数据
            Map<String, String> flodMap = PDFToCSV.getFoldMap();

            // 遍历行
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // 跳过前 5 行
                if (row.getRowNum() > 5) {
                    Cell dateCell = null, amountCell = null, remarkCell = null, notesCell = null;

                    // 遍历每一行的单元格
                    for (Cell cell : row) {
                        int columnIndex = cell.getColumnIndex();
                        switch (columnIndex) {
                            case 1: // 假设 "Date" 在第 1 列
                                dateCell = cell;
                                break;
                            case 2: // 假设 "Amount" 在第 2 列
                                amountCell = cell;
                                break;
                            case 5: // 假设 "Remark" 在第 5 列
                                remarkCell = cell;
                                break;
                            case 6: // 假设 "Notes" 在第 6 列
                                notesCell = cell;
                                break;
                            default:
                                // 不处理其他列
                        }
                    }
                    if (StringUtils.hasLength(getCellValue(dateCell))) {
                        String note = getCellValue(remarkCell) + getCellValue(notesCell);
                        note = note.replace("/", "");
                        String category = buildFolderCategory(note);
                        String flod = flodMap.get(category);

                        // Date, Amount, Source Currency, Target Currency, Exchange Rate, Budget Book, Account, Folder, Category, Payee, Tags, Notes
                        // 将结果写入 CSV 文件
                        //Date
                        String csvLine = convertToFullDate(getCellValue(dateCell)) + ","
                                // Amount
                                + getCellValue(amountCell) + ","
                                // Source Currency
                                + "CNY" + ","
                                // Target Currency
                                + "CNY" + ","
                                // Exchange Rate
                                + "1" + ","
                                // Budget Book
                                + "我的帐单" + ","
                                + "平安银行储蓄卡0142" + ","
                                // Folder
                                + flod + ","
                                // Category
                                + category + ","
                                // Payee
                                + ","
                                // Tags
                                + ","
                                // Notes
                                + note;
                        writer.println(csvLine);
                    }
                }
            }

            System.out.println("Data successfully written to " + csvFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String buildFolderCategory(String notes) {
        if (StringUtil.isNotBlank(notes)) {
            Map<String, String> categoryMap = getCategoryMap();
            for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
                if (notes.toLowerCase().contains(entry.getKey().toLowerCase())) {
                    return entry.getValue();
                }
            }
        }
        return "其他";
    }

    // 将日期转换为完整的日期格式,时间部分为 12:00:00
    public static String convertToFullDate(String inputDate) {
        try {
            // 解析为完整的日期对象
            Date fullDate = INPUT_DATE_FORMAT.parse(inputDate);
            // 格式化输出完整日期,时间默认设置为 12:00:00
            return OUTPUT_DATE_FORMAT.format(fullDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 如果解析失败,返回默认格式
        return inputDate;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }


    private static Map<String, String> getCategoryMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("支付利息", "利息");
        map.put("结息", "利息");
        map.put("自助消费", "电商");
        map.put("招商银行信用卡还款", "房贷");
        map.put("招行手机银行一网通", "转出");
        map.put("借钱", "转出");
        map.put("房贷", "房贷");
        map.put("手续费", "其他");
        map.put("长服计划分红", "工资");
        map.put("代发工资", "工资");
        map.put("灵活宝", "灵活宝");
        map.put("灵活宝自动赎回", "灵活宝");
        map.put("灵活宝[LHB001]扣款", "灵活宝");
        map.put("余额宝提现", "提现");
        map.put("网银转款本金", "提现");
        map.put("银联渠道他代本借记卡无卡交易", "提现");
        map.put("快捷支付", "提现");
        map.put("转账", "转入");
        map.put("红包提现代付", "转入");
        map.put("跨行转出", "提现");
        map.put("财付通", "其他");
        map.put("支付宝", "其他");

        return map;
    }

}
