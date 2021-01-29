## 快快硬件-臂带 WIFI 版

### 导入

1、项目 build.gradle 添加仓库地址

```
allprojects {
    repositories {
		// 快快maven仓库地址
        maven { url 'http://36.110.31.137:8082/nexus/content/groups/public/' }
        ...
	}
}
```

2、需要应用的模块 build.gradle 添加引用

```
dependencies {
    implementation 'com.kuaikuai.hardware:wifi:1.2.5-SNAPSHOT'
}
```

### 使用
1、臂带处理类：Armlet

```
// 设置臂带监听（开启服务之后才会有数据），得到臂带数据，心率、消耗···（强度需在绑定后获取）
Armlet.setDeviceChangeListener(IDeviceChangeListener)

// 获取在线设备列表
Armlet.getDevices()

// 绑定
Armlet.bindDevice(HeartRateEntity,BindUserCommand)

// 解绑
Armlet.unbindDevice(HeartRateEntity,BindUserCommand)

// 获取绑定关系
Armlet.getDeviceBindMap()

// 添加拦截器
Armlet.addInterceptors(IReceiveInterceptor<HeartRateEntity>)

// 开启臂带服务
Armlet.start()

// 结束臂带服务
Armlet.stop()

// 课程单元信息配置，当前单元不为 0 时，才会有消耗，单元默认为 0
Armlet.heartLowerConfig = HeartLowerCommandConfig(currentUnit,···)

```

2、臂带指令交互
* ClassEndCommand：课程结束

```
HeartRateEntity.execute(AbstractCommand)
```
3、类说明

* HeartRateEntity：臂带数据
* BindUserCommand：绑定实体
* HeartLowerCommandConfig：单元信息、心率下限···配置
* IDeviceChangeListener：设备新增/移除/变化监听