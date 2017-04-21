package com.zx;

import org.junit.Test;

/**
 * Created by 97038 on 2017-04-11.
 */
public class MainTest {

    @Test
    public void test(){
        //截取出链接中的code
        String novelUrl = "http://www.23us.com/html/0/328/";
        novelUrl = novelUrl.substring(0,novelUrl.length()-1);
        String code = novelUrl.substring(novelUrl.lastIndexOf("/")+1, novelUrl.length());
        System.out.println(code);
    }
}
