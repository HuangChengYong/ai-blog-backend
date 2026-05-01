--liquibase formatted sql

--changeset ai-blog:001-schema splitStatements:true endDelimiter:;
create table if not exists sys_user (
  id bigint primary key,
  username varchar(64) not null,
  password_hash varchar(255) not null,
  nickname varchar(64) not null,
  avatar_url varchar(512) null,
  email varchar(128) null,
  phone varchar(32) null,
  status tinyint not null default 1,
  user_type varchar(32) not null default 'NORMAL',
  data_scope varchar(32) not null default 'SELF',
  last_login_at datetime null,
  last_login_ip varchar(64) null,
  password_updated_at datetime null,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  unique key uk_sys_user_username (username),
  key idx_sys_user_status (status),
  key idx_sys_user_deleted (deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists sys_role (
  id bigint primary key,
  role_code varchar(64) not null,
  role_name varchar(64) not null,
  description varchar(255) null,
  data_scope varchar(32) not null default 'SELF',
  sort_order int not null default 0,
  status tinyint not null default 1,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  unique key uk_sys_role_code (role_code),
  key idx_sys_role_status (status),
  key idx_sys_role_deleted (deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists sys_permission (
  id bigint primary key,
  code varchar(128) not null,
  name varchar(64) not null,
  permission_type varchar(32) not null,
  resource varchar(128) not null,
  action varchar(64) not null,
  description varchar(255) null,
  status tinyint not null default 1,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  unique key uk_sys_permission_code (code),
  key idx_sys_permission_type (permission_type),
  key idx_sys_permission_status (status),
  key idx_sys_permission_deleted (deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists sys_menu (
  id bigint primary key,
  parent_id bigint not null default 0,
  title varchar(64) not null,
  menu_type varchar(32) not null default 'MENU',
  route_path varchar(255) not null,
  component varchar(255) null,
  permission_code varchar(128) null,
  icon varchar(64) null,
  sort_order int not null default 0,
  visible tinyint not null default 1,
  keep_alive tinyint not null default 0,
  external tinyint not null default 0,
  status tinyint not null default 1,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  key idx_sys_menu_parent (parent_id),
  key idx_sys_menu_permission (permission_code),
  key idx_sys_menu_status (status, visible),
  key idx_sys_menu_deleted (deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists sys_user_role (
  id bigint primary key,
  user_id bigint not null,
  role_id bigint not null,
  created_at datetime not null default current_timestamp,
  unique key uk_sys_user_role (user_id, role_id),
  key idx_sys_user_role_user (user_id),
  key idx_sys_user_role_role (role_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists sys_role_permission (
  id bigint primary key,
  role_id bigint not null,
  permission_id bigint not null,
  created_at datetime not null default current_timestamp,
  unique key uk_sys_role_permission (role_id, permission_id),
  key idx_sys_role_permission_role (role_id),
  key idx_sys_role_permission_permission (permission_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists sys_role_menu (
  id bigint primary key,
  role_id bigint not null,
  menu_id bigint not null,
  created_at datetime not null default current_timestamp,
  unique key uk_sys_role_menu (role_id, menu_id),
  key idx_sys_role_menu_role (role_id),
  key idx_sys_role_menu_menu (menu_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists sys_config (
  id bigint primary key,
  config_key varchar(128) not null,
  config_value text null,
  value_type varchar(32) not null default 'STRING',
  group_code varchar(64) not null default 'system',
  description varchar(255) null,
  editable tinyint not null default 1,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  unique key uk_sys_config_key (config_key),
  key idx_sys_config_group (group_code),
  key idx_sys_config_deleted (deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists sys_dict (
  id bigint primary key,
  dict_code varchar(64) not null,
  dict_name varchar(64) not null,
  description varchar(255) null,
  status tinyint not null default 1,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  unique key uk_sys_dict_code (dict_code),
  key idx_sys_dict_deleted (deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists sys_dict_item (
  id bigint primary key,
  dict_code varchar(64) not null,
  item_label varchar(64) not null,
  item_value varchar(128) not null,
  sort_order int not null default 0,
  status tinyint not null default 1,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  unique key uk_sys_dict_item (dict_code, item_value),
  key idx_sys_dict_item_dict (dict_code)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists sys_login_log (
  id bigint primary key,
  username varchar(64) not null,
  user_id bigint null,
  login_ip varchar(64) null,
  user_agent varchar(512) null,
  success tinyint not null,
  failure_reason varchar(255) null,
  created_at datetime not null default current_timestamp,
  key idx_sys_login_log_user (user_id),
  key idx_sys_login_log_created (created_at)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists sys_operation_log (
  id bigint primary key,
  trace_id varchar(64) null,
  user_id bigint null,
  username varchar(64) null,
  module varchar(64) not null,
  operation varchar(128) not null,
  request_method varchar(16) null,
  request_uri varchar(512) null,
  request_body json null,
  response_status int null,
  cost_ms bigint null,
  client_ip varchar(64) null,
  user_agent varchar(512) null,
  created_at datetime not null default current_timestamp,
  key idx_sys_operation_log_user (user_id),
  key idx_sys_operation_log_trace (trace_id),
  key idx_sys_operation_log_created (created_at)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists sys_audit_log (
  id bigint primary key,
  actor_id bigint null,
  actor_name varchar(64) null,
  event_type varchar(64) not null,
  target_type varchar(64) not null,
  target_id bigint null,
  before_value json null,
  after_value json null,
  remark varchar(512) null,
  created_at datetime not null default current_timestamp,
  key idx_sys_audit_log_actor (actor_id),
  key idx_sys_audit_log_target (target_type, target_id),
  key idx_sys_audit_log_created (created_at)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists sys_notice (
  id bigint primary key,
  title varchar(128) not null,
  content text not null,
  notice_type varchar(32) not null default 'INFO',
  target_scope varchar(32) not null default 'ALL',
  status varchar(32) not null default 'DRAFT',
  publish_at datetime null,
  expire_at datetime null,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  key idx_sys_notice_status (status),
  key idx_sys_notice_deleted (deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists blog_author (
  id bigint primary key,
  name varchar(64) not null,
  role varchar(128) null,
  avatar_url varchar(512) null,
  bio varchar(512) null,
  ai_preference varchar(512) null,
  status tinyint not null default 1,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  key idx_blog_author_status (status),
  key idx_blog_author_deleted (deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists blog_category (
  id bigint primary key,
  parent_id bigint not null default 0,
  name varchar(64) not null,
  slug varchar(128) not null,
  description varchar(255) null,
  sort_order int not null default 0,
  status tinyint not null default 1,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  unique key uk_blog_category_slug (slug),
  key idx_blog_category_parent (parent_id),
  key idx_blog_category_deleted (deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists blog_tag (
  id bigint primary key,
  name varchar(64) not null,
  slug varchar(128) not null,
  color varchar(32) null,
  article_count int not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  unique key uk_blog_tag_slug (slug),
  unique key uk_blog_tag_name (name)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists blog_topic (
  id bigint primary key,
  title varchar(128) not null,
  description varchar(512) null,
  status tinyint not null default 1,
  sort_order int not null default 0,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  key idx_blog_topic_status (status),
  key idx_blog_topic_deleted (deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists blog_topic_tag (
  id bigint primary key,
  topic_id bigint not null,
  tag_id bigint not null,
  created_at datetime not null default current_timestamp,
  unique key uk_blog_topic_tag (topic_id, tag_id),
  key idx_blog_topic_tag_topic (topic_id),
  key idx_blog_topic_tag_tag (tag_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists blog_article (
  id bigint primary key,
  title varchar(180) not null,
  summary varchar(512) not null,
  author_id bigint not null,
  category_id bigint not null,
  source varchar(32) not null default 'MANUAL',
  status varchar(32) not null default 'IDEA',
  listing_status varchar(32) not null default 'UNLISTED',
  cover_type varchar(32) not null default 'GRADIENT',
  cover_value varchar(1024) null,
  seo_title varchar(180) null,
  seo_description varchar(512) null,
  read_minutes int not null default 3,
  heat int not null default 0,
  submitted_at datetime null,
  approved_at datetime null,
  published_at datetime null,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  key idx_blog_article_status (status),
  key idx_blog_article_listing (listing_status),
  key idx_blog_article_author (author_id),
  key idx_blog_article_category (category_id),
  key idx_blog_article_published (published_at),
  key idx_blog_article_deleted (deleted),
  fulltext key ft_blog_article_title_summary (title, summary)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists blog_article_content (
  id bigint primary key,
  article_id bigint not null,
  markdown_content longtext not null,
  html_content longtext null,
  content_version int not null default 1,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  unique key uk_blog_article_content_article (article_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists blog_article_tag (
  id bigint primary key,
  article_id bigint not null,
  tag_id bigint not null,
  created_at datetime not null default current_timestamp,
  unique key uk_blog_article_tag (article_id, tag_id),
  key idx_blog_article_tag_article (article_id),
  key idx_blog_article_tag_tag (tag_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists blog_article_revision (
  id bigint primary key,
  article_id bigint not null,
  revision_no int not null,
  title varchar(180) not null,
  summary varchar(512) not null,
  markdown_content longtext not null,
  status varchar(32) not null,
  operator_id bigint null,
  remark varchar(512) null,
  created_at datetime not null default current_timestamp,
  unique key uk_blog_article_revision (article_id, revision_no),
  key idx_blog_article_revision_article (article_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists blog_review_record (
  id bigint primary key,
  article_id bigint not null,
  reviewer_id bigint null,
  action varchar(32) not null,
  from_status varchar(32) null,
  to_status varchar(32) null,
  comment varchar(512) null,
  created_at datetime not null default current_timestamp,
  key idx_blog_review_record_article (article_id),
  key idx_blog_review_record_reviewer (reviewer_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists blog_comment (
  id bigint primary key,
  article_id bigint not null,
  parent_id bigint not null default 0,
  nickname varchar(64) not null,
  email varchar(128) null,
  content text not null,
  status varchar(32) not null default 'PENDING',
  ip_address varchar(64) null,
  user_agent varchar(512) null,
  reviewed_by bigint null,
  reviewed_at datetime null,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  key idx_blog_comment_article (article_id),
  key idx_blog_comment_status (status)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists blog_article_metric (
  id bigint primary key,
  article_id bigint not null,
  view_count bigint not null default 0,
  like_count bigint not null default 0,
  comment_count bigint not null default 0,
  share_count bigint not null default 0,
  favorite_count bigint not null default 0,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  unique key uk_blog_article_metric_article (article_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists ai_prompt_template (
  id bigint primary key,
  name varchar(128) not null,
  scenario varchar(64) not null,
  prompt_text text not null,
  variables json null,
  status tinyint not null default 1,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  key idx_ai_prompt_template_scenario (scenario),
  key idx_ai_prompt_template_deleted (deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists ai_generation_task (
  id bigint primary key,
  prompt varchar(512) not null,
  style varchar(64) null,
  length_label varchar(64) null,
  tags json null,
  provider varchar(64) null,
  model varchar(128) null,
  status varchar(32) not null default 'PENDING',
  result_article_id bigint null,
  result_title varchar(180) null,
  result_summary varchar(512) null,
  result_content longtext null,
  error_message text null,
  requested_by bigint null,
  started_at datetime null,
  finished_at datetime null,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  key idx_ai_generation_task_status (status),
  key idx_ai_generation_task_user (requested_by)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists storage_provider (
  id bigint primary key,
  provider_code varchar(64) not null,
  provider_name varchar(64) not null,
  provider_type varchar(32) not null,
  endpoint varchar(512) null,
  bucket_name varchar(128) not null,
  region varchar(64) null,
  encrypted_config json null,
  active tinyint not null default 0,
  status tinyint not null default 1,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  unique key uk_storage_provider_code (provider_code),
  key idx_storage_provider_active (active),
  key idx_storage_provider_deleted (deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists storage_object (
  id bigint primary key,
  provider_code varchar(64) not null,
  bucket_name varchar(128) not null,
  object_key varchar(1024) not null,
  original_name varchar(255) null,
  content_type varchar(128) null,
  size_bytes bigint not null default 0,
  etag varchar(128) null,
  public_url varchar(1024) null,
  checksum_sha256 varchar(128) null,
  status tinyint not null default 1,
  created_by bigint null,
  updated_by bigint null,
  deleted tinyint not null default 0,
  created_at datetime not null default current_timestamp,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  unique key uk_storage_object_key (provider_code, bucket_name, object_key(191)),
  key idx_storage_object_provider (provider_code),
  key idx_storage_object_deleted (deleted)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;

create table if not exists storage_object_ref (
  id bigint primary key,
  object_id bigint not null,
  ref_type varchar(64) not null,
  ref_id bigint not null,
  usage_type varchar(64) not null,
  created_at datetime not null default current_timestamp,
  unique key uk_storage_object_ref (object_id, ref_type, ref_id, usage_type),
  key idx_storage_object_ref_ref (ref_type, ref_id)
) engine=InnoDB default charset=utf8mb4 collate=utf8mb4_0900_ai_ci;
