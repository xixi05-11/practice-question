package com.jie.practicequestions.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jie.practicequestions.domain.dto.question.QuestionQueryRequest;
import com.jie.practicequestions.domain.enums.QuestionDiffEnum;
import com.jie.practicequestions.domain.enums.UserQuestionStatusEnum;
import com.jie.practicequestions.domain.model.Question;
import com.jie.practicequestions.domain.model.QuestionBankQuestion;
import com.jie.practicequestions.domain.model.UserQuestionStatus;
import com.jie.practicequestions.domain.vo.QuestionVO;
import com.jie.practicequestions.domain.vo.UserVO;
import com.jie.practicequestions.mapper.QuestionMapper;
import com.jie.practicequestions.service.QuestionBankQuestionService;
import com.jie.practicequestions.service.QuestionService;
import com.jie.practicequestions.service.UserQuestionStatusService;
import com.jie.practicequestions.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 杰
 * @description 针对表【question(题目表)】的数据库操作Service实现
 * @createDate 2026-01-15 18:12:19
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

    @Resource
    private UserService userService;

    @Resource
    private UserQuestionStatusService userQuestionStatusService;

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;


    private static final Map<String, SFunction<Question, ?>> SORT_FIELD_MAP = new HashMap<>();

    static {
        SORT_FIELD_MAP.put("createTime", Question::getCreateTime);
        SORT_FIELD_MAP.put("editTime", Question::getEditTime);
        SORT_FIELD_MAP.put("thumbNum", Question::getThumbNum);
        SORT_FIELD_MAP.put("difficulty", Question::getDifficulty);
    }

    /**
     * 获取题目
     *
     * @param question
     * @param request
     * @return
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        //获取当前登录用户
        Long userId = userService.getLoginUser(request).getId();
        QuestionVO questionVO = new QuestionVO();
        BeanUtil.copyProperties(question, questionVO);
        //补充 question 中不包含的属性
        questionVO.setTagList(JSONUtil.toList(question.getTags(), String.class));
        //获取作者信息
        Long authorId = question.getUserId();
        questionVO.setUserVO(UserVO.objToVO(userService.getById(authorId)));
        //设置用户对该题目的状态
        UserQuestionStatus userQuestionStatus = userQuestionStatusService.lambdaQuery()
                .select(UserQuestionStatus::getStatus)
                .eq(UserQuestionStatus::getUserId, userId)
                .eq(UserQuestionStatus::getQuestionId, question.getId())
                .one();
        questionVO.setStatus(UserQuestionStatusEnum.getEnumByValue(userQuestionStatus.getStatus()).getText());
        return questionVO;
    }

    /**
     * 获取题目列表查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public LambdaQueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tagList = questionQueryRequest.getTagList();
        String difficulty = questionQueryRequest.getDifficulty();
        Long userId = questionQueryRequest.getUserId();
        LocalDateTime startCreateTime = questionQueryRequest.getStartCreateTime();
        LocalDateTime endCreateTime = questionQueryRequest.getEndCreateTime();
        LocalDateTime startEditTime = questionQueryRequest.getStartEditTime();
        LocalDateTime endEditTime = questionQueryRequest.getEndEditTime();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        String searchText = questionQueryRequest.getSearchText();
        Long questionBankId = questionQueryRequest.getQuestionBankId();
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        //题库 id 操作
        if(questionBankId != null){
            LambdaQueryWrapper<QuestionBankQuestion> query = new LambdaQueryWrapper<>();
            query.eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                    .select(QuestionBankQuestion::getQuestionId);
            List<Long> questionIdList = questionBankQuestionService.list(query)
                    .stream()
                    .map(QuestionBankQuestion::getQuestionId)
                    .toList();
            queryWrapper.in(CollUtil.isNotEmpty(questionIdList), Question::getId, questionIdList);
        }
        queryWrapper.eq(id != null, Question::getId, id);
        queryWrapper.like(StrUtil.isNotBlank(title), Question::getTitle, title);
        queryWrapper.like(StrUtil.isNotBlank(content), Question::getContent, content);
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like(Question::getTags, "\"" + tag + "\"");
            }
        }
        if (StrUtil.isNotBlank(difficulty)) {
            QuestionDiffEnum diffEnum = QuestionDiffEnum.getEnumByText(difficulty);
            if (diffEnum != null) {
                queryWrapper.eq(Question::getDifficulty, diffEnum.getValue());
            }
        }
        queryWrapper.eq(userId !=  null, Question::getUserId, userId);
        // 创建时间
        queryWrapper.ge(startCreateTime != null, Question::getCreateTime, startCreateTime);
        queryWrapper.le(endCreateTime != null, Question::getCreateTime,endCreateTime);
        // 编辑时间
        queryWrapper.ge(startEditTime != null, Question::getEditTime, startEditTime);
        queryWrapper.le(endEditTime != null, Question::getEditTime, endEditTime);
        // 搜索词查询
        if (StrUtil.isNotBlank(searchText)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Question::getTitle, searchText)
                    .or()
                    .like(Question::getContent, searchText)
            );
        }

        boolean asc = "asc".equalsIgnoreCase(sortOrder);
        SFunction<Question, ?> sortColumn = SORT_FIELD_MAP.get(sortField);

        if (sortColumn != null) {
            queryWrapper.orderBy(true, asc, sortColumn);
        } else {
            queryWrapper.orderByDesc(Question::getCreateTime);
        }
        return queryWrapper;
    }
}




