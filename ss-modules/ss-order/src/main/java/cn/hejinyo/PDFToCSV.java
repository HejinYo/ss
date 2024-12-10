package cn.hejinyo;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2024/12/10 16:16
 */

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFToCSV {

    // 定义日期格式化规则
    private static final SimpleDateFormat inputDateFormat = new SimpleDateFormat("MM/dd");
    private static final SimpleDateFormat inputDateFormat2 = new SimpleDateFormat("yyyy-MM/dd");
    private static final SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        String pdfFilePath = "/Users/hejinyo/Downloads/2023年12月信用卡账单.pdf"; // PDF 文件路径
        String csvFilePath = "/Users/hejinyo/Downloads/2023年12月信用卡账单.csv"; // 输出 Excel 文件路径

        try {
            // 1. 解析 PDF 文件
            String pdfContent = extractTextFromPDF(pdfFilePath);

            // 2. 解析账单明细
            String[][] parsedData = parseBillDetails(pdfContent);

            // 3. 输出为 CSV 格式
            writeToCSV(parsedData, csvFilePath);

            System.out.println("账单明细已成功导出为 CSV 文件：" + csvFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 提取 PDF 文本
    private static String extractTextFromPDF(String pdfFilePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    // 解析账单明细
    private static String[][] parseBillDetails(String pdfContent) {
        String[] lines = pdfContent.split("\n");
        int rowCount = 0;
        String[][] data = new String[lines.length][4]; // 假设账单有4列：交易日、摘要、金额、卡号末四位

        for (String line : lines) {
            // 替换所有 [NBSP] 空格为普通空格
            line = line.replace("\u00A0", " ").trim().replaceAll("\\s+", " ");
            // 打印调试信息
            System.out.println("Line: [" + line + "]");

            // 匹配以日期开头的交易明细
            if (line.matches("^\\d{2}/\\d{2}.*")) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 5) {
                    data[rowCount][0] = parts[0];  // 交易日
                    data[rowCount][1] = parts[2];  // 摘要（商户名）
                    data[rowCount][2] = parts[parts.length - 2];  // 金额
                    data[rowCount][3] = parts[parts.length - 1].replace("(CN)", "").trim();  // 卡号末四位
                    rowCount++;
                }
            }
        }
        return data;
    }

    // 将日期转换为完整的日期格式，时间部分为 12:00:00
    public static String convertToFullDate(String inputDate) {
        try {
            // 获取当前年份
            String currentYear = new SimpleDateFormat("yyyy").format(new Date());
            // 生成新的日期字符串，格式为：yyyy-MM-dd
            String fullDateString = currentYear + "-" + inputDate; // 例如：2024-11/25

            // 解析为完整的日期对象
            Date fullDate = inputDateFormat2.parse(fullDateString);

            // 格式化输出完整日期，时间默认设置为 12:00:00
            return outputDateFormat.format(fullDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputDate + " 12:00:00";  // 如果解析失败，返回默认格式
    }

    // 将数据写入 CSV 文件
    private static void writeToCSV(String[][] data, String csvFilePath) throws IOException {
        try (FileWriter csvWriter = new FileWriter(csvFilePath)) {
            // 写入表头
            csvWriter.append("交易日, 摘要, 金额, 卡号末四位\n");

            // 写入数据
            for (String[] row : data) {
                if (row[0] != null) {
                    // 格式化交易日期
                    String formattedDate = convertToFullDate(row[0]);
                    csvWriter.append(formattedDate).append(", ")
                            .append(row[1]).append(", ")
                            .append(row[2]).append(", ")
                            .append(row[3]).append("\n");
                }
            }
        }
    }
}
