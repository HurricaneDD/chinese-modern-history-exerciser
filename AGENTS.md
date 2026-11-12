# AGENTS.md - 项目内容与文件结构说明

## 1. 项目概述
本项目为 **“近现代史刷题”**（中国近现代史纲要课程单选题复习与刷题 Android 应用），基于 Kotlin、Jetpack Compose (Material 3) 与 Room 本地持久化数据库构建。
题库完整收录了《近现代史纲要复习 - 选择题合集（按章节分）》PDF 文档中的全部 **10 个章节、共 156 道单选题** 及对应标准答案。

## 2. 核心功能与四大模式
1. **学习模式（按单元和题号顺序刷题）**
   - 支持按“全部章节 (1~10章)”或筛选单个章节（第一章至第十章），严格按照单元与题号顺序逐题学习。
   - 支持“直接看答案（背题模式）”开关与“选题号面板”快速跳题。
2. **刷题模式（全题库随机顺序刷题）**
   - 将题库中全部 156 道题目按随机顺序打乱（Shuffle）供用户刷题。
   - 每次作答（写对或写错）均实时持久化记录至 Room 数据库（累加写过遍数 `totalAttempts` 与写错遍数 `wrongAttempts`）。
   - 支持随时点击 **“半途退出”**，展示本轮已答统计摘要，并支持“继续当前随机队列”或“重新打乱全部题目开始新刷题”。
3. **错题专练模式（按错误率加权随机抽题）**
   - 仅从错题集合（`wrongAttempts > 0`）中随机抽取题目。
   - 采用基于错误率（`errorRate = wrongAttempts / totalAttempts`）的加权随机算法（`QuestionRepository.pickWeightedWrongQuestion`），错误率越高的题目出现频率越高。
   - 作答时若再次写错会实时累加 `totalAttempts` 与 `wrongAttempts`，动态更新错误率与抽题权重。
4. **查看题库（统计展示、错题筛选与错误率排序）**
   - 展示每道题目的 **写过的遍数**、**写错的遍数**、**错误率 (%)** 以及标准答案。
   - 支持切换 **“专门展示错题”** 过滤视图。
   - 支持 **“按错误率从高到低”**、**“按写错遍数从高到低”**、**“按写过遍数从高到低”** 及 **“按章节与题号顺序”** 排序，并支持关键字搜索与单章筛选。

## 3. 目录与文件结构
- `/metadata.json`：AI Studio 平台元数据配置（应用名称与简介）。
- `/app/build.gradle.kts`：应用模块构建配置，启用 Compose 与 Room (KSP)。
- `/app/src/main/java/com/example/`
  - `MainActivity.kt`：应用主入口，配置 Edge-to-Edge、顶部导航栏、底部五栏导航（首页、学习模式、刷题模式、错题专练、查看题库）。
  - `data/`
    - `QuestionEntity.kt`：Room 题目实体类，包含章节号、题号、题干、A/B/C/D 选项、正确答案、写过遍数 `totalAttempts`、写错遍数 `wrongAttempts`，以及错误率 `errorRate` 和加权专练权重 `drillWeight` 计算属性。
    - `QuestionDao.kt`：Room 数据访问接口，提供响应式题目流查询、作答记录更新与统计重置。
    - `AppDatabase.kt`：Room 数据库单例配置。
    - `InitialQuestionsPart1.kt`：第一至五章（第 1~71 题）初始题库与答案数据。
    - `InitialQuestionsPart2.kt`：第六至十章（第 72~156 题）初始题库与答案数据。
    - `QuestionRepository.kt`：数据仓库层，负责首次启动自动导入 156 道题目、记录作答数据及错题专练加权随机抽题算法。
  - `ui/`
    - `QuizViewModel.kt`：MVVM 状态管理，维护四大模式的 UI 状态、筛选条件、排序方式及随机/加权抽题逻辑。
    - `HomeAndStudyScreens.kt`：首页概览看板、学习模式界面及通用题目作答卡片组件 `QuestionCardComponent`。
    - `PracticeAndDrillScreens.kt`：刷题模式（随机乱序、半途退出与恢复）及错题专练模式（加权高频抽题）界面。
    - `QuestionBankScreen.kt`：查看题库界面（搜索、专门展示错题筛选、按错误率从高到低排序、遍数/错数/错误率展示）。
    - `theme/`：`Color.kt`、`Type.kt`、`Theme.kt`，定义朱砂红与宣纸暖白学术主题配色及本地字体排版。

## 4. 修改记录
- **初始构建**：解析并导入 PDF 中全部 10 章 156 道“中国近现代史纲要”单选题与答案，实现学习模式、随机刷题模式（支持半途退出与数据记录）、错题专练模式（按错误率加权随机与再次写错记录）以及查看题库（展示写过遍数、写错遍数、错误率，支持专门展示错题与按错误率从高到低排序）。
