
# 重要提醒
anyRTC 对该版本已经不再维护，如需娃娃机方案，请前往官网联系客服获取。

**公司网址： [www.anyrtc.io](https://www.anyrtc.io)**

# 开始集成SDK

# 注意事项

>1. 加QQ技术咨询群：580477436Anyrtcwawaserver 是运行在adroid板子上的程序，demo中含有串口通信开源项目SerialPort 请勿随意修改该类的名字及位置
>2. 本demo中的娃娃机采用的为 “暴雪"娃娃机  其他品牌的娃娃机也可使用Anyrtcwawaserver SDK。只需要转发客户端发的命令给娃娃机再将结果转发给anyRTC信令服务
即可。
>3. 本demo中包含anyRTC公司RTCP实时直播SDK，需要摄像头，录音权限。
>4. 在手机上运行可能回Crash属正常现象，需要在root后的android板上运行。
>5. 如有疑问 请联系 QQ群：554714720




# >方式一（推荐）

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

 DEMO中的 aar SDK

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

### 技术支持
- anyRTC官方网址：[https://www.anyrtc.io](https://www.anyrtc.io/resoure)
- QQ技术咨询群：554714720
- 联系电话:021-65650071-816
- Email:hi@dync.cc
