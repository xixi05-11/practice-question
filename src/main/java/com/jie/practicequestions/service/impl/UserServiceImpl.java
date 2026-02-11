package com.jie.practicequestions.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jie.practicequestions.constant.EmailConstant;
import com.jie.practicequestions.constant.UserConstant;
import com.jie.practicequestions.constant.UserOperationConstant;
import com.jie.practicequestions.domain.dto.user.EmailRequest;
import com.jie.practicequestions.domain.dto.user.UserChangePwdRequest;
import com.jie.practicequestions.domain.dto.user.UserEditRequest;
import com.jie.practicequestions.domain.dto.user.UserQueryRequest;
import com.jie.practicequestions.domain.enums.UserOperationEnum;
import com.jie.practicequestions.domain.model.User;
import com.jie.practicequestions.domain.vo.UserVO;
import com.jie.practicequestions.exception.BusinessException;
import com.jie.practicequestions.exception.ErrorCode;
import com.jie.practicequestions.mapper.UserMapper;
import com.jie.practicequestions.service.UserService;
import com.jie.practicequestions.utils.UserAccountUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author 杰
 * @description 针对表【s_user(用户)】的数据库操作Service实现
 * @createDate 2026-01-08 14:32:10
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private JavaMailSender mailSender;
    @Resource
    private Configuration freemarkerConfig;

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送邮件
     *
     * @param email
     * @param operation
     */
    @Override
    public void sendEmail(String email, String operation) {
        if (!Validator.isEmail(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
        }
        UserOperationEnum userOperationEnum = UserOperationEnum.getEnumByValue(operation);
        if (userOperationEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "操作类型错误");
        }
        // 查询验证码是否已经存在
        // 生成验证码
        SecureRandom random = new SecureRandom();
        String verifyCode = String.valueOf(random.nextInt(9000) + 1000);
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(
                        userOperationEnum.getRedisKey(email),
                        verifyCode,
                        EmailConstant.MIN_EMAIL_CODE_EXPIRE_MINUTES,
                        TimeUnit.MINUTES
                );
        if (!success) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码已存在");
        }
        // 发送邮件
        send(email, userOperationEnum.getText(), verifyCode, EmailConstant.MIN_EMAIL_CODE_EXPIRE_MINUTES);
    }


    /**
     * 注册
     *
     * @param emailRequest
     * @return
     */
    @Override
    public Long register(EmailRequest emailRequest) {
        String email = emailRequest.getEmail();
        String emailCode = emailRequest.getEmailCode();
        String password = emailRequest.getPassword();
        String checkPassword = emailRequest.getCheckPassword();
        if (emailCode.isEmpty() || password.isEmpty() || checkPassword.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入不能为空");
        }
        if (password.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8");
        }
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不一致");
        }

        if (!Validator.isEmail(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
        }
        String redisKey = UserOperationEnum.getEnumByValue(UserOperationConstant.REGISTER).getRedisKey(email);
        String code = stringRedisTemplate.opsForValue().get(redisKey);
        if (code == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码已过期或未发送");
        }

        if (!emailCode.equals(code)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码错误");
        }
        // 生成用户账户
        User user = new User();
        user.setUserAccount(UserAccountUtil.getUserAccount());
        user.setPassword(encryptPassword(password));
        user.setUserName(UserConstant.DEFAULT_NICKNAME);
        user.setEmail(email);
        user.setRole(UserConstant.DEFAULT_ROLE);
        user.setEditTime(LocalDateTime.now());
        user.setCreateTime(LocalDateTime.now());
        boolean save = this.save(user);
        if (!save) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败");
        }
        //删除验证码
        stringRedisTemplate.delete(redisKey);
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param text     用户账户/邮箱
     * @param password 用户密码
     * @param request
     * @return
     */
    @Override
    public UserVO login(String text, String password, HttpServletRequest request) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPassword, encryptPassword(password));
        // 判断是邮箱还是用户账户
        if (Validator.isEmail(text)) {
            queryWrapper.eq(User::getEmail, text);
        } else {
            queryWrapper.eq(User::getUserAccount, text);
        }
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "账户或密码错误");
        }
        // 设置登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return UserVO.objToVO(user);
    }

    /**
     * 邮箱登录
     *
     * @param email
     * @param emailCode
     * @param request
     * @return
     */
    @Override
    public UserVO loginByEmailCode(String email, String emailCode, HttpServletRequest request) {
        if (emailCode.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入验证码");
        }
        if (!Validator.isEmail(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户不存在");
        }
        String redisKey = UserOperationEnum.getEnumByValue(UserOperationConstant.LOGIN).getRedisKey(email);
        String code = stringRedisTemplate.opsForValue().get(redisKey);
        if (code == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码已过期或未发送");
        }

        if (!emailCode.equals(code)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码错误");
        }
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        //删除验证码
        stringRedisTemplate.delete(redisKey);
        return UserVO.objToVO(user);
    }

    /**
     * 登出
     *
     * @param request
     * @return
     */
    @Override
    public boolean logout(HttpServletRequest request) {
        Object o = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (o == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object o = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (o == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        return (User) o;
    }

    /**
     * 修改密码
     *
     * @param userChangePwdRequest
     * @param request
     * @return
     */
    @Override
    public boolean changeUserPwd(UserChangePwdRequest userChangePwdRequest, HttpServletRequest request) {
        Long id = userChangePwdRequest.getId();
        String oldPassword = userChangePwdRequest.getOldPassword();
        String userPassword = userChangePwdRequest.getUserPassword();
        String checkPassword = userChangePwdRequest.getCheckPassword();
        if (oldPassword.isEmpty() || userPassword.isEmpty() || checkPassword.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入不能为空");
        }
        if (oldPassword.length() < 8 || userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8");
        }
        User loginUser = getLoginUser(request);
        if (!Objects.equals(id, loginUser.getId()) &&
                !loginUser.getRole().equals(UserConstant.ADMIN_ROLE)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无权限修改");
        }
        if (!encryptPassword(oldPassword).equals(loginUser.getPassword())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "旧密码错误");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        loginUser.setPassword(encryptPassword(userPassword));
        boolean update = this.updateById(loginUser);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "修改密码失败");
        }
        //移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    /**
     * 修改密码（邮箱验证码）
     *
     * @param emailRequest
     * @param request
     * @return
     */
    @Override
    public boolean changeUserPwdByEmail(EmailRequest emailRequest, HttpServletRequest request) {
        String email = emailRequest.getEmail();
        String emailCode = emailRequest.getEmailCode();
        String password = emailRequest.getPassword();
        String checkPassword = emailRequest.getCheckPassword();
        if (emailCode.isEmpty() || email.isEmpty() || password.isEmpty() || checkPassword.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (password.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8");
        }
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        User user = this.getLoginUser(request);
        if (!user.getEmail().equals(email)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "邮箱错误");
        }
        //验证码获取
        String redisKey = UserOperationEnum.getEnumByValue(UserOperationConstant.RESET_PWD).getRedisKey(email);
        String code = stringRedisTemplate.opsForValue().get(redisKey);
        if (code == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码已过期或未发送");
        }

        if (!emailCode.equals(code)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码错误");
        }
        user.setPassword(encryptPassword(password));
        boolean update = this.updateById(user);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "修改密码失败");
        }
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        //删除验证码
        stringRedisTemplate.delete(redisKey);
        return true;
    }

    /**
     * 修改邮箱
     *
     * @param email
     * @param emailCode
     * @param newEmail
     * @param request
     * @return
     */
    @Override
    public boolean changeUserEmail(String email, String emailCode, String newEmail, HttpServletRequest request) {
        if (emailCode.isEmpty() || email.isEmpty() || newEmail.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = this.getLoginUser(request);
        if (!user.getEmail().equals(email)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "原邮箱错误");
        }
        String redisKey = UserOperationEnum.getEnumByValue(UserOperationConstant.CHANGE_EMAIL).getRedisKey(email);
        String code = stringRedisTemplate.opsForValue().get(redisKey);
        if (code == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码已过期或未发送");
        }
        if (!emailCode.equals(code)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码错误");
        }
        if (this.getOne(new QueryWrapper<User>().eq("email", newEmail)) != null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "邮箱已存在");
        }
        user.setEmail(newEmail);
        boolean update = this.updateById(user);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "修改邮箱失败");
        }
        //刷新登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        //删除验证码
        stringRedisTemplate.delete(redisKey);
        return true;
    }

    /**
     * 修改用户信息
     *
     * @param userEditRequest
     * @param request
     * @return
     */
    @Override
    public boolean editUser(UserEditRequest userEditRequest, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        BeanUtil.copyProperties(userEditRequest, loginUser);
        loginUser.setEditTime(LocalDateTime.now());
        boolean update = this.updateById(loginUser);
        if (!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "修改用户信息失败");
        }
        //刷新登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, loginUser);
        return true;
    }


    /**
     * 发送邮件
     *
     * @param email
     * @param operation
     * @param verifyCode
     * @param validTimeMinutes
     */
    private void send(String email, String operation, String verifyCode, long validTimeMinutes) {

        // 1. 构建邮件对象
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("【验证码】" + operation + "确认");
            // 2. 渲染 HTML 模板
            Map<String, Object> model = new HashMap<>();
            model.put("operation", operation);
            model.put("verifyCode", verifyCode);
            model.put("validTime", validTimeMinutes);
            model.put("sendTime",
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            Template template = freemarkerConfig.getTemplate("email-code.html");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            // 3. 设置邮件内容（HTML）
            helper.setText(html, true);
        } catch (Exception e) {
            log.error("邮件发送失败：{}", email, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "邮件发送失败");

        }

        // 4. 发送采用虚拟线程异步
        Thread.startVirtualThread(() -> {
            try {
                mailSender.send(message);
            } catch (Exception e) {
                log.error("验证码邮件发送失败：{}", email, e);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "邮件发送失败");
            }
            log.info("验证码邮件发送成功：{}", email);
        });

    }

    /**
     * 重置密码
     *
     * @param userId
     * @return
     */
    @Override
    public boolean resetPassword(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        user.setPassword(this.encryptPassword(UserConstant.DEFAULT_PASSWORD));
        return this.updateById(user);
    }


    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String email = userQueryRequest.getEmail();
        LocalDateTime startCreateTime = userQueryRequest.getStartCreateTime();
        LocalDateTime endCreateTime = userQueryRequest.getEndCreateTime();
        LocalDateTime startEditTime = userQueryRequest.getStartEditTime();
        LocalDateTime endEditTime = userQueryRequest.getEndEditTime();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // id 精确查询
        queryWrapper.eq(id != null, "id", id);

        // 用户角色精确查询
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "role", userRole);

        // 邮箱模糊查询
        queryWrapper.like(StringUtils.isNotBlank(email), "email", email);

        // 账号模糊查询
        queryWrapper.like(StringUtils.isNotBlank(userAccount), "user_account", userAccount);

        // 简介模糊查询
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "profile", userProfile);

        // 昵称模糊查询
        queryWrapper.like(StringUtils.isNotBlank(userName), "user_name", userName);

        // 创建时间范围查询
        queryWrapper.ge(startCreateTime != null, "create_time", startCreateTime);
        queryWrapper.le(endCreateTime != null, "create_time", endCreateTime);

        // 编辑时间范围查询
        queryWrapper.ge(startEditTime != null, "edit_time", startEditTime);
        queryWrapper.le(endEditTime != null, "edit_time", endEditTime);

        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);

        return queryWrapper;
    }
    /**
     * 加密密码
     */
    @Override
    public String encryptPassword(String password) {
        String salt = "jie";
        return DigestUtils.md5DigestAsHex((salt + password).getBytes());
    }
}




