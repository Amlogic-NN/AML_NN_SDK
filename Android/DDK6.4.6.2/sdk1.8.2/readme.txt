目录结构
	image_classify:   物体分类模型
	so : armeabi-v7a环境下的相关so
	readme.txt

1. 物体分类模型（image_classify）
	1.1 目录结构
		apk : 基于ddk6443的物体分类demo apk
		demo : demo source code
		package : jpg and nbg files
	1.2 apk 使用说明：
		a. 安装apk
			adb install image_classify-6443-debug.apk
		b. 放置测试文件
			adb push */inceptionv1.nb  /storage/emulated/0/Android/data/com.jnitest/files/
			adb push */224x224x3.jpeg  /storage/emulated/0/Android/data/com.jnitest/files/images
	1.3 Demo 使用说明：
		安装APK后，直接点击start，demo运行并输出最后的分类结果。

注：
	若同一DDK中有多套SDK，请拉取最新的SDK版本。
