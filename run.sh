#!/bin/bash
set -e

echo "=== Flow Reader MVP 本地运行脚本 ==="

# 检查是否存在 local.properties
if [ ! -f "local.properties" ]; then
    SDK_DIR="$HOME/Library/Android/sdk"
    if [ -d "$SDK_DIR" ]; then
        echo "sdk.dir=$SDK_DIR" > local.properties
        echo "已创建 local.properties -> $SDK_DIR"
    else
        echo "错误：找不到 Android SDK，请手动设置 ANDROID_HOME 环境变量"
        exit 1
    fi
fi

# 编译 Debug APK
echo "正在编译 app:assembleDebug..."
./gradlew :app:assembleDebug

# 尝试安装到连接的设备
if command -v adb &> /dev/null; then
    DEVICES=$(adb devices | grep -v "List" | grep "device" | wc -l | tr -d ' ')
    if [ "$DEVICES" -gt "0" ]; then
        echo "发现 $DEVICES 台设备，正在安装 APK..."
        adb install -r app/build/outputs/apk/debug/app-debug.apk
        echo "安装完成！请在设备上找到 Flow Reader 并打开。"
    else
        echo "未发现连接的 Android 设备或模拟器。"
        echo "APK 路径：app/build/outputs/apk/debug/app-debug.apk"
        echo "你可以手动拖进模拟器，或在 Android Studio 中点击 Run 按钮运行。"
    fi
else
    echo "未找到 adb，跳过自动安装。"
    echo "APK 路径：app/build/outputs/apk/debug/app-debug.apk"
fi
