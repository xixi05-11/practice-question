package com.jie.practicequestions.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jie.practicequestions.domain.dto.questionbank.QuestionBankQueryRequest;
import com.jie.practicequestions.domain.model.Question;
import com.jie.practicequestions.domain.model.QuestionBank;
import com.jie.practicequestions.mapper.QuestionBankMapper;
import com.jie.practicequestions.service.QuestionBankService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 杰
 * @description 针对表【question_bank(题库表)】的数据库操作Service实现
 * @createDate 2026-01-15 18:12:19
 */
@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank>
        implements QuestionBankService {

    private static final Map<String, SFunction<QuestionBank, ?>> SORT_FIELD_MAP = new HashMap<>();

    static {
        SORT_FIELD_MAP.put("createTime", QuestionBank::getCreateTime);
        SORT_FIELD_MAP.put("editTime", QuestionBank::getEditTime);
    }
    @Override
    public LambdaQueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionBankQueryRequest) {
        Long id = questionBankQueryRequest.getId();
        String title = questionBankQueryRequest.getTitle();
        String description = questionBankQueryRequest.getDescription();
        String coverUrl = questionBankQueryRequest.getCoverUrl();
        Long userId = questionBankQueryRequest.getUserId();
        LocalDateTime startEditTime = questionBankQueryRequest.getStartEditTime();
        LocalDateTime endEditTime = questionBankQueryRequest.getEndEditTime();
        LocalDateTime startCreateTime = questionBankQueryRequest.getStartCreateTime();
        LocalDateTime endCreateTime = questionBankQueryRequest.getEndCreateTime();
        String sortField = questionBankQueryRequest.getSortField();
        String sortOrder = questionBankQueryRequest.getSortOrder();
        String searchText = questionBankQueryRequest.getSearchText();
        LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(id != null, QuestionBank::getId, id)
                .like(StrUtil.isNotBlank(title), QuestionBank::getTitle, title)
                .like(StrUtil.isNotBlank(description), QuestionBank::getDescription, description)
                .eq(StrUtil.isNotBlank(coverUrl), QuestionBank::getCoverUrl, coverUrl)
                .eq(userId != null, QuestionBank::getUserId, userId)
                .ge(startEditTime != null, QuestionBank::getEditTime, startEditTime)
                .le(endEditTime != null, QuestionBank::getEditTime, endEditTime)
                .ge(startCreateTime != null, QuestionBank::getCreateTime, startCreateTime)
                .le(endCreateTime != null, QuestionBank::getCreateTime, endCreateTime);
        if(StrUtil.isNotBlank(searchText)) {
            wrapper.like(QuestionBank::getTitle, searchText)
                    .or()
                    .like(QuestionBank::getDescription, searchText);
        }
        boolean asc = "asc".equalsIgnoreCase(sortOrder);
        SFunction<QuestionBank, ?> sortColumn = SORT_FIELD_MAP.get(sortField);
        if (sortColumn != null) {
            wrapper.orderBy(true, asc, sortColumn);
        } else {
            wrapper.orderByDesc(QuestionBank::getCreateTime);
        }
        return wrapper;
    }
}




