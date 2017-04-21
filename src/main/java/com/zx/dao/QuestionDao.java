package com.zx.dao;

import com.zx.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by 97038 on 2017-04-18.
 */
public interface QuestionDao extends JpaRepository<Question,Integer> {
}
