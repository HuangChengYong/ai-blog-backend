package com.aiblog.system.admin.service.impl;

import com.aiblog.content.ArticleStatus;
import com.aiblog.content.entity.BlogArticle;
import com.aiblog.content.mapper.BlogArticleMapper;
import com.aiblog.system.admin.service.AdminDashboardService;
import com.aiblog.system.entity.SysMenu;
import com.aiblog.system.mapper.SysMenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final BlogArticleMapper articleMapper;
    private final SysMenuMapper menuMapper;

    public AdminDashboardServiceImpl(BlogArticleMapper articleMapper, SysMenuMapper menuMapper) {
        this.articleMapper = articleMapper;
        this.menuMapper = menuMapper;
    }

    @Override
    public AdminOverviewResponse overview() {
        long total = articleMapper.selectCount(new LambdaQueryWrapper<BlogArticle>());
        long review = articleMapper.selectCount(new LambdaQueryWrapper<BlogArticle>().eq(BlogArticle::getStatus, ArticleStatus.REVIEW));
        long listed = articleMapper.selectCount(new LambdaQueryWrapper<BlogArticle>().eq(BlogArticle::getListingStatus, ArticleStatus.LISTED));
        long ready = articleMapper.selectCount(new LambdaQueryWrapper<BlogArticle>().in(BlogArticle::getStatus, ArticleStatus.READY, ArticleStatus.PUBLISHED));
        long enabledMenus = menuMapper.selectCount(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getVisible, 1).eq(SysMenu::getStatus, 1));

        return new AdminOverviewResponse(
                List.of(
                        new AdminMetricResponse("全部内容", total),
                        new AdminMetricResponse("待审核", review),
                        new AdminMetricResponse("已上架", listed),
                        new AdminMetricResponse("可发布", ready),
                        new AdminMetricResponse("启用菜单", enabledMenus)
                ),
                92,
                "内容池状态良好，建议优先处理待审核文章。"
        );
    }
}