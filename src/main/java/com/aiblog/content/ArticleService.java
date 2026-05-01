package com.aiblog.content;

import com.aiblog.common.BusinessException;
import com.aiblog.common.ErrorCode;
import com.aiblog.common.PageResult;
import com.aiblog.content.dto.ArticleRequest;
import com.aiblog.content.dto.ArticleResponse;
import com.aiblog.content.dto.GenerateArticleRequest;
import com.aiblog.content.dto.PublicAuthorResponse;
import com.aiblog.content.dto.PublicTopicResponse;
import com.aiblog.content.entity.BlogArticle;
import com.aiblog.content.entity.BlogArticleContent;
import com.aiblog.content.entity.BlogArticleTag;
import com.aiblog.content.entity.BlogAuthor;
import com.aiblog.content.entity.BlogCategory;
import com.aiblog.content.entity.BlogTag;
import com.aiblog.content.entity.BlogTopic;
import com.aiblog.content.entity.BlogTopicTag;
import com.aiblog.content.mapper.BlogArticleContentMapper;
import com.aiblog.content.mapper.BlogArticleMapper;
import com.aiblog.content.mapper.BlogArticleTagMapper;
import com.aiblog.content.mapper.BlogAuthorMapper;
import com.aiblog.content.mapper.BlogCategoryMapper;
import com.aiblog.content.mapper.BlogTagMapper;
import com.aiblog.content.mapper.BlogTopicMapper;
import com.aiblog.content.mapper.BlogTopicTagMapper;
import com.aiblog.security.SecurityUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleService {

  private final BlogArticleMapper articleMapper;
  private final BlogArticleContentMapper contentMapper;
  private final BlogArticleTagMapper articleTagMapper;
  private final BlogAuthorMapper authorMapper;
  private final BlogCategoryMapper categoryMapper;
  private final BlogTagMapper tagMapper;
  private final BlogTopicMapper topicMapper;
  private final BlogTopicTagMapper topicTagMapper;

  public ArticleService(
      BlogArticleMapper articleMapper,
      BlogArticleContentMapper contentMapper,
      BlogArticleTagMapper articleTagMapper,
      BlogAuthorMapper authorMapper,
      BlogCategoryMapper categoryMapper,
      BlogTagMapper tagMapper,
      BlogTopicMapper topicMapper,
      BlogTopicTagMapper topicTagMapper
  ) {
    this.articleMapper = articleMapper;
    this.contentMapper = contentMapper;
    this.articleTagMapper = articleTagMapper;
    this.authorMapper = authorMapper;
    this.categoryMapper = categoryMapper;
    this.tagMapper = tagMapper;
    this.topicMapper = topicMapper;
    this.topicTagMapper = topicTagMapper;
  }

  public PageResult<ArticleResponse> publicArticles(long page, long size) {
    Page<BlogArticle> result = articleMapper.selectPage(
        Page.of(page, size),
        new LambdaQueryWrapper<BlogArticle>()
            .eq(BlogArticle::getStatus, ArticleStatus.PUBLISHED)
            .eq(BlogArticle::getListingStatus, ArticleStatus.LISTED)
            .orderByDesc(BlogArticle::getPublishedAt)
    );
    return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), toResponses(result.getRecords(), false));
  }

  public ArticleResponse publicArticle(Long id) {
    BlogArticle article = articleMapper.selectOne(
        new LambdaQueryWrapper<BlogArticle>()
            .eq(BlogArticle::getId, id)
            .eq(BlogArticle::getStatus, ArticleStatus.PUBLISHED)
            .eq(BlogArticle::getListingStatus, ArticleStatus.LISTED)
    );
    if (article == null) {
      throw new BusinessException(ErrorCode.NOT_FOUND, "文章不存在或未发布");
    }
    return toResponse(article, true);
  }

  public PageResult<ArticleResponse> adminArticles(long page, long size) {
    Page<BlogArticle> result = articleMapper.selectPage(
        Page.of(page, size),
        new LambdaQueryWrapper<BlogArticle>().orderByDesc(BlogArticle::getUpdatedAt)
    );
    return new PageResult<>(result.getCurrent(), result.getSize(), result.getTotal(), toResponses(result.getRecords(), true));
  }

  @Transactional(rollbackFor = Exception.class)
  public ArticleResponse create(ArticleRequest request) {
    LocalDateTime now = LocalDateTime.now();
    Long operatorId = currentUserId();
    BlogArticle article = new BlogArticle();
    applyArticleRequest(article, request);
    article.setStatus(ArticleStatus.REVIEW);
    article.setListingStatus(ArticleStatus.UNLISTED);
    article.setCoverType("GRADIENT");
    article.setCoverValue(defaultCover());
    article.setReadMinutes(readMinutes(request.content()));
    article.setHeat(0);
    article.setSubmittedAt(now);
    article.setCreatedBy(operatorId);
    article.setUpdatedBy(operatorId);
    article.setCreatedAt(now);
    article.setUpdatedAt(now);
    articleMapper.insert(article);
    upsertContent(article.getId(), request.content());
    replaceTags(article.getId(), request.tags());
    return toResponse(articleMapper.selectById(article.getId()), true);
  }

  @Transactional(rollbackFor = Exception.class)
  public ArticleResponse update(Long id, ArticleRequest request) {
    BlogArticle article = requireArticle(id);
    applyArticleRequest(article, request);
    article.setReadMinutes(readMinutes(request.content()));
    article.setUpdatedBy(currentUserId());
    article.setUpdatedAt(LocalDateTime.now());
    articleMapper.updateById(article);
    upsertContent(article.getId(), request.content());
    replaceTags(article.getId(), request.tags());
    return toResponse(articleMapper.selectById(id), true);
  }

  @Transactional(rollbackFor = Exception.class)
  public ArticleResponse submit(Long id) {
    BlogArticle article = requireArticle(id);
    article.setStatus(ArticleStatus.REVIEW);
    article.setListingStatus(ArticleStatus.UNLISTED);
    article.setSubmittedAt(LocalDateTime.now());
    article.setUpdatedBy(currentUserId());
    article.setUpdatedAt(LocalDateTime.now());
    articleMapper.updateById(article);
    return toResponse(article, true);
  }

  @Transactional(rollbackFor = Exception.class)
  public ArticleResponse approve(Long id) {
    BlogArticle article = requireArticle(id);
    article.setStatus(ArticleStatus.READY);
    article.setApprovedAt(LocalDateTime.now());
    article.setUpdatedBy(currentUserId());
    article.setUpdatedAt(LocalDateTime.now());
    articleMapper.updateById(article);
    return toResponse(article, true);
  }

  @Transactional(rollbackFor = Exception.class)
  public ArticleResponse reject(Long id) {
    BlogArticle article = requireArticle(id);
    article.setStatus(ArticleStatus.IDEA);
    article.setListingStatus(ArticleStatus.UNLISTED);
    article.setUpdatedBy(currentUserId());
    article.setUpdatedAt(LocalDateTime.now());
    articleMapper.updateById(article);
    return toResponse(article, true);
  }

  @Transactional(rollbackFor = Exception.class)
  public ArticleResponse listing(Long id, String listingStatus) {
    BlogArticle article = requireArticle(id);
    String normalized = listingStatus.toUpperCase(Locale.ROOT);
    if (!ArticleStatus.LISTED.equals(normalized) && !ArticleStatus.UNLISTED.equals(normalized)) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "上架状态不合法");
    }
    if (!ArticleStatus.READY.equals(article.getStatus())
        && !ArticleStatus.PUBLISHED.equals(article.getStatus())) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "只有审核通过的文章才能上架");
    }
    article.setListingStatus(normalized);
    if (ArticleStatus.LISTED.equals(normalized)) {
      article.setStatus(ArticleStatus.PUBLISHED);
      article.setPublishedAt(LocalDateTime.now());
    }
    article.setUpdatedBy(currentUserId());
    article.setUpdatedAt(LocalDateTime.now());
    articleMapper.updateById(article);
    return toResponse(article, true);
  }

  public ArticleResponse generate(GenerateArticleRequest request) {
    List<String> tags = request.tags() == null || request.tags().isEmpty() ? List.of("AI 写作", "工程化") : request.tags();
    String style = request.style() == null || request.style().isBlank() ? "工程实践" : request.style();
    String length = request.length() == null || request.length().isBlank() ? "中篇" : request.length();
    String content = "# " + request.prompt() + "\n\n"
        + "这是一篇偏" + style + "风格的" + length + "文章草稿。\n\n"
        + "## 核心思路\n"
        + "1. 先定义真实业务问题。\n"
        + "2. 再拆解内容生产、审核和发布链路。\n"
        + "3. 最后围绕 " + String.join("、", tags) + " 沉淀可复用方法。\n\n"
        + "## 正文草稿\n"
        + "好的 AI 博客平台不只是生成文本，而是把选题、资料、结构、编辑、审核和发布变成连续可追踪的工作流。";
    return new ArticleResponse(
        null,
        request.prompt(),
        "一篇聚焦 " + String.join("、", tags) + " 的文章草稿。",
        content,
        idOf(defaultAuthorId()),
        null,
        idOf(defaultCategoryId()),
        null,
        tags,
        ArticleStatus.AI_GENERATED,
        ArticleStatus.REVIEW,
        ArticleStatus.UNLISTED,
        defaultCover(),
        readMinutes(content),
        0,
        null,
        LocalDateTime.now()
    );
  }

  public List<PublicAuthorResponse> authors() {
    return authorMapper.selectList(new LambdaQueryWrapper<BlogAuthor>().eq(BlogAuthor::getStatus, 1).orderByAsc(BlogAuthor::getId))
        .stream()
        .map(author -> new PublicAuthorResponse(
            idOf(author.getId()),
            author.getName(),
            author.getRole(),
            author.getAvatarUrl(),
            author.getBio(),
            author.getAiPreference()
        ))
        .toList();
  }

  public List<PublicTopicResponse> topics() {
    List<BlogTopic> topics = topicMapper.selectList(
        new LambdaQueryWrapper<BlogTopic>().eq(BlogTopic::getStatus, 1).orderByAsc(BlogTopic::getSortOrder)
    );
    return topics.stream()
        .map(topic -> new PublicTopicResponse(idOf(topic.getId()), topic.getTitle(), topic.getDescription(), tagNamesForTopic(topic.getId())))
        .toList();
  }

  private void applyArticleRequest(BlogArticle article, ArticleRequest request) {
    article.setTitle(request.title().trim());
    article.setSummary(request.summary().trim());
    Long authorId = parseId(request.authorId());
    Long categoryId = parseId(request.categoryId());
    article.setAuthorId(authorId == null ? defaultAuthorId() : authorId);
    article.setCategoryId(categoryId == null ? defaultCategoryId() : categoryId);
    article.setSource(request.source() == null || request.source().isBlank() ? ArticleStatus.MANUAL : request.source());
  }

  private String idOf(Long id) {
    return id == null ? null : String.valueOf(id);
  }

  private Long parseId(String id) {
    if (id == null || id.isBlank()) {
      return null;
    }
    try {
      return Long.valueOf(id);
    } catch (NumberFormatException ex) {
      throw new BusinessException(ErrorCode.BAD_REQUEST, "ID不合法");
    }
  }

  private BlogArticle requireArticle(Long id) {
    BlogArticle article = articleMapper.selectById(id);
    if (article == null) {
      throw new BusinessException(ErrorCode.NOT_FOUND, "文章不存在");
    }
    return article;
  }

  private void upsertContent(Long articleId, String markdown) {
    BlogArticleContent content = contentMapper.selectOne(
        new LambdaQueryWrapper<BlogArticleContent>().eq(BlogArticleContent::getArticleId, articleId)
    );
    LocalDateTime now = LocalDateTime.now();
    if (content == null) {
      content = new BlogArticleContent();
      content.setArticleId(articleId);
      content.setMarkdownContent(markdown);
      content.setContentVersion(1);
      content.setCreatedAt(now);
      content.setUpdatedAt(now);
      contentMapper.insert(content);
      return;
    }
    content.setMarkdownContent(markdown);
    content.setContentVersion((content.getContentVersion() == null ? 1 : content.getContentVersion()) + 1);
    content.setUpdatedAt(now);
    contentMapper.updateById(content);
  }

  private void replaceTags(Long articleId, List<String> tagNames) {
    articleTagMapper.delete(new LambdaQueryWrapper<BlogArticleTag>().eq(BlogArticleTag::getArticleId, articleId));
    for (String tagName : tagNames.stream().filter(Objects::nonNull).map(String::trim).filter(item -> !item.isBlank()).distinct().toList()) {
      BlogTag tag = findOrCreateTag(tagName);
      BlogArticleTag relation = new BlogArticleTag();
      relation.setArticleId(articleId);
      relation.setTagId(tag.getId());
      relation.setCreatedAt(LocalDateTime.now());
      articleTagMapper.insert(relation);
    }
  }

  private BlogTag findOrCreateTag(String name) {
    BlogTag tag = tagMapper.selectOne(new LambdaQueryWrapper<BlogTag>().eq(BlogTag::getName, name));
    if (tag != null) {
      return tag;
    }
    tag = new BlogTag();
    tag.setName(name);
    tag.setSlug(slug(name));
    tag.setColor("#22d3ee");
    tag.setArticleCount(0);
    tag.setCreatedAt(LocalDateTime.now());
    tag.setUpdatedAt(LocalDateTime.now());
    tagMapper.insert(tag);
    return tag;
  }

  private ArticleResponse toResponse(BlogArticle article, boolean includeContent) {
    Map<Long, BlogAuthor> authors = mapById(authorMapper.selectBatchIds(List.of(article.getAuthorId())), BlogAuthor::getId);
    Map<Long, BlogCategory> categories = mapById(categoryMapper.selectBatchIds(List.of(article.getCategoryId())), BlogCategory::getId);
    return toResponse(article, includeContent, authors, categories);
  }

  private List<ArticleResponse> toResponses(List<BlogArticle> articles, boolean includeContent) {
    if (articles.isEmpty()) {
      return List.of();
    }
    List<Long> authorIds = articles.stream().map(BlogArticle::getAuthorId).filter(Objects::nonNull).distinct().toList();
    List<Long> categoryIds = articles.stream().map(BlogArticle::getCategoryId).filter(Objects::nonNull).distinct().toList();
    Map<Long, BlogAuthor> authors = authorIds.isEmpty() ? Collections.emptyMap() : mapById(authorMapper.selectBatchIds(authorIds), BlogAuthor::getId);
    Map<Long, BlogCategory> categories = categoryIds.isEmpty() ? Collections.emptyMap() : mapById(categoryMapper.selectBatchIds(categoryIds), BlogCategory::getId);
    return articles.stream().map(article -> toResponse(article, includeContent, authors, categories)).toList();
  }

  private ArticleResponse toResponse(
      BlogArticle article,
      boolean includeContent,
      Map<Long, BlogAuthor> authors,
      Map<Long, BlogCategory> categories
  ) {
    BlogAuthor author = authors.get(article.getAuthorId());
    BlogCategory category = categories.get(article.getCategoryId());
    return new ArticleResponse(
        idOf(article.getId()),
        article.getTitle(),
        article.getSummary(),
        includeContent ? contentOf(article.getId()) : "",
        idOf(article.getAuthorId()),
        author == null ? null : author.getName(),
        idOf(article.getCategoryId()),
        category == null ? null : category.getName(),
        tagNamesForArticle(article.getId()),
        article.getSource(),
        article.getStatus(),
        article.getListingStatus(),
        article.getCoverValue() == null || article.getCoverValue().isBlank() ? defaultCover() : article.getCoverValue(),
        article.getReadMinutes(),
        article.getHeat(),
        article.getPublishedAt(),
        article.getUpdatedAt()
    );
  }

  private String contentOf(Long articleId) {
    BlogArticleContent content = contentMapper.selectOne(
        new LambdaQueryWrapper<BlogArticleContent>().eq(BlogArticleContent::getArticleId, articleId)
    );
    return content == null ? "" : content.getMarkdownContent();
  }

  private List<String> tagNamesForArticle(Long articleId) {
    List<Long> tagIds = articleTagMapper.selectList(
            new LambdaQueryWrapper<BlogArticleTag>().eq(BlogArticleTag::getArticleId, articleId)
        )
        .stream()
        .map(BlogArticleTag::getTagId)
        .toList();
    if (tagIds.isEmpty()) {
      return List.of();
    }
    return tagMapper.selectBatchIds(tagIds).stream().map(BlogTag::getName).toList();
  }

  private List<String> tagNamesForTopic(Long topicId) {
    List<Long> tagIds = topicTagMapper.selectList(
            new LambdaQueryWrapper<BlogTopicTag>().eq(BlogTopicTag::getTopicId, topicId)
        )
        .stream()
        .map(BlogTopicTag::getTagId)
        .toList();
    if (tagIds.isEmpty()) {
      return List.of();
    }
    return tagMapper.selectBatchIds(tagIds).stream().map(BlogTag::getName).toList();
  }

  private <T> Map<Long, T> mapById(List<T> items, Function<T, Long> idGetter) {
    return items.stream().collect(Collectors.toMap(idGetter, Function.identity(), (left, right) -> left));
  }

  private Long defaultAuthorId() {
    BlogAuthor author = authorMapper.selectOne(
        new LambdaQueryWrapper<BlogAuthor>().eq(BlogAuthor::getStatus, 1).orderByAsc(BlogAuthor::getId).last("limit 1")
    );
    return author == null ? 1L : author.getId();
  }

  private Long defaultCategoryId() {
    BlogCategory category = categoryMapper.selectOne(
        new LambdaQueryWrapper<BlogCategory>().eq(BlogCategory::getStatus, 1).orderByAsc(BlogCategory::getSortOrder).last("limit 1")
    );
    return category == null ? 1L : category.getId();
  }

  private int readMinutes(String content) {
    return Math.max(3, (int) Math.ceil((content == null ? 0 : content.length()) / 450.0));
  }

  private String defaultCover() {
    return "linear-gradient(135deg, #0f172a, #22d3ee 46%, #34d399)";
  }

  private String slug(String value) {
    String slug = value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9\\u4e00-\\u9fa5]+", "-").replaceAll("(^-|-$)", "");
    return slug.isBlank() ? "tag-" + System.currentTimeMillis() : slug;
  }

  private Long currentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof SecurityUser user) {
      return user.id();
    }
    return null;
  }
}
