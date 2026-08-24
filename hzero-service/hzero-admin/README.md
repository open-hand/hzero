<p align="center">
    <img src="https://file.open.hand-china.com/hsop-image/doc_classify/0/fed03e0fcb9d4a408d5be052fced12d1/hzero.png" width="150">
    <h3><p style="text-align:center">hzero-admin</p></h3>
    <p align="center">
        平台治理
        <br>
        <a href="http://open.hand-china.com/document-center/doc/application/10041/10148?doc_id=5133"><strong>-- Home Page --</strong></a>
        <br>
        <br>
         <a href="http://www.apache.org/licenses/LICENSE-2.0">
             <img src="https://img.shields.io/github/license/alibaba/arthas.svg" >
         </a>
    </p>    
</p>

## Introduction
平台治理服务，提供自动化的路由刷新、权限刷新、swagger信息刷新功能，提供界面化的服务、配置、路由、限流、熔断管理功能以及Spring Boot Admin控制台

## Documentation
- [中文文档](http://open.hand-china.com/document-center/doc/application/10041/10148?doc_id=5133)

## Features

- 服务管理：统一管理系统所有服务
- 服务配置：维护服务配置信息
- 服务治理：配置管理服务的熔断、限流与在线运维等
- 服务监控：提供API统计与服务实例监控

## Architecture

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/b8a7b8d8053446589d46f497959e21a7/Admin%E6%9C%8D%E5%8A%A1%E6%9E%B6%E6%9E%84%E5%9B%BE.png)

## Functions

* 服务管理

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/ff80187d0f8a475b8dd6ab27da395e27/20200720204408.png)

* 服务配置

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/7efaed24ab1443f78eaa1332426d52b8/20200720204435.png)

* 服务治理

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/e182c257c984472ab3ded4c11fa65075/20200720204528.png)

* 服务监控

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/45b2da53e5b846328ec3a4284e7d34d0/20200720204821.png)

## Dependencies

* 服务 maven 坐标

```xml
<dependency>
    <groupId>org.hzero</groupId>
    <artifactId>hzero-admin-saas</artifactId>
    <version>${hzero.service.version}</version>
</dependency>
```

## Contributing

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建Issue讨论新特性或者变更。

Copyright (c) 2020-present, HZERO
