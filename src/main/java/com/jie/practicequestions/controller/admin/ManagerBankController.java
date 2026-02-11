package com.jie.practicequestions.controller.admin;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jie.practicequestions.annotation.AuthCheck;
import com.jie.practicequestions.common.BaseResponse;
import com.jie.practicequestions.common.ResultUtils;
import com.jie.practicequestions.config.CosClientConfig;
import com.jie.practicequestions.cos.CosManager;
import com.jie.practicequestions.domain.dto.question.QuestionQueryRequest;
import com.jie.practicequestions.domain.dto.questionbank.QuestionBankAddRequest;
import com.jie.practicequestions.domain.dto.questionbank.QuestionBankEditRequest;
import com.jie.practicequestions.domain.dto.questionbank.QuestionBankQueryRequest;
import com.jie.practicequestions.domain.model.Question;
import com.jie.practicequestions.domain.model.QuestionBank;
import com.jie.practicequestions.domain.model.QuestionBankQuestion;
import com.jie.practicequestions.domain.model.User;
import com.jie.practicequestions.domain.vo.QuestionBankVO;
import com.jie.practicequestions.domain.vo.QuestionVO;
import com.jie.practicequestions.domain.vo.UserVO;
import com.jie.practicequestions.exception.BusinessException;
import com.jie.practicequestions.exception.ErrorCode;
import com.jie.practicequestions.service.QuestionBankQuestionService;
import com.jie.practicequestions.service.QuestionBankService;
import com.jie.practicequestions.service.QuestionService;
import com.jie.practicequestions.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/bank")
@Slf4j
public class ManagerBankController {


    @Resource
    private QuestionBankService questionBankService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    @Resource
    private CosManager cosManager;

    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 封面上传（统一接口，只负责上传文件，不操作数据库）
     * @param file 上传的文件
     * @return 文件访问 URL
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadQuestionBank(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择文件");
        }

        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名无效");
        }

        // 提取文件扩展名
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        // 生成唯一文件名：UUID + 扩展名
        String uniqueFileName = UUID.randomUUID() + extension;
        // 统一文件路径格式：/bank_cover/{UUID}.ext
        String filepath = "/bank_cover/" + uniqueFileName;

        File tempFile = null;
        try {
            // 创建临时文件
            tempFile = File.createTempFile("upload", extension);
            file.transferTo(tempFile);

            // 上传到 COS
            cosManager.putObject(filepath, tempFile);

            // 返回完整访问 URL
            String fullUrl = cosClientConfig.getHost() + filepath;
            log.info("File upload success, filepath = {}", filepath);

            return ResultUtils.success(fullUrl);
        } catch (Exception e) {
            log.error("file upload error, filepath = {}", filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败: " + e.getMessage());
        } finally {
            // 删除临时文件
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                if (!deleted) {
                    log.error("Temp file delete failed, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 获取题库列表
     */
    @PostMapping("/get/list")
    public BaseResponse<Page<QuestionBankVO>> getQuestionBankList(@RequestBody QuestionBankQueryRequest questionBankQueryRequest) {
        if (BeanUtil.isEmpty(questionBankQueryRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int pageNum = questionBankQueryRequest.getPageNum();
        int pageSize = questionBankQueryRequest.getPageSize();
        LambdaQueryWrapper<QuestionBank> wrapper = questionBankService.getQueryWrapper(questionBankQueryRequest);
        Page<QuestionBank> page = questionBankService.page(new Page<>(pageNum, pageSize), wrapper);
        // 转换成 VO
        List<QuestionBankVO> questionBankVOList = page.getRecords().stream().map(questionBank ->
                        QuestionBankVO.objToVO(questionBank, UserVO.objToVO(userService.getById(questionBank.getUserId()))))
                .toList();
        Page<QuestionBankVO> questionBankVOPage = new Page<>(pageNum, pageSize, page.getTotal());
        questionBankVOPage.setRecords(questionBankVOList);
        return ResultUtils.success(questionBankVOPage);
    }


    /**
     * 获取题库
     */
    @GetMapping("/get/id")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<QuestionBankVO> getQuestionBankById(Long id, HttpServletRequest request) {
        QuestionBank questionBank = questionBankService.getById(id);
        Long userId = questionBank.getUserId();
        User user = userService.getById(userId);
        // 设置题目的查询条件
        QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
        questionQueryRequest.setQuestionBankId(id);
        LambdaQueryWrapper<Question> queryWrapper = questionService.getQueryWrapper(questionQueryRequest);
        // 获取题目列表
        List<Question> questionList = questionService.list(queryWrapper);
        // 转换成 VO
        List<QuestionVO> questionVOList = questionList.stream().map(
                        question -> questionService.getQuestionVO(question, request))
                .toList();
        QuestionBankVO questionBankVO = QuestionBankVO.objToVO(questionBank, UserVO.objToVO(user));
        questionBankVO.setQuestionVOList(questionVOList);
        return ResultUtils.success(questionBankVO);
    }

    /**
     * 创建题库
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addQuestionBank(@RequestBody QuestionBankAddRequest questionBankAddRequest, HttpServletRequest request) {
        if (BeanUtil.isEmpty(questionBankAddRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = questionBankAddRequest.getTitle();
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题库标题不能为空");
        }
        // 检查题库名称是否已存在
        LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionBank::getTitle, title.trim());
        QuestionBank existingBank = questionBankService.getOne(wrapper);
        if (existingBank != null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题库名称已存在");
        }
        // 创建题库
        QuestionBank questionBank = new QuestionBank();
        questionBank.setTitle(title.trim());
        questionBank.setDescription(questionBankAddRequest.getDescription());
        questionBank.setCoverUrl(questionBankAddRequest.getCoverUrl());

        // 获取当前登录用户
        Long userId = userService.getLoginUser(request).getId();
        questionBank.setUserId(userId);
        boolean result = questionBankService.save(questionBank);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建题库失败");
        }
        return ResultUtils.success(questionBank.getId());
    }

    /**
     * 修改题库
     */
    @PostMapping("/edit")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> editQuestionBank(@RequestBody QuestionBankEditRequest questionBankEditRequest) {
        if (BeanUtil.isEmpty(questionBankEditRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = questionBankEditRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题库 id 不能为空");
        }
        // 检查题库是否存在
        QuestionBank existingBank = questionBankService.getById(id);
        if (existingBank == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题库不存在");
        }
        // 检查名称是否重复（如果修改了名称）
        String newTitle = questionBankEditRequest.getTitle();
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            if (!newTitle.trim().equals(existingBank.getTitle())) {
                LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(QuestionBank::getTitle, newTitle.trim());
                wrapper.ne(QuestionBank::getId, id);
                QuestionBank duplicateBank = questionBankService.getOne(wrapper);
                if (duplicateBank != null) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "题库名称已存在");
                }
            }
            existingBank.setTitle(newTitle.trim());
        }
        // 更新其他字段
        if (questionBankEditRequest.getDescription() != null) {
            existingBank.setDescription(questionBankEditRequest.getDescription());
        }
        if (questionBankEditRequest.getCoverUrl() != null) {
            existingBank.setCoverUrl(questionBankEditRequest.getCoverUrl());
        }
        boolean result = questionBankService.updateById(existingBank);
        return ResultUtils.success(result);
    }

    /**
     * 删除题库
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteQuestionBank(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 检查题库是否存在
        QuestionBank questionBank = questionBankService.getById(id);
        if (questionBank == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 检查题库中是否存在题目
        LambdaQueryWrapper<QuestionBankQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionBankQuestion::getQuestionBankId, id);
        QuestionBankQuestion bankQuestion = questionBankQuestionService.getOne(wrapper);
        if (!BeanUtil.isEmpty(bankQuestion)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题库中存在题目，无法删除");
        }
        // 删除题库
        boolean result = questionBankService.removeById(id);
        return ResultUtils.success(result);
    }

}
