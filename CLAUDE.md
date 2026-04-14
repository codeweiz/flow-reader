# Flow Reader — CLAUDE.md

> 参考项目：[Mihon](https://github.com/mihonapp/mihon)（漫画阅读器）、[Legado](https://github.com/gedoor/legado)（开源阅读/小说阅读器）
> 文档用途：为 Claude Code 提供项目上下文与开发约束

---

## 1. 项目定位

**Flow Reader** 是一款 Android 阅读器应用，目标是融合漫画阅读（Mihon）与小说阅读（Legado）的核心能力，打造统一的本地+网络内容阅读体验。

- **零内容原则**：应用本身不提供内容，依赖用户自定义书源/图源规则或本地文件导入。
- **核心场景**：小说（TXT/EPUB/PDF）、漫画（本地压缩包/网络图源）、音频（TTS/Media3）。

---

## 2. 架构设计

### 2.1 模块结构（Clean Architecture + 多模块）

采用统一的 `kebab-case` 模块名，反向域名包名规范，避免嵌套过深：

```
flow-reader/
├── app/                        # Application 入口、Activity、Application 级初始化
│   └── src/main/java/io/flowreader/app/
├── core/
│   ├── common/                 # 通用工具类、扩展函数
│   │   └── src/main/java/io/flowreader/core/common/
│   └── archive/                # 压缩文件处理（7z/zip/rar/cbz）
│       └── src/main/java/io/flowreader/core/archive/
├── domain/                     # 业务逻辑层：UseCase、Repository 接口、领域模型
│   └── src/main/java/io/flowreader/domain/
├── data/                       # 数据层：Repository 实现、Room、偏好设置、网络客户端
│   └── src/main/java/io/flowreader/data/
├── source/
│   ├── api/                    # 书源/图源接口定义（扩展系统基础协议）
│   │   └── src/main/java/io/flowreader/source/api/
│   ├── local/                  # 本地文件解析（TXT/EPUB/PDF/MOBI/压缩包）
│   │   └── src/main/java/io/flowreader/source/local/
│   └── engine/                 # 规则解析引擎（JSoup/XPath/JSONPath/Rhino JS）
│       └── src/main/java/io/flowreader/source/engine/
├── ui/
│   ├── core/                   # 通用 Compose 组件、主题、Material You
│   │   └── src/main/java/io/flowreader/ui/core/
│   ├── reader/                 # 阅读器相关 UI（文字/漫画/音频）
│   │   └── src/main/java/io/flowreader/ui/reader/
│   └── widget/                 # 桌面小部件（可选）
│       └── src/main/java/io/flowreader/ui/widget/
├── sync/                       # WebDAV/云端同步、Web 服务（可选模块）
│   └── src/main/java/io/flowreader/sync/
├── i18n/                       # 国际化资源（Moko Resources）
│   └── src/main/.../io/flowreader/i18n/
└── gradle/build-logic/         # 共享构建逻辑（Convention Plugins）
```

### 2.2 架构分层

```
┌─────────────────────────────────────────────┐
│  UI 层 (app / ui-*)                         │
│  Jetpack Compose + Voyager/Navigation       │
├─────────────────────────────────────────────┤
│  表现层 (ui-core / ui-reader / ui-widget)   │
│  通用 Compose 组件、主题、阅读器 UI、小部件  │
├─────────────────────────────────────────────┤
│  领域层 (domain)                            │
│  UseCase、Repository 接口、领域模型          │
├─────────────────────────────────────────────┤
│  数据层 (data)                              │
│  Repository 实现、Room、Datastore、网络请求  │
├─────────────────────────────────────────────┤
│  核心层 (core / source-* / sync)            │
│  工具类、图源接口、本地解析、规则引擎、同步  │
└─────────────────────────────────────────────┘
```

**原则**：
- `app` 模块仅负责导航、生命周期与依赖注入装配，不包含业务逻辑。
- 领域层不依赖 Android Framework，便于单元测试。
- 数据层通过 `source:api` 的接口对接各类图源实现。

---

## 3. 技术栈

### 3.1 平台与构建

| 项目 | 技术/版本 | 说明 |
|------|-----------|------|
| 语言 | Kotlin 2.3.x | 首选语言，少量 Java 遗留允许存在 |
| 构建 | Gradle (Kotlin DSL) + AGP 8.13.2 | 使用 `libs.versions.toml` + `build-logic` 共享配置 |
| 最低 SDK | 26 (Android 8.0) | Mihon 标准；如需覆盖旧设备可降至 21（参考 Legado） |
| 目标 SDK | 36 | 紧跟最新 Android 版本 |
| JVM | Java 17 | 编译工具链统一 |

### 3.2 UI 层

| 类别 | 库 | 版本/说明 |
|------|-----|-----------|
| UI 框架 | Jetpack Compose (BOM) | 声明式 UI，全面替代传统 View |
| 导航 | Voyager | 屏幕导航与状态管理（参考 Mihon） |
| 主题 | Material Design 3 + MaterialKolor | 支持 Material You 动态取色 |
| 图片加载 | Coil 3.x | Kotlin 优先、Compose 原生支持（替代 Glide） |
| 富文本 | Compose Rich Editor / Markwon | Markdown/富文本渲染 |
| 手势/缩放 | Subsampling Scale Image View + PhotoView | 漫画大图、缩放浏览 |

### 3.3 数据与网络

| 类别 | 库 | 说明 |
|------|-----|------|
| 数据库 | **Room** 2.7.x | 首选。使用 KSP 替代 KAPT，开启增量编译与扩展投影优化，Schema 导出到 `app/schemas` 或 `data/schemas` |
| 网络请求 | OkHttp 5.3.x | 统一 HTTP 客户端 |
| 序列化 | kotlinx.serialization | JSON/Protobuf 首选 |
| 协程 | kotlinx.coroutines 1.10.x | 所有异步逻辑基于 Coroutines + Flow |
| 分页 | Paging 3 | 列表分页（书架/搜索结果） |
| 后台任务 | WorkManager 2.x | 图书馆更新、定时任务 |
| 数据存储 | DataStore / MMKV | 用户偏好设置存储 |

### 3.4 阅读核心引擎

| 类别 | 库 | 说明 |
|------|-----|------|
| HTML 解析 | JSoup 1.16.2 | 网页 DOM 解析（注意：Legado 锁定此版本，升级需兼容性验证） |
| XPath | JsoupXpath | 基于 JSoup 的 XPath 支持 |
| JSONPath | json-path | API 返回解析 |
| JS 引擎 | Mozilla Rhino 1.8.1 | 书源规则中的 JavaScript 执行（注意：低版本 Android 兼容问题） |
| 正则 | Kotlin Regex / Apache commons-text | 文本提取与替换净化 |
| 中文 NLP | HanLP / quick-chinese-transfer | 简繁转换、分词 |
| EPUB 解析 | epublib-core | EPUB 文件解析 |
| 压缩解压 | libarchive | 本地漫画压缩包读取（zip/rar/7z/cbz 等） |
| Web 服务 | NanoHTTPD + WebSocket | 内置 Web 服务器，支持浏览器端管理（参考 Legado） |
| 媒体播放 | Media3 (ExoPlayer) 1.8.x | 音频书、TTS、在线朗读引擎 |

### 3.5 依赖注入与其他

| 类别 | 库 | 说明 |
|------|-----|------|
| DI | Injekt（轻量）或 Hilt | 优先 Injekt（Mihon 风格），复杂场景可用 Hilt |
| 权限 | Shizuku | 免 Root 安装扩展/图源 APK（参考 Mihon） |
| 生物识别 | Biometric | 应用锁、隐私保护 |
| 启动屏 | Core SplashScreen | 适配 Android 12+ 启动图标 |
| 日志 | Logcat / Timber | 结构化日志 |
| 内存泄漏 | LeakCanary (plumber) | 生产环境集成 plumber，调试可选 full |

---

## 4. 代码规范与约定

### 4.1 格式化

- **Spotless + klint 1.8.x** 强制代码格式化。
- CI 中运行 `./gradlew spotlessCheck` 与 `./gradlew spotlessApply`。
- 提交前确保代码通过格式检查。

### 4.2 命名约定

| 层级 | 命名规则 | 示例 |
|------|----------|------|
| UseCase | 动词 + 名词 | `GetBookListUseCase`, `SyncReadingProgressUseCase` |
| Repository 接口 | 名词 + Repository | `BookRepository`, `SourceRepository` |
| Repository 实现 | 名词 + Repository + Impl | `BookRepositoryImpl`, `SourceRepositoryImpl` |
| ViewModel | 页面名 + ViewModel | `BookshelfViewModel`, `ReaderViewModel` |
| Screen (Compose) | 页面名 + Screen | `BookshelfScreen`, `ReaderScreen` |
| 数据库表 | 小写复数 | `books`, `chapters`, `book_sources` |
| 领域模型 | 不与数据库实体同名 | `Book` (domain) vs `BookEntity` (data) |

### 4.3 依赖方向

```
app -> ui-* -> domain <- data <- source-* <- core
```

- **严禁循环依赖**。
- `domain` 不依赖任何 Android 库或第三方框架（纯 Kotlin）。
- `data` 可依赖 `source:api`、`core`、Room、OkHttp 等。

### 4.4 状态管理

- UI 状态使用 `StateFlow` 或 Compose `remember`/`collectAsState`。
- 页面级状态统一由 `ViewModel` 暴露为 `UiState` 数据类。
- 避免在 Composable 中直接发起副作用，使用 `LaunchedEffect` / `SideEffect`。

---

## 5. 核心功能开发指南

### 5.1 图源/书源扩展系统

参考 Mihon 的 APK 扩展 + Legado 的规则引擎两条线：

1. **APK 扩展（Mihon 模式）**
   - 定义 `source:api` 模块中的接口（如 `Source`, `MangaSource`, `NovelSource`）。
   - 第三方通过实现接口打包为独立 APK 安装。
   - 应用通过 `PackageManager` 查询并加载扩展。

2. **规则引擎（Legado 模式）**
   - 定义 `source:engine` 模块，封装 `AnalyzeRule`。
   - 规则支持：JSoup CSS Selector、XPath、JSONPath、正则、内嵌 Rhino JS。
   - 书源配置统一为 JSON/YAML 格式，支持导入导出。
   - Web 服务端提供在线书源编辑器对接。

**建议策略**：
- 漫画图源优先走 **APK 扩展**（结构复杂、需要图片处理、登录态管理）。
- 小说书源优先走 **规则引擎**（HTML 结构抓取为主，轻量灵活）。

### 5.2 阅读器实现

#### 文字阅读器
- 参考 Legado `ReadBook` 状态机模式。
- 核心类：`ReadBook`（单例/作用域对象），维护当前书、章节、页码、预加载状态。
- 翻页动画：委托模式实现（Cover / Simulation / Slide / Scroll / NoAnim）。
- 排版：自定义 View/Compose 实现分页排版，支持字体、行距、段距、简繁转换。

#### 漫画阅读器
- 参考 Mihon 的多视图模式。
- `Pager` 模式：单页翻页，支持缩放。
- `Webtoon` 模式：长条连续滚动，自动分割长图。
- 预加载：前后章节图片预加载，结合 Coil 缓存策略。

#### 音频/TTS
- 基于 Media3 + ExoPlayer。
- 支持系统 TTS 与在线 HTTP TTS 引擎。
- 朗读服务以前台 Service (`mediaPlayback`) 运行。

### 5.3 本地文件解析

统一在 `source:local` 模块中实现：

| 格式 | 解析器 |
|------|--------|
| TXT | `TextFileParser` — 智能分章、编码检测 |
| EPUB | `EpubFileParser` — 基于 epublib |
| PDF | `PdfFileParser` |
| MOBI/AZW3 | `MobiFileParser` |
| 压缩包 | `ArchiveFileParser` — 基于 libarchive，用于漫画包 |

### 5.4 Web 服务

基于 NanoHTTPD：
- 端口可配置，默认后台运行。
- 提供 REST API：书架列表、书籍详情、章节内容、阅读进度同步。
- WebSocket 支持实时推送（如阅读进度、书源调试日志）。
- 配套 Web 前端项目可独立维护。

---

## 6. 数据库设计要点

### 6.1 核心表结构（参考 Legado + Mihon）

| 表/Entity | 说明 |
|-----------|------|
| `books` | 书籍主表（URL、书名、作者、阅读进度、分组、封面） |
| `chapters` | 章节列表（索引、标题、链接、是否缓存、字数/页数） |
| `book_sources` | 书源/图源规则表（JSON 规则字段） |
| `rss_sources` | RSS 订阅源表 |
| `replace_rules` | 替换净化规则（正则） |
| `book_groups` | 书架分组 |
| `bookmarks` | 书签/笔记 |
| `book_progress` | 阅读进度同步表（WebDAV/服务端） |
| `cookies` | 网络请求 Cookie 存储 |
| `caches` | 通用缓存 |

### 6.2 数据库选型

- **首选 Room 2.7.x**：团队熟悉度高、生态成熟、与 Paging/LiveData/Flow 集成度好。
- **必须使用 KSP**（`com.google.devtools.ksp` 插件）替代 KAPT，显著提升编译速度。
- **Schema 管理**：开启 `room.schemaLocation`，每次迁移自动导出 schema 到 `data/schemas/` 或 `app/schemas/`。
- **增量编译**：配置 `room.incremental=true` 和 `room.expandProjection=true`。
- **实体命名**：数据层实体后缀 `Entity`（如 `BookEntity`），避免与领域层 `Book` 混淆。

---

## 7. 构建配置

### 7.1 Build Types

| 变体 | 用途 | applicationIdSuffix |
|------|------|---------------------|
| `debug` | 开发调试 | `.dev` |
| `release` | 正式版 | - |
| `foss` | 开源纯净版（无 Google 服务） | `.foss` |
| `preview` | 预览/内测版 | `.debug` |

### 7.2 ABI 支持

- `armeabi-v7a`
- `arm64-v8a`
- `x86`
- `x86_64`
- 支持 `universalApk = true`

### 7.3 CI/CD 流水线

GitHub Actions 必备步骤：
1. 代码检出
2. 依赖审查（Dependabot / Renovate）
3. JDK 17 环境设置
4. Spotless 格式检查
5. 单元测试（`./gradlew test`）
6. 数据库迁移验证（Room schema 检查）
7. 构建 Release APK
8. 上传产物与 mapping 文件

---

## 8. 安全与兼容性注意事项

### 8.1 依赖版本锁定（参考 Legado 经验）

以下依赖升级需经过充分兼容性测试：

| 库 | 锁定版本 | 原因 |
|----|----------|------|
| jsoup | 1.16.2 | 新版有破坏性变更，影响 `AnalyzeByJSoup` 和 JsoupXpath |
| commons-text | 1.13.1 | 新版使用 Android 6 以下缺失的 `Arrays.setAll` |
| rhino | 1.8.1 | 新版使用 Android 8 以下无法编译的 `VarHandle` |
| media3 | 1.8.0 | 兼容性锁定 |

### 8.2 低版本兼容

- 使用 `desugar_jdk_libs_nio` 对 Java 8+ API 脱糖。
- Android 10 以下设备通过 Conscrypt 或 GMS 注入启用 TLS 1.3。

### 8.3 权限模型

- **存储**：优先使用 `DocumentFile` / `Storage Access Framework`，避免直接申请 `MANAGE_EXTERNAL_STORAGE`。
- **安装**：如需安装 APK 扩展，集成 Shizuku 实现免 Root 静默安装。
- **前台服务**：Android 14+ 需声明前台服务类型（`dataSync`、`mediaPlayback`）。

---

## 9. 测试策略

### 9.1 测试结构

```
module/src/test/              # 单元测试（JUnit 5 / Kotest / MockK）
module/src/androidTest/       # 仪器测试（Compose Test / Espresso）
```

### 9.2 重点测试对象

- `source:engine` 模块：规则解析器单元测试（JSoup/XPath/JSONPath 输入输出断言）。
- `domain` 模块：UseCase 逻辑测试（Mock Repository）。
- `source:local` 模块：本地文件解析测试（多种格式样本文件）。

---

## 10. 总结

Flow Reader 的技术选型以 **Mihon 的现代 Android 架构** 为骨架，以 **Legado 的强大书源引擎与本地阅读能力** 为血肉：

1. **现代 UI**：Jetpack Compose + Material You + Voyager 导航。
2. **清晰分层**：Clean Architecture + 多模块（`ui-*` / `source-*` / `core-*`），包名统一、依赖方向明确。
3. **双轨扩展**：APK 扩展（漫画图源）+ 规则引擎（小说书源）。
4. **统一阅读**：文字、漫画、音频三种阅读模式共享一套书架与 `data` 层。
5. **成熟数据层**：Room + KSP + Schema 管理，稳定可靠。

---

*本文档基于 Mihon v0.19.9 与 Legado 3.x 技术栈分析生成，随项目演进持续更新。*
