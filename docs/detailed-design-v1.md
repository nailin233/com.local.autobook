# 安卓自动记账 APP 详细设计文档 v1

## 1. 文档目的

本文档基于《安卓自动记账 APP 需求文档 v1》展开，目标是把需求转化为可执行的工程设计。后续开发应以本文档作为第一版实现边界，不在未确认的情况下扩展到云同步、账号系统、iOS、复杂预算、多账本协作或公开应用商店上架适配。

第一版产品定位为自用/内测 Android 原生 APP。核心能力是通过无障碍服务识别微信、支付宝支付完成页面，结合通知监听做辅助校验，生成待确认流水；用户确认后写入本地账本。

## 2. 默认决策

需求文档中的待确认项在 v1 中采用以下默认决策：

| 项目 | v1 决策 |
| --- | --- |
| APP 名称 | 暂定为「随手自记」 |
| 最低 Android 版本 | Android 10，minSdk 29 |
| 技术路线 | Kotlin + Jetpack Compose + Room + DataStore |
| 备份格式 | JSON 备份文件 + CSV 导出 |
| 月度统计页 | v1 不做独立统计页，只做列表筛选 |
| 应用锁 | v1 不做密码、指纹或应用锁 |
| 深色模式 | 跟随系统主题，使用 Compose Material 3 基础主题 |
| 账单文件导入 | v1 不导入微信/支付宝账单文件 |
| 发布目标 | 自用/内测 APK，不优先处理 Google Play 审核 |

这些决策不是永久产品方向，只是为了让第一版可以闭环交付。

## 3. 总体架构

APP 采用单体原生 Android 架构，按功能分层组织：

```text
UI 层
- Compose 页面
- ViewModel
- 权限引导、账本列表、流水编辑、候选确认、备份恢复

领域层
- 账本用例
- 候选流水用例
- 支付识别用例
- 备份恢复用例

数据层
- Room 数据库
- DataStore 设置
- JSON/CSV 文件读写

系统集成层
- AccessibilityService
- NotificationListenerService
- Android 权限跳转
- 前台 Activity 唤起候选确认页
```

设计原则：

- 系统服务只负责采集和提交候选识别结果，不直接写入正式账本。
- 候选流水必须经过用户确认才会成为正式流水。
- 识别层不保存完整屏幕文本，只生成结构化字段和简短摘要。
- 微信、支付宝解析器彼此隔离，输出统一领域模型。
- 数据默认保存在本机，不做网络请求。

## 4. 模块设计

### 4.1 app 模块

第一版只建立一个 Android app 模块，避免过早拆分多模块。包结构建议如下：

```text
com.local.autobook
├── MainActivity.kt
├── service
│   ├── PaymentAccessibilityService.kt
│   └── PaymentNotificationListenerService.kt
├── detection
│   ├── DetectedPayment.kt
│   ├── PaymentDetector.kt
│   ├── WeChatPaymentDetector.kt
│   ├── AlipayPaymentDetector.kt
│   └── PaymentDeduplicator.kt
├── data
│   ├── AppDatabase.kt
│   ├── TransactionDao.kt
│   ├── PendingTransactionDao.kt
│   ├── MerchantRuleDao.kt
│   └── entity
├── repository
│   ├── TransactionRepository.kt
│   ├── PendingTransactionRepository.kt
│   └── BackupRepository.kt
├── domain
│   ├── ConfirmPendingTransactionUseCase.kt
│   ├── CreatePendingFromDetectionUseCase.kt
│   ├── ExportCsvUseCase.kt
│   └── RestoreBackupUseCase.kt
└── ui
    ├── AppNav.kt
    ├── permission
    ├── ledger
    ├── edit
    ├── pending
    └── backup
```

### 4.2 UI 层职责

UI 层只处理展示、输入和导航，不写识别规则。

主要页面：

1. 权限引导页
   - 展示无障碍服务用途。
   - 展示通知监听用途。
   - 提供跳转系统设置按钮。
   - 提示数据只在本机保存。

2. 账本首页
   - 展示流水列表。
   - 支持按时间、分类、方向筛选。
   - 提供手动新增入口。
   - 提供备份/导出入口。

3. 候选确认页
   - 展示待确认流水。
   - 支持修改金额、方向、分类、商户、时间、备注。
   - 支持确认入账和取消。

4. 流水编辑页
   - 新增或编辑正式流水。
   - 删除已有流水。

5. 备份导出页
   - 导出 CSV。
   - 导出 JSON 备份。
   - 从 JSON 备份恢复。

### 4.3 系统服务职责

无障碍服务：

- 监听微信、支付宝相关窗口变化事件。
- 读取当前窗口节点文本。
- 将文本交给对应支付解析器。
- 解析成功后提交 `DetectedPayment` 给候选流水用例。
- 不直接保存正式流水。

通知监听服务：

- 监听微信、支付宝通知。
- 提取标题、正文、包名、通知时间。
- 将通知文本交给解析器。
- 作为补充识别来源参与去重。

服务启动后不需要常驻前台通知。若后续系统限制导致后台稳定性不足，再评估是否增加前台服务，但 v1 不主动增加。

## 5. 数据模型

### 5.1 TransactionEntity

正式流水表。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | Long | 主键，自增 |
| amountCents | Long | 金额，单位分，避免浮点误差 |
| direction | String | INCOME / EXPENSE |
| category | String | 分类名称 |
| merchant | String | 商户或收款方 |
| paymentSource | String | WECHAT / ALIPAY / MANUAL |
| detectedFrom | String | ACCESSIBILITY / NOTIFICATION / MANUAL |
| occurredAt | Long | 发生时间，epoch millis |
| note | String | 备注 |
| createdAt | Long | 创建时间 |
| updatedAt | Long | 更新时间 |

索引：

- `occurredAt DESC`
- `category`
- `direction`
- `paymentSource`

### 5.2 PendingTransactionEntity

待确认流水表。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | Long | 主键，自增 |
| amountCents | Long | 金额，单位分 |
| direction | String | INCOME / EXPENSE / UNKNOWN |
| category | String | 默认分类 |
| merchant | String | 商户或收款方 |
| paymentSource | String | WECHAT / ALIPAY |
| detectedFrom | String | ACCESSIBILITY / NOTIFICATION |
| occurredAt | Long | 识别到的发生时间 |
| confidence | String | CLEAR / UNCLEAR |
| rawSummary | String | 简短摘要，不保存完整屏幕文本 |
| dedupeKey | String | 去重键 |
| createdAt | Long | 创建时间 |

候选记录保留策略：

- 用户确认后删除对应候选。
- 用户取消后删除对应候选。
- 超过 7 天未处理的候选可在启动时清理。

### 5.3 MerchantRuleEntity

商户关键词分类规则。

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | Long | 主键，自增 |
| keyword | String | 商户关键词 |
| category | String | 默认分类 |
| createdAt | Long | 创建时间 |

v1 可先内置少量规则，不提供复杂规则管理页面。

### 5.4 Settings

使用 DataStore 保存轻量设置：

- `hasSeenPermissionGuide`
- `lastBackupPath`，可选
- `defaultFilterRange`，可选

## 6. 支付识别设计

### 6.1 统一输出模型

`DetectedPayment` 字段：

| 字段 | 说明 |
| --- | --- |
| amountCents | 金额，单位分 |
| direction | INCOME / EXPENSE / UNKNOWN |
| merchant | 商户或收款方 |
| paymentSource | WECHAT / ALIPAY |
| detectedFrom | ACCESSIBILITY / NOTIFICATION |
| occurredAt | 发生时间 |
| confidence | CLEAR / UNCLEAR |
| rawSummary | 简短摘要 |

解析器只有在能识别金额时才输出结果；金额缺失时返回空。

### 6.2 微信识别

无障碍窗口识别目标：

- 支付成功页面。
- 收款到账页面。
- 退款成功页面。

通知识别目标：

- 微信支付通知。
- 微信收款到账通知。
- 微信退款通知。

初版规则：

- 文本包含「支付成功」「付款成功」「已支付」时，方向倾向 EXPENSE。
- 文本包含「收款到账」「已收款」时，方向倾向 INCOME。
- 文本包含「退款」「已退回」时，方向倾向 INCOME，分类默认「退款」。
- 金额使用正则提取 `¥12.34`、`￥12.34`、`12.34元`、`12元`。
- 商户优先从「商户」「收款方」「付款给」「来自」附近文本提取；提取不到则填「微信支付」。

### 6.3 支付宝识别

无障碍窗口识别目标：

- 支付成功页面。
- 收款到账页面。
- 退款成功页面。

通知识别目标：

- 支付宝支付通知。
- 支付宝收款到账通知。
- 支付宝退款通知。

初版规则：

- 文本包含「支付成功」「付款成功」「交易成功」时，方向倾向 EXPENSE。
- 文本包含「收款到账」「收钱到账」时，方向倾向 INCOME。
- 文本包含「退款成功」「退款到账」时，方向倾向 INCOME，分类默认「退款」。
- 金额提取规则与微信一致。
- 商户优先从「商家」「收款方」「付款给」「交易对象」附近文本提取；提取不到则填「支付宝」。

### 6.4 置信度规则

`CLEAR` 条件：

- 金额存在。
- 支付来源明确。
- 方向明确。
- 文本含支付完成、收款到账或退款完成类关键词。

`UNCLEAR` 条件：

- 金额存在，但方向不明确。
- 金额存在，但页面语义不是明确完成态。
- 商户缺失不单独导致 UNCLEAR。

UNCLEAR 候选可以展示给用户，但确认页必须明显提示「请核对」。

### 6.5 去重规则

去重键由以下字段组成：

```text
paymentSource + amountCents + direction + normalizedMerchant + minuteBucket(occurredAt)
```

规则：

- 2 分钟内相同去重键只保留一条候选。
- 无障碍和通知同时识别到同一笔时，优先保留无障碍来源。
- 若已有正式流水与候选在 2 分钟内高度相同，不再创建候选。

## 7. 业务流程

### 7.1 自动识别到确认

```text
系统事件
-> AccessibilityService 或 NotificationListenerService
-> 支付解析器
-> DetectedPayment
-> 去重检查
-> 创建 PendingTransaction
-> 唤起 MainActivity 候选确认页
-> 用户确认
-> 写入 Transaction
-> 删除 PendingTransaction
```

如果 APP 在后台：

- 创建候选后启动 Activity 展示确认页。
- 若系统限制后台启动，则发送本地通知，用户点击后进入确认页。

### 7.2 手动记账

```text
首页点击新增
-> 流水编辑页
-> 用户输入金额、方向、分类、商户、时间、备注
-> 保存 Transaction，paymentSource=MANUAL，detectedFrom=MANUAL
```

### 7.3 取消候选

```text
候选确认页点击取消
-> 删除 PendingTransaction
-> 返回账本首页
```

取消不会创建正式流水。

### 7.4 编辑正式流水

```text
首页点击流水
-> 流水编辑页
-> 修改字段
-> 保存并更新 updatedAt
```

删除流水需要二次确认。

## 8. 备份与导出设计

### 8.1 CSV 导出

CSV 文件字段：

```text
发生时间,类型,金额,分类,商户,支付来源,识别来源,备注,创建时间,更新时间
```

要求：

- 使用 UTF-8 with BOM，方便 Excel 打开中文。
- 金额导出为元，保留两位小数。
- 文件名格式：`autobook-transactions-yyyyMMdd-HHmmss.csv`。

### 8.2 JSON 备份

备份文件结构：

```json
{
  "schemaVersion": 1,
  "exportedAt": 1710000000000,
  "transactions": [],
  "merchantRules": []
}
```

要求：

- 文件名格式：`autobook-backup-yyyyMMdd-HHmmss.json`。
- 导入时校验 `schemaVersion`。
- v1 只支持 `schemaVersion=1`。
- 导入采用追加策略，使用近似去重避免重复导入。

### 8.3 恢复策略

导入备份时：

- 解析失败则提示文件无效。
- schema 不支持则提示版本不兼容。
- 对正式流水按 `amountCents + direction + merchant + occurredAt` 去重。
- 成功后提示新增条数和跳过重复条数。

## 9. 权限与隐私设计

### 9.1 Android Manifest 权限

需要声明：

- 无障碍服务配置。
- 通知监听服务配置。
- Android 13 及以上通知权限，若需要发送候选通知。

不申请：

- 通讯录权限。
- 精确位置权限。
- 相册权限。
- 麦克风权限。
- 摄像头权限。

### 9.2 无障碍服务说明

无障碍服务配置说明必须明确：

- 服务用途是辅助用户记录自己的支付流水。
- 只处理微信和支付宝支付相关页面。
- 不执行点击、滑动、输入等替用户操作。
- 不保存完整屏幕文本。

### 9.3 本地数据原则

- 数据库位于 APP 私有目录。
- 备份和导出只在用户主动操作时生成。
- 不内置任何上传接口。
- 不接入第三方统计 SDK。

## 10. UI 设计细节

### 10.1 导航结构

```text
PermissionGuideScreen
LedgerListScreen
PendingConfirmScreen
TransactionEditScreen
BackupScreen
```

启动逻辑：

- 首次启动进入权限引导页。
- 已看过引导后进入账本首页。
- 如果启动 Intent 携带 pendingId，直接进入候选确认页。

### 10.2 账本首页

顶部：

- 当前筛选月份或全部。
- 收入合计、支出合计、结余。

列表项：

- 左侧分类。
- 中间商户和时间。
- 右侧金额，收入和支出用不同颜色区分。

操作：

- 悬浮按钮新增手动流水。
- 顶部筛选入口。
- 菜单进入备份导出页。

### 10.3 候选确认页

字段默认值来自识别结果：

- 金额必填。
- 类型必选。
- 分类必选。
- 商户可为空，但保存时空值转为「未填写」。
- 时间默认识别时间。
- 备注可为空。

如果 `confidence=UNCLEAR`，顶部显示核对提示。

### 10.4 权限引导页

展示三段内容：

1. 为什么需要无障碍服务。
2. 为什么需要通知监听。
3. 隐私承诺。

按钮：

- 开启无障碍服务。
- 开启通知监听。
- 我已了解，进入账本。

## 11. 错误处理

| 场景 | 处理 |
| --- | --- |
| 无障碍权限未开启 | 权限页显示未开启状态，不自动识别 |
| 通知监听未开启 | 权限页显示未开启状态，仍允许无障碍识别 |
| 金额解析失败 | 不创建候选 |
| 方向不明确 | 创建 UNCLEAR 候选，要求用户核对 |
| 重复支付事件 | 去重后不重复弹窗 |
| 数据库写入失败 | 提示保存失败，保留候选 |
| CSV 导出失败 | 显示失败原因，允许重试 |
| JSON 导入失败 | 提示文件无效或版本不兼容 |
| 后台无法启动确认页 | 发送本地通知引导用户打开 |

## 12. 测试设计

### 12.1 单元测试

支付解析：

- 微信支付成功文本能识别为支出。
- 微信收款到账文本能识别为收入。
- 微信退款文本能识别为收入且分类为退款。
- 支付宝支付成功文本能识别为支出。
- 支付宝收款到账文本能识别为收入。
- 支付宝退款文本能识别为收入且分类为退款。
- 金额格式 `¥12.34`、`￥12.34`、`12.34元`、`12元` 均可识别。
- 无金额文本不生成识别结果。

去重：

- 2 分钟内相同支付不重复生成候选。
- 无障碍和通知同时命中时优先保留无障碍。
- 已存在正式流水时不生成重复候选。

备份：

- CSV 字段顺序正确。
- JSON 备份包含 schemaVersion。
- JSON 导入能跳过重复流水。
- 不支持的 schemaVersion 会失败。

### 12.2 UI 测试

- 权限引导页按钮能跳转设置。
- 账本首页能展示流水。
- 筛选能按分类、方向、时间生效。
- 候选确认页能修改字段并确认入账。
- 候选取消后不入账。
- 手动新增、编辑、删除流水可用。
- 备份导出页能完成导出和导入。

### 12.3 手工验收

在真实设备上验证：

- Android 10、Android 12、Android 14 或以上至少各一台或模拟环境。
- 微信支付完成后出现候选确认。
- 支付宝支付完成后出现候选确认。
- 微信/支付宝收款到账可识别为收入。
- 退款可识别为收入并默认分类为退款。
- 权限关闭后自动识别停止。
- APP 重启后账本数据仍存在。
- CSV 可被 Excel 或 WPS 打开且中文不乱码。
- JSON 备份可恢复数据。

## 13. 实施阶段

### 阶段一：工程骨架和本地账本

目标：完成可运行 APP 和本地记账闭环。

内容：

- 创建 Android 原生工程。
- 接入 Compose、Room、DataStore。
- 实现正式流水表和 DAO。
- 实现账本首页。
- 实现手动新增、编辑、删除流水。
- 实现基础分类。

验收：

- APP 可安装运行。
- 手动记账完整可用。
- 数据重启后仍存在。

### 阶段二：待确认流水

目标：完成候选流水到正式流水的确认流程。

内容：

- 实现 PendingTransaction 表和 DAO。
- 实现候选确认页。
- 实现确认入账和取消。
- 实现去重服务。

验收：

- 测试数据可生成候选。
- 用户确认后正式入账。
- 用户取消后不入账。

### 阶段三：支付识别

目标：接入无障碍服务和通知监听服务。

内容：

- 实现无障碍服务配置和权限引导。
- 实现通知监听服务配置和权限引导。
- 实现微信解析器。
- 实现支付宝解析器。
- 接入识别结果到候选流水。

验收：

- 微信支付完成可生成候选。
- 支付宝支付完成可生成候选。
- 重复事件不会重复弹窗。

### 阶段四：备份导出

目标：完成本地数据可迁移能力。

内容：

- 实现 CSV 导出。
- 实现 JSON 备份导出。
- 实现 JSON 备份恢复。
- 实现导入去重和错误提示。

验收：

- CSV 中文不乱码。
- JSON 备份可恢复。
- 重复导入不会制造大量重复流水。

### 阶段五：稳定性和验收

目标：按需求文档完成第一版验收。

内容：

- 补齐测试。
- 真机验证微信、支付宝场景。
- 优化权限说明和失败提示。
- 整理 README 和使用说明。

验收：

- 需求文档第 7 节验收标准全部通过或有明确记录。

## 14. 风险与应对

| 风险 | 影响 | 应对 |
| --- | --- | --- |
| 微信/支付宝页面文案变化 | 识别失败 | 保守解析，失败不入账；后续按样本文本更新规则 |
| 部分系统限制后台启动 Activity | 候选页不弹出 | 退化为本地通知入口 |
| 无障碍权限被系统关闭 | 自动识别停止 | 权限页显示状态，用户手动恢复 |
| 通知内容被隐藏 | 通知识别失败 | 以无障碍识别为主 |
| 误识别金额 | 误生成候选 | 必须用户确认后入账，降低误记账风险 |
| 备份文件损坏 | 无法恢复 | 导入前校验 JSON 和 schemaVersion |
| Google Play 审核限制 | 无法上架 | v1 不以 Google Play 为目标 |

## 15. 开发约束

- 不接入网络上传功能。
- 不保存完整屏幕文本。
- 不让无障碍服务执行点击、输入、滑动等操作。
- 不在用户未确认时写入正式流水。
- 不为 v1 引入云同步、账号体系或账单文件导入。
- 新增识别规则必须有单元测试覆盖。
- 涉及数据导入导出必须有错误处理和格式版本判断。

## 16. 完成定义

v1 完成必须同时满足：

1. APP 可在 Android 10 及以上设备安装运行。
2. 手动记账完整可用。
3. 微信、支付宝支付完成后可生成待确认流水。
4. 用户确认后流水写入本地账本。
5. 用户取消后不写入账本。
6. CSV 导出可正常打开。
7. JSON 备份可恢复账本。
8. 权限关闭后自动识别停止。
9. 不上传账本或支付信息。
10. 自动化测试和手工验收记录完整。
