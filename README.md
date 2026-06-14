# 阴阳师账号管理器

一个功能完整的Android应用，用于管理阴阳师游戏账号。

## 功能特性

- ✅ **账号录入** - 支持账号类型、区服、手机号、密码、式神、等级、状态等字段
- ✅ **账号列表展示** - 卡片式布局，清晰展示所有账号信息
- ✅ **搜索功能** - 按手机号、式神、区服等关键词搜索
- ✅ **筛选功能** - 按账号状态（正常/冻结/封禁）筛选
- ✅ **本地SQLite存储** - 数据安全存储在本地
- ✅ **密码加密存储** - AES加密保护账号密码
- ✅ **数据导出备份** - 支持CSV和JSON格式导出
- ✅ **Material Design** - 简洁美观的界面设计

## 技术栈

- **语言**: Kotlin
- **最低SDK**: API 24 (Android 7.0)
- **目标SDK**: API 34 (Android 14)
- **架构**: MVVM
- **数据库**: SQLite
- **UI**: Material Design Components

## 构建说明

### 本地构建

```bash
# 克隆项目
git clone <repository-url>
cd OnmyojiAccountManager

# 构建Debug APK
./gradlew assembleDebug
```

构建完成后，APK文件位于: `app/build/outputs/apk/debug/app-debug.apk`

### GitHub Actions 自动构建

本项目配置了GitHub Actions自动构建，每次推送到main分支都会自动触发构建并生成APK。

## 下载安装

最新构建的APK可以在GitHub Releases页面下载。

## 应用截图

### 主界面
- 账号列表展示
- 搜索和筛选栏
- 悬浮添加按钮

### 添加/编辑界面
- 完整的账号信息表单
- 账号类型和状态选择器

## 安全说明

- 所有密码使用AES加密存储
- 数据仅保存在本地，不上传任何服务器
- 支持导出备份，建议定期备份数据

## 许可证

MIT License
