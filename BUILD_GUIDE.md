# HourChime 整点报时 - 构建指南

## 方式一：用 Android Studio 构建（推荐）

### 前提条件
- 安装 [Android Studio](https://developer.android.com/studio)（内含JDK + Gradle + SDK）

### 步骤
1. 打开 Android Studio → File → Open
2. 选择本文件夹 `HourChime/`
3. 等待 Gradle Sync 完成（首次需下载依赖，约2-5分钟）
4. 菜单栏：Build → Build Bundle(s) / APK(s) → **Build APK(s)**
5. APK 输出位置：`app/build/outputs/apk/debug/app-debug.apk`
6. 将 APK 复制到手机安装即可

---

## 方式二：命令行构建（已有JDK+SDK）

```bash
cd HourChime
# Windows
gradlew.bat assembleRelease

# 输出位置
# app/build/outputs/apk/release/app-release.apk
```

---

## 方式三：在线 CI 构建（无需本地环境）

### 使用 GitHub Actions

1. 将本项目上传到 GitHub
2. 创建 `.github/workflows/build.yml`：

```yaml
name: Build APK
on: [push]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: chmod +x gradlew && ./gradlew assembleDebug
      - uses: actions/upload-artifact@v3
        with:
          name: app-debug.apk
          path: app/build/outputs/apk/debug/app-debug.apk
```

3. Push 代码后，在 Actions 页面下载 APK

---

## 安装说明

1. 手机设置 → 安全 → 允许安装未知来源应用
2. 打开 APK 文件安装
3. 打开 App，开启开关即可

## 权限说明

| 权限 | 用途 |
|------|------|
| SCHEDULE_EXACT_ALARM | 精准整点触发 |
| RECEIVE_BOOT_COMPLETED | 开机自启 |
| FOREGROUND_SERVICE | 后台保活（可选） |
| WAKE_LOCK | 播放提示音时短暂唤醒CPU（<0.5秒） |
| POST_NOTIFICATIONS | Android 13+ 显示后台通知 |

## 资源占用
- 内存：< 5MB（无常驻进程，仅闹钟触发）
- CPU：每小时唤醒约 0.5 秒
- 电量：可忽略不计
