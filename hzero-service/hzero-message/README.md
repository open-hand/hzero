<p align="center">
    <img src="https://file.open.hand-china.com/hsop-image/doc_classify/0/fed03e0fcb9d4a408d5be052fced12d1/hzero.png" width="150">
    <h3><p style="text-align:center">hzero-message</p></h3>
    <p align="center">
        消息管理
        <br>
        <a href="http://open.hand-china.com/document-center/doc/application/10027/10158?doc_id=4691"><strong>-- Home Page --</strong></a>
        <br>
        <br>
         <a href="http://www.apache.org/licenses/LICENSE-2.0">
             <img src="https://img.shields.io/github/license/alibaba/arthas.svg" >
         </a>
    </p>    
</p>


## Introduction
消息管理，平台统一的消息推送入口

## Documentation
- [中文文档](http://open.hand-china.com/document-center/doc/application/10027/10158?doc_id=4691)

## Features
- 公告管理：管理系统的公告与通知
- 模板管理：发送消息的内容模板
- 账户配置：邮箱、短信、企业微信、微信公众号、钉钉、webhook配置
- 消息发送配置：模板与账户关联
- 消息接收配置：允许用户指定消息接收方式
- 消息监控：消息发送的记录

## Architecture

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/9a5fb3d45bea4e209cd22fab5bc7fb9b/20200713171014.png)

## Functions

* 公告管理

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/a792a131939a40fca640cb05ea4deed4/20200709135928.png)

* 模板管理

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/90d0961a331b4fbc8f94b6399580f645/20200709135349.png)

* 邮箱账户

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/6d91de7ac010489792cec72297093b7e/20200709135703.png)

* 消息发送配置

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/723aec7e38c9496e8df30ce5e2bbc07d/20200709140134.png)

* 消息接收配置

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/82579193bd3d45f3b602d63bb1e76f1c/20200709140255.png)

## Dependencies


* 服务 maven 坐标

```xml
<dependency>
    <groupId>org.hzero</groupId>
    <artifactId>hzero-message-saas</artifactId>
    <version>${hzero.service.version}</version>
</dependency>
```

## Contributing

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建Issue讨论新特性或者变更。

Copyright (c) 2020-present, HZERO
