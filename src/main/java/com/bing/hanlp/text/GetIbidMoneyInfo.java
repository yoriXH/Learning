/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bing.hanlp.text;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Administrator
 */
public class GetIbidMoneyInfo {

    private Connection conn;
    private String[] NUM = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾", "佰", "仟", "万", "亿", "元", "圆"};
    private String[] num = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "", "", ""};
    List<Term> words = new ArrayList<>();

    public GetIbidMoneyInfo(String content) {
        deleteDate(content);
        deletePhone(content);
        String html = content.replace(",", "").replace("￥", "").replace(" . ", ".").replace("．", ".").replace(" .", ".").replace(" ", "");
        Pattern pattern = Pattern.compile("<.+?>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        html = matcher.replaceAll("").replaceAll(" ", "");
        //去除文中的所有空格。
        pattern = Pattern.compile("/\\s+/g");
        matcher = pattern.matcher(html);
        html = matcher.replaceAll("");
        //去除&nbsp;
        html = html.replaceAll("&nbsp", "");
        html = html.replaceAll(" ", "");
        Segment segment = HanLP.newSegment().enableOrganizationRecognize(true);
        words = segment.seg(content);
    }

    public List<Term> getwords() {
        return words;
    }

    /**
     * 排除电话号码
     *
     * @param str 传入字符串
     * @return 返回排除电话号码之后的字符串
     */
    public String deletePhone(String str) {
        Pattern p = Pattern.compile("1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}");
        Matcher m = p.matcher(str);
        while (m.find()) {
            str = str.replace(m.group(), "");
        }
        return str;
    }

    /**
     * 删除某些特殊情况数据
     *
     * @param str 传入字符串
     * @return 返回删除特殊情况之后的字符串
     */
    public String deleteSome(String str) {
        Pattern p = Pattern.compile("20\\d{2}|\\d{1}、|\\(([\\d{1}])\\)|\\（([\\d{1}])\\）|[亿]+[一-龥]{1,10}+[公司]|[万]+[一-龥]{1,10}+[公司]|0(\\d{1,10})");
        Matcher m = p.matcher(str);
        while (m.find()) {
            String temp = m.group();
            str = str.replace(m.group(), "");
        }
        return str;
    }

    /**
     * 排除日期
     *
     * @param str 传入字符串
     * @return 返回排除日期之后的字符串
     */
    public String deleteDate(String str) {
        Pattern p = Pattern.compile("(\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)");
        Matcher m = p.matcher(str);
        while (m.find()) {
            str = str.replace(m.group(), "");
        }
        return str;
    }

    public String getMoneyInfo(String content) {

        String info = "";
        //用于分词之后的计数
        int index = 0;
        //基本单位，某些表格中金额数据的单位位于该词的前方。例子：中标金额(元)：123456       
        //某些情况下会单独出现不相关的数字，此参数用于辨别第一次单独出现的数字
        String pri_str = "";
        try {
            for (Term word : words) {
                String unit = "元";
                boolean flag = false;
                index++;
                if (index >= words.size() - 1) {
                    break;
                }
                //获取词性
                Nature type = word.nature;
                //获取词内容
                String text = word.word;
                pri_str = text;
                //处理小数词
                String next = words.get(index).word;
                String unit_str = "";
                int first_pass = 0;
                for (int unit_count = 2; unit_count < 8; unit_count++) {
                    try {
                        if ((index - unit_count >= 0) && (index - unit_count < words.size())) {
                            unit_str = words.get(index - unit_count).word;
                        }
                    } catch (Exception e) {
                        System.out.println("截取错误");
                    }
                    String unit_m = "";
                    for (int unit_index = -5; unit_index < 5; unit_index++) {
                        try {
                            if ((index - unit_count + unit_index >= 0) && (index - unit_count + unit_index < words.size())) {
                                unit_m = words.get(index - unit_count + unit_index).word;
                            }
                        } catch (Exception e) {
                            System.out.println("截取错误");
                        }
                        if ((unit_str.contains("标") || unit_str.contains("价") || unit_str.contains("金")) && (unit_m.contains("元") || unit_m.contains("万") || unit_m.contains("亿")) && (!unit_m.contains("单元")) && (!unit_m.contains("万源"))) {
                            unit = unit_m;
                            flag = true;
                        }
                    }
                    if (unit_str.contains("邮编") || unit_str.contains("注册")) {
                        first_pass = 1;
                    }
                }
                if (first_pass == 1) {
                    continue;
                }
                String result = "";
                String second = "";
                try {
                    result = words.get(index - 2).word;
                    second = words.get(index + 1).word;
                } catch (Exception e) {
                }
                if (Pattern.matches("(千米)|(平方)|(公顷)|(亩)|(米)|(高)|(平方米)|(立方米)|(年)|(km)|(万方)", next)) {     //后一词不包含单位（eg：123平方米）
                    continue;
                }
                if (Pattern.matches("(千米)|(平方)|(公顷)|(亩)|(米)|(高)|(平方米)|(立方米)|(年)|(km)|(万方)", second)) {   //后两词不包含单位（eg:123万米）
                    continue;
                }
                boolean flag1 = false;
                for (int i = 1; i < 38; i++) {

                    try {
                        if (Pattern.matches("(保证金)|(信用等级)|(低于)|(提交)|(少于)|(小于)|(累计)|(投标人)|(以上)|(承揽)|(≥)|(均达)|(货币资金)|(批准文号)|(项目编号)|(不少)|(每套)|(编码)|(营业额)|(达万)", words.get(index - i).word)) {   //前7个词应不包含的词
                            flag1 = true;
                            break;
                        }
                    } catch (Exception e) {
                    }
                }
                for (int i = 1; i < 10; i++) {

                    try {
                        if (Pattern.matches("(担保)", words.get(index + i).word)) {   //前7个词应不包含的词
                            flag1 = true;
                            break;
                        }
                    } catch (Exception e) {
                    }
                }
                if (flag1) {
                    flag1 = false;
                    continue;
                }
                if (type == Nature.m) {
                    if (index == 1) {
                        continue;
                    }
                    if (text.matches("[0-9][\\.][0-9]") || text.matches("[0-9]")) {   //去除标题
                        next = words.get(index).word;
                        if ((!next.contains("万")) || (!next.contains("亿"))) {
                            continue;
                        }
                    }
                    if (Pattern.matches("[0-9]{1,11}[\\.]*[0-9]*", result)) { //前一词位包含数字的
                        if (result.charAt(0) == '0' || next.contains("-")) {   //去掉0开头的，前后词为-的电话号码
                            continue;
                        }
                        if (text.contains("元") || text.contains("万") || text.contains("亿")) {
                            String number = result;
                            if (result.contains(".")) {
                                number = result.substring(0, result.indexOf("."));
                            }
                            if ((Long.parseLong(number) < 5000 || number.length() > 11) && ((!text.contains("万")) && (!text.contains("亿")))) {
                                continue;
                            }
                            if (number.length() < 3 && (text.contains("万") || next.contains("万")) && ((!text.contains("元")) && (!next.contains("元")) && (!second.contains("元")))) {
                                continue;
                            } else {
                                info = result + text;
                                pri_str = result;
                                break;
                            }
                        }
                        if (("元".equals(next) || "万元".equals(next) || "亿元".equals(next) || "亿".equals(next) || "万".equals(next))) {
                            info = result + second;
                            pri_str = result;
                            break;
                        }
                        if (flag) {
                            info = result + unit;
                            pri_str = result;
                            break;
                        }
                    }
                    if (Pattern.matches("[0-9]{1,11}[\\.]*[0-9]*", text)) { //如果text为纯数字
                        if (text.charAt(0) == '0' || result.contains("-") || next.contains("-")) {   //去掉0开头的，前后词为-的电话号码
                            continue;
                        }
                        if ((next.contains("元") || next.contains("万") || next.contains("亿")) && (!next.contains("广元")) && (!next.contains("达万"))) {
                            String number = text;
                            if (text.contains(".")) {
                                number = text.substring(0, text.indexOf("."));
                            }
                            if ((Long.parseLong(number) < 5000 || number.length() > 11) && ((!next.contains("万")) && (!next.contains("亿")))) {
                                continue;
                            }
                            if (number.length() < 3 && (next.contains("万") || second.contains("万")) && ((!next.contains("元")) && (!second.contains("元")))) {
                                continue;
                            } else {
                                info = text + next;
                                pri_str = text;
                                break;
                            }
                        }
                        if (second.contains("元") || second.contains("万") || second.contains("亿")) {
                            String number = text;
                            if (text.contains(".")) {
                                number = text.substring(0, text.indexOf("."));
                            }
                            if ((Long.parseLong(number) < 5000 || number.length() > 11) && ((!second.contains("万")) && (!second.contains("亿")))) {
                                continue;
                            }
                            if (number.length() < 3 && (second.contains("万")) && (!second.contains("元"))) {
                                continue;
                            } else {
                                info = text + second;
                                pri_str = text;
                                break;
                            }
                        } else if (flag) {
                            info = text + unit;
                            pri_str = text;
                            break;
                        }
                    }
                }
                //处理数量词 如：123万元
                if (type == Nature.mq && (text.contains("元") || text.contains("万") || text.contains("亿"))) {
                    Pattern p = Pattern.compile("[0-9]+[\\.]*[0-9]*");
                    Matcher m = p.matcher(text);
                    String number = "0";
                    if (m.find()) {
                        number = m.group();
                    }
                    if (text.contains(".") && !"0".equals(number)) {
                        number = text.substring(0, text.indexOf("."));
                    }
                    if ((Long.parseLong(number) < 5000 || number.length() > 11) && (!text.contains("万")) && (!text.contains("亿"))) {
                        continue;
                    }
                    if (number.length() < 3 && text.contains("万") && ((!text.contains("元")) && (!next.contains("元")) && (!second.contains("元")))) {
                        continue;
                    } else {
                        info = text;
                        pri_str = text;
                        break;
                    }
                }
                //处理数词，范围最广
                if (type == Nature.m) {
                    if (index == 1) {
                        continue;
                    }
                    if (text.matches("[0-9][\\.][0-9]") || text.matches("[0-9]")) {   //去除标题
                        next = words.get(index).word;
                        if ((!next.contains("万")) || (!next.contains("亿"))) {
                            continue;
                        }
                    }
                    if (Pattern.matches("[0-9]{1,11}[\\.]*[0-9]*", result)) { //前一词位包含数字的
                        if (result.charAt(0) == '0' || next.contains("-")) {   //去掉0开头的，前后词为-的电话号码
                            continue;
                        }
                        if (text.contains("元") || text.contains("万") || text.contains("亿")) {
                            String number = result;
                            if (result.contains(".")) {
                                number = result.substring(0, result.indexOf("."));
                            }
                            if ((Long.parseLong(number) < 5000 || number.length() > 11) && ((!text.contains("万")) && (!text.contains("亿")))) {
                                continue;
                            }
                            if (number.length() < 3 && (text.contains("万") || next.contains("万")) && ((!text.contains("元")) && (!next.contains("元")) && (!second.contains("元")))) {
                                continue;
                            } else {
                                info = result + text;
                                pri_str = result;
                                break;
                            }
                        }
                        if (("元".equals(next) || "万元".equals(next) || "亿元".equals(next) || "亿".equals(next) || "万".equals(next))) {
                            info = result + second;
                            pri_str = result;
                            break;
                        }
                    }
                    if (Pattern.matches("[0-9]{1,11}[\\.]*[0-9]*", text)) { //如果text为纯数字
                        if (text.charAt(0) == '0' || result.contains("-") || next.contains("-")) {   //去掉0开头的，前后词为-的电话号码
                            continue;
                        }
                        if (next.contains("元") || next.contains("万") || next.contains("亿")) {
                            String number = text;
                            if (text.contains(".")) {
                                number = text.substring(0, text.indexOf("."));
                            }
                            if ((Long.parseLong(number) < 5000 || number.length() > 11) && ((!next.contains("万")) && (!next.contains("亿")))) {
                                continue;
                            }
                            if (number.length() < 3 && (next.contains("万") || second.contains("万")) && ((!next.contains("元")) && (!second.contains("元")))) {
                                continue;
                            } else {
                                info = text + next;
                                pri_str = text;
                                break;
                            }
                        }
                        if (second.contains("元") || second.contains("万") || second.contains("亿")) {
                            String number = text;
                            if (text.contains(".")) {
                                number = text.substring(0, text.indexOf("."));
                            }
                            if ((Long.parseLong(number) < 5000 || number.length() > 11) && ((!second.contains("万")) && (!second.contains("亿")))) {
                                continue;
                            }
                            if (number.length() < 3 && (second.contains("万")) && (!second.contains("元"))) {
                                continue;
                            } else {
                                info = text + second;
                                pri_str = text;
                                break;
                            }
                        } else if (flag) {
                            info = text + unit;
                            pri_str = text;
                            break;
                        }
                    }
                }
                //处理没有单位的
                if (type == Nature.m) {
                    if (index == 1) {
                        continue;
                    }
                    for (int i = 1; i < 5; i++) {
                        try {
                            if (Pattern.matches("(预算)|(预算金额)", words.get(index - i).word) && Pattern.matches("[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*", text)) {   //前7个词应不包含的词
                                flag1 = true;
                                break;
                            }
                        } catch (Exception e) {
                        }
                    }
                    if (flag1) {
                        try {
                            float monney2 = Float.valueOf(text);
                            if (monney2 < 3000) {
                                continue;
                            }
                            info = text + "元";
                            pri_str = text;
                            flag1 = false;
                            break;
                        } catch (Exception e) {
                        }
                    }
                }

            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        if (Pattern.matches(".*公司.*", info)) {   //最终不包含公司
               info = "";
        }
        return info;
    }
}
