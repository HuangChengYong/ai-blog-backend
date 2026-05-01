package com.aiblog.content;

import com.aiblog.common.ApiResponse;
import com.aiblog.common.PageResult;
import com.aiblog.common.RepeatSubmit;
import com.aiblog.content.dto.ArticleRequest;
import com.aiblog.content.dto.ArticleResponse;
import com.aiblog.content.dto.GenerateArticleRequest;
import com.aiblog.content.dto.ListingRequest;
import com.aiblog.content.dto.PublicAuthorResponse;
import com.aiblog.content.dto.PublicTopicResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ArticleController {

  private final ArticleService articleService;

  public ArticleController(ArticleService articleService) {
    this.articleService = articleService;
  }

  @GetMapping("/public/articles")
  public ApiResponse<PageResult<ArticleResponse>> publicArticles(
      @RequestParam(defaultValue = "1") long page,
      @RequestParam(defaultValue = "10") long size
  ) {
    return ApiResponse.ok(articleService.publicArticles(page, size));
  }

  @GetMapping("/public/articles/{id}")
  public ApiResponse<ArticleResponse> publicArticle(@PathVariable Long id) {
    return ApiResponse.ok(articleService.publicArticle(id));
  }

  @GetMapping("/public/authors")
  public ApiResponse<List<PublicAuthorResponse>> authors() {
    return ApiResponse.ok(articleService.authors());
  }

  @GetMapping("/public/topics")
  public ApiResponse<List<PublicTopicResponse>> topics() {
    return ApiResponse.ok(articleService.topics());
  }

  @PreAuthorize("hasAuthority('article.edit')")
  @GetMapping("/admin/articles")
  public ApiResponse<PageResult<ArticleResponse>> adminArticles(
      @RequestParam(defaultValue = "1") long page,
      @RequestParam(defaultValue = "100") long size
  ) {
    return ApiResponse.ok(articleService.adminArticles(page, size));
  }

  @RepeatSubmit
  @PreAuthorize("hasAuthority('article.create')")
  @PostMapping("/admin/articles")
  public ApiResponse<ArticleResponse> create(@Valid @RequestBody ArticleRequest request) {
    return ApiResponse.ok(articleService.create(request));
  }

  @RepeatSubmit
  @PreAuthorize("hasAuthority('article.edit')")
  @PutMapping("/admin/articles/{id}")
  public ApiResponse<ArticleResponse> update(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
    return ApiResponse.ok(articleService.update(id, request));
  }

  @RepeatSubmit
  @PreAuthorize("hasAuthority('studio.generate')")
  @PostMapping("/admin/articles/generate")
  public ApiResponse<ArticleResponse> generate(@Valid @RequestBody GenerateArticleRequest request) {
    return ApiResponse.ok(articleService.generate(request));
  }

  @RepeatSubmit
  @PreAuthorize("hasAuthority('article.edit')")
  @PatchMapping("/admin/articles/{id}/submit")
  public ApiResponse<ArticleResponse> submit(@PathVariable Long id) {
    return ApiResponse.ok(articleService.submit(id));
  }

  @RepeatSubmit
  @PreAuthorize("hasAuthority('approval.review')")
  @PatchMapping("/admin/articles/{id}/approve")
  public ApiResponse<ArticleResponse> approve(@PathVariable Long id) {
    return ApiResponse.ok(articleService.approve(id));
  }

  @RepeatSubmit
  @PreAuthorize("hasAuthority('approval.review')")
  @PatchMapping("/admin/articles/{id}/reject")
  public ApiResponse<ArticleResponse> reject(@PathVariable Long id) {
    return ApiResponse.ok(articleService.reject(id));
  }

  @RepeatSubmit
  @PreAuthorize("hasAuthority('article.publish')")
  @PatchMapping("/admin/articles/{id}/listing")
  public ApiResponse<ArticleResponse> listing(@PathVariable Long id, @Valid @RequestBody ListingRequest request) {
    return ApiResponse.ok(articleService.listing(id, request.listingStatus()));
  }
}
