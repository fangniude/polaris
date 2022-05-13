

CREATE SCHEMA IF NOT EXISTS "metadata";
-- ----------------------------
-- Table structure for entity
-- ----------------------------
DROP TABLE IF EXISTS "metadata"."entity";
CREATE TABLE "metadata"."entity" (
  "id" int8 NOT NULL,
  "namespace" varchar(255) NOT NULL,
  "name" varchar(255) NOT NULL,
  "comment" varchar(255) NOT NULL,
  "attributes" json NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "expired_time" timestamp(6) NOT NULL,
  "term" int8 NOT NULL,
  "version" int8 NOT NULL
)
;
COMMENT ON COLUMN "metadata"."entity"."id" IS 'ID';
COMMENT ON COLUMN "metadata"."entity"."namespace" IS '命名空间';
COMMENT ON COLUMN "metadata"."entity"."name" IS '名称';
COMMENT ON COLUMN "metadata"."entity"."comment" IS '注释';
COMMENT ON COLUMN "metadata"."entity"."attributes" IS '所有属性';
COMMENT ON COLUMN "metadata"."entity"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata"."entity"."expired_time" IS '失效时间';
COMMENT ON COLUMN "metadata"."entity"."term" IS '任期';
COMMENT ON COLUMN "metadata"."entity"."version" IS '版本';
COMMENT ON TABLE "metadata"."entity" IS '实体';

-- ----------------------------
-- Table structure for relation
-- ----------------------------
DROP TABLE IF EXISTS "metadata"."relation";
CREATE TABLE "metadata"."relation" (
  "id" int8 NOT NULL,
  "source_entity_id" int8 NOT NULL,
  "reference_entity_id" int8 NOT NULL,
  "one_to_one" bool NOT NULL,
  "mapping" json NOT NULL,
  "create_time" timestamp(6) NOT NULL,
  "expired_time" timestamp(6) NOT NULL,
  "source_point" varchar(255) NOT NULL,
  "reference_point" varchar(255) NOT NULL
)
;
COMMENT ON COLUMN "metadata"."relation"."id" IS 'ID';
COMMENT ON COLUMN "metadata"."relation"."source_entity_id" IS '源实体ID';
COMMENT ON COLUMN "metadata"."relation"."reference_entity_id" IS '引用实体ID';
COMMENT ON COLUMN "metadata"."relation"."one_to_one" IS '是否为一对一';
COMMENT ON COLUMN "metadata"."relation"."mapping" IS '映射规则';
COMMENT ON COLUMN "metadata"."relation"."create_time" IS '创建时间';
COMMENT ON COLUMN "metadata"."relation"."expired_time" IS '失效时间';
COMMENT ON COLUMN "metadata"."relation"."source_point" IS '源实体坐标';
COMMENT ON COLUMN "metadata"."relation"."reference_point" IS '引用实体坐标';
COMMENT ON TABLE "metadata"."relation" IS '关系';

-- ----------------------------
-- Primary Key structure for table entity
-- ----------------------------
ALTER TABLE "metadata"."entity" ADD CONSTRAINT "entity_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table relation
-- ----------------------------
ALTER TABLE "metadata"."relation" ADD CONSTRAINT "relation_pkey" PRIMARY KEY ("id");
