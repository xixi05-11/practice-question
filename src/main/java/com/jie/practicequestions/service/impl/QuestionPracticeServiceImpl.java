package com.jie.practicequestions.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jie.practicequestions.domain.dto.questionpractice.QuestionPracticeQueryRequest;
import com.jie.practicequestions.domain.dto.questionpractice.QuestionPracticeSubmitRequest;
import com.jie.practicequestions.domain.enums.QuestionPracticeTypeEnum;
import com.jie.practicequestions.domain.model.QuestionPractice;
import com.jie.practicequestions.domain.vo.QuestionPracticeCheckResultVO;
import com.jie.practicequestions.domain.vo.QuestionPracticeVO;
import com.jie.practicequestions.exception.BusinessException;
import com.jie.practicequestions.exception.ErrorCode;
import com.jie.practicequestions.mapper.QuestionPracticeMapper;
import com.jie.practicequestions.service.QuestionPracticeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * @author 杰
 * @description 针对表【question_practice(练习题目表)】的数据库操作Service实现
 * @createDate 2026-01-31 22:23:16
 */
@Service
public class QuestionPracticeServiceImpl extends ServiceImpl<QuestionPracticeMapper, QuestionPractice>
        implements QuestionPracticeService {

    /**
     * 获取练习题封装
     *
     * @param questionPractice 练习题实体
     * @param request          HTTP请求
     * @return 练习题VO
     */
    @Override
    public QuestionPracticeVO getQuestionPracticeVO(QuestionPractice questionPractice, HttpServletRequest request) {
        if (questionPractice == null) {
            return null;
        }
        QuestionPracticeVO questionPracticeVO = new QuestionPracticeVO();
        BeanUtil.copyProperties(questionPractice, questionPracticeVO);
        return questionPracticeVO;
    }

    /**
     * 获取练习题列表查询条件
     *
     * @param questionPracticeQueryRequest 查询请求
     * @return 查询条件
     */
    @Override
    public LambdaQueryWrapper<QuestionPractice> getQueryWrapper(QuestionPracticeQueryRequest questionPracticeQueryRequest) {
        Long id = questionPracticeQueryRequest.getId();
        Long questionId = questionPracticeQueryRequest.getQuestionId();
        Integer type = questionPracticeQueryRequest.getType();
        String content = questionPracticeQueryRequest.getContent();
        LocalDateTime startCreateTime = questionPracticeQueryRequest.getStartCreateTime();
        LocalDateTime endCreateTime = questionPracticeQueryRequest.getEndCreateTime();
        String sortField = questionPracticeQueryRequest.getSortField();
        String sortOrder = questionPracticeQueryRequest.getSortOrder();

        LambdaQueryWrapper<QuestionPractice> queryWrapper = new LambdaQueryWrapper<>();

        // id 查询
        queryWrapper.eq(id != null, QuestionPractice::getId, id);

        // 问答题 id 查询
        queryWrapper.eq(questionId != null, QuestionPractice::getQuestionId, questionId);

        // 题型查询
        queryWrapper.eq(type != null, QuestionPractice::getType, type);

        // 题干模糊查询
        queryWrapper.like(StrUtil.isNotBlank(content), QuestionPractice::getContent, content);

        // 创建时间范围查询
        queryWrapper.ge(startCreateTime != null, QuestionPractice::getCreateTime, startCreateTime);
        queryWrapper.le(endCreateTime != null, QuestionPractice::getCreateTime, endCreateTime);

        // 排序
        if ("asc".equalsIgnoreCase(sortOrder)) {
            queryWrapper.orderByAsc(QuestionPractice::getSortOrder);
        } else {
            queryWrapper.orderByDesc(QuestionPractice::getSortOrder);
        }

        return queryWrapper;
    }

    /**
     * 校验练习题答案
     *
     * @param submitRequest 提交答案请求
     * @return 校验结果
     */
    @Override
    public QuestionPracticeCheckResultVO checkAnswer(QuestionPracticeSubmitRequest submitRequest) {
        // 参数校验
        if (submitRequest == null || submitRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "练习题id不能为空");
        }
        if (StrUtil.isBlank(submitRequest.getUserAnswer())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户答案不能为空");
        }

        // 查询练习题
        QuestionPractice questionPractice = this.getById(submitRequest.getId());
        if (questionPractice == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "练习题不存在");
        }
        if (StrUtil.isBlank(questionPractice.getAnswer())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目答案配置错误");
        }

        // 获取题型
        QuestionPracticeTypeEnum typeEnum = QuestionPracticeTypeEnum.getEnumByValue(questionPractice.getType());
        if (typeEnum == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题型配置错误");
        }

        // 标准化并比较答案
        boolean isCorrect = checkAnswerCorrect(
                submitRequest.getUserAnswer(),
                questionPractice.getAnswer(),
                typeEnum
        );

        // 构建返回结果
        return QuestionPracticeCheckResultVO.builder()
                .questionPracticeId(questionPractice.getId())
                .isCorrect(isCorrect)
                .correctAnswer(questionPractice.getAnswer())
                .userAnswer(submitRequest.getUserAnswer())
                .explanation(questionPractice.getExplanation())
                .build();
    }

    /**
     * 校验答案是否正确
     *
     * @param userAnswer    用户答案
     * @param correctAnswer 正确答案
     * @param typeEnum      题型枚举
     * @return 是否正确
     */
    private boolean checkAnswerCorrect(String userAnswer, String correctAnswer, QuestionPracticeTypeEnum typeEnum) {
        // 去除空格并转为大写
        String normalizedUserAnswer = userAnswer.trim().toUpperCase();
        String normalizedCorrectAnswer = correctAnswer.trim().toUpperCase();

        return switch (typeEnum) {
            // 单选题：完全匹配
            case SINGLE_CHOICE -> normalizedUserAnswer.equals(normalizedCorrectAnswer);
            // 多选题：排序后比较（支持 "ABC", "A,B,C" 等格式）
            case MULTIPLE_CHOICE -> {
                // 移除分隔符，排序后比较
                String sortedUserAnswer = normalizeMultipleChoiceAnswer(normalizedUserAnswer);
                String sortedCorrectAnswer = normalizeMultipleChoiceAnswer(normalizedCorrectAnswer);
                yield sortedUserAnswer.equals(sortedCorrectAnswer);
            }
            // 判断题：支持多种格式（true/false, 1/0, 是/否, 对/错）
            case TRUE_FALSE -> normalizedUserAnswer.equals(normalizedCorrectAnswer);

        };
    }

    /**
     * 标准化多选题答案（排序并去除分隔符）
     * 例如: "C,B,A" -> "ABC", "CBA" -> "ABC"
     */
    private String normalizeMultipleChoiceAnswer(String answer) {
        // 移除常见分隔符（逗号、空格、分号等）
        String cleaned = answer.replaceAll("[,\\s;]+", "");
        // 排序字符
        return cleaned.chars()
                .sorted()
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }
}
