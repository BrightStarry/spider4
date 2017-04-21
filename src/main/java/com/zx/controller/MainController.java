package com.zx.controller;

import com.zx.dao.QuestionDao;
import com.zx.domain.Question;
import com.zx.spider.main.ZhiHu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 97038 on 2017-04-10.
 */
@RestController
public class MainController {
    @Autowired
    private QuestionDao questionDao;


    @PostMapping("/test")
    public void test() throws Exception {
        ZhiHu zhihu = new ZhiHu();
        zhihu.login();
        List<Question> list = zhihu.getList();
        questionDao.save(list);
    }

}
