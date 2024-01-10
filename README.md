# AML_NN_SDK

    Amlogic's NN NBG model file ，Provide to Amlogic's  NN customers.

目录结构:

Android:  包含SDK各版本Android DEMO文件

Linux:

Model: 包含DDK643之后版本的各平台nbg文件

Tool: 包含后续开发的各工具

README.md: readme文档



注：

1. 单独拉取某文件或某文件夹

   由于当前GitHub中存放的文件过多，clone完整的仓耗时过长，提供一种单独拉取某文件或某文件夹的方式。用户可根据DDK版本和SDK版本获取对应的文件。

   ~~~
   1. sudo apt install subversion             //linux端安装svn，windows端可以安装TortoiseSVN
   2. 例如需要拉取文件的链接为：
   https://github.com/Amlogic-NN/AML_NN_SDK/tree/master/Model/DDK6.4.4.3/7d
   将tree/master换成trunk/即可，即拉取命令为：
   svn checkout https://github.com/Amlogic-NN/AML_NN_SDK/trunk/Model/DDK6.4.4.3/7d  //拉取DDK6443和7d平台对应的nbg文件
   ~~~

2. 确认平台及DDK版本

   2.1 获取平台信息

      待完善

   2.2 获取DDK版本信息

   ​	执行命令：

   ​         dmesg|grep version

   ​	打印信息：

   ​        Galcore version 6.4.4.3.310723

   ​    其中6.4.4.3即为DDK版本

3.当前版本AdlaInfo_Netron上传位置如下,请使用以下链接下载使用:

   Link:https://gitee.com/aml_nn_team/adla_-info_-netron_-tool