# Technology Options To Choose

The project already includes Spring Boot 3.x, Spring Security, MyBatis Plus, MySQL, Liquibase, Redis, MinIO, and ELK. These are popular optional additions you can choose later.

## API Docs

- `springdoc-openapi`: popular and lightweight Swagger UI for Spring Boot 3.
- Knife4j: popular in Chinese teams, richer UI around OpenAPI.

Recommended: `springdoc-openapi` unless you prefer Knife4j UI.

## Cache Client

- Spring Data Redis: official Spring integration, already included.
- Redisson: stronger distributed locks, delayed queues, rate limiters.

Recommended: add Redisson if you need distributed locks or scheduled queues.

## Async And Messaging

- RabbitMQ: simple business events and delayed queues.
- Kafka: high throughput event streams and log pipelines.
- RocketMQ: popular in Java enterprise systems in China.

Recommended: RabbitMQ for this blog platform unless you expect heavy event streams.

## Search

- Elasticsearch: aligns with ELK, good for article search.
- OpenSearch: Elasticsearch-compatible open source option.
- Meilisearch: simpler, fast full-text search for small teams.

Recommended: Elasticsearch because ELK is already deployed.

## Object Storage Providers

- MinIO: local and private cloud default, S3-compatible.
- Tencent COS: production option for Tencent Cloud.
- Alibaba OSS: production option for Alibaba Cloud.
- AWS S3: international cloud option.

Recommended: keep MinIO locally, choose COS/OSS/S3 by production cloud.

## Config Center

- Nacos: popular in Java microservice systems in China.
- Spring Cloud Config: official Spring option.
- Kubernetes ConfigMap/Secret: best if deployed on K8s.

Recommended: environment variables for this stage, Nacos only when services grow.

## Observability

- Micrometer + Prometheus + Grafana: standard metrics dashboard.
- OpenTelemetry: distributed tracing standard.
- SkyWalking: popular Java APM, easy service topology.

Recommended: Micrometer + Prometheus + Grafana first, OpenTelemetry when service count grows.

## Permission Model

- RBAC: roles, permissions, menus, and users. Already implemented in schema.
- RBAC + data scope: department/self/custom row-level access. Schema includes `data_scope`.
- ABAC: attribute-based dynamic policies for complex enterprise rules.

Recommended: RBAC + data scope for this project.
