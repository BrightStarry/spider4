package com.zx.spider.main;

import com.zx.dao.QuestionDao;
import com.zx.domain.Question;
import com.zx.spider.resolve.HtmlResolver;
import com.zx.spider.util.HttpClientUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知乎爬虫
 */
@Service
public class ZhiHu {
    @Autowired
    private QuestionDao questionDao;

    private static final String USERNAME = "17826824998";
    private static final String PASSWORD = "ZhengXing36";

    private static final String INDEX_URL = "https://www.zhihu.com";
    private static final String IMG_URL = "https://www.zhihu.com/captcha.gif";
    private static final String LOGIN_URL = "https://www.zhihu.com/login/phone_num";
    private static final String LOCAL_IMG_PATH="D://桌面//Desktop//checkImg//";

    private HttpClientUtil httpClientUtil = new HttpClientUtil();

    private List<Question> list = new ArrayList<>();

    public List<Question> getList() {
        return list;
    }

    public static void main(String[] args) throws Exception {
        ZhiHu zhihu = new ZhiHu();
        zhihu.login();
    }

    /**
     * 登录后的 主页
     * 爬取 十条 问题动态
     * #feed-0 div id 规则
     */
    public void loginAfterIndex(){
        String html = httpClientUtil.responseToString(httpClientUtil.sendGetRequest(INDEX_URL));
        if(html == null){
            return;
        }
            //每条动态 DIV
            Element element1 = HtmlResolver.getElement(html, "#feed-0");
            Element element2 = element1.select("div.feed-item-inner > div.feed-main > div.feed-content > h2 > a").first();
            //问题名
            String name = element2.text();
            //问题链接
            String href = INDEX_URL + element2.attr("href");
            //回答内容
            String content = element1.select("div.feed-item-inner > div.feed-main > div.feed-content > " +
                    "div.expandable.entry-body > div.zm-item-rich-text.expandable.js-collapse-body > textarea").first().html();
            Question question = new Question(name,href,content);
            list.add(question);
    }



    /**
     * 登录
     * _xsrf 首页携带的一串字符
     * password 密码
     * phone_num 手机号
     * captcha 验证码
     */
    public  void login() throws Exception {
        String xsrf = getXSRF();
        boolean flag = downloadImg();
        String code = "";
        if(flag)
            code = getCode();
        HttpPost httpPost = new HttpPost(LOGIN_URL);
        httpClientUtil.setDefaultHeaders(httpPost);
        //参数
        List<NameValuePair> param = new ArrayList<>();
        param.add(new BasicNameValuePair("_xsrf",xsrf));
        param.add(new BasicNameValuePair("password",PASSWORD));
        param.add(new BasicNameValuePair("phone_num",USERNAME));
        //验证码是否存在
        if(flag){
            param.add(new BasicNameValuePair("captcha",code));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(param,"UTF-8"));
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        header.put("Referer","https://www.zhihu.com/");
        header.put("X-Xsrftoken",xsrf);
        header.put("Origin","https://www.zhihu.com");
        header.put("Host","www.zhihu.com");
        header.put("Accept","*/*");
        for (String key:header.keySet()){
            httpPost.addHeader(key,header.get(key));
        }
        CloseableHttpClient sslHttpClient = httpClientUtil.getSSLHttpClient();
        CloseableHttpResponse response = sslHttpClient.execute(httpPost);
        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(EntityUtils.toString(response.getEntity(),"UTF-8"));
        HttpClientUtil.closeHttpResponse(response);
        loginAfterIndex();
    }






    /**
     * 获取xsrf
     */
    public String getXSRF(){
        CloseableHttpResponse response = httpClientUtil.sendGetRequest(INDEX_URL);
        final String html = httpClientUtil.responseToString(response);
        HttpClientUtil.closeHttpResponse(response);
        Element element = HtmlResolver.getElement(html, "body > input[name=\"_xsrf\"]");
        String xsrf = element.attr("value");
        return xsrf;
    }

    /**
     * 下载验证码到本地
     */
    public boolean downloadImg() throws IOException {
        CloseableHttpResponse response = httpClientUtil.sendGetRequest(INDEX_URL);
        String html = httpClientUtil.responseToString(response);
        HttpClientUtil.closeHttpResponse(response);
        Element element = HtmlResolver.getElement(html, "img.js-refreshCaptcha.captcha");
        if(element == null){
            return false;
        }
        //如果有，则发送请求获取验证码
        String r = String.valueOf(System.currentTimeMillis());
        String type = "login";
        String imgUrl = IMG_URL + "?r=" + r + "&type=" + type;
        CloseableHttpResponse responseImg = httpClientUtil.sendGetRequest(imgUrl);
        String fileName = LOCAL_IMG_PATH + String.valueOf(System.currentTimeMillis()) + ".png";
        return HttpClientUtil.downloadFileToLocal(responseImg, fileName);
    }

    /**
     *获取验证码 字符
     * 1.下载到本地，手工输入
     * 2.验证码识别接口
     */
    private static String getCode() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("请输入验证码：");
        String code = br.readLine().trim();
        return code;
    }

}
