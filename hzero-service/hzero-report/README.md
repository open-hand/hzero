<p align="center">
    <img src="https://file.open.hand-china.com/hsop-image/doc_classify/0/fed03e0fcb9d4a408d5be052fced12d1/hzero.png" width="150">
    <h3><p style="text-align:center">hzero-report</p></h3>
    <p align="center">
        报表平台
        <br>
        <a href="http://open.hand-china.com/document-center/doc/application/10018/10165?doc_id=5184"><strong>-- Home Page --</strong></a>
        <br>
        <br>
         <a href="http://www.apache.org/licenses/LICENSE-2.0">
             <img src="https://img.shields.io/github/license/alibaba/arthas.svg" >
         </a>
    </p>    
</p>


## Introduction
报表平台，提供数据报表渲染能力


## Documentation
- [中文文档](http://open.hand-china.com/document-center/doc/application/10018/10165?doc_id=5184)

## Features
- 数据集管理：报表的数据来源，关联平台数据源功能
- 报表模板管理：模板报表的模板管理功能
- 报表定义：数据报表定义及权限分配
- 报表查询：数据报表查看
- 标签维护及打印：标签模板维护、预览及打印配置

## Architecture

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/36d4ce7c4b574e058cdeed4d7cecf56a/20200710093328.png)

## Functions

* 数据集管理

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/4c6825fb51f9471cb64cdf1e7b4ded8c/dataSet-list.png)

* 报表模板管理

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/a361f2d00ee3454faac5037b93ee8436/tplManage-list.png)

* 报表定义

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/b8b7e689f6224be987577db2a2ddca86/reportDefine-list.png)

* 报表查询

![](http://file.open.hand-china.com/hsop-doc/doc_classify/0/76c8016dda064e96847d110c4cf5172f/reportQuery-query.png)

* 标签维护及打印

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/61315e447eb64d4cae8ff941dc3d8f72/20200709112244.png)

## Dependencies


* 服务 maven 坐标

```xml
<dependency>
    <groupId>org.hzero</groupId>
    <artifactId>hzero-report-saas</artifactId>
    <version>${hzero.service.version}</version>
</dependency>
```

* 依赖服务
    - hzero-file
    - hzero-scheduler
    - hzero-message

## Contributing

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建Issue讨论新特性或者变更。

Copyright (c) 2020-present, HZERO
