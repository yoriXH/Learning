/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bing.hanlp.text;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author X.H.Yang
 */
public class TextMining {

    private static Connection conn;
    public static GetLocation getl = new GetLocation();

//    public TextMining() {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            conn = DriverManager.getConnection("jdbc:mysql://192.168.1.252:3306/suidaobig?zeroDateTimeBehavior=convertToNull&characterEncoding=utf8&allowMultiQueries=true", "root", "123asd123asd");
//            conn.setAutoCommit(false);
//        } catch (ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(TextMining.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    //提取招标公告中的字段
    public static HashMap<String, Object> getbidInfo(String word, String title, String author, String pubtime, int area_id, int id, int cate_id, List<HashMap<String, Object>> trlist) throws FileNotFoundException, IOException, SQLException, ParseException {
        String regex_name = "(招标人|招标单位|建设单位|采购人|采购单位|业主|发布部门|招标单位名称|业主单位|项目单位|项目建设方|采购机构|邀标单位|联系科室)[\\S\\s]{0,10}?";
        String regex_name1 = "(招标代理机构|代理机构|代理单位|名称|招标代理|采购代理机构|采购代理人)[\\S\\s]{0,10}?";
        String regex_name2 = "受[\\S\\s]{3,20}?(委托|托付)";
        String regex_name3 = "解释权归[\\S\\s]{3,10}?所有";
        String regex_namelink = "(联系人|联系方式|组长|经办人|接收人|负责人)[\\S\\s]{0,5}?";
        String regex_tellink = "(联系电话|联系人|联系方式|电话|手机|负责人)[\\S\\s]{0,20}?";
        String regex_tel = "^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}|(1\\d{10}))|((0\\d{2}((-|—){0,1}?)\\d{8})|(0\\d{3}((-|—){0,1}?)\\d{7,8}))";
        String regex_prelength = "(长度|全长|线路|规模|里程|长|面积|占地|工程)[\\S\\s]{0,10}?";
        String regex_length = "(([1-9]\\d*)|0)(\\.\\d+)?";
        String regex_unit = "米|千米|公里|m|km|M|KM";
        String regex_add = "[\\S\\s]{0,5}地[\\S\\s]{0,2}址[\\S\\s]{0,10}?";
        String regex_zizhi = "(具有|具备){0,1}([\\s\\S]{3,10})((颁|核)发的){0,1}([\\s\\S]{3,20})资质";
        String fuhao = "[\\pP\\pS\\pN]";
        String[] filter = {"采购人", "采购代理机构", "代理", "地址", "邮编", "项目联系人", "招标", "联系", "代理机构", "采购单位", "传真", "．", ".", "本级采购人", "投标"};
        Pattern pf = Pattern.compile(fuhao);
        String[] allarea = {"中国", "北京", "安徽", "福建", "甘肃", "广东", "广西", "贵州", "海南", "河北", "河南", "黑龙江", "湖北", "湖南", "吉林", "江苏", "江西", "辽宁", "内蒙古", "宁夏", "青海", "山东", "山西", "陕西", "上海", "四川", "天津", "西藏", "新疆", "云南", "浙江", "重庆", "香港", "台湾", "澳门"};
        Boolean boo = true;
        HashMap<String, Object> bid_result = new HashMap<>();
        while (boo) {
            try {
                int owner_index = 0;
                int link_index = 0;
                int tel_index = 0;
                int add_index = 0;
                int length_index = 0;

                //提取项目金额接口
                GetMoneyInfo key = new GetMoneyInfo();
                String money = key.getinfo(id, cate_id, word, author);  //项目金额

                //提取建设单位
                Document doc = Jsoup.parse(word);
                word = doc.text();
                //去除文中的所有空格。
                Pattern pattern = Pattern.compile("<.+?>", Pattern.DOTALL);
                Matcher matcher = pattern.matcher(word);
                word = matcher.replaceAll("");
                //去除&nbsp;
                word = word.replaceAll("&nbsp;", "");
                Pattern pt = Pattern.compile("\\s*|t|r|n");
                Matcher mt = pt.matcher(word);
                word = mt.replaceAll("");
                word = word.replaceAll("\\s*", "");
                word = word.trim();
                word = word.replace("*", "").replace("(", "").replace(")", "").replace("|", "");
                word = word.replaceAll("\\pZ", "");
                word = word.replace("壹", "一");
                word = word.replace("贰", "二");
                word = word.replace("叁", "三");
                word = word.replace("肆", "四");
                word = word.replace("伍", "五");

                String com = "";  //建设单位
                String link = ""; //联系人
                String tel = "";  //联系方式
                String construct_add = "";  //建设单位地址
                String length = ""; //建设长度
                String zizhi = "";  //资质
                String area = "";   //项目地址
                String type = "";   //项目类型
                String project = "";   //项目行业

                Pattern pr2 = Pattern.compile(regex_name2);
                Matcher mr2 = pr2.matcher(word);

                Pattern pr3 = Pattern.compile(regex_name3);
                Matcher mr3 = pr3.matcher(word);

                Pattern p4 = Pattern.compile(regex_name);

                if (mr2.find() && owner_index == 0) {
                    if (!pf.matcher(mr2.group()).find() && !p4.matcher(mr2.group()).find()) {
                        owner_index = 1;
                        com = mr2.group().replace("受", "").replace("委托", "").replace("托付", "").replace("的", "");
                    }
                } else if (com.equals("") && mr3.find() && owner_index == 0) {
                    if (!pf.matcher(mr3.group()).find()) {
                        owner_index = 1;
                        com = mr3.group().replace("解释权归", "").replace("所有", "");
                    }
                }

                Segment segment = HanLP.newSegment().enableOrganizationRecognize(true);
                List<Term> termList = segment.seg(word);
                HashMap<String, String> map = new HashMap<>();
                for (int count = 0; count < termList.size(); count++) {
                    String nt = "";

                    //////////////////////////匹配词性为nt的分词是否为建设单位//////////////////////////////////
                    if (termList.get(count).nature.startsWith("nt") && owner_index == 0) {
                        nt = "";
                        for (int temp = count - 1; temp >= 0; temp--) {
                            if (termList.get(temp).nature.startsWith("ns") && count - temp < 5) {
                                for (int i = 0; i <= count - temp; i++) {
                                    nt += termList.get(temp + i).word;
                                }
                            }
                            if (count - temp > 5) {
                                break;
                            }
                        }
                        if (nt.equals("")) {
                            nt = termList.get(count).word;
                        }

                        Pattern p1 = Pattern.compile(regex_name + nt);
                        Pattern p2 = Pattern.compile(regex_name1 + nt);
                        Matcher m1 = p1.matcher(word);
                        Matcher m2 = p2.matcher(word);
                        if (!pf.matcher(nt).find()) {
                            if (m1.find()) {
                                owner_index = 1;
                                com = nt.replace("业主为", "").replace("为", "").replace("以", "");
                            } else if (m2.find()) {
                                owner_index = 1;
                                com = nt;
                            }

                        }

                    }
                    ///////////////////////////以词性匹配建设单位结束///////////////////////////////////////////

                    ///////////////////////////匹配项目联系人///////////////////////////////////////////
                    if (termList.get(count).nature.startsWith("nr") && link_index == 0) {
                        Pattern plink = Pattern.compile(regex_namelink + termList.get(count).word);
                        Matcher mlink = plink.matcher(word);
                        if (mlink.find()) {
                            link_index = 1;
                            link = termList.get(count).word;
                            if (link.equals("先生") || link.equals("女士") || link.equals("老师")) {
                                link = termList.get(count - 1).word + termList.get(count).word;
                            } else if (link.length() == 1 && count + 1 < termList.size()) {
                                link = termList.get(count).word + termList.get(count + 1).word;
                            }
                        }
                    }
                    ///////////////////////////以词性匹配项目联系人结束///////////////////////////////////////////

                    /////////////////////////////////匹配项目联系方式//////////////////////////////////////////////
                    if (termList.get(count).nature.startsWith("m") && tel_index == 0) {
                        Pattern plink = Pattern.compile(regex_tellink + termList.get(count).word);
                        Matcher mlink = plink.matcher(word);
                        Pattern ptel = Pattern.compile(regex_tel);
                        if (mlink.find()) {
                            tel = termList.get(count).word;
                            if (count + 2 < termList.size() && (termList.get(count + 2).nature.startsWith("m") | termList.get(count + 2).nature.startsWith("nt") | termList.get(count + 2).nature.startsWith("ns"))) {
                                tel = termList.get(count).word + termList.get(count + 1).word + termList.get(count + 2).word;
                            }
                            Matcher mtel = ptel.matcher(tel);
                            if (mtel.find() && termList.get(count).word.length() < 12) {
                                tel_index = 1;
                                tel = mtel.group();
                            }
                        }
                    }
                    //////////////////////////////////以词性匹配项目联系方式结束/////////////////////////////////////////////

                    ///////////////////////////////////////匹配建设单位地址//////////////////////////////////////////////
                    if ((termList.get(count).nature.startsWith("ns") || termList.get(count).nature.startsWith("nt")) && add_index == 0) {
                        String add = "";
                        int i = 1;
                        while (count + i < termList.size() && !termList.get(count + i).nature.startsWith("w") && i <= 10) {
                            if (termList.get(count + i).nature.startsWith("q")) {
                                i++;
                                break;
                            }
                            i++;
                        }
                        for (int j = 0; j < i; j++) {
                            add += termList.get(count + j).word;
                        }
                        Pattern padd = Pattern.compile(regex_add + add);
                        Matcher madd = padd.matcher(word);
                        if (madd.find()) {
                            add_index = 1;
                            for (String str : filter) {
                                if (add.indexOf(str) > 0) {
                                    add = add.substring(0, add.indexOf(str));
                                    break;
                                }
                            }
                            construct_add = add;
                        }
                    }
                    ////////////////////////////////////////////////////////////////////////////////////////////////////

                    if (termList.get(count).nature.startsWith("gg")) {
                        zizhi = termList.get(count).word;
                        zizhi = zizhi + ";";
                    }
                    //////////////////////////////////////////////////////////////////////////////////////////////////////

                    ///////////////////////////////////////提取建设地址///////////////////////////////////////////////////
                    if (termList.get(count).nature.startsWith("ns")) {
                        getNums(map, termList.get(count).word);
                    }
                    //////////////////////////////////////////////////////////////////////////////////////////////////////

                    ///////////////////////////////////匹配项目建设长度///////////////////////////////////////////////////////////////
                    if (termList.get(count).nature.startsWith("m") && length_index == 0) {
                        Pattern plength = Pattern.compile(regex_length);
                        Pattern prelength = Pattern.compile(regex_prelength + termList.get(count).word);
                        Pattern punit = Pattern.compile(regex_unit);
                        Matcher mm = plength.matcher(termList.get(count).word);
                        Matcher mpre = prelength.matcher(word);
                        if (mm.find() && mpre.find()) {
                            if (count + 1 < termList.size() && (termList.get(count + 1).nature.startsWith("q") || termList.get(count + 1).nature.startsWith("nx"))) {
                                Matcher mu = punit.matcher(termList.get(count + 1).word);
                                if (mu.find()) {
                                    length_index = 1;
                                    length = termList.get(count).word + termList.get(count + 1).word;

                                }
                            } else {
                                String uu = "";
                                for (int index = count - 1; index >= 0; index--) {
                                    if (termList.get(index).nature.startsWith("q") || termList.get(index).nature.startsWith("nx")) {
                                        uu = termList.get(index).word;
                                        break;
                                    }
                                    if (count - index > 5) {
                                        break;
                                    }
                                }
                                Matcher mu = punit.matcher(uu);
                                if (mu.find()) {
                                    length_index = 1;
                                    length = termList.get(count).word + uu;
                                }
                            }
                        }
                    }
                    /////////////////////////////////////////////////////////////////////////////////////////////////////

                }

                area = getMaxNumbers(map);
                HashMap<String, Object> map_address = new HashMap<>();
                if (area.equals("")) {
                    map_address = getl.getRealAreaId(title);
                } else {
                    map_address = getl.getRealAreaId(area);
                }

                if ((int) map_address.get("area_id") != 1 && area_id == 1) {
                    area_id = (int) map_address.get("area_id");
                }

                if (map_address.get("address").toString().equals("")) {
                    area = allarea[area_id - 1] + area;
                } else {
                    area = map_address.get("address").toString();
                }
                double longitude = (double) map_address.get("longitude");
                double latitude = (double) map_address.get("latitude");
                int city_id = (int) map_address.get("city_id");

                if (zizhi.equals("")) {
                    Pattern f = Pattern.compile(regex_zizhi);
                    Matcher g = f.matcher(word);
                    while (g.find()) {
                        String subzizhi = "";
                        String zz = g.group();
                        if (zz.lastIndexOf("的") > -1 && zz.lastIndexOf("级") > zz.lastIndexOf("的")) {
                            subzizhi = zz.substring(zz.lastIndexOf("的") + 1, zz.lastIndexOf("级") + 1);
                            if (zizhi.contains("具有") && zizhi.indexOf("的") < 1) {
                                subzizhi = zizhi.substring(zizhi.indexOf("具有") + 2, zizhi.length());
                            } else if (zizhi.contains("具备") && zizhi.indexOf("的") < 1) {
                                subzizhi = zizhi.substring(zizhi.indexOf("具备") + 2, zizhi.length());
                            } else if (zizhi.contains("①")) {
                                subzizhi = zizhi.substring(zizhi.indexOf("①") + 1, zizhi.length());
                            }
                        } else if (zz.lastIndexOf("具备") > -1 && (zz.lastIndexOf("具备") < zz.lastIndexOf("资质"))) {
                            subzizhi = zz.substring(zz.lastIndexOf("具备") + 2, zz.lastIndexOf("资质") + 2);

                        } else if (zz.lastIndexOf("具有") > -1 && (zz.lastIndexOf("具有") < zz.lastIndexOf("资质"))) {
                            subzizhi = zz.substring(zz.lastIndexOf("具有") + 2, zz.lastIndexOf("资质") + 2);

                        } else if (zz.lastIndexOf("：") > -1 && zz.lastIndexOf("级") > -1 && (zz.lastIndexOf("：") < zz.lastIndexOf("级"))) {
                            subzizhi = zz.substring(zz.lastIndexOf("：") + 1, zz.lastIndexOf("级") + 1);
                        }

                        String num = "\\d";
                        Pattern fnum = Pattern.compile(num);
                        if (!"".equals(subzizhi) && !subzizhi.contains("以下") && !subzizhi.contains("。") && !fnum.matcher(subzizhi).find()) {
                            zizhi = subzizhi + ";";
                        }

                    }

                }

                if (!"".equals(zizhi)) {
                    zizhi = zizhi.substring(0, zizhi.length() - 1);
                }

                if (!"".equals(title)) {
                    type = getType(type, title);
                    project = getProject(trlist, title, project);
                }
                if (type.equals("")) {
                    type = "其他";
                }

                if (project.equals("")) {
                    project = "其他";
                }
                Pattern patterncate = Pattern.compile("(变更|补充公告|补遗|澄清|废标|流标|更正)");
                if (patterncate.matcher(title).find()) {
                    cate_id = 3;
                }

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                String time = df.format(new Date());// new Date()为获取当前系统时间
                if (pubtime.equals("")) {
                    pubtime = time;
                } else {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = simpleDateFormat.parse(pubtime);
                    pubtime = simpleDateFormat.format(date);
                }

                bid_result.put("mysqlId", id);
                bid_result.put("title", title);
                bid_result.put("bidid", id);
                bid_result.put("area_id", area_id);
                bid_result.put("city_id", city_id);
                bid_result.put("cate_id", cate_id);
                bid_result.put("longitude", longitude);
                bid_result.put("latitude", latitude);
                bid_result.put("project", project); //行业
                bid_result.put("pubtime", pubtime);
                bid_result.put("type", type); //类型
                bid_result.put("zizhi", zizhi);  //资质
                bid_result.put("tablename", "stang_bid");
                bid_result.put("construct_address", area); //项目建设地址
                bid_result.put("owner", com); //业主
                bid_result.put("owner_address", construct_add); //业主地址
                bid_result.put("owner_tel", tel); //业主电话
                bid_result.put("linkman", link); //联系人
                bid_result.put("investment", money); //投资金额（万元）
                bid_result.put("length", length); //建设长度
                boo = false;
            } catch (Exception e) {
                System.out.println("BidId：" + id + "发生错误：" + e.toString());
            }
        }
        return bid_result;
    }

    //提取中标公告中的字段
    public static HashMap<String, Object> getzidInfo(String word, String title, String author, String pubtime, int area_id, int id, int cate_id, List<HashMap<String, Object>> trlist) throws FileNotFoundException, IOException, SQLException, ParseException {
        HashMap<String, Object> zid_result = new HashMap<>();
        try {
            String[] allarea = {"中国", "北京", "安徽", "福建", "甘肃", "广东", "广西", "贵州", "海南", "河北", "河南", "黑龙江", "湖北", "湖南", "吉林", "江苏", "江西", "辽宁", "内蒙古", "宁夏", "青海", "山东", "山西", "陕西", "上海", "四川", "天津", "西藏", "新疆", "云南", "浙江", "重庆", "香港", "台湾", "澳门"};
            String regex_name = "(中标单位|中标人|中标供应商|供应商|成交候选单位|成交候选人|成交单位|中标人名称|候选人|第一|第二|第三)[\\S\\s]{0,20}?";

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
            String time = df.format(new Date());// new Date()为获取当前系统时间
            if (pubtime.equals("")) {
                pubtime = time;
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = simpleDateFormat.parse(pubtime);
                pubtime = simpleDateFormat.format(date);
            }
            
            //提取项目金额接口
            GetMoneyInfo key = new GetMoneyInfo();
            String money = key.getinfo(id, cate_id, word, author);  //项目金额

            //提取项目地址
            String area = "";
            String type = "";
            String project = "";

            Document doc = Jsoup.parse(word);
            word = doc.text();
            //去除文中的所有空格。
            Pattern pattern = Pattern.compile("<.+?>", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(word);
            word = matcher.replaceAll("");
            //去除&nbsp;
            word = word.replaceAll("&nbsp;", "");
            Pattern pt = Pattern.compile("\\s*|t|r|n");
            Matcher mt = pt.matcher(word);
            word = mt.replaceAll("");
            word = word.replaceAll("\\s*", "");
            word = word.trim();
            word = word.replace("*", "").replace("(", "").replace(")", "").replace("|", "");
            word = word.replaceAll("\\pZ", "");

            Segment segment = HanLP.newSegment().enableOrganizationRecognize(true);
            List<Term> termList = segment.seg(word);
            HashMap<String, Object> com_map = new HashMap<>();
            HashMap<String, String> map = new HashMap<>();
            String company = "";
            for (int count = 0; count < termList.size(); count++) {
                ///////////////////////////////////////提取建设地址///////////////////////////////////////////////////
                if (termList.get(count).nature.startsWith("ns")) {
                    getNums(map, termList.get(count).word);
                }
                //////////////////////////////////////////////////////////////////////////////////////////////////////

                ///////////////////////////////////////提取中标单位///////////////////////////////////////////////////
                String nt = "";
                if (termList.get(count).nature.startsWith("nt")) {
                    for (int temp = count - 1; temp >= 0; temp--) {
                        if (termList.get(temp).nature.startsWith("ns") && count - temp < 5) {
                            for (int i = 0; i <= count - temp; i++) {
                                nt += termList.get(temp + i).word;
                            }
                        }
                        if (count - temp > 5) {
                            break;
                        }
                    }
                    if (nt.equals("")) {
                        nt = termList.get(count).word;
                    }
                    nt = nt.replace("(", "").replace(")", "");

                    if (nt.contains("公司")) {
                        Pattern p1 = Pattern.compile(regex_name + nt);
                        Matcher m1 = p1.matcher(word);
                        if (m1.find()) {
                            if (map.containsKey(nt)) {
                                int num = (int) com_map.get(nt);
                                com_map.put(nt, num + 1);
                            } else {
                                com_map.put(nt, 1);
                            }
                        }
                    }
                }
            }
            Iterator iter = com_map.keySet().iterator();
            while (iter.hasNext()) {
                String comkey = (String) iter.next();
                company += comkey + ",";
            }
            if (!"".equals(company)) {
                company = company.substring(0, company.length() - 1);
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////

            area = getMaxNumbers(map);
            HashMap<String, Object> map_address = new HashMap<>();
            if (area.equals("")) {
                map_address = getl.getRealAreaId(title);
            } else {
                map_address = getl.getRealAreaId(area);
            }

            if ((int) map_address.get("area_id") != 1 && area_id == 1) {
                area_id = (int) map_address.get("area_id");
            }

            if (map_address.get("address").toString().equals("")) {
                area = allarea[area_id - 1] + area;
            } else {
                area = map_address.get("address").toString();
            }
            double longitude = (double) map_address.get("longitude");
            double latitude = (double) map_address.get("latitude");
            int city_id = (int) map_address.get("city_id");
            if (!"".equals(title)) {
                type = getType(type, title);
                project = getProject(trlist, title, project);
            }
            if (type.equals("")) {
                type = "其他";
            }

            if (project.equals("")) {
                project = "其他";
            }

            zid_result.put("mysqlId", id);
            zid_result.put("title", title);
            zid_result.put("bidid", id);
            zid_result.put("area_id", area_id);
            zid_result.put("city_id", city_id);
            zid_result.put("cate_id", 2);
            zid_result.put("longitude", longitude);
            zid_result.put("latitude", latitude);
            zid_result.put("project", project);
            zid_result.put("pubtime", pubtime);
            zid_result.put("type", type);
            zid_result.put("zizhi", "");
            zid_result.put("tablename", "stang_bid");
            zid_result.put("construct_address", area); //建设地址
            zid_result.put("company_names", company); //所有中标单位  
            zid_result.put("moneys", money); //所有中标金额

        } catch (Exception e) {
            e.toString();
        }
        return zid_result;

    }

    /**
     * 得到hashmap的最大value的key值
     *
     * @param map
     * @param temp
     */
    public static void getNums(HashMap<String, String> map, String temp) {
        if (map.get(temp) == null) {
            map.put(temp, "1");
        } else {
            map.put(temp, String.valueOf(Integer.valueOf(map.get(temp)) + 1));
        }
    }

    /**
     * 得到hashmap的最大value值
     *
     * @param map
     * @return
     */
    public static String getMaxNumbers(HashMap<String, String> map) {
        Iterator iter = map.entrySet().iterator();
        String maxkey = "";
        int count = 0;
        String num = "\\d";
        Pattern fnum = Pattern.compile(num);
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            if (key.contains("中国") || key.contains("中华人民共和国") || key.contains("交易中心") || fnum.matcher(key).find()) {
                continue;
            }
            if (key.length() == 1) {
                continue;
            }
            int val = Integer.valueOf((String) entry.getValue());
            if (val > count) {
                maxkey = key;
                count = val;
            }
        }
        return maxkey;
    }

    public static String getType(String type, String title) {
        Pattern pattern2 = Pattern.compile("(物资|采购|设备|自购|购物|目部|钢材|水泥|混凝土|粉煤灰|钢筋|商品|材料|集采|钢绞线|模板|电缆|砂|型材|碎石|砖|系统集成|锚具|柴油|电线|砼|支座|管片|机电|钢管|配电箱|防水材料|砂浆|砂石|钢板|石料|盾构|集装箱|屏障|供电系统|防护栅|购买)");
        Pattern pattern3 = Pattern.compile("(施工|扩建|改建|土建|新建|改造|建设|临建|代建|总承包)");
        if (title.contains("设计") | title.contains("勘察")) {
            type = "设计";
        } else if (title.contains("监理")) {
            type = "监理";
        } else if ((title.contains("检测") && !title.contains("检测车")) | title.contains("监测")) {
            type = "检测";
        } else if (title.contains("劳务")) {
            type = "劳务";
        } else if (title.contains("咨询") && !title.contains("公司")) {
            type = "咨询";
        } else if (title.contains("整治")) {
            type = "整治";
        } else if (title.contains("养护")) {
            type = "养护";
        } else if (pattern2.matcher(title).find()) {
            type = "物资";
        } else if (pattern3.matcher(title).find()) {
            type = "施工";
        } else {
            type = "其他";
        }
        return type;
    }

    public static String getProject(List<HashMap<String, Object>> list, String info, String project) {
        project = "";
        try {
            out:
            for (HashMap<String, Object> map : list) {
                String keyword1 = (String) map.get("keyword");
                String[] keywords1 = keyword1.split("、");
                for (String keyword2 : keywords1) {
                    if (info.contains(keyword2)) {
                        if ("化工".equals(keyword2) && (info.contains("绿化") | info.contains("一体化") | info.contains("信息化") | info.contains("美化") | info.contains("硬化工") | info.contains("学校") | info.contains("学院") | info.contains("亮化工") | info.contains("研究院") | info.contains("公司") | info.contains("大楼") | info.contains("智能化"))) {
                            break;
                        }

                        if ("石油".equals(keyword2) && (info.contains("学校") | info.contains("学院") | info.contains("中心") | info.contains("公司") | info.contains("大楼") | info.contains("大学") | info.contains("工厂"))) {
                            break;
                        }

                        if ("冶金".equals(keyword2) && (info.contains("校") | info.contains("楼") | info.contains("学院") | info.contains("研究院") | info.contains("中心") | info.contains("公司") | info.contains("大学"))) {
                            break;
                        }

                        if (("通信".equals(keyword2) | "电信".equals(keyword2) | "机电".equals(keyword2)) && (info.contains("楼") | info.contains("公司"))) {
                            break;
                        }
                        String type = (String) map.get("type");
                        if (type.equals("轨道交通工程")) {
                            project = type;
                            project = project + ";";
                            break out;
                        }
                        project += type + ";";
                        break;
                    }
                }
            }
            if (project.length() > 0) {
                project = project.substring(0, project.length() - 1);
            }
            return project;
        } catch (Exception e) {
            System.out.println("出错" + e.toString());
            return "";
        }
    }

    public static void main(String[] args) throws SQLException, FileNotFoundException, IOException {
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://192.168.1.252:3306/suidaobig?zeroDateTimeBehavior=convertToNull&characterEncoding=utf8&allowMultiQueries=true", "root", "123asd123asd");
            conn.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(TextMining.class.getName()).log(Level.SEVERE, null, ex);
        }

        TextMining tm = new TextMining();
        List<HashMap<String, Object>> trlist = new ArrayList<>();
        String sql4 = "SELECT * FROM stang_bid_classfiykey where label = 4 order by id desc";
        PreparedStatement ps4 = conn.prepareStatement(sql4);
        ResultSet rs4 = null;
        rs4 = ps4.executeQuery();
        while (rs4.next()) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("type", rs4.getNString("type"));
            params.put("keyword", rs4.getNString("keyword"));
            trlist.add(params);
        }

        String sql = "SELECT id, area_id, title, info, pubtime, author, cate_id FROM stang_bid WHERE info != '' and author != '中国轨道交通网' and pubtime = '2017-10-11' and cate_id = 2 limit 100";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);

        } catch (SQLException ex) {
            Logger.getLogger(GetLocation.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet rs = pstmt.executeQuery();
        int total = 0;
        while (rs.next()) {
            total++;
            int cate_id = rs.getInt("cate_id");
            int area_id = rs.getInt("area_id");
            int id = rs.getInt("id");
            String pubtime = rs.getNString("pubtime");
            String author = rs.getNString("author");
            String title = rs.getNString("title");
            String info = rs.getNString("info");
            if (cate_id == 1) {
                try {
                    HashMap<String, Object> map = tm.getbidInfo(info, title, author, pubtime, area_id, id, cate_id, trlist);
                    System.out.println(map.toString());
                } catch (ParseException ex) {
                    Logger.getLogger(TextMining.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    HashMap<String, Object> map = tm.getzidInfo(info, title, author, pubtime, area_id, id, cate_id, trlist);
                    System.out.println(map.toString());
                } catch (Exception ex) {
                    Logger.getLogger(TextMining.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("第" + total + "条信息");
        }
        conn.close();

    }

}
