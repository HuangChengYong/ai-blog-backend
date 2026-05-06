package com.aiblog.system.admin.service.impl;

import com.aiblog.content.entity.BlogTag;
import com.aiblog.content.mapper.BlogTagMapper;
import com.aiblog.system.admin.service.AdminStudioService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminStudioServiceImpl implements AdminStudioService {

    private final BlogTagMapper tagMapper;

    public AdminStudioServiceImpl(BlogTagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @Override
    public AdminStudioOptionsResponse studioOptions() {
        List<String> tags = tagMapper.selectList(new LambdaQueryWrapper<BlogTag>().orderByDesc(BlogTag::getArticleCount).orderByAsc(BlogTag::getId))
                .stream().map(BlogTag::getName).toList();

        return new AdminStudioOptionsResponse(
                List.of("工程实践", "产品分析", "教程指南"),
                List.of("短篇", "中篇", "长篇"),
                tags.isEmpty() ? List.of("Agent", "RAG", "AI 写作", "Vue", "评测") : tags
        );
    }
}