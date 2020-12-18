目录结构
	face_age:         年龄检测模型
	image_classify:   物体分类模型
	object_detect:    物体检测模型
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

2. 年龄检测模型（face_age）
	2.1 目录结构
		apk : 基于ddk6443的年龄检测demo apk
		demo : demo source code
		package : jpg and nbg files
	2.2 apk 使用说明：
		a. 安装apk
			adb install face_age-6443-debug.apk
		b. 放置测试文件
			adb push */age.nb  /storage/emulated/0/Android/data/com.jnitest/files/
			adb push */64x64x3.jpeg  /storage/emulated/0/Android/data/com.jnitest/files/images
	2.3 Demo 使用说明：
		安装APK后，直接点击start，demo运行并输出最后的年龄检测结果。

3. 物体检测模型（object_detect）
	3.1 目录结构
		apk : 基于ddk6443的物体分类demo apk
		demo : demo source code
		package : nbg files
	3.2 apk 使用说明：
		a. 安装apk
			adb install app-debug.apk
		b. 放置测试文件
			adb push object_detect.nb  /storage/emulated/0/Android/data/com.android.camera2/files/
		c. 获取存储权限
			进入settings-->apps--->app permissions---->storage----->apk name 打开存储权限
		注：
			此demo需要调用USB摄像头，运行该demo前请确保摄像头可以正常运行。
	3.3 Demo 使用说明：
		安装APK后，demo调取USB运行并输出最后的分类结果。

注：
	若同一DDK中有多套SDK，请拉取最新的SDK版本。
