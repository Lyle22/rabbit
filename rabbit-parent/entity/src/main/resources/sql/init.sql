USE `rabbit`;


CREATE TABLE `form_design`
(
    `id`                     varchar(64)  NOT NULL,
    `name`                   varchar(255)          DEFAULT NULL,
    `table_name`             varchar(255)          DEFAULT NULL,
    `publish_status`         varchar(64)           DEFAULT NULL,
    `permission`             varchar(1024)         DEFAULT NULL,
    `info_json`              json                  DEFAULT NULL,
    `form_result`            json                  DEFAULT NULL,
    `created_by`             varchar(255)          DEFAULT NULL,
    `created_date`           datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_by`            varchar(255) NOT NULL,
    `modified_date`          datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `enable`                 bit(1)                DEFAULT b'0',
    `process_definition_key` varchar(512)          DEFAULT NULL,
    `preview_style`          mediumtext,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='Form Designer table';

CREATE TABLE `form_design_draft`
(
    `id`                     varchar(64) NOT NULL,
    `name`                   varchar(255)  DEFAULT NULL,
    `permission`             varchar(1024) DEFAULT NULL,
    `info_json`              json          DEFAULT NULL,
    `form_result`            json          DEFAULT NULL,
    `process_definition_key` varchar(512)  DEFAULT NULL,
    `workflow_approval`      varchar(512)  DEFAULT NULL,
    `preview_style`          mediumtext,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='Form Designer draft';

CREATE TABLE `form_info`
(
    `id`                varchar(64)  NOT NULL,
    `biz_id`            varchar(256) NOT NULL COMMENT 'business id',
    `label`             varchar(255) NOT NULL COMMENT 'form label',
    `table_name`        varchar(255) NOT NULL COMMENT 'database table name',
    `table_name_prefix` varchar(32)           DEFAULT NULL COMMENT 'prefix of database table name',
    `status`            char(1)               DEFAULT 'A',
    `created_by`        varchar(255) NOT NULL,
    `created_date`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_by`       varchar(255) NOT NULL,
    `modified_date`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='case information table';

CREATE TABLE `form_field_mapping`
(
    `id`           varchar(64)  NOT NULL,
    `form_info_id` varchar(256) NOT NULL COMMENT 'id of form info',
    `column_name`  varchar(255) NOT NULL COMMENT 'column name of table',
    `field_name`   varchar(255) NOT NULL COMMENT 'form field name',
    `data_type`    varchar(64) DEFAULT NULL,
    `status`       char(1)     DEFAULT 'A',
    `required_`    bit(1)      DEFAULT false,
    `unique_`      bit(1)      DEFAULT false,
    `primary_key_` bit(1)      DEFAULT false,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='form field mapping';


CREATE TABLE `cmmn_case_table` (
                                   `id` varchar(64) NOT NULL,
                                   `case_type_id` varchar(64) NOT NULL,
                                   `label` varchar(255) NOT NULL,
                                   `table_name` varchar(255) DEFAULT NULL,
                                   `status` char(1) DEFAULT 'A',
                                   `created_by` varchar(255) NOT NULL,
                                   `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `modified_by` varchar(255) NOT NULL,
                                   `modified_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='case table';

CREATE TABLE `cmmn_case_table_relation` (
                                            `id` varchar(255) NOT NULL,
                                            `source_field` varchar(256) DEFAULT NULL,
                                            `source_table_id` varchar(64) DEFAULT NULL,
                                            `join_field` varchar(256) DEFAULT NULL,
                                            `join_table_id` varchar(64) DEFAULT NULL,
                                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='CMMN case table relation';