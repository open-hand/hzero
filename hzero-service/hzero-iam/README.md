<p align="center">
    <img src="https://file.open.hand-china.com/hsop-image/doc_classify/0/fed03e0fcb9d4a408d5be052fced12d1/hzero.png" width="150">
    <h3><p style="text-align:center">hzero-iam</p></h3>
    <p align="center">
        用户身份管理服务
        <br>
        <a href="http://open.hand-china.com/document-center/doc/application/10038/10150?doc_id=4900"><strong>-- Home Page --</strong></a>
        <br>
        <br>
         <a href="http://www.apache.org/licenses/LICENSE-2.0">
             <img src="https://img.shields.io/github/license/alibaba/arthas.svg" >
         </a>
    </p>    
</p>

## Introduction
权限管理服务，平台统一的权限体系架构

## Documentation
- [中文文档](http://open.hand-china.com/document-center/doc/application/10038/10150?doc_id=4900)


## Features
- 租户管理：租户定义及管理
- 角色管理：支持SaaS的多级角色管理体系，支持一键下分权限和回收权限
- 菜单管理：标准菜单管理、多前端菜单管理，维护权限集和API权限
- 用户管理：多租户用户管理，可给用户分配角色、分配客户端、分配数据权限等
- 配置管理：三方应用、客户端、域名等配置的维护管理
- 权限管理：用于配置业务单据类型以及可控制的权限维度基础数据

## Architecture

* 基础架构

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/6657a3b9adb14deda7032726558bcf65/image.png)

* 多级管理员体系

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/12b076e089744736ab210942b2bd9fa8/image.png)

## Functions

* 租户管理

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/4107c1c83871447393b80456288ea719/20200720201602.png)

* 角色管理

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/8f25c899f786424e80134c4dff1706c9/20200720201734.png)

* 菜单管理

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/d86adb51ed0945e0b4eb2799c5a3061d/20200720202008.png)

* 用户管理

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/2148e77fd2554588972107a9e8412de0/20200720202404.png)

* 配置管理

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/5503d1c94290413d8ba7189751375912/20200720202538.png)

* 权限管理

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/e521484adcf9481d9153916373cabefe/20200720202706.png)

## Dependencies


* 服务 maven 坐标

```xml
<dependency>
    <groupId>org.hzero</groupId>
    <artifactId>hzero-iam-saas</artifactId>
    <version>${hzero.service.version}</version>
</dependency>
```

* 依赖服务
    - hzero-file
    - hzero-message
    - hzero-scheduler
    - hzero-import

## Contributing

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建Issue讨论新特性或者变更。

Copyright (c) 2020-present, HZERO
