package com.jie.practicequestions.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jie.practicequestions.domain.model.QuestionBankQuestion;
import com.jie.practicequestions.service.QuestionBankQuestionService;
import com.jie.practicequestions.mapper.QuestionBankQuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author 杰
* @description 针对表【question_bank_question(题库题目关联表)】的数据库操作Service实现
* @createDate 2026-01-15 18:12:19
*/
@Service
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion>
    implements QuestionBankQuestionService{

}




