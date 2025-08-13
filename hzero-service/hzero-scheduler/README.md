<p align="center">
    <img src="https://file.open.hand-china.com/hsop-image/doc_classify/0/fed03e0fcb9d4a408d5be052fced12d1/hzero.png" width="150">
    <h3><p style="text-align:center">hzero-scheduler</p></h3>
    <p align="center">
        分布式调度
        <br>
        <a href="http://open.hand-china.com/document-center/doc/application/10025/10159?doc_id=4683"><strong>-- Home Page --</strong></a>
        <br>
        <br>
         <a href="http://www.apache.org/licenses/LICENSE-2.0">
             <img src="https://img.shields.io/github/license/alibaba/arthas.svg" >
         </a>
    </p>    
</p>


## Introduction
分布式调度，平台统一的任务调度管理平台


## Documentation
- [中文文档](http://open.hand-china.com/document-center/doc/application/10025/10159?doc_id=4683)


## Features
- 执行器管理：负责执行任务的具体服务地址
- 调度任务：定时任务管理界面
- 调度日志：任务执行产生的日志记录

## Architecture

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/2966e22b5e0d4a1991a412fa7c76ddfd/20200709162754.png)

## Functions

* 执行器管理

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/28fa0bcb0799487f9efaad5126cf9456/20200709100928.png)

* 调度任务

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/6f425e9ffb594a18a9b517e5ce1f87af/20200709101311.png)

* 调度日志

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/0fbe9bd6389c410ea8dd8af31e59be58/20200709151929.png)

## Dependencies


* 服务 maven 坐标

```xml
<dependency>
    <groupId>org.hzero</groupId>
    <artifactId>hzero-scheduler-saas</artifactId>
    <version>${hzero.service.version}</version>
</dependency>
```

* 依赖服务
    - hzero-message
    - hzero-file
   
## Contributing

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建Issue讨论新特性或者变更。

Copyright (c) 2020-present, HZERO
