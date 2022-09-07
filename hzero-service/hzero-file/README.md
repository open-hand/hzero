<p align="center">
    <img src="https://file.open.hand-china.com/hsop-image/doc_classify/0/fed03e0fcb9d4a408d5be052fced12d1/hzero.png" width="150">
    <h3><p style="text-align:center">hzero-file</p></h3>
    <p align="center">
        文件管理
        <br>
        <a href="http://open.hand-china.com/document-center/doc/application/10029/10161?doc_id=4451"><strong>-- Home Page --</strong></a>
        <br>
        <br>
         <a href="http://www.apache.org/licenses/LICENSE-2.0">
             <img src="https://img.shields.io/github/license/alibaba/arthas.svg" >
         </a>
    </p>    
</p>


## Introduction
文件管理，为平台提供文件存储服务

## Documentation
- [中文文档](http://open.hand-china.com/document-center/doc/application/10029/10161?doc_id=4451)

## Features
- 文件存储配置：对象存储配置，支持多种云服务
- 文件上传配置：租户存储容量及指定目录存储文件格式限制
- 文件汇总查询：记录所有上传的文件
- 服务器上传配置：服务器上传配置，支持FTP/SFTP协议
- 文件水印配置：文件的水印配置，支持文字水印和图片水印

## Architecture

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/2fb5eb72740f4022b49edbe16669c24d/20200713104656.png)

## Functions

* 文件存储配置

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/d233d741bfe942a89015dc54a02d3b64/20200709142933.png)

* 文件上传配置

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/295d100470cb4582abab6dde70ace880/20200709143133.png)

* 文件汇总查询

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/ef10bb49e23c451aa902e328bec3d727/20200709143304.png)

* 服务器上传配置

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/4b81791d0bb5471997124ccf213811ee/20200709143505.png)

* 文件水印配置

![](http://file.open.hand-china.com/hsop-image/doc_classify/0/b16daa8f49134a438f97d2aed76f3b4c/20200709143620.png)

## Dependencies


* 服务 maven 坐标

```xml
<dependency>
    <groupId>org.hzero</groupId>
    <artifactId>hzero-file-saas</artifactId>
    <version>${hzero.service.version}</version>
</dependency>
```

## Contributing

欢迎参与项目贡献！比如提交PR修复一个bug，或者新建Issue讨论新特性或者变更。

Copyright (c) 2020-present, HZERO
