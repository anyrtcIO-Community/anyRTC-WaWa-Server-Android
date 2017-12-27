
# Android AnyRTCWaWaServer SDK集成指南

## 使用提示
本文是AnyRTCWaWaServer SDK Android标准的集成指南，用以指导SDK的使用方法，默认读者已经熟悉
IDE（Eclipse 或者 Android Studio）的基本使用方法，以及具有一定的 Android 编程知识基础。
本篇指南匹配的 AnyRTCWaWaServer SDK 版本为：1.0.0 及以后版本。

- [Demo演示](http://note.youdao.com/)
- 更多详细方法使用，请查看[API文档](http://note.youdao.com/)




# 创建应用
> 登陆[AnyRTC官网](http://www.anyrtc.io/)创建应用，再管理中心获取开发者ID，AppID，AppKey，AppToken。

# 开始集成SDK

>方式一（推荐）

添加Jcenter仓库 Gradle依赖：

```
dependencies {
    compile 'org.anyrtc:anyrtcwawaserver:1.0.0'
}
```

或者 Maven
```
<dependency>
  <groupId>org.anyrtc</groupId>
  <artifactId>anyrtcwawaserver</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

>方式二

 [点击下载aar格式SDK](http://www.anyrtc.io/)

>1. 将下载好的anyrtcwawaserver-release.aar文件放入项目的libs目录中
>2. 在Model下的build.gradle文件添加如下代码依赖anyrtcwawaserver SDK

```
android
{

 repositories {
        flatDir {dirs 'libs'}
    }
    
 }
    
```
```
dependencies {
    compile(name: 'anyrtcwawaserver-release', ext: 'aar')
    //还需添加第三方socket框架
    compile('io.socket:socket.io-client:1.0.0') {
        exclude group: 'org.json', module: 'json'
    }
}
```
# 权限说明
>anyrtcwawaserver 需要网络权限


权限 | 用途
---|---
 INTERNET | 允许应用可以访问网络




# 混淆配置
```
-dontwarn org.anyrtc.anyrtcwawaserver.**
-keep org.anyrtc.anyrtcwawaserver.**{*;}

```
    
# 配置开发者信息

> 配置信息。此步骤建议在Application中进行。

```
public class WaWa_Application extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
      AnyRTCWaWaServer.getInstance().setAnyRTCInfo(DEVELOPERID, APPID, APPKEY, APPTOKEN);
    }
}
        
 ```
# 设置监听回调
> 除配置外，调用任何anyRTCwawaClient中的方法请先设置回调实现类
```
AnyRTCWaWaServer.getInstance().setServerListener(this);
```

# 打开服务
```
//可在安卓板推流成功后打开（具体查看demo代码）
AnyRTCWaWaServer.getInstance().openServer();

```

# 加入房间(也可以理解为娃娃机在anyrtc服务上线)
```
  AnyRTCWaWaServer.getInstance().joinAnyRTC("", "", "", "");
 ```
# 返回娃娃机结果给服务

```
  AnyRTCWaWaServer.getInstance().sendResult(true); true代表抓到 反之没抓到
  ```




  >说明
- SDK 中采用单例模式始终保持一个服务对象。

- SDK所做的就是将客户端操作的命令告诉娃娃机终端，所以请具体查看api文档中各个回调方法作用。

例如，收到向左的信令就回走向左移动的回调方法，此时发送 向左命令给娃娃机即可。



- SDK中的方法，回调都很简单。具体使用，回调参数含义可查看[API文档](http://note.youdao.com/)。

   
# 注意事项
1. AnyRTCWaWaClient所有回调均在子线程中，所以在回调中操作UI等，应切换主线程。

# 技术支持 

加QQ技术咨询群：580477436





 