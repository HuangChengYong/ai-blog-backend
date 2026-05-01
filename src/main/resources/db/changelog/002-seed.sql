--liquibase formatted sql

--changeset ai-blog:002-seed splitStatements:true endDelimiter:;
insert into sys_user (id, username, password_hash, nickname, email, status, user_type, data_scope, password_updated_at)
values (1, 'admin', '{noop}huangcy125643', '超级管理员', 'admin@neuroblog.local', 1, 'SUPER_ADMIN', 'ALL', now());

insert into sys_role (id, role_code, role_name, description, data_scope, sort_order, status)
values
  (1, 'SUPER_ADMIN', '超级管理员', '拥有后台所有模块和系统配置能力。', 'ALL', 1, 1),
  (2, 'CONTENT_EDITOR', '内容编辑', '负责 AI 写作、草稿维护和提交审批。', 'SELF', 2, 1),
  (3, 'REVIEWER', '审批员', '负责内容复核、审批通过和发布建议。', 'SELF', 3, 1);

insert into sys_user_role (id, user_id, role_id)
values (1, 1, 1);

insert into sys_permission (id, code, name, permission_type, resource, action, description, status)
values
  (1001, 'dashboard.view', '查看运营概览', 'MENU', 'dashboard', 'view', '查看后台指标、工作流和发布健康度。', 1),
  (1002, 'studio.generate', '使用 AI 写作', 'BUTTON', 'studio', 'generate', '生成文章草稿并保存到文章管理。', 1),
  (1003, 'article.create', '新增文章', 'BUTTON', 'article', 'create', '新建文章、草稿和选题内容。', 1),
  (1004, 'article.edit', '编辑文章', 'BUTTON', 'article', 'edit', '修改标题、摘要、正文、分类和标签。', 1),
  (1005, 'article.delete', '删除文章', 'BUTTON', 'article', 'delete', '删除无效草稿或下线内容。', 1),
  (1006, 'article.publish', '上下架文章', 'BUTTON', 'article', 'publish', '将审批通过的文章上架或下架。', 1),
  (1007, 'approval.review', '处理审批', 'BUTTON', 'approval', 'review', '通过或退回待审批文章。', 1),
  (1008, 'comment.manage', '管理评论', 'BUTTON', 'comment', 'manage', '审核、隐藏或删除用户评论。', 1),
  (1009, 'media.manage', '管理素材', 'BUTTON', 'media', 'manage', '上传、替换和归档封面图等媒体素材。', 1),
  (1010, 'user.view', '查看用户', 'MENU', 'user', 'view', '查看后台账号列表、角色归属和活跃状态。', 1),
  (1011, 'user.create', '新增用户', 'BUTTON', 'user', 'create', '创建后台账号并分配初始角色。', 1),
  (1012, 'user.update', '编辑用户', 'BUTTON', 'user', 'update', '修改账号资料、角色归属和登录状态。', 1),
  (1013, 'user.disable', '禁用用户', 'BUTTON', 'user', 'disable', '停用账号或限制后台登录。', 1),
  (1014, 'role.view', '查看角色', 'MENU', 'role', 'view', '查看角色列表、成员数量和权限摘要。', 1),
  (1015, 'role.create', '新增角色', 'BUTTON', 'role', 'create', '创建新的后台角色模板。', 1),
  (1016, 'role.update', '编辑角色', 'BUTTON', 'role', 'update', '维护角色名称、说明和成员配置。', 1),
  (1017, 'permission.manage', '配置权限', 'BUTTON', 'permission', 'manage', '为角色分配具体功能权限。', 1),
  (1018, 'menu.manage', '配置菜单', 'BUTTON', 'menu', 'manage', '调整后台菜单的显示、排序和访问权限。', 1),
  (1019, 'config.manage', '系统设置', 'BUTTON', 'config', 'manage', '维护站点配置、基础参数和安全策略。', 1),
  (1020, 'audit.log', '查看审计日志', 'MENU', 'audit', 'view', '查看登录、审批、发布和权限变更记录。', 1),
  (1021, 'data.export', '导出数据', 'DATA', 'data', 'export', '导出文章、用户和运营统计数据。', 1),
  (1022, 'notice.manage', '通知公告', 'BUTTON', 'notice', 'manage', '发布后台通知和运营公告。', 1);

insert into sys_menu (id, parent_id, title, menu_type, route_path, component, permission_code, icon, sort_order, visible, keep_alive, external, status)
values
  (2001, 0, '运营概览', 'MENU', '/admin#overview', 'AdminOverview', 'dashboard.view', 'DataAnalysis', 10, 1, 1, 0, 1),
  (2002, 0, 'AI写作工作台', 'MENU', '/admin#studio', 'AdminStudio', 'studio.generate', 'MagicStick', 20, 1, 1, 0, 1),
  (2003, 0, '文章管理', 'MENU', '/admin#articles', 'AdminArticles', 'article.edit', 'Document', 30, 1, 1, 0, 1),
  (2004, 0, '审批管理', 'MENU', '/admin#approvals', 'AdminApprovals', 'approval.review', 'FolderChecked', 40, 1, 1, 0, 1),
  (2005, 0, '用户管理', 'MENU', '/admin#users', 'AdminUsers', 'user.view', 'User', 50, 1, 1, 0, 1),
  (2006, 0, '角色管理', 'MENU', '/admin#roles', 'AdminRoles', 'role.view', 'Setting', 60, 1, 1, 0, 1),
  (2007, 0, '权限管理', 'MENU', '/admin#permissions', 'AdminPermissions', 'permission.manage', 'Key', 70, 1, 1, 0, 1),
  (2008, 0, '菜单管理', 'MENU', '/admin#menus', 'AdminMenus', 'menu.manage', 'Menu', 80, 1, 1, 0, 1);

insert into sys_role_permission (id, role_id, permission_id)
select 100000 + id, 1, id from sys_permission;

insert into sys_role_permission (id, role_id, permission_id)
values
  (200001, 2, 1001),
  (200002, 2, 1002),
  (200003, 2, 1003),
  (200004, 2, 1004),
  (200005, 2, 1009),
  (300001, 3, 1001),
  (300002, 3, 1004),
  (300003, 3, 1006),
  (300004, 3, 1007),
  (300005, 3, 1020);

insert into sys_role_menu (id, role_id, menu_id)
select 110000 + id, 1, id from sys_menu;

insert into sys_role_menu (id, role_id, menu_id)
values
  (220001, 2, 2001),
  (220002, 2, 2002),
  (220003, 2, 2003),
  (330001, 3, 2001),
  (330002, 3, 2003),
  (330003, 3, 2004);

insert into sys_config (id, config_key, config_value, value_type, group_code, description, editable)
values
  (9001, 'site.name', 'NeuroBlog', 'STRING', 'site', '站点名称', 1),
  (9002, 'site.description', '智能技术博客平台', 'STRING', 'site', '站点描述', 1),
  (9003, 'security.password.minLength', '8', 'NUMBER', 'security', '后台密码最小长度', 1);

insert into sys_dict (id, dict_code, dict_name, description, status)
values
  (9101, 'article_status', '文章状态', '文章草稿、审批和发布状态', 1),
  (9102, 'listing_status', '上架状态', '访客端展示状态', 1);

insert into sys_dict_item (id, dict_code, item_label, item_value, sort_order, status)
values
  (9201, 'article_status', '灵感', 'IDEA', 10, 1),
  (9202, 'article_status', '待审批', 'REVIEW', 20, 1),
  (9203, 'article_status', '可发布', 'READY', 30, 1),
  (9204, 'article_status', '已发布', 'PUBLISHED', 40, 1),
  (9211, 'listing_status', '未上架', 'UNLISTED', 10, 1),
  (9212, 'listing_status', '已上架', 'LISTED', 20, 1);

insert into blog_author (id, name, role, avatar_url, bio, ai_preference, status)
values
  (4001, '林澈', 'AI 平台工程师', 'https://api.dicebear.com/9.x/notionists/svg?seed=lin', '关注 RAG、Agent 编排和工程化交付。', '喜欢用结构化提示词拆解复杂系统。', 1),
  (4002, '许知行', '技术产品负责人', 'https://api.dicebear.com/9.x/notionists/svg?seed=xu', '把模型能力包装成清晰的用户工作流。', '偏好先定义评测标准，再打磨体验。', 1),
  (4003, '沈墨', '前端架构师', 'https://api.dicebear.com/9.x/notionists/svg?seed=shen', '长期写 Vue、设计系统和数据可视化。', '让 AI 承担重复劳动，人来决定产品判断。', 1);

insert into blog_category (id, parent_id, name, slug, description, sort_order, status)
values
  (3001, 0, 'AI 工程', 'ai-engineering', 'RAG、Agent 和模型工程实践。', 10, 1),
  (3002, 0, '模型应用', 'model-application', '围绕真实业务工作流的模型应用。', 20, 1),
  (3003, 0, '产品设计', 'product-design', 'AI Native 产品体验与信息架构。', 30, 1),
  (3004, 0, '开发实践', 'development-practice', '前后端工程化与平台建设。', 40, 1);

insert into blog_tag (id, name, slug, color, article_count)
values
  (5001, 'RAG', 'rag', '#22d3ee', 1),
  (5002, '可观测性', 'observability', '#34d399', 1),
  (5003, '评测', 'evaluation', '#f59e0b', 2),
  (5004, 'Agent', 'agent', '#22d3ee', 1),
  (5005, '工作流', 'workflow', '#34d399', 1),
  (5006, 'AI 写作', 'ai-writing', '#22d3ee', 1),
  (5007, 'UX', 'ux', '#fb7185', 1),
  (5008, 'Vue', 'vue', '#34d399', 1),
  (5009, 'Mock', 'mock', '#f59e0b', 1);

insert into blog_topic (id, title, description, status, sort_order)
values
  (8001, 'AI Native 产品拆解', '围绕真实工作流，而不是模型炫技来设计功能。', 1, 10),
  (8002, '评测与质量闭环', '从主观好用走向可追踪、可复现、可迭代。', 1, 20),
  (8003, '前端工程化', '让 Mock、路由和组件结构提前对齐未来接口。', 1, 30);

insert into blog_topic_tag (id, topic_id, tag_id)
values
  (8101, 8001, 5004),
  (8102, 8001, 5007),
  (8103, 8002, 5001),
  (8104, 8002, 5003),
  (8105, 8003, 5008),
  (8106, 8003, 5009);

insert into blog_article (id, title, summary, author_id, category_id, source, status, listing_status, cover_type, cover_value, read_minutes, heat, published_at, created_by, updated_by)
values
  (6001, '给 RAG 系统加一层可观测性', '从召回、重排、生成三个阶段拆分指标，让团队能定位回答质量波动。', 4001, 3001, 'MANUAL', 'PUBLISHED', 'LISTED', 'GRADIENT', 'linear-gradient(135deg, #0f172a, #14b8a6 52%, #f59e0b)', 8, 94, '2026-04-20 09:00:00', 1, 1),
  (6002, 'Agent 工作流别急着全自动', '让 Agent 先成为可审计的协作者，再逐步接管稳定步骤。', 4002, 3002, 'MANUAL', 'PUBLISHED', 'LISTED', 'GRADIENT', 'linear-gradient(135deg, #111827, #6366f1 48%, #22c55e)', 6, 88, '2026-04-18 09:00:00', 1, 1),
  (6003, 'AI 编辑器里的好按钮应该很少', '写作工具不是功能货架，真正重要的是让作者知道下一步该做什么。', 4003, 3003, 'MANUAL', 'PUBLISHED', 'LISTED', 'GRADIENT', 'linear-gradient(135deg, #172554, #06b6d4 44%, #f97316)', 5, 76, '2026-04-15 09:00:00', 1, 1),
  (6004, '用 Vue 做内容平台的交互骨架', '路由、Mock 服务和状态更新先跑顺，后端接口替换会轻很多。', 4003, 3004, 'MANUAL', 'PUBLISHED', 'LISTED', 'GRADIENT', 'linear-gradient(135deg, #052e16, #10b981 48%, #eab308)', 7, 82, '2026-04-12 09:00:00', 1, 1),
  (6005, '如何为团队建立 Prompt 资产库', '把高频提示词沉淀成可复用模板，并记录适用场景。', 4001, 3001, 'MANUAL', 'REVIEW', 'UNLISTED', 'GRADIENT', 'linear-gradient(135deg, #0f172a, #22d3ee 46%, #34d399)', 4, 0, null, 1, 1),
  (6006, '从失败样例开始优化 AI 搜索', '把用户反馈转成召回和生成阶段的可执行任务。', 4001, 3001, 'AI_GENERATED', 'IDEA', 'UNLISTED', 'GRADIENT', 'linear-gradient(135deg, #0f172a, #22d3ee 46%, #34d399)', 4, 0, null, 1, 1);

insert into blog_article_content (id, article_id, markdown_content, html_content, content_version)
values
  (7001, 6001, 'RAG 最容易被误判成一个单点功能，但它实际是一条多阶段链路。任何一个阶段漂移，最终答案都会显得不稳定。', null, 1),
  (7002, 6002, '很多 Agent 产品失败不是因为模型不够强，而是因为边界被设计得太模糊。', null, 1),
  (7003, 6003, 'AI 写作界面如果堆满按钮，用户会把每一次操作都当成一次试错。', null, 1),
  (7004, 6004, '内容平台前端的第一版重点不是把所有边界都抽象完，而是把读、写、管三个闭环跑起来。', null, 1),
  (7005, 6005, '计划从命名、版本、评测和权限四个角度展开。', null, 1),
  (7006, 6006, '先收集低分回答，再标注失败原因，最后更新评测集。', null, 1);

insert into blog_article_tag (id, article_id, tag_id)
values
  (6101, 6001, 5001),
  (6102, 6001, 5002),
  (6103, 6001, 5003),
  (6104, 6002, 5004),
  (6105, 6002, 5005),
  (6106, 6003, 5006),
  (6107, 6003, 5007),
  (6108, 6004, 5008),
  (6109, 6004, 5009),
  (6110, 6005, 5003),
  (6111, 6006, 5003);

insert into blog_article_metric (id, article_id, view_count, like_count, comment_count, share_count, favorite_count)
values
  (6201, 6001, 9400, 231, 18, 42, 88),
  (6202, 6002, 8800, 206, 12, 38, 79),
  (6203, 6003, 7600, 166, 9, 21, 52),
  (6204, 6004, 8200, 188, 11, 32, 64);

insert into blog_review_record (id, article_id, reviewer_id, action, from_status, to_status, comment)
values
  (6301, 6005, null, 'SUBMIT', 'IDEA', 'REVIEW', '初始化待审批草稿。');

insert into ai_prompt_template (id, name, scenario, prompt_text, variables, status, created_by)
values
  (12001, 'AI 博客文章生成', 'article_generation', '根据主题、风格、篇幅和标签生成博客草稿。', json_object('topic', 'string', 'style', 'string', 'length', 'string', 'tags', 'array'), 1, 1);

insert into storage_provider (id, provider_code, provider_name, provider_type, endpoint, bucket_name, active, status)
values
  (13001, 'minio', '本地 MinIO', 'S3_COMPATIBLE', 'http://127.0.0.1:9000', 'ai-blog', 1, 1),
  (13002, 'tencent-cos', '腾讯云 COS', 'COS', null, 'replace-with-cos-bucket', 0, 0);
