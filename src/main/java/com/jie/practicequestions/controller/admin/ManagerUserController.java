package com.jie.practicequestions.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jie.practicequestions.annotation.AuthCheck;
import com.jie.practicequestions.common.BaseResponse;
import com.jie.practicequestions.common.DeleteRequest;
import com.jie.practicequestions.common.ResultUtils;
import com.jie.practicequestions.constant.UserConstant;
import com.jie.practicequestions.domain.dto.user.UserQueryRequest;
import com.jie.practicequestions.domain.dto.user.UserUpdateRequest;
import com.jie.practicequestions.domain.model.User;
import com.jie.practicequestions.domain.vo.UserVO;
import com.jie.practicequestions.exception.BusinessException;
import com.jie.practicequestions.exception.ErrorCode;
import com.jie.practicequestions.exception.ThrowUtils;
import com.jie.practicequestions.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class ManagerUserController {

    @Resource
    private UserService userService;


    /**
     * 登录
     *
     * @param text
     * @param password
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> login(String text, String password, HttpServletRequest request) {
        UserVO userVO = userService.login(text, password, request);
        ThrowUtils.throwIf(userVO == null, ErrorCode.NOT_FOUND_ERROR);
        if(!userVO.getRole().equals(UserConstant.ADMIN_ROLE)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无管理员权限");
        }
        return ResultUtils.success(userVO);
    }

    /**
     * 获取登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(UserVO.objToVO(loginUser));
    }

    /**
     * 登出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        return ResultUtils.success(userService.logout(request));
    }


    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }


    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 重置密码
     */
    @PostMapping("/resetPwd")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> resetPassword(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.resetPassword(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = userQueryRequest.getPageNum();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(Page.of(pageNum, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        // 数据脱敏
        Page<UserVO> userVOPage = new Page<>(pageNum, pageSize, userPage.getTotal());

        List<User> userList = userPage.getRecords();
        List<UserVO> userVOList = userList.stream().map(UserVO::objToVO).toList();
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
}
