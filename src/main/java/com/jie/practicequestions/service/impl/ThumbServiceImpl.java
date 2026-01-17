package com.jie.practicequestions.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jie.practicequestions.domain.model.Thumb;
import com.jie.practicequestions.service.ThumbService;
import com.jie.practicequestions.mapper.ThumbMapper;
import org.springframework.stereotype.Service;

/**
* @author 杰
* @description 针对表【thumb(点赞记录表)】的数据库操作Service实现
* @createDate 2026-01-15 18:12:19
*/
@Service
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb>
    implements ThumbService{

}




