<p align="center">
    <img src="https://file.open.hand-china.com/hsop-image/doc_classify/0/fed03e0fcb9d4a408d5be052fced12d1/hzero.png" width="150">
    <h3><p style="text-align:center">hzero-platform</p></h3>
    <p align="center">
        平台管理
        <br>
        <a href="http://open.hand-china.com/document-center/doc/application/10035/10153?doc_id=5148"><strong>-- Home Page --</strong></a>
        <br>
        <br>
         <a href="http://www.apache.org/licenses/LICENSE-2.0">
             <img src="https://img.shields.io/github/license/alibaba/arthas.svg" >
         </a>
    </p>    
</p>


## Introduction
平台管理，作为平台的基础管理服务，主要为平台提供一些基础能力，如系统配置、开发配置等。

## Documentation
- [中文文档](http://open.hand-china.com/document-center/doc/application/10035/10153?doc_id=5148)

## Features
- 系统配置：用于配置系统环境的标题、LOGO和布局等。
- 配置维护：用于配置参数在不同角色或用户下的具体展现，例如币种，在A角色下为CNY，在B角色下可能需要展示为USD
- 编码规则：用于维护编码的生成策略，在应用时会根据规则生成相应的字符串内容。
- 数据权限规则：用于控制用户的可访问数据，在系统使用过程中可以通过调整配置来实现实时、动态、灵活的调整用户、角色或者其他维度的可访问数据，同时支持配置数据库前缀来实现不同数据库之间的跨库访问。
- 卡片管理：用于维护平台的工作台卡片信息，可通过配置卡片并辅以前端开发来自定义实现工作台卡片的样式和内容。
- 数据层级配置：结合数据权限规则功能，用于为用户的数据权限提供更加丰富多层级的控制。默认数据权限规则可以控制租户、角色、用户等层级，可通过该功能进行扩展。
- 值集配置：用于维护值集信息。
- 值集视图配置：用于配置值集返回数据的展示形式，支持在线预览。
- 数据源设置：用于维护服务所需的数据源信息。
- 平台多语言：用于维护平台的多语言信息，目前支持中文、英文和日文三种语言。

## Architecture

<img src="http://file.open.hand-china.com/hsop-doc/doc_classify/0/9ac4eb56e9fd4070b0f7bfccca6bd06f/image.png" alt="" width="auto" height="auto" >

## Functions

* 系统配置

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/4b6119ae3a8847c99b3f9bda9881c2f1/基础数据配置.png)

* 配置维护

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/5e7ff7e3d4b14a038e7238c12ce28379/image.png)

* 编码规则

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/52ff440767794289a94ebd2cf0f24929/image.png)

* 数据权限规则

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/0f32cab6091040f5ac5b75ff6adfaad1/image.png)

* 卡片管理

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/3b9cf065c1a24bfeb4e26086af7db1e6/image.png)

* 数据层级配置

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/92f6bfe956f2433188dfd4faf4638c0d/image.png)

* 值集配置

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/ff514a109b9b4fffbb8920d9d837cc86/image.png)

* 值集视图配置

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/e8d5215864314b7ba47839a87d188108/image.png)

* 数据源设置

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/9e81b2b513ba4d8d8272b500147c8c7d/image.png)

* 平台多语言

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/494950b51802436d99ce3849ae968f0a/image.png)
 

## Dependencies

* 服务 maven 坐标

```xml
<dependency>
    <groupId>org.hzero</groupId>
    <artifactId>hzero-platform-saas</artifactId>
    <version>${hzero.service.version}</version>
</dependency>
```

* 依赖服务
    - hzero-message
    - hzero-scheduler
    - hzero-iam

## Contributing

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建Issue讨论新特性或者变更。

Copyright (c) 2020-present, HZERO
