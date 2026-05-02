# AI Blog Backend

AI Blog Backend 是一个基于 **Spring Boot 3.5 + Java 21** 的博客 / 内容管理后台服务，面向 AI 写作、文章管理、审批发布、用户权限、对象存储和日志观测等场景。

项目当前提供：

- 后端 API 服务：Spring Boot 单体应用
- 本地中间件编排：MySQL、Redis、MinIO、Elasticsearch、Logstash、Kibana
- 数据库初始化：Liquibase 自动建表和初始化种子数据
- 后台登录认证：Spring Security + JWT
- 对象存储：MinIO，本地可直接使用
- 日志链路：Logback + Logstash + Elasticsearch + Kibana

> 注意：当前项目包中没有 `Dockerfile`，所以 Docker Compose 主要用于启动中间件容器。后端应用本身默认通过 Maven 在宿主机启动。

---

## 目录结构

```text
ai-blog-backend/
├── docker/
│   ├── docker-compose.local.yml      # 本地开发中间件编排
│   ├── docker-compose.prod.yml       # 生产环境中间件编排
│   └── logstash/pipeline/            # Logstash 管道配置
├── docs/
│   ├── api-examples.http             # API 调用示例
│   └── technology-options.md         # 技术选型说明
├── scripts/
│   ├── windows/                      # Windows 本地启动脚本
│   └── linux/                        # Linux 生产启动脚本
├── src/main/java/com/aiblog/
│   ├── auth/                         # 登录认证
│   ├── common/                       # 通用响应、异常、日志、拦截器
│   ├── config/                       # 安全、CORS、存储、MyBatis 配置
│   ├── content/                      # 文章、作者、专题、标签等内容模块
│   ├── security/                     # JWT、安全用户上下文
│   ├── storage/                      # 对象存储上传与引用
│   └── system/                       # 用户、角色、权限、菜单、当前用户
├── src/main/resources/
│   ├── application.yml               # 默认配置，本地 profile
│   ├── application-prod.yml          # 生产 profile 补充配置
│   ├── db/changelog/                 # Liquibase 建表与种子数据
│   └── logback-spring.xml            # 日志配置
├── .env.example                      # 环境变量示例
├── pom.xml                           # Maven 项目配置
└── README.md
```

---

## 技术栈

| 类型 | 技术 |
|---|---|
| 运行时 | Java 21 |
| Web 框架 | Spring Boot 3.5.0 |
| 安全认证 | Spring Security、JWT |
| ORM / 数据访问 | MyBatis Plus |
| 数据库 | MySQL 8.4 |
| 数据库迁移 | Liquibase |
| 缓存 | Redis 7.4 |
| 对象存储 | MinIO |
| 日志 | Logback、Logstash Logback Encoder |
| 搜索 / 日志观测 | Elasticsearch、Logstash、Kibana |
| 构建工具 | Maven |
| 容器编排 | Docker Compose |

---

## 环境要求

### 本地开发

- JDK 21
- Maven 3.9+
- Docker Desktop / Docker Engine
- Docker Compose v2
- Windows PowerShell 或 Linux Bash

### 推荐端口占用检查

启动前请确认以下端口没有被其他服务占用：

| 端口 | 服务 |
|---:|---|
| 8080 | Spring Boot 后端 |
| 3306 | MySQL |
| 6379 | Redis |
| 9000 | MinIO API |
| 9001 | MinIO Console |
| 9200 | Elasticsearch |
| 5000 | Logstash 输入端口 |
| 9600 | Logstash Monitoring API |
| 5601 | Kibana |

---

## 快速启动：Windows 本地开发

### 1. 进入项目目录

```powershell
cd ai-blog-backend
```

### 2. 复制环境变量文件

```powershell
copy .env.example .env
```

如只是本地开发，可以先使用默认值；如用于生产或公网环境，请务必修改所有密码和 `JWT_SECRET`。

### 3. 启动本地中间件容器

```powershell
.\scripts\windows\up-local.ps1
```

等价命令：

```powershell
docker compose --env-file .env -f docker/docker-compose.local.yml up -d
```

### 4. 查看容器状态

```powershell
docker ps
```

### 5. 启动后端服务

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
mvn spring-boot:run
```

如果你的 JDK 21 安装路径不同，请把 `JAVA_HOME` 改为自己的实际路径。

### 6. 访问后端 API

```text
http://localhost:8080/api
```

健康检查接口：

```text
http://localhost:8080/api/actuator/health
```

---

## 快速启动：Linux / 服务器环境

### 1. 进入项目目录

```bash
cd ai-blog-backend
```

### 2. 复制并修改环境变量

```bash
cp .env.example .env
```

生产环境建议至少修改：

```env
MYSQL_ROOT_PASSWORD=请修改
MYSQL_PASSWORD=请修改
REDIS_PASSWORD=请修改
MINIO_ROOT_USER=请修改
MINIO_ROOT_PASSWORD=请修改
JWT_SECRET=请修改为至少32字节的高强度密钥
```

### 3. 启动生产中间件

```bash
chmod +x scripts/linux/*.sh
./scripts/linux/up-prod.sh
```

等价命令：

```bash
docker compose --env-file .env -f docker/docker-compose.prod.yml up -d
```

### 4. 启动后端服务

当前项目未提供后端应用 Dockerfile。你可以使用 Maven 直接启动：

```bash
mvn spring-boot:run
```

或先打包再运行：

```bash
mvn clean package -DskipTests
java -jar target/ai-blog-backend-0.1.0-SNAPSHOT.jar
```

生产环境建议进一步封装为 systemd 服务或补充 Dockerfile 后容器化部署。

---

## 常用脚本

### Windows 本地脚本

| 脚本 | 作用 |
|---|---|
| `scripts/windows/up-local.ps1` | 启动本地中间件容器 |
| `scripts/windows/down-local.ps1` | 停止并移除本地中间件容器 |
| `scripts/windows/logs-local.ps1` | 查看本地中间件日志 |

示例：

```powershell
.\scripts\windows\logs-local.ps1
```

### Linux 生产脚本

| 脚本 | 作用 |
|---|---|
| `scripts/linux/up-prod.sh` | 启动生产中间件容器 |
| `scripts/linux/down-prod.sh` | 停止并移除生产中间件容器 |
| `scripts/linux/logs-prod.sh` | 查看生产中间件日志 |

示例：

```bash
./scripts/linux/logs-prod.sh
```

---

## 本地容器访问地址与账号密码

以下为 `.env.example` 中的默认值，仅适用于本地开发。

| 服务 | 容器名 | 访问地址 | 端口 | 用户名 | 密码 | 说明 |
|---|---|---|---:|---|---|---|
| MySQL | `ai-blog-mysql` | `localhost:3306` | 3306 | `root` | `ai_blog_root` | MySQL root 账号 |
| MySQL | `ai-blog-mysql` | `localhost:3306` | 3306 | `ai_blog` | `ai_blog_password` | 应用数据库账号 |
| Redis | `ai-blog-redis` | `localhost:6379` | 6379 | 无 | `ai_blog_redis` | 使用 requirepass |
| MinIO API | `ai-blog-minio` | `http://localhost:9000` | 9000 | `ai_blog_minio` | `ai_blog_minio_password` | 对象存储 API |
| MinIO Console | `ai-blog-minio` | `http://localhost:9001` | 9001 | `ai_blog_minio` | `ai_blog_minio_password` | 浏览器管理后台 |
| Elasticsearch | `ai-blog-elasticsearch` | `http://localhost:9200` | 9200 | 无 | 无 | 本地关闭安全认证 |
| Logstash | `ai-blog-logstash` | `localhost:5000` | 5000 | 无 | 无 | 日志输入端口 |
| Logstash API | `ai-blog-logstash` | `http://localhost:9600` | 9600 | 无 | 无 | Logstash Monitoring API |
| Kibana | `ai-blog-kibana` | `http://localhost:5601` | 5601 | 无 | 无 | 依赖 Elasticsearch |

> Windows 本地浏览器访问 MinIO 控制台请使用 `http://localhost:9001`，不要使用 `http://minio:9001`。`minio:9001` 是 Docker Compose 内部网络地址，主要给同网络容器访问。

---

## 后台默认账号

Liquibase 种子数据会初始化一个超级管理员账号：

| 类型 | 值 |
|---|---|
| 用户名 | `admin` |
| 密码 | `huangcy125643` |
| 邮箱 | `admin@neuroblog.local` |
| 角色 | `SUPER_ADMIN` |

> 当前种子密码使用 `{noop}` 明文编码方式，仅适合本地初始化。生产环境请改为 BCrypt 哈希并强制修改默认密码。

---

## 环境变量说明

| 变量 | 默认值 | 说明 |
|---|---|---|
| `MYSQL_ROOT_PASSWORD` | `ai_blog_root` | MySQL root 密码 |
| `MYSQL_DATABASE` | `ai_blog` | 应用数据库名 |
| `MYSQL_USERNAME` | `ai_blog` | 应用数据库用户名 |
| `MYSQL_PASSWORD` | `ai_blog_password` | 应用数据库密码 |
| `REDIS_PASSWORD` | `ai_blog_redis` | Redis 密码 |
| `MINIO_ROOT_USER` | `ai_blog_minio` | MinIO 管理员用户名 |
| `MINIO_ROOT_PASSWORD` | `ai_blog_minio_password` | MinIO 管理员密码 |
| `MINIO_BUCKET` | `ai-blog` | 默认存储桶 |
| `JWT_SECRET` | `replace-this-with-at-least-32-bytes-secret-key` | JWT 签名密钥 |
| `ELASTIC_PASSWORD` | `ai_blog_elastic` | 预留变量，当前本地 ES 未启用认证 |
| `KIBANA_PASSWORD` | `ai_blog_kibana` | 预留变量，当前 Kibana 未启用认证 |

Spring Boot 运行时还会读取以下变量：

| 变量 | 默认值 | 说明 |
|---|---|---|
| `MYSQL_HOST` | `127.0.0.1` | MySQL 主机 |
| `MYSQL_PORT` | `3306` | MySQL 端口 |
| `REDIS_HOST` | `127.0.0.1` | Redis 主机 |
| `REDIS_PORT` | `6379` | Redis 端口 |
| `REDIS_DATABASE` | `0` | Redis 数据库编号 |
| `JWT_ACCESS_TOKEN_MINUTES` | `120` | 访问令牌有效期，单位分钟 |
| `STORAGE_ACTIVE_PROVIDER` | `minio` | 当前启用的对象存储提供商 |
| `MINIO_ENDPOINT` | `http://127.0.0.1:9000` | 后端访问 MinIO 的地址 |
| `MINIO_PUBLIC_ENDPOINT` | `http://127.0.0.1:9000` | 对外访问 MinIO 文件的地址 |
| `FRONTEND_ORIGIN` | `https://example.com` | 生产环境允许跨域的前端地址 |

---

## Docker Compose 说明

### 本地开发 compose

文件：

```text
docker/docker-compose.local.yml
```

本地 compose 会把中间件端口映射到宿主机，因此可以直接通过 `localhost` 访问。

### 生产环境 compose

文件：

```text
docker/docker-compose.prod.yml
```

生产环境 compose 更偏向内部网络访问。通常只有 Kibana 会暴露到宿主机端口；MySQL、Redis、MinIO、Elasticsearch、Logstash 主要给同一 Docker 网络内的服务访问。

容器内部地址示例：

| 服务 | Docker 内部地址 |
|---|---|
| MySQL | `mysql:3306` |
| Redis | `redis:6379` |
| MinIO API | `http://minio:9000` |
| MinIO Console | `http://minio:9001` |
| Elasticsearch | `http://elasticsearch:9200` |
| Logstash | `logstash:5000` / `http://logstash:9600` |
| Kibana | `http://kibana:5601` |

---

## 数据库初始化

项目使用 Liquibase 自动执行数据库初始化。

主入口：

```text
src/main/resources/db/changelog/db.changelog-master.yaml
```

包含：

| 文件 | 作用 |
|---|---|
| `001-schema.sql` | 创建系统表、内容表、存储表、日志表等 |
| `002-seed.sql` | 初始化管理员、角色、权限、菜单、系统配置等数据 |

只要后端成功连接 MySQL，启动时会自动执行 Liquibase。

---

## 主要 API

后端统一上下文路径为：

```text
/api
```

### 认证

| 方法 | 地址 | 说明 |
|---|---|---|
| `POST` | `/api/auth/login` | 用户登录，返回 JWT |

### 当前用户

| 方法 | 地址 | 说明 |
|---|---|---|
| `GET` | `/api/admin/current` | 获取当前登录用户信息 |
| `PUT` | `/api/admin/current/password` | 修改当前用户密码 |

### 内容管理

| 方法 | 地址 | 说明 |
|---|---|---|
| `GET` | `/api/public/articles` | 公开文章列表 |
| `GET` | `/api/public/articles/{id}` | 公开文章详情 |
| `GET` | `/api/public/authors` | 公开作者列表 |
| `GET` | `/api/public/topics` | 公开专题列表 |
| `GET` | `/api/admin/articles` | 后台文章列表 |
| `GET` | `/api/admin/approvals/articles` | 审批文章列表 |
| `POST` | `/api/admin/articles` | 新建文章 |
| `PUT` | `/api/admin/articles/{id}` | 更新文章 |
| `DELETE` | `/api/admin/articles/{id}` | 删除文章 |
| `POST` | `/api/admin/articles/generate` | 生成文章草稿 |

### 存储

| 方法 | 地址 | 说明 |
|---|---|---|
| `POST` | `/api/admin/storage/objects` | 上传对象文件 |

### 后台查询

| 方法 | 地址 | 说明 |
|---|---|---|
| `GET` | `/api/admin/dashboard/overview` | 运营概览 |
| `GET` | `/api/admin/studio/options` | AI 写作工作台选项 |
| `GET` | `/api/admin/users` | 用户列表 |
| `GET` | `/api/admin/roles` | 角色列表 |
| `GET` | `/api/admin/permissions` | 权限列表 |
| `GET` | `/api/admin/menus` | 菜单列表 |

更多示例可参考：

```text
docs/api-examples.http
```

---

## 常见操作

### 查看所有容器

```bash
docker ps
```

### 查看本地中间件日志

```powershell
.\scripts\windows\logs-local.ps1
```

或：

```bash
docker compose --env-file .env -f docker/docker-compose.local.yml logs -f --tail=200
```

### 停止本地中间件

```powershell
.\scripts\windows\down-local.ps1
```

或：

```bash
docker compose --env-file .env -f docker/docker-compose.local.yml down
```

### 清理本地中间件数据卷

谨慎执行，该命令会删除 MySQL、Redis、MinIO、Elasticsearch 的本地数据卷：

```bash
docker compose --env-file .env -f docker/docker-compose.local.yml down -v
```

---

## 常见问题

### 1. 浏览器能不能访问 `http://minio:9001`？

通常不能。Windows 或宿主机浏览器应访问：

```text
http://localhost:9001
```

`http://minio:9001` 是 Docker Compose 网络里的服务名地址，主要给同一 Docker 网络中的容器使用。

### 2. 后端启动时报 MySQL 连接失败怎么办？

先确认 MySQL 容器已启动：

```bash
docker ps
```

再确认端口是否正常：

```bash
docker logs ai-blog-mysql
```

本地默认连接信息：

```text
host: 127.0.0.1
port: 3306
database: ai_blog
username: ai_blog
password: ai_blog_password
```

### 3. Redis 报认证失败怎么办？

确认 `.env` 中的 `REDIS_PASSWORD` 与 `application.yml` 中读取到的值一致。本地默认密码：

```text
ai_blog_redis
```

### 4. Kibana 打开后没有登录页正常吗？

正常。当前本地 Elasticsearch 配置为：

```yaml
xpack.security.enabled: "false"
```

因此本地 Kibana 默认不需要登录。

### 5. 为什么 Docker Compose 没有启动后端应用？

因为当前项目包中没有后端应用的 `Dockerfile`，Compose 文件只编排了中间件。后端应用需要通过 Maven 启动，或者你可以自行补充 Dockerfile 后再加入 compose。

---

## 生产安全建议

上线前建议完成以下事项：

- 修改 `.env` 中所有默认密码
- 修改 `JWT_SECRET` 为高强度随机密钥
- 修改默认管理员密码
- 将种子数据中的 `{noop}` 密码替换为 BCrypt 哈希
- 限制 MySQL、Redis、MinIO、Elasticsearch 等端口的公网访问
- 为 MinIO、Kibana、后端 API 配置 HTTPS
- 配置合理的 CORS 白名单
- 使用 systemd、Supervisor 或容器编排平台托管后端进程
- 为数据库和对象存储配置定期备份

---

## 开发备注

- 默认服务端口：`8080`
- 默认 API 前缀：`/api`
- 默认激活 profile：`local`
- 默认跨域前端地址：`http://127.0.0.1:5173`、`http://localhost:5173`
- 默认对象存储 provider：`minio`
- 默认 MinIO bucket：`ai-blog`

