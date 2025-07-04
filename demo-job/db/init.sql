CREATE TABLE `oms_order`
(
    `id`                  bigint         NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`            varchar(64)    NOT NULL COMMENT '系统订单号',
    `platform_order_no`   varchar(64) COMMENT '平台订单号',
    `platform_type`       tinyint        NOT NULL COMMENT '平台类型:1-Amazon,2-Shopee,3-eBay,4-手工订单',
    `order_status`        tinyint        NOT NULL COMMENT '订单状态:1-待处理,2-已确认,3-已发货,4-已完成,5-已取消',
    `order_type`          tinyint COMMENT '订单类型:1-普通订单,2-预售订单,3-换货订单',
    `order_time`          datetime       NOT NULL COMMENT '下单时间',
    `pay_time`            datetime COMMENT '支付时间',
    `pay_method`          tinyint COMMENT '支付方式',
    `pay_amount`          decimal(12, 2) NOT NULL COMMENT '订单金额',
    `currency`            varchar(10) COMMENT '币种',
    `customer_id`         bigint COMMENT '客户ID',
    `customer_name`       varchar(100) COMMENT '客户姓名',
    `customer_phone`      varchar(50) COMMENT '客户电话',
    `customer_email`      varchar(100) COMMENT '客户邮箱',
    `shipping_address_id` bigint COMMENT '收货地址ID',
    `shipping_method`     tinyint COMMENT '配送方式',
    `shipping_fee`        decimal(10, 2) COMMENT '运费',
    `tax_amount`          decimal(10, 2) COMMENT '税费',
    `discount_amount`     decimal(10, 2) COMMENT '优惠金额',
    `total_amount`        decimal(10, 2) NOT NULL COMMENT '应付总额',
    `remark`              varchar(500) COMMENT '订单备注',
    `source_channel`      varchar(50) COMMENT '来源渠道',
    `is_deleted`          tinyint                 DEFAULT 0 COMMENT '是否删除',
    `create_time`         datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_platform_order` (`platform_type`, `platform_order_no`),
    KEY `idx_order_time` (`order_time`),
    KEY `idx_customer` (`customer_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单主表';

CREATE TABLE `oms_order_item`
(
    `id`              bigint         NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id`        bigint         NOT NULL COMMENT '订单ID',
    `order_no`        varchar(64)    NOT NULL COMMENT '订单编号',
    `sku_code`        varchar(64)    NOT NULL COMMENT 'SKU编码',
    `product_name`    varchar(200)   NOT NULL COMMENT '商品名称',
    `product_image`   varchar(255) COMMENT '商品图片',
    `quantity`        int            NOT NULL COMMENT '数量',
    `original_price`  decimal(10, 2) COMMENT '原价',
    `actual_price`    decimal(10, 2) NOT NULL COMMENT '实际售价',
    `discount_amount` decimal(10, 2) COMMENT '优惠金额',
    `tax_amount`      decimal(10, 2) COMMENT '税费',
    `weight`          decimal(10, 3) COMMENT '重量(kg)',
    `volume`          decimal(10, 3) COMMENT '体积(m³)',
    `specifications`  json COMMENT '规格属性',
    `is_gift`         tinyint                 DEFAULT 0 COMMENT '是否赠品',
    `refund_status`   tinyint                 DEFAULT 0 COMMENT '退款状态',
    `refund_amount`   decimal(10, 2) COMMENT '退款金额',
    `refund_quantity` int COMMENT '退款数量',
    `create_time`     datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_sku` (`sku_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单商品明细表';

CREATE TABLE `oms_order_operation_log`
(
    `id`                bigint      NOT NULL AUTO_INCREMENT,
    `order_id`          bigint      NOT NULL,
    `order_no`          varchar(64) NOT NULL,
    `operator`          varchar(50) NOT NULL COMMENT '操作人',
    `operation_type`    tinyint     NOT NULL COMMENT '操作类型',
    `operation_content` varchar(500) COMMENT '操作内容',
    `operation_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `ip_address`        varchar(50) COMMENT 'IP地址',
    `remark`            varchar(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_operation_time` (`operation_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='订单操作日志表';

CREATE TABLE `wms_warehouse`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT,
    `warehouse_code` varchar(50)  NOT NULL COMMENT '仓库编码',
    `warehouse_name` varchar(100) NOT NULL COMMENT '仓库名称',
    `warehouse_type` tinyint      NOT NULL COMMENT '仓库类型:1-国内仓,2-海外仓,3-保税仓',
    `country_code`   varchar(10) COMMENT '国家代码',
    `province`       varchar(50) COMMENT '省/州',
    `city`           varchar(50) COMMENT '城市',
    `address`        varchar(200) COMMENT '详细地址',
    `contact_person` varchar(50) COMMENT '联系人',
    `contact_phone`  varchar(50) COMMENT '联系电话',
    `status`         tinyint               DEFAULT 1 COMMENT '状态:0-禁用,1-启用',
    `is_default`     tinyint               DEFAULT 0 COMMENT '是否默认仓库',
    `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_warehouse_code` (`warehouse_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='仓库主表';

CREATE TABLE `wms_inventory`
(
    `id`              bigint      NOT NULL AUTO_INCREMENT,
    `sku_code`        varchar(64) NOT NULL COMMENT 'SKU编码',
    `warehouse_id`    bigint      NOT NULL COMMENT '仓库ID',
    `warehouse_code`  varchar(50) NOT NULL COMMENT '仓库编码',
    `available_qty`   int         NOT NULL DEFAULT 0 COMMENT '可用库存',
    `allocated_qty`   int         NOT NULL DEFAULT 0 COMMENT '已分配库存',
    `frozen_qty`      int         NOT NULL DEFAULT 0 COMMENT '冻结库存',
    `damaged_qty`     int         NOT NULL DEFAULT 0 COMMENT '残次品库存',
    `in_transit_qty`  int         NOT NULL DEFAULT 0 COMMENT '在途库存',
    `safety_stock`    int                  DEFAULT 0 COMMENT '安全库存',
    `last_check_time` datetime COMMENT '最后盘点时间',
    `create_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sku_warehouse` (`sku_code`, `warehouse_id`),
    KEY `idx_warehouse` (`warehouse_id`),
    KEY `idx_sku` (`sku_code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='库存主表';

CREATE TABLE `wms_inbound_order`
(
    `id`                    bigint      NOT NULL AUTO_INCREMENT,
    `inbound_no`            varchar(64) NOT NULL COMMENT '入库单号',
    `warehouse_id`          bigint      NOT NULL COMMENT '仓库ID',
    `warehouse_code`        varchar(50) NOT NULL COMMENT '仓库编码',
    `inbound_type`          tinyint     NOT NULL COMMENT '入库类型:1-采购入库,2-退货入库,3-调拨入库',
    `source_no`             varchar(64) COMMENT '来源单号',
    `supplier_id`           bigint COMMENT '供应商ID',
    `supplier_name`         varchar(100) COMMENT '供应商名称',
    `expected_arrival_time` datetime COMMENT '预计到货时间',
    `actual_arrival_time`   datetime COMMENT '实际到货时间',
    `status`                tinyint     NOT NULL COMMENT '状态:1-待审核,2-已审核,3-部分收货,4-已完成,5-已取消',
    `total_qty`             int         NOT NULL DEFAULT 0 COMMENT '总数量',
    `received_qty`          int         NOT NULL DEFAULT 0 COMMENT '已收货数量',
    `total_sku`             int         NOT NULL DEFAULT 0 COMMENT '总SKU数',
    `remark`                varchar(500) COMMENT '备注',
    `create_by`             varchar(50) COMMENT '创建人',
    `create_time`           datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`           datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_inbound_no` (`inbound_no`),
    KEY `idx_warehouse` (`warehouse_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='入库单表';

CREATE TABLE `wms_outbound_order`
(
    `id`              bigint      NOT NULL AUTO_INCREMENT,
    `outbound_no`     varchar(64) NOT NULL COMMENT '出库单号',
    `warehouse_id`    bigint      NOT NULL COMMENT '仓库ID',
    `warehouse_code`  varchar(50) NOT NULL COMMENT '仓库编码',
    `order_id`        bigint COMMENT '关联订单ID',
    `order_no`        varchar(64) COMMENT '关联订单号',
    `outbound_type`   tinyint     NOT NULL COMMENT '出库类型:1-销售出库,2-调拨出库,3-退货出库',
    `status`          tinyint     NOT NULL COMMENT '状态:1-待处理,2-拣货中,3-已打包,4-已发货,5-已完成,6-已取消',
    `shipping_method` tinyint COMMENT '配送方式',
    `tracking_no`     varchar(100) COMMENT '物流单号',
    `carrier_code`    varchar(50) COMMENT '物流公司编码',
    `total_qty`       int         NOT NULL DEFAULT 0 COMMENT '总数量',
    `picked_qty`      int         NOT NULL DEFAULT 0 COMMENT '已拣货数量',
    `packed_qty`      int         NOT NULL DEFAULT 0 COMMENT '已打包数量',
    `shipped_qty`     int         NOT NULL DEFAULT 0 COMMENT '已发货数量',
    `total_sku`       int         NOT NULL DEFAULT 0 COMMENT '总SKU数',
    `remark`          varchar(500) COMMENT '备注',
    `create_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_outbound_no` (`outbound_no`),
    KEY `idx_order` (`order_id`),
    KEY `idx_warehouse` (`warehouse_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='出库单表';

CREATE TABLE `sys_platform_sync_log`
(
    `id`            bigint   NOT NULL AUTO_INCREMENT,
    `platform_type` tinyint  NOT NULL COMMENT '平台类型',
    `sync_type`     tinyint  NOT NULL COMMENT '同步类型:1-订单,2-库存,3-商品',
    `start_time`    datetime NOT NULL COMMENT '开始时间',
    `end_time`      datetime COMMENT '结束时间',
    `status`        tinyint  NOT NULL COMMENT '状态:1-进行中,2-成功,3-失败',
    `total_count`   int               DEFAULT 0 COMMENT '总记录数',
    `success_count` int               DEFAULT 0 COMMENT '成功数',
    `fail_count`    int               DEFAULT 0 COMMENT '失败数',
    `error_msg`     text COMMENT '错误信息',
    `operator`      varchar(50) COMMENT '操作人',
    `create_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_platform` (`platform_type`),
    KEY `idx_time` (`start_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='平台同步记录表';

CREATE TABLE `sys_config`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `config_key`   varchar(100) NOT NULL COMMENT '配置键',
    `config_value` text COMMENT '配置值',
    `config_desc`  varchar(200) COMMENT '配置描述',
    `is_system`    tinyint               DEFAULT 0 COMMENT '是否系统配置',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='系统配置表';

CREATE TABLE `t_api_auth_token`
(
    `id`                  bigint  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `platform_type`       tinyint NOT NULL COMMENT '1-Shopee, 2-Amazon...',
    `auth_type`           tinyint NOT NULL DEFAULT 1 COMMENT '授权类型：1-ACCESS_TOKEN',
    `auth_expire_time`    bigint COMMENT '授权过期时间',
    `auth_source`         tinyint          DEFAULT 1 COMMENT '授权来源，1 手动',
    `access_token`        varchar(512) COMMENT '访问令牌',
    `refresh_token`       varchar(512) COMMENT '刷新令牌',
    `expire_time`         bigint COMMENT 'access_token过期时间戳',
    `refresh_expire_time` bigint COMMENT 'refresh_token过期时间戳',
    `next_refresh_time`   bigint COMMENT '下一次刷新时间',
    `last_refresh_time`   bigint COMMENT '上一次刷新时间',
    `merchant_id`         varchar(64) COMMENT '商户ID',
    `shop_id`             varchar(64) COMMENT '店铺ID',
    `status`              tinyint          DEFAULT 1 COMMENT '状态：1-有效 0-无效',
    `scope`               varchar(255) COMMENT '权限范围，逗号分隔',
    `ip_whitelist`        varchar(255) COMMENT 'IP白名单，逗号分隔',
    `platform_ext`        text             DEFAULT NULL COMMENT '如Amazon的seller_id, eBay的site_id等',
    `create_time`         datetime         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         datetime         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_access_token` (`access_token`),
    KEY `idx_shop_merchant` (`shop_id`, `merchant_id`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='API 访问令牌表';