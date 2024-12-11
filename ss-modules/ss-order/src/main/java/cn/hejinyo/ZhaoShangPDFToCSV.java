package cn.hejinyo;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2024/12/10 16:16
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.util.StringUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ZhaoShangPDFToCSV {

    // 定义日期格式化规则
    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    public static void main(String[] args) {
        for (String month : Arrays.asList("1", "2", "3")) {
            doProcess(month);
        }
    }

    private static void doProcess(String month) {
        String dateName = "招商银行交易流水";
        String pdfFilePath = "/Users/hejinyo/Downloads/招商储蓄卡/" + dateName + month + ".pdf"; // PDF 文件路径
        String csvFilePath = "/Users/hejinyo/Downloads/招商储蓄卡/" + dateName + month + ".csv"; // 输出 Excel 文件路径

        try {
            // 1. 解析 PDF 文件
            String pdfContent = extractTextFromPDF(pdfFilePath);

            // 2. 解析账单明细
            String[][] parsedData = parseBillDetails(pdfContent);

            // 3. 输出为 CSV 格式
            writeToCSV(parsedData, csvFilePath);

            log.info("账单明细已成功导出为 CSV 文件：" + csvFilePath);
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
            // 匹配以日期开头的交易明细
            String bankName = "招商银行储蓄卡9557";
            if (line.matches("^\\d{4}-\\d{2}-\\d{2}.*")) {
                String[] parts = line.split("\\s+");
                // 打印调试信息
                log.info("Line: [" + line + "],length=" + parts.length);
                if (parts.length == 6) {
                    data[rowCount][0] = parts[0];  // 交易日
                    data[rowCount][1] = parts[4] + parts[5];  // 摘要（商户名）
                    data[rowCount][2] = parts[2].replace(",", "").trim();  // 金额
                }
                data[rowCount][3] = bankName;
                rowCount++;
            }
        }
        return data;
    }

    // 将日期转换为完整的日期格式,时间部分为 12:00:00
    public static String convertToFullDate(String inputDate) {
        try {
            // 生成新的日期字符串,格式为：MM/dd/yyyy
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

    // 将数据写入 CSV 文件
    private static void writeToCSV(String[][] data, String csvFilePath) throws IOException {
        try (FileWriter csvWriter = new FileWriter(csvFilePath)) {
            // 写入表头
            csvWriter.append("Date, Amount, Source Currency, Target Currency, Exchange Rate, Budget Book, Account, Folder, Category, Payee, Tags, Notes\n");
            // 写入数据
            Map<String, String> flodMap = getFoldMap();
            for (String[] row : data) {
                if (row[0] != null) {
                    // 格式化交易日期
                    String formattedDate = convertToFullDate(row[0]);
                    String category = buildFolderCategory(row[1]);
                    String flod = flodMap.get(category);
                    BigDecimal amount = new BigDecimal(row[2]);
                    // 为0的数据过滤掉
                    if (amount.compareTo(BigDecimal.ZERO) == 0) {
                        continue;
                    }
                    csvWriter
                            // Date
                            .append(formattedDate).append(", ")
                            // Amount,取负数
                            .append(amount.toPlainString()).append(", ")
                            // Source Currency
                            .append("CNY").append(", ")
                            // Target Currency
                            .append("CNY").append(", ")
                            // Exchange Rate
                            .append("1").append(", ")
                            // Budget Book
                            .append("我的帐单").append(", ")
                            // Account
                            .append(row[3]).append(", ")
                            // Folder
                            .append(flod).append(", ")
                            // Category
                            .append(category).append(", ")
                            // Payee
                            .append("").append(", ")
                            // Tags
                            .append("").append(", ")
                            // Notes
                            .append(row[1]).append("\n");
                }
            }
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

    private static Map<String, String> getCategoryMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("包", "吃饭");
        map.put("食", "吃饭");
        map.put("锅", "吃饭");
        map.put("饿", "吃饭");
        map.put("馆", "吃饭");
        map.put("哥", "吃饭");
        map.put("饭", "吃饭");
        map.put("烫", "吃饭");
        map.put("辣", "吃饭");
        map.put("麻", "吃饭");
        map.put("烤", "吃饭");
        map.put("粉", "吃饭");
        map.put("餐", "吃饭");
        map.put("饼", "吃饭");
        map.put("饮", "吃饭");
        map.put("肥肠", "吃饭");
        map.put("肠", "吃饭");
        map.put("牛", "吃饭");
        map.put("煲", "吃饭");
        map.put("面", "吃饭");
        map.put("味", "吃饭");
        map.put("鸡", "吃饭");
        map.put("鸭", "吃饭");
        map.put("豆腐", "吃饭");
        map.put("炸", "吃饭");
        map.put("肉", "吃饭");
        map.put("鹅", "吃饭");
        map.put("猪", "吃饭");
        map.put("记", "吃饭");
        map.put("菜", "吃饭");
        map.put("公交", "公交");
        map.put("交通", "公交");
        map.put("滴滴", "打车");
        map.put("高德", "打车");
        map.put("百度", "打车");
        map.put("曹操", "打车");
        map.put("地铁", "地铁");
        map.put("天府通", "地铁");
        map.put("中铁网络", "高铁");
        map.put("中国铁路网络", "高铁");
        map.put("衣服", "衣服");
        map.put("服饰", "衣服");
        map.put("携程", "住宿");
        map.put("飞猪", "住宿");
        map.put("旅馆", "住宿");
        map.put("酒店", "住宿");
        map.put("民宿", "住宿");
        map.put("过夜", "住宿");
        map.put("房贷", "房贷");
        map.put("自如", "房租");
        map.put("房租", "房租");
        map.put("物业", "房租");
        map.put("挂号", "挂号");
        map.put("药", "药品");
        map.put("医院", "治疗");
        map.put("医疗", "治疗");
        map.put("诊所", "治疗");
        map.put("诊断", "治疗");
        map.put("保险", "保险");
        map.put("人保", "保险");
        map.put("社保", "社保");
        map.put("税务", "社保");
        map.put("中国人民财产保险股份有限公司", "保险");
        map.put("加油", "加油");
        map.put("汽油", "加油");
        map.put("石化", "加油");
        map.put("中化", "加油");
        map.put("石油", "加油");
        map.put("中油", "加油");
        map.put("别克", "维保");
        map.put("停车", "停车");
        map.put("渝A", "停车");
        map.put("临停", "停车");
        map.put("车库", "停车");
        map.put("红包", "红包");
        map.put("生日", "生日");
        map.put("话费", "话费");
        map.put("移动", "话费");
        map.put("联通", "话费");
        map.put("电信", "话费");
        map.put("小吃", "娱乐");
        map.put("零食", "娱乐");
        map.put("奶茶", "娱乐");
        map.put("辣条", "娱乐");
        map.put("送礼", "送礼");

        map.put("欧尚", "其他");
        map.put("其他", "其他");

        map.put("电子", "电子");
        map.put("电脑", "电子");
        map.put("手机", "电子");
        map.put("保护膜", "电子");
        map.put("钢化膜", "电子");
        map.put("电池", "电子");
        map.put("耳机", "电子");
        map.put("显示器", "电子");
        map.put("键盘", "电子");
        map.put("鼠标", "电子");
        map.put("拼多多", "电子");
        map.put("京东", "电子");
        map.put("Nintendo", "电子");
        map.put("爱回收", "电子");
        map.put("switch", "电子");
        map.put("游戏", "电子");
        map.put("嘉立创", "电子");
        map.put("小米", "电子");
        map.put("iphone", "电子");

        map.put("还款", "还款");

        map.put("朝朝宝", "朝朝宝");
        map.put("个贷交易招商银行股份有限公司", "房贷");
        map.put("成都住房公积金管理中心", "房贷");
        map.put("转账汇款", "转出");
        map.put("退款", "转入");
        map.put("收款", "转入");
        map.put("转入", "转入");

        map.put("支付利息", "利息");
        map.put("结息", "利息");
        map.put("自助消费", "电子");
        map.put("借钱", "转出");
        map.put("手续费", "其他");
        map.put("长服计划分红", "工资");
        map.put("代发工资", "工资");
        map.put("余额宝提现", "提现");
        map.put("网银转款本金", "提现");
        map.put("银联渠道他代本借记卡无卡交易", "提现");
        map.put("快捷支付", "其他");
        map.put("转账", "转入");
        map.put("跨行转出", "提现");
        map.put("财付通", "其他");
        map.put("支付宝", "其他");

        map.put("有限公司", "电子");
        return map;
    }

    public static Map<String, String> getFoldMap() {
        Map<String, String> map = new HashMap<>();
        map.put("公交", "出行");
        map.put("地铁", "出行");
        map.put("打车", "出行");
        map.put("吃饭", "生活");
        map.put("衣服", "生活");
        map.put("理发", "生活");
        map.put("住宿", "生活");
        map.put("社保", "生活");

        map.put("房贷", "房屋");
        map.put("房租", "房屋");

        map.put("挂号", "医疗");
        map.put("药品", "医疗");
        map.put("治疗", "医疗");

        map.put("保险", "汽车");
        map.put("加油", "汽车");
        map.put("维保", "汽车");
        map.put("停车", "汽车");
        map.put("成都启阳", "汽车");

        map.put("红包", "转账");
        map.put("生日", "转账");
        map.put("转出", "转账");

        map.put("话费", "消费");
        map.put("电子", "消费");
        map.put("娱乐", "消费");
        map.put("约饭", "消费");
        map.put("零食", "消费");
        map.put("送礼", "消费");
        map.put("其他", "消费");
        map.put("还款", "消费");

        map.put("利息", "收入");
        map.put("提现", "收入");
        map.put("工资", "收入");
        map.put("转入", "收入");
        map.put("灵活宝", "理财");
        map.put("朝朝宝", "理财");


        return map;
    }
}
