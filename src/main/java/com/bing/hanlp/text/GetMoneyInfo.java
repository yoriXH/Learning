/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bing.hanlp.text;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import static com.hankcs.hanlp.corpus.tag.Nature.nf;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.learn.test.tools.Connections;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author ZYH
 */
public class GetMoneyInfo {

    private static int i;

    /**
     * @param args the command line arguments
     */
    public String getinfo(int id, int cateid, String info, String author) throws ClassNotFoundException, SQLException {
        String monneyInfo = "";
        String text = "";
        if (info != null) {
            Document d = Jsoup.parse(info);
            text = d.text();
            Pattern pattern = Pattern.compile("<.+?>", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);
            text = matcher.replaceAll("").replaceAll(" ", "");
            //去除文中的所有空格。
            pattern = Pattern.compile("/\\s+/g");
            matcher = pattern.matcher(text);
            text = matcher.replaceAll("");
            //去除&nbsp;
            text = text.replaceAll("&nbsp;&nbsp;&nbsp;", "");
            text = text.replaceAll(" ", "");
            Pattern p = Pattern.compile("\\s*|t|r|n");
            Matcher m = p.matcher(text);
            text = m.replaceAll("");
            text = text.replaceAll("\\s*", "");
            text = text.trim();
            if (cateid != 1) {
                GetBidMoneyInfo byRegular = new GetBidMoneyInfo(text);
                String monney1 = byRegular.getMoneyInfo(text);
                monneyInfo = monney1;
                if ("".equals(monney1)) {
                    monneyInfo = getMonneyInfo2(info, cateid, author, text);
                }
            } else {
                GetIbidMoneyInfo Ibid = new GetIbidMoneyInfo(text);
                String monney2 = Ibid.getMoneyInfo(text);
                monneyInfo = monney2;
            }
        }
        return monneyInfo;
    }
//分词提取非表格提取方法，格式为：第一名深圳科安达电子科技股份有限公司262498XH11综合防雷第二名上海铁大电信科技股份有限公司183424第三名深圳市恒毅兴实业有限公司362124

    public static String getMonneyInfo(String text, int cateid) {
        NumberFormat nf = NumberFormat.getInstance();
        // 是否以逗号隔开, 默认true以逗号隔开,如[123,456,789.128]
        nf.setGroupingUsed(false);
        // 结果未做任何处理
        String map = "";
        double monneyNum = 1;
        String com;
        String regex_double = "[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*";
        String regex_tel = "\\d{3}-\\d{8}|\\d{4}-\\{7,8}";
        String regex_phone = "0?(13|14|15|18)[0-9]{9}";
        String regex_chinese = "[\\u4e00-\\u9fa5]";
        //String regex_chinese = "[\\u4e00-\\u9fa5]";
        if (text.contains("投诉受理部门")) {
            text = text.substring(0, text.indexOf("投诉受理部门"));
        }
        Segment segment = HanLP.newSegment().enableOrganizationRecognize(true);
        List<Term> termList = segment.seg(text);
        if (!text.contains("保证金")) {//没有保证金的情况
            for (int count = 0; count < termList.size() - 2; count++) {
                if (termList.get(count).nature == Nature.nt) {
                    com = termList.get(count).word;
                    Pattern p = Pattern.compile(regex_double);
                    Pattern p1 = Pattern.compile(regex_phone);
                    Pattern p2 = Pattern.compile(regex_chinese);
                    //判断其中是否所有字为数字，且不为电话号码
                    if (count + 1 < termList.size()) {
                        Matcher m = p.matcher(termList.get(count + 1).word);
                        Matcher m1 = p1.matcher(termList.get(count + 1).word);
                        if (termList.get(count + 1).word.charAt(0) == '0' || p2.matcher(termList.get(count + 1).word).find()) {   //去掉0开头的，前后词为-的电话号码
                            continue;
                        }
                        if (Pattern.matches("(千米)|(平方)|(公顷)|(亩)|(米)|(高)|(平方米)|(立方米)|(万亩)|(m)|(㎡)|(年)|(%)", termList.get(count + 2).word)) {     //后一词不包含单位（eg：123平方米）
                            continue;
                        }
                        if (m.find() && !m1.find()) {
                            String monney = termList.get(count + 1).word;
                            monneyNum = Double.parseDouble(monney);
                            if (monneyNum < 125) {
                                continue;
                            }
                            if (text.contains("万元") && !text.contains("（元）") && monneyNum < 5000000) {
                                map = map + monneyNum + "万元,";
                                i++;
                            } else if (monneyNum > 3000) {
                                map = map + nf.format(monneyNum) + "元,";
                                i++;
                            }
                        }
                    }
                }
            }
        } else if (text.contains("保证金")) {
            for (int count = 0; count < termList.size(); count++) {
                if (termList.get(count).nature == Nature.nt) {
                    com = termList.get(count).word;
                    Pattern p = Pattern.compile(regex_double);
                    Pattern p1 = Pattern.compile(regex_phone);
                    Pattern p2 = Pattern.compile(regex_chinese);
                    //判断其中是否为double
                    if (count + 1 < termList.size()) {
                        Matcher m = p.matcher(termList.get(count + 1).word);
                        Matcher m1 = p1.matcher(termList.get(count + 1).word);

                        if (termList.get(count + 1).word.charAt(0) == '0' || p2.matcher(termList.get(count + 1).word).find()) {   //去掉0开头的，前后词为-的电话号码
                            continue;
                        }
                        if (Pattern.matches("(千米)|(平方)|(公顷)|(亩)|(米)|(高)|(平方米)|(立方米)|(万亩)|(m)|(㎡)", termList.get(count + 2).word)) {     //后一词不包含单位（eg：123平方米）
                            continue;
                        }
                        if (m.find() && !m1.find()) {
                            String monney = termList.get(count + 1).word;
                            if ((monney.length() < monney.indexOf(".") + 5) || (monney.indexOf(".") < 0)) {
                                continue;
                            }
                            monney = monney.substring(0, monney.indexOf(".") + 5);
                            monneyNum = Double.valueOf(monney);
                            if (monneyNum < 125) {
                                continue;
                            }
                            if (text.contains("万元") && !text.contains("（元）")) {
                                map = map + monneyNum + "万元,";
                            } else if (monneyNum > 3000) {
                                map = map + nf.format(monneyNum) + "元,";
                            }
                            i++;
                        }
                    }
                }
            }
        }
        return map;
    }

    //另外一种方法，传入info 分析，对中国交通网及表格提取，包含保证金情况。先进入判断,此时text为未去掉标签
    public static String getMonneyInfo2(String text, int cateid, String author, String info) {
        NumberFormat nf = NumberFormat.getInstance();
        // 是否以逗号隔开, 默认true以逗号隔开,如[123,456,789.128]
        nf.setGroupingUsed(false);
        // 结果未做任何处理
        String map = "";
        double monneyNum = 0;
        String com;
        String text1 = text;
        String regex_double = "[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*";
        String regex_phone = "0?(13|14|15|18)[0-9]{9}";
        //String regex_chinese = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regex_double);
        Pattern p1 = Pattern.compile(regex_phone);
        Segment segment = HanLP.newSegment().enableOrganizationRecognize(true);
        //缩小范围
        if (text.contains("评标价")) {
            text = text.substring(text.indexOf("评标价"));
        } else if (text.contains("候选人")) {
            text = text.substring(text.indexOf("候选人"));
        }
        if (text.contains("异议受理部门")) {
            text = text.substring(0, text.indexOf("异议受理部门"));
        }
        if (text.contains("投诉受理部门")) {
            text = text.substring(0, text.indexOf("投诉受理部门"));
        }
        List<Term> termList = segment.seg(text);
        if ("四川省交通厅".equals(author) || "陕西省建设工程招标投标管理信息网".equals(author) || "四川建设网".equals(author)) {//候选人横向排列
            for (int count = 0; count < termList.size(); count++) {
                if (termList.get(count).nature == Nature.nt) {
                    com = termList.get(count).word;
                    text = text.substring(text.indexOf(com));
                    List<Term> termList1 = segment.seg(text);
                    for (int count1 = 1; count1 < termList1.size()-1; count1++) {
                        if (termList1.get(count1).nature == Nature.nt) {
                            continue;
                        }
                        if (termList1.get(count1).nature == Nature.m && ">".equals(termList1.get(count1 - 1).word) && !"_lbID".equals(termList1.get(count1 - 3).word)) {
                            if (Pattern.matches("(千米)|(平方)|(公顷)|(亩)|(米)|(高)|(平方米)|(立方米)|(万亩)|(m)|(㎡)|(%)|(年)|(-)", termList1.get(count1+1).word)) {     //后一词不包含单位（eg：123平方米）
                                continue;
                            }
                            Matcher m = p.matcher(termList1.get(count1).word);
                            Matcher m1 = p1.matcher(termList1.get(count1).word);
                            if (m.find() && !m1.find()) {
                                if (Double.valueOf(termList1.get(count1).word) < 4) {
                                    continue;
                                } else {
                                    monneyNum = Double.valueOf(termList1.get(count1).word);
                                    if (text1.contains("万元") && monneyNum < 5000000 && !text1.contains("“元”")&& !text1.contains("（元）")) {
                                        map = map + monneyNum + "万元,";
                                    } else if (monneyNum > 3000) {
                                        map = map + nf.format(monneyNum) + "元,";
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } else if ("广州公共资源网".equals(author)) { //候选人纵向排列
            int flag = 0;
            boolean b1 = false;
            for (int count = 0; count < termList.size(); count++) {
                if (termList.get(count).nature == Nature.nt) {
                    com = termList.get(count).word;
                    text = text.substring(text.indexOf(com));
                    List<Term> termList1 = segment.seg(text);
                    for (int count1 = 1; count1 < termList1.size(); count1++) {
                        if (termList1.get(count1).nature == Nature.m && termList1.get(count1 - 1).nature == Nature.nx) {
                            if (b1) {
                                count1 = count1 + flag;
                                b1 = false;
                                continue;
                            }
                            Matcher m = p.matcher(termList1.get(count1).word);
                            if (m.find()) {
                                if (Double.valueOf(termList1.get(count1).word) < 4) {
                                    continue;
                                } else {
                                    monneyNum = Double.valueOf(termList1.get(count1).word);
                                    if (text1.contains("万元") && monneyNum < 5000000) {
                                        map = map + monneyNum + "万元,";
                                        b1 = true;
                                        flag++;
                                    } else if (monneyNum > 3000) {
                                        map = map + nf.format(monneyNum) + "元,";
                                        b1 = true;
                                        flag++;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } else {//不属于特殊网站，用一般分词提取方法提取
            map = getMonneyInfo(info, cateid);
        }
        return map;
    }

    public static void main(String[] args) {
        // TODO code application logic here
        Connections cons = new Connections();
        try {
            Connection con = cons.createCon_local();
            //8160165 8160164 8160165 8561
//            String sql = "SELECT * FROM stang_bid where id = 8796";
//            String sql = "SELECT * FROM stang_bid where info != '' limit 1000,3000";

            String sql = "SELECT * FROM stang_bid where author = '四川建设网'";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = null;
            rs = ps.executeQuery();
            String text = "";
            int total = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                int cateid = rs.getInt("cate_id");
                String author = rs.getNString("author");
                String info = rs.getNString("info");
                GetMoneyInfo key = new GetMoneyInfo();
                try {
                    String monneyinfo = key.getinfo(id, cateid, info, author);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(GetMoneyInfo.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(GetMoneyInfo.class.getName()).log(Level.SEVERE, null, ex);
                }
                total++;
            }
            System.out.println("总数为：" + total);
            System.out.println("提取数为：" + i);
            con.close();
        } catch (Exception e) {
            e.toString();
            System.out.println(e.toString());
        }
    }

}
