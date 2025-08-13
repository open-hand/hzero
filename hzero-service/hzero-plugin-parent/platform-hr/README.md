<p align="center">
    <img src="https://file.open.hand-china.com/hsop-image/doc_classify/0/fed03e0fcb9d4a408d5be052fced12d1/hzero.png" width="150">
    <h3><p style="text-align:center">platform-hr</p></h3>
    <p align="center">
        HR组织架构
        <br>
        <a href="http://open.hand-china.com/document-center/doc/component/143/10491?doc_id=5384"><strong>-- Home Page --</strong></a>
        <br>
        <br>
         <a href="http://www.apache.org/licenses/LICENSE-2.0">
             <img src="https://img.shields.io/github/license/alibaba/arthas.svg" >
         </a>
    </p>    
</p>


## Introduction
HR 组织架构插件，依赖于平台服务，包含组织架构、员工定义、企业通讯录、通讯录同步等功能。该插件提供了多种导入功能，如组织架构导入、员工导入、员工岗位和员工用户的导入，用户可以快捷的将原有系统中的组织架构、员工等信息批量导入到 HZERO 系统中。

## Documentation
- [中文文档](http://open.hand-china.com/document-center/doc/component/143/10491?doc_id=5384)

## Features
- 组织架构：用于维护企业的组织架构信息，支持维护多级公司/部门/岗位信息，支持通过Excel导入组织架构信息。
- 员工定义：用于维护企业的员工信息，支持维护员工-用户、员工-岗位的关联关系，支持通过Excel导入员工、员工-用户、员工-岗位信息。
- 企业通讯录：组织架构的另外一种展现模式，在该功能下也可以对组织架构、员工等信息进行维护。该信息可通过通讯录同步功能从第三方（钉钉、企业微信）同步过来。

## Functions

* 组织架构

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/cb0419874f344e4c948b33d68259dd8b/image.png)

* 员工定义

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/2d578f1cc29340bb9b1a54cd804d8944/image.png)

* 企业通讯录

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/66d9cf915a0c4926843317a0387ad509/image.png)

## Dependencies

* 服务 maven 坐标

```xml
<dependency>
    <groupId>org.hzero</groupId>
    <artifactId>platform-hr-saas</artifactId>
    <version>${hzero.service.version}</version>
</dependency>
```

* 依赖服务
    - hzero-platform
    - hzero-import
    - hzero-scheduler

## Contributing

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建Issue讨论新特性或者变更。

Copyright (c) 2020-present, HZERO
