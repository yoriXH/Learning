/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.learn.test.learning;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author YJ
 */
public class GetBidMoneyInfo {

    List<Term> words = new ArrayList<>();
    Boolean isok = null;
    List list_name = new ArrayList();

    public GetBidMoneyInfo(String content) {
        content = deleteDate(content);
        // content = deletePhone(content);
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

    public String subProject(String str) {
        StringBuffer source = new StringBuffer(str);
        if (source.length() < 10) {
            return "";
        }
        String[] total = {"中标信息", "中标人信息", "成交信息"};
        String[] name_1 = {"中标单位", "中标人:", "中标人：", "中标供应商", "供应商", "成交候选单位", "成交候选人", "成交单位", "中标人名称："};
        String[] name_2 = {"第一名", "第一中标候选人", "第一候选人", "第一", "候选人一"};
        String[] name_3 = {"第二名", "第二中标候选人", "第二候选人", "第二", "候选人二"};
        String[] name_4 = {"第三名", "第三中标候选人", "第三候选人", "第三", "候选人三"};
        StringBuilder subResult = null;
        subResult = new StringBuilder(source.substring(0, source.length() - 1));
        for (String str1 : total) {//减小搜索范围
            int index_total = source.indexOf(str1);
            if (index_total >= 0) {
                subResult = new StringBuilder(source.substring(index_total, source.length() - 1));
                break;
            }
        }   //减小搜索范围
        isok = false;
        for (String str2 : name_1) {
            int index_name_1 = subResult.indexOf(str2);
            if (index_name_1 >= 0) {
                subResult = new StringBuilder(subResult.substring(index_name_1, subResult.length() - 1));
                String company_name = getname(subResult);
                if ((!company_name.equals("")) && (Pattern.matches(".*(公司|所|企业|集团|局|部|院|中心|处|站).*", company_name))) {
                    if (company_name.contains("公司")) {
                        company_name = company_name.substring(0, company_name.lastIndexOf("公司") + 2);
                    }
                    list_name.add(company_name);
                    isok = true;
                    break;
                }
            }
        }
        if (isok == false) {
            for (String str3 : name_2) {
                while (subResult.indexOf(str3) > 0) {
                    int index_name_2 = subResult.indexOf(str3);
                    if (index_name_2 >= 0) {
                        subResult = new StringBuilder(subResult.substring(index_name_2, subResult.length() - 1));
                        String company_name = getname(subResult);
                        if ((!company_name.equals("")) && (Pattern.matches(".*(公司|所|企业|集团|局|部|院|中心|处|站).*", company_name))) {
                            Pattern p2 = Pattern.compile(".+?(公司|所|企业|集团|局|部|院|中心|处|站)");
                            Matcher m2 = p2.matcher(company_name);
                            if (m2.find()) {
                                company_name = m2.group();
                            }
                            company_name = cut_more(company_name);
                            list_name.add(company_name);
                            for (String str4 : name_3) {
                                index_name_2 = subResult.indexOf(str4);
                                if (index_name_2 >= 0) {
                                    subResult = new StringBuilder(subResult.substring(index_name_2, subResult.length() - 1));
                                    company_name = getname(subResult);
                                    if ((!company_name.equals("")) && (Pattern.matches(".*(公司|所|企业|集团|局|工程|部|院|中心|处|站).*", company_name))) {
                                        p2 = Pattern.compile(".+?(公司|所|企业|集团|局|部|院|中心|处|站)");
                                        m2 = p2.matcher(company_name);
                                        if (m2.find()) {
                                            company_name = m2.group();
                                        }
                                        company_name = cut_more(company_name);
                                        list_name.add(company_name);
                                        for (String str5 : name_4) {
                                            index_name_2 = subResult.indexOf(str5);
                                            if (index_name_2 >= 0) {
                                                subResult = new StringBuilder(subResult.substring(index_name_2, subResult.length() - 1));
                                                company_name = getname(subResult);
                                                company_name = cut_more(company_name);
                                                if ((!company_name.equals("")) && (Pattern.matches(".*(公司|所|企业|集团|局|工程|部|院|中心|处|站).*", company_name))) {
                                                    p2 = Pattern.compile(".+?(公司|所|企业|集团|局|部|院|中心|处|站)");
                                                    m2 = p2.matcher(company_name);
                                                    if (m2.find()) {
                                                        company_name = m2.group();
                                                    }
                                                    list_name.add(company_name);
                                                    break;
                                                }
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (list_name.isEmpty()) {
            String[] find_str = {"中标人", "候选人", "供应商", "第一", "第二", "第三"};
            for (String find : find_str) {
                String result_str = source.toString();
                if (result_str.contains(find)) {
                    int cut_1 = result_str.indexOf(find);
                    if (cut_1 + 50 < result_str.length()) {
                        String str_50 = result_str.substring(cut_1, cut_1 + 50);
                        result_str = result_str.substring(cut_1);
                        if (str_50.contains("公司")) {
                            String company_name = getname_50(str_50);
                            if (!company_name.equals("")) {
                                company_name = cut_more(company_name);
                                if (!list_name.contains(company_name)) {
                                    list_name.add(company_name);
                                }
                            }
                        }
                    }
                }
            }
        }
        String res_name = "";
        Iterator it = list_name.iterator();
        while (it.hasNext()) {
            String name_str = it.next().toString();
            if (!res_name.contains(name_str)) {
                res_name = res_name + name_str + ",";
            }
        }
        if (!res_name.equals("")) {
            res_name = res_name.substring(0, res_name.length() - 1);
        }
        return res_name;
    }

    private String cut_more(String company_name) {
        if (company_name.contains("第")) {
            String cut_str = company_name.substring(company_name.lastIndexOf("第"));
            if (!Pattern.matches(".*(公司|所|企业|集团|局|工程|部|院|中心|处|站).*", cut_str)) {
                company_name = company_name.substring(0, company_name.lastIndexOf("第"));
            }
        }
        if (company_name.contains("公司")) {
            company_name = company_name.substring(0, company_name.indexOf("公司") + 2);
        }
        return company_name;
    }

    private String getname(StringBuilder subResult) {
        int name_stat = 0;
        int name_end = name_stat;
        Boolean stat_ok = false;
        for (name_end = name_stat; name_end < subResult.length() - 2; name_end++) {
            if (!stat_ok) {
                String stat = subResult.substring(name_end, name_end + 1);
                String next = subResult.substring(name_end + 1, name_end + 2);
                if (stat.matches("\\s*|\\t|\\r|\\n||\\:||：") && (!next.matches("\\s*|\\t|\\r|\\n||\\:||："))) {
                    name_stat = name_end + 1;
                    stat_ok = true;
                }
            } else {
                String end = subResult.substring(name_end, name_end + 1);
                if (end.matches("\\s*|,|，|\\t|\\r|\\n||\\:||：") || (end.matches("成") && subResult.substring(name_end + 1, name_end + 2).matches("交"))) {
                    break;
                }
            }
        }
        String result = "";
        if (name_stat >= 0 && name_end > name_stat) {
            result = subResult.substring(name_stat, name_end);
            if (result.contains("第")) {
                int cut = result.lastIndexOf("第");
                int a = result.length() - 1 - cut;
                if (a < 3) {
                    result = result.substring(0, cut);
                }
            }
        }
        return result;
    }

    private String getname_50(String str_50) {
        String company_name = "";
        int cut_start = str_50.indexOf("公司");
        int cut_end = cut_start + 2;
        for (int a = cut_start; a > 0; a--) {
            String stat = str_50.substring(a - 1, a);
            if (stat.matches("\\s*|\\t|\\r|\\n||\\:||：")) {
                cut_start = a;
                break;
            }
        }
        if (cut_end - cut_start > 3) {
            company_name = str_50.substring(cut_start, cut_end);
        }
        return company_name;
    }

    public String deleteDate(String str) {
        Pattern p = Pattern.compile("(\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)");
        Matcher m = p.matcher(str);
        while (m.find()) {
            str = str.replace(m.group(), "");
        }
        return str;
    }

    public String deletePhone(String str) {
        Pattern p = Pattern.compile("1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}");
        Matcher m = p.matcher(str);
        while (m.find()) {
            str = str.replace(m.group(), "");
        }
        return str;
    }

    private String getmoney(String content, String key) {
        int a = content.indexOf(key);
        String money = "";
        String danwei = "";
        if (a + 50 < content.length() - 1) {
            String cut_money = content.substring(a, a + 25);
            Pattern p2 = Pattern.compile("([0-9]+,[0-9]{0,},?[0-9]{0,},?[0-9]{0,}\\.[0-9]+)|([0-9]+,[0-9]{0,},?[0-9]{0,},?[0-9]{0,})|([0-9]+\\.[0-9]+)|(\\d{2,})");
            Matcher m2 = p2.matcher(cut_money);
            if (m2.find()) {
                money = m2.group();
                int index_cut = cut_money.indexOf(money) + money.length();
                String index_str = "";
                if (index_cut + 3 < cut_money.length()) {
                    index_str = cut_money.substring(index_cut, index_cut + 3);
                } else {
                    return "";
                }
                if (cut_money.contains("(元)") || cut_money.contains("（元）")) {
                    danwei = "元";
                } else if (cut_money.contains("(万)") || cut_money.contains("（万）")
                        || cut_money.contains("(万元)") || cut_money.contains("（万元）")) {
                    danwei = "万";
                } else {
                    char[] stringArr = index_str.toCharArray();
                    for (char ch : stringArr) {
                        if (ch == '%' || ch == '分') {
                            money = "";
                        }
                        char[] danwei_s = {'万', '￥', '元'};
                        for (char danwei_each : danwei_s) {
                            if (danwei_each == ch) {
                                danwei = danwei_each + "";
                                break;
                            }
                        }
                        if (!danwei.equals("")) {
                            break;
                        }
                    }
                }
                if (!danwei.equals("")) {
                    if (danwei.contains("￥")) {
                        danwei = danwei.replace("￥", "元");
                    }
                    if (danwei.equals("元")) {
                        String num = money;
                        if (money.contains(".")) {
                            int index_d = money.indexOf(".");
                            num = money.substring(0, index_d);
                        }
                        if (num.length() < 5) {
                            return "";
                        }
                    }
                    money = money + danwei;
                }
            }
        }
        return money;
    }

    public String getMoneyInfo(String content) {
        List<String> list_money = new ArrayList<>();
        String[] key_moneys = {"中标金额", "中标价", "中标价格", "成交金额", "总报价", "投标金额", "总中标金额", "投标报价", "最终报价"};
        String money = "";
        String danwei = "";
        for (String key : key_moneys) {
            if (content.contains(key)) {
                money = getmoney(content, key);
                money = money.replaceAll(",", "");
                if (money.matches("0.*")) {
                    break;
                }
                if (!money.equals("")) {
                    if (money.matches("([0-9]+\\.[0-9]+)|(\\d{2,})|([0-9]+,[0-9]{0,},?[0-9]{0,},?[0-9]{0,}\\.[0-9]+)|[0-9]+,[0-9]{0,},?[0-9]{0,},?[0-9]{0,}")) {
                        String[] danwei_s = {"(万元)", "￥", "(万)", "(元)"};
                        for (String danwei_each : danwei_s) {
                            if (content.contains(danwei_each)) {
                                danwei = danwei_each;
                                break;
                            }
                        }
                        if (!danwei.equals("")) {
                            if (danwei.contains("￥")) {
                                danwei = danwei.replace("￥", "元");
                            }
                        } else {
                            String m_int = money;
                            if (money.contains(".")) {
                                m_int = money.substring(0, money.indexOf("."));
                            }
                            if (m_int.length() >= 5) {
                                danwei = "元";
                            } else {
                                danwei = "万";
                            }
                        }
                        money = money + danwei;
                    }
                    list_money.add(money);
                    break;
                }
            }
        }
        if (list_money.isEmpty()) {
            String info = "";
            int index = 0;
            //基本单位，某些表格中金额数据的单位位于该词的前方。例子：中标金额(元)：123456       
            //某些情况下会单独出现不相关的数字，此参数用于辨别第一次单独出现的数字
            String pri_str = "";
            try {
                String unit = "";
                for (Term word : words) {
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
                    if (!flag) {
                        for (int unit_count = 2; unit_count < 10; unit_count++) {
                            if (flag) {
                                break;
                            }
                            try {
                                if ((index - unit_count >= 0) && (index - unit_count < words.size())) {
                                    unit_str = words.get(index - unit_count).word;
                                }
                            } catch (Exception e) {
                                System.out.println("截取错误");
                            }
                            String unit_m = "";
                            for (int unit_index = -10; unit_index < 5; unit_index++) {
                                try {
                                    if ((index - unit_count + unit_index >= 0) && (index - unit_count + unit_index < words.size())) {
                                        unit_m = words.get(index - unit_count + unit_index).word;
                                    }
                                } catch (Exception e) {
                                    System.out.println("截取错误");
                                }
                                if ((unit_str.contains("标") || unit_str.contains("价") || unit_str.contains("金")) && (!unit_str.contains("售价")) && (unit_m.contains("亿") || unit_m.contains("万") || unit_m.contains("元"))) {
                                    unit = unit_m;
                                    flag = true;
                                    break;
                                }
                            }
                        }
                    }

                    String result = "";
                    String second = "";
                    try {
                        result = words.get(index - 2).word;
                        second = words.get(index + 1).word;
                    } catch (Exception e) {
                    }
                    if (Pattern.matches("(千米)|(平方)|(公顷)|(亩)|(米)|(高)|(平方米)|(立方米)|(万亩)|(m)|(㎡)|(%)", result)) {     //前一词不包含单位（eg：123平方米）
                        continue;
                    }
                    if (Pattern.matches("(千米)|(平方)|(公顷)|(亩)|(米)|(高)|(平方米)|(立方米)|(万亩)|(m)|(㎡)|(%)", next)) {     //后一词不包含单位（eg：123平方米）
                        continue;
                    }
                    if (Pattern.matches("(千米)|(平方)|(公顷)|(亩)|(米)|(高)|(平方米)|(立方米)|(万亩)|(m)|(㎡)|(%)", second)) {   //后两词不包含单位（eg:123万米）
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
                            if (text.equals("元") || text.equals("万") || text.equals("万元")) {
                                info = result + text;
                                pri_str = result;
                                list_money.add(info);
                            }
//                            if (flag) {
//                                if (result.contains(".")) {
//                                    if (unit.equals("元")) {
//                                        String num = result.substring(0, result.indexOf("."));
//                                        if (num.length() < 5) {
//                                            continue;
//                                        }
//                                    }
//                                    info = result + unit;
//                                    pri_str = result;
//                                    list_money.add(info);
//                                    continue;
//                                } else if (!result.contains(".")) {
//                                    if (unit.contains("万") || unit.contains("亿")) {
//                                        info = result + unit;
//                                        pri_str = result;
//                                        list_money.add(info);
//                                        continue;
//                                    } else if (result.length() >= 5) {
//                                        info = result + unit;
//                                        pri_str = result;
//                                        list_money.add(info);
//                                        continue;
//                                    }
//                                }
//                            }
                        }
                        if (Pattern.matches("[0-9]{1,11}[\\.]*[0-9]*", text)) { //如果text为纯数字
                            if (text.charAt(0) == '0' || result.contains("-") || next.contains("-")) {   //去掉0开头的，前后词为-的电话号码
                                continue;
                            }
                            if (next.equals("元") || next.equals("万") || next.equals("万元")) {
                                info = text + next;
                                pri_str = result;
                                list_money.add(info);
                                continue;
                            }
//                            else if (flag) {
//                                if (text.contains(".")) {
//                                    if (unit.equals("元")) {
//                                        String num = text.substring(0, text.indexOf("."));
//                                        if (num.length() < 5) {
//                                            continue;
//                                        }
//                                    }
//                                    info = text + unit;
//                                    pri_str = text;
//                                    list_money.add(info);
//                                    continue;
//                                } else if (text.length() >= 5) {
//                                    info = text + unit;
//                                    pri_str = text;
//                                    list_money.add(info);
//                                    continue;
//                                }
//                            }
                        }
                    }

                    //处理数量词 如：123万元
                    if (type == Nature.mq && (text.contains("元") || text.contains("万"))) {
                        Pattern p = Pattern.compile("[0-9]+[\\.]*[0-9]*");
                        Matcher m = p.matcher(text);
                        String number = "0";
                        if (m.find()) {
                            number = m.group();
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
                                list_money.add(info);
                                continue;
                            }
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
                            if ((text.equals("元") || text.equals("万")) && (!text.contains("亿"))) {
                                info = result + text;
                                pri_str = result;
                                list_money.add(info);
                                continue;
                            } else if (("元".equals(next) || "万元".equals(next) || "亿元".equals(next) || "亿".equals(next) || "万".equals(next))) {
                                info = result + next;
                                pri_str = result;
                                list_money.add(info);
                                continue;
                            }
//                            else if (flag) {
//                                if (result.contains(".")) {
//                                    info = result + unit;
//                                    pri_str = result;
//                                    list_money.add(info);
//                                    continue;
//                                } else if (!result.contains(".")) {
//                                    if (result.length() >= 5) {
//                                        info = result + unit;
//                                        pri_str = result;
//                                        list_money.add(info);
//                                        continue;
//                                    }
//                                }
//                            }
                        }
                        if (Pattern.matches("[0-9]{1,11}[\\.]*[0-9]*", text)) { //如果text为纯数字
                            if (text.charAt(0) == '0' || result.contains("-") || next.contains("-")) {   //去掉0开头的，前后词为-的电话号码
                                continue;
                            }
                            if ((next.equals("元") || next.equals("万")) && (!next.contains("亿"))) {
                                info = text + next;
                                pri_str = text;
                                list_money.add(info);
                                continue;
                            }
//                            else if (flag) {
//                                if (text.contains(".")) {
//                                    info = text + unit;
//                                    pri_str = text;
//                                    list_money.add(info);
//                                    continue;
//                                } else if (!text.contains(".")) {
//                                    if (unit.equals("万") || unit.equals("万元")) {
//                                        info = text + unit;
//                                        pri_str = text;
//                                        list_money.add(info);
//                                        continue;
//                                    } else if (text.length() >= 5) {
//                                        info = text + unit;
//                                        pri_str = text;
//                                        list_money.add(info);
//                                        continue;
//                                    }
//                                }
//                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
            list_money = getMoneyByName(list_money, content);
        }
        String res = "";

        if (!list_money.isEmpty()) {
            for (int i = 0; i < list_money.size(); i++) {
                String index_str = list_money.get(i);
                if (res.contains(index_str)) {
                    continue;
                }
                Pattern p2 = Pattern.compile("([0-9]+\\.[0-9]+)|(\\d+)");
                Matcher m2 = p2.matcher(index_str);
                if (m2.find()) {
                    String number = m2.group();
                    if (content.contains(number)) {
                        int index_num = content.indexOf(number);
                        if (index_num - 10 >= 0) {
                            String before_num = content.substring(index_num - 10, index_num);
                            if (number.matches("0.*") || before_num.contains("最高") || before_num.contains("限制") || before_num.contains("投资")
                                    || before_num.contains("增") || before_num.contains("减") || before_num.contains("预")
                                    || before_num.contains("%") || before_num.contains("注册") || before_num.contains("合同")) {
                                list_money.remove(i);
                                i--;
                                continue;
                            }
                        }
                    }
                }
                if (index_str.matches("(\\d{2,})|([0-9]+\\.[0-9]+)")) {
                    String m_int = index_str;
                    if (index_str.contains(".")) {
                        m_int = index_str.substring(0, index_str.indexOf("."));
                    }
                    if (m_int.length() >= 5) {
                        danwei = "元";
                    } else {
                        danwei = "万";
                    }
                    index_str = index_str + danwei;
                }
                if (index_str.contains("元") && (!index_str.contains("万元"))) {
                    float money1 = Float.valueOf(index_str.substring(0, index_str.indexOf("元")));
                    if (money1 < 3000) {
                        continue;
                    }
                }
                res = res + index_str + ",";
            }
            if (!res.equals("")) {
                res = res.substring(0, res.length() - 1);
            }
        }
        return res;
    }

    private List<String> getMoneyByName(List<String> list_money, String content) {
        try {
            String danwei = "";
            if (content.contains("(万)") || content.contains("(万元)") || content.contains("（万）") || content.contains("（万元）")) {
                danwei = "万";
            }
            if (content.contains("(元)") || content.contains("（元）")) {
                danwei = "元";
            }
            for (int i = 0; i < list_name.size() + 1; i++) {
                String name = "";
                if (i < list_name.size()) {
                    name = list_name.get(i).toString();
                } else {
                    name = "￥";
                }
                while (content.contains(name)) {
                    int index_str = content.indexOf(name) + name.length();
                    if (index_str + 20 < content.length()) {
                        String cut_str = content.substring(index_str, index_str + 20);
                        Pattern p2 = Pattern.compile("([0-9]+\\.[0-9]+)|(\\d{2,})|([0-9]+,[0-9]{0,},?[0-9]{0,},?[0-9]{0,}\\.[0-9]+)|[0-9]+,[0-9]{0,},?[0-9]{0,},?[0-9]{0,}");
                        Matcher m2 = p2.matcher(cut_str);
                        if (m2.find()) {
                            String money = m2.group();
                            if (!money.matches("0.*")) {
                                int index_mon = content.indexOf(money) + money.length();
                                if (index_mon + 5 < content.length() && content.indexOf(money) - 5 >= 0) {
                                    String cut_danwei = content.substring(index_mon, index_mon + 5);
                                    String cut_before = content.substring(content.indexOf(money) - 5, index_mon);
                                    money = money.replaceAll(",", "");
                                    if (cut_danwei.contains("年") || cut_danwei.contains("亩") || cut_danwei.contains("平方")
                                            || cut_danwei.contains("立方") || cut_danwei.contains("高") || cut_danwei.contains("千米")
                                            || cut_danwei.contains("公顷") || cut_danwei.contains("米") || cut_before.contains("电话")
                                            || cut_before.contains("邮编") || cut_before.contains("码") || cut_before.contains("号")
                                            || cut_before.contains("增") || cut_before.contains("减") || cut_before.contains("%")
                                            || cut_danwei.contains("%") || cut_danwei.contains("-") || cut_danwei.contains("m")
                                            || cut_danwei.contains("(㎡)") || cut_danwei.contains("号")) {
                                        content = content.substring(index_str);
                                        continue;
                                    }
                                    if (cut_danwei.contains("万")) {
                                        cut_danwei = "万";
                                    } else if (cut_danwei.contains("元")) {
                                        cut_danwei = "元";
                                    } else {
                                        cut_danwei = "";
                                    }
                                    if (cut_danwei.equals("")) {
                                        String num = money;
                                        if (money.contains(".")) {
                                            int index_d = money.indexOf(".");
                                            num = money.substring(0, index_d);
                                        }
                                        if (num.length() > 10) {
                                            content = content.substring(index_str);
                                            continue;
                                        }
                                        if (danwei.equals("元") || danwei.equals("")) {
                                            if (num.length() < 5) {
                                                content = content.substring(index_str);
                                                continue;
                                            }
                                        }
                                        money = money + danwei;
                                        list_money.add(money);
                                    } else {
                                        money = money + cut_danwei;
                                        list_money.add(money);
                                    }
                                }
                            }
                        }
                    }
                    content = content.substring(index_str);
                }
            }
        } catch (Exception e) {
            System.out.println("全局获取金额出错" + e.toString());
            return list_money;
        }
        return list_money;
    }
}
