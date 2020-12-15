<p align="center">
    <img src="https://file.open.hand-china.com/hsop-image/doc_classify/0/fed03e0fcb9d4a408d5be052fced12d1/hzero.png" width="150">
    <p align="center">
        <br>
         <a href="http://www.apache.org/licenses/LICENSE-2.0">
             <img src="https://img.shields.io/github/license/alibaba/arthas.svg" >
         </a>
    </p>    
</p>

# HZERO-基于微服务架构开源免费的企业级PaaS平台

HZERO应用微服务、容器、DevOps等云原生技术，封装了大量技术开发包、技术应用组件、技术场景实现能力，并支持SaaS模式应用，提供了一个可支持企业各业务系统或产品快速开发实现的微服务应用数字化融合平台，富含各类开箱即用的组件G-General、A-AI、B-BigData、M-Mobile、D-DevOps，助力企业跨越Cloud（IaaS/PaaS）与自身数字化的鸿沟，共享业务服务的组合重用，为企业服务化中台整合、数字化转型提供强力支撑，也为企业提供了最佳架构实践。

HZERO提供：

- 企业级应用系统所包含的常用开箱即用的模块，并支持灵活的可配置性和拓展性。

- 一套基于Spring Cloud的微服务应用程序框架，可帮助公司更快，更高效地进行微服务开发。

## 源码下载说明

HZERO平台由多个微服务程序组成，各微服务的下载请参见下文 [HZERO的组成](https://github.com/open-hand/hzero#hzero%E7%9A%84%E7%BB%84%E6%88%90) 中列示的服务下载链接，或者切换到 [open-hand](https://github.com/open-hand) 父组织中搜索 hzero-xxx 代码仓库即可找到对应微服务的源码。

本代码仓库仅作为HZERO平台使用的引导说明，不包含平台源码，请务必仔细阅读本文档以下内容。

## HZERO主要特征

- [平台治理](http://open.hand-china.com/document-center/doc/application/10041/10148?doc_id=5133) - 提供了一系列的服务治理功能，用户可在该服务下快速实现服务的路由管理、熔断、限流以及API权限刷新等功能，以及可以通过服务监控控制台来监控已经部署的服务。功能点包含服务管理、服务配置、限流规则、熔断规则、服务监控控制台、API访问控制、请求链路追踪等。

- [系统管理](http://open.hand-china.com/document-center/doc/application/10035/10153?doc_id=5148) - 该功能模块作为HZERO基础数据管理模块，主要包含了租户管理、角色管理、菜单管理、用户管理、配置管理、权限管理、登录管理、数据组管理、服务器定义等功能。用户可在该功能模块下进行账户、角色、菜单、权限等信息的维护，还可以自定义HZERO环境内的一些基础信息，例如修改LOGO、系统标题、页面布局方式。此外该功能模块下还提供了用户登录日志以及在线用户的查看，用户可在此功能下查看用户的登录信息和用户在线的情况。

- [开发管理](http://open.hand-china.com/document-center/doc/application/10035/10153?doc_id=5148) - 该功能模块下的功能主要用于为使用HZERO开发提供支持，主要包含了规则管理、多语言管理、个性化管理、值集管理、数据源管理、静态文本管理、CA证书管理、系统工具等功能。用户可以在该功能模块下配置一些开发过程中需要的数据，例如配置一些数据源、静态文本、值集、多语言等内容，通过平台提供的客户端API进行调用，这样就可以实现对一些配置数据的统一管理。

- [组织管理](http://open.hand-china.com/document-center/doc/application/10035/10153?doc_id=5148) - 该功能模块下的功能用于维护企业的组织架构信息以及员工信息。主要包含了组织信息、员工定义、组织架构、企业通讯录、通信录同步等功能。用户可在此功能模块下维护公司的组织架构信息（公司/部门/岗位）和员工信息，也可以通过第三方渠道（钉钉/企业微信）将组织架构信息一键导入到系统中。也可以使用组织架构和员工信息的导入功能将数据导入到系统中。

- [消息管理](http://open.hand-china.com/document-center/doc/application/10027/10158?doc_id=5163) - 提供了多种类型消息配置及发送的能力，提供消息发送的通用API，用户可通过此功能模块轻松实现多种类型消息的发送，无需烦恼对接多种消息平台。功能点包含系统公告与通知、消息模板配置、消息发送配置、消息接收配置、账户配置（包含邮箱、短信、企业微信、微信公众号、钉钉、webhook、电话语音配置）、接收组维护、消息监控。

- [文件管理](http://open.hand-china.com/document-center/doc/application/10029/10161?doc_id=5172) - 集成了多种常用的文件功能，用户可在界面上轻松实现文件的上传、下载、在线预览和编辑等。功能点包含文件存储配置、上传配置、汇总查询、在线编辑、文件预览、服务器上传以及PDF水印等，同时文件存储配置支持多种云平台，包含百度云、阿里云、腾讯云、华为云、京东云等。

- [分布式调度](http://open.hand-china.com/document-center/doc/application/10025/10159?doc_id=5167) - 基于Quartz 2.3.0自研的分布式调度平台，提供了一系列的配置API，用户可在界面上通过配置快速的创建分布式调度任务。功能点包含执行器管理、调度任务、调度日志、可执行定义、请求定义、并发请求等。

- [报表平台](http://open.hand-china.com/document-center/doc/application/10018/10165?doc_id=5184) - 无需开发任何代码，用户只需在界面上配置一系列参数即可实现多种类型报表的配置、在线渲染报表内容、报表导出以及标签打印等。功能点包含数据集、报表模板管理、报表定义、报表查询、报表请求、标签打印管理等功能。

- [通用数据导入](http://open.hand-china.com/document-center/doc/application/10023/10162?doc_id=5175) - 提供了通用的数据导入方案，用户仅需配置好导入模板即可在导入模板管理功能下实现数据导入功能，同时用户可以使用HZERO提供的通用导入组件来自定义实现数据导入（例如对导入的数据做一些自定义校验），支持Excel和CSV方式导入。此外，HZERO还提供了导入历史功能来查看数据导入的情况。

另外，您可以查看的[屏幕快照](./SCREENSHOT.md)以最直观地了解HZERO，还可以访问[汉得开放平台](https://open.hand-china.com/open-source)的网站下[HZERO开源版](https://open.hand-china.com/document-center/doc/product/10067/10032?doc_id=5946)获取平台详细文档。

## 服务及组件关系列表
欲获取HZERO详细的组件信息，请参考文档[组件说明](http://open.hand-china.com/document-center/doc/product/10067/10032?doc_id=6302)

```
└─ hzero-parent                                      HZERO父依赖
    ├─ hzero-register                                注册中心服务
    ├─ hzero-config                                  配置中心服务
    ├─ hzero-gateway                                 网关服务
    ├─ hzero-swagger                                 swagger服务
    ├─ hzero-admin                                   平台治理服务
    ├─ hzero-oauth                                   认证服务
    ├─ hzero-iam                                     IAM服务
    ├─ hzero-platform                                平台管理服务
    ├─ hzero-file                                    文件服务
    ├─ hzero-import                                  导入服务
    ├─ hzero-message                                 消息服务
    ├─ hzero-scheduler                               调度服务
    ├─ hzero-report                                  报表服务
    ├─ hzero-gateway-helper                          网关鉴权组件
    │   ├─ hzero-gateway-helper-api                  网关鉴权抽象组件
    │   ├─ hzero-gateway-helper-default              网关鉴权默认实现组件（用户角色鉴权） 
    │   └─ hzero-gateway-helper-login                登录可访问鉴权组件
    ├─ hzero-plugin-parent                           服务可插拔功能管理服务
    │   └─ platform-hr                               HR组织架构功能插件
    ├─ hzero-boot-parent                             客户端服务
    │   ├─ hzero-boot-admin                          平台治理服务客户端
    │   ├─ hzero-boot-message                        消息服务客户端
    │   ├─ hzero-boot-import                         导入服务客户端
    │   ├─ hzero-boot-platform                       平台管理服务客户端
    │   ├─ hzero-boot-scheduler                      调度服务客户端
    │   ├─ hzero-boot-file                           文件服务客户端
    │   ├─ hzero-boot-iam                            iam服务客户端
    │   ├─ hzero-boot-oauth                          认证服务客户端
    │   └─ hzero-boot-report                         报表服务客户端
    ├─ hzero-starter-parent                          通用开发父组件
    │   ├─ hzero-starter-core                        基础依赖组件
    │   ├─ hzero-starter-redis                       redis组件
    │   ├─ hzero-starter-mybatis-mapper              通用mapper组件
    │   ├─ hzero-starter-export                      数据导出组件
    │   ├─ hzero-starter-websocket                   websocket组件
    │   ├─ hzero-starter-lock                        Redis锁组件
    │   ├─ hzero-starter-register-event              服务注册组件
    │   ├─ hzero-starter-local-feign                 Feign转本地调用组件
    │   ├─ hzero-starter-feign-replay                Feign调用客户端支持组件
    │   ├─ hzero-starter-apollo-config               阿波罗配置中心客户端组件
    │   ├─ hzero-starter-metric                      JVM监控组件
    │   ├─ hzero-starter-config-client               配置中心客户端组件
    │   ├─ hzero-starter-jdbc                        动态JDBC组件
    │   ├─ hzero-starter-excel                       Excel开发帮助组件
    │   ├─ hzero-starter-fragment                    文件分片组件
    │   ├─ hzero-starter-sqlparser                   Sql解析器组件
    │   ├─ hzero-starter-keyencrypt                  主键加密组件
    │   └─ hzero-starter-seata                       seata分布式事务组件
    ├─ hzero-starter-sso-parent                      单点登录父组件
    │   ├─ hzero-starter-sso-core                    单点登录核心组件
    │   ├─ hzero-starter-sso-cas                     cas单点登录组件
    │   ├─ hzero-starter-sso-oauth                   oauth2单点登录组件
    │   ├─ hzero-starter-sso-saml                    saml单点登录组件
    │   ├─ hzero-starter-sso-azure                   微软云AD单点登录组件
    │   └─ hzero-starter-sso-idm                     idm单点登录组件
    ├─ hzero-starter-file-parent                     文件对象存储父组件
    │   ├─ hzero-starter-file-core                   对象存储支持核心组件
    │   ├─ hzero-starter-file-minio                  MinIO存储支持组件
    │   ├─ hzero-starter-file-aliyun                 阿里云存储支持组件
    │   ├─ hzero-starter-file-bos                    百度云存储支持组件
    │   ├─ hzero-starter-file-obs                    华为云存储支持组件
    │   ├─ hzero-starter-file-cos                    腾讯云存储支持组件
    │   ├─ hzero-starter-file-aws                    aws存储支持组件
    │   ├─ hzero-starter-file-azure                  微软存储支持组件
    │   ├─ hzero-starter-file-jdcloud                京东云存储支持组件
    │   └─ hzero-starter-file-ceph                   Ceph存储支持组件
    ├─ hzero-starter-social-parent                   三方登录父组件
    │   ├─ hzero-starter-social-core                 三方登录核心依赖    
    │   ├─ hzero-starter-social-qq                   三方QQ登录
    │   ├─ hzero-starter-social-wechat               三方微信登录
    │   ├─ hzero-starter-social-sina                 三方微博登录
    │   ├─ hzero-starter-social-wechat-enterprise    企业微信登录
    │   ├─ hzero-starter-social-hippius              海马汇三方登录组件
    │   └─ hzero-starter-social-apple                IOS苹果三方登录
    ├─ hzero-starter-integrate-parent                三方支持父组件
    │   ├─ hzero-starter-integrate-core              三方支持核心组件
    │   ├─ hzero-starter-integrate-wechat-official   微信公众号支持组件
    │   ├─ hzero-starter-integrate-dd                钉钉支持组件
    │   └─ hzero-starter-integrate-wechat-enterprise 企业微信支持组件
    ├─ hzero-starter-sms-parent                      短信服务父组件
    │   ├─ hzero-starter-sms-core                    短信服务核心支持组件
    │   ├─ hzero-starter-sms-aliyun                  阿里云短信支持组件
    │   ├─ hzero-starter-sms-qcloud                  腾讯云短信支持组件
    │   └─ hzero-starter-sms-baidu                   百度云短信支持组件
    ├─ hzero-starter-call-parent                     语音消息父组件
    │   ├─ hzero-starter-call-core                 语音服务支持组件
    │   └─ hzero-starter-call-jingdong              京东语音服务组件
    └─ hzero-template-parent                         登录模板父组件
        ├─ hzero-template-oauth-main                 简化版登录模板组件
        └─ hzero-template-oauth-slide                带轮播图的登录模板组件
```

## 安装

请遵循[安装文档](http://open.hand-china.com/document-center/doc/product/10067/10032?doc_id=5953)以安装HZERO，可根据自己实际情况选择安装模式。

## 开始使用HZERO

有关操作手册，请[阅读文档](http://open.hand-china.com/document-center/doc/product/10067/10032?doc_id=5967)。

## 开始开发

HZERO微服务开发框架有两个方面，即 **微服务后端**和 **前端**。

如果您要开发微服务后端，请参阅[微服务开发人员的文档](http://open.hand-china.com/document-center/doc/product/10067/10032?doc_id=5965)。

另外，借助[前端开发人员的文档](http://open.hand-china.com/document-center/doc/product/10067/10032?doc_id=5964)，您可以使用HZERO的前端样式。

## HZERO的组成

该存储库包含HZERO文档的源代码。如果您要查找单个组件，则可访问组件自己的存储库中。

### 前端应用

- [hzero-front](https://github.com/open-hand/hzero-front) - HZERO 前端使用AntD Pro进行封装拓展。核心技术栈包含React、AntD Pro 以及Node.js
- [hzero-template-parent](https://github.com/open-hand/hzero-template-parent)- HZERO登录首页模板，基于thymeleaf进行开发。目前支持标准页面和滑动页面两种。

### 后端微服务

- [hzero-register](https://github.com/open-hand/hzero-register) - 基于Eureka的平台注册中心服务，包括服务注册发现，服务健康检查，服务监控，注册中心其他功能。
- [hzero-config](https://github.com/open-hand/hzero-config)  - 配置服务作为配置中心，为微服务体系中的其他服务提供配置存储、配置推送的服务。Spring Cloud 提供了默认配置中心的实现，包含svn、git等几种实现，hzero-config则是基于数据库的实现，可以管理大数据量的配置，并且有更快捷的配置推送方式。
- [hzero-gateway](https://github.com/open-hand/hzero-gateway)  - HZERO网关服务，基于Spring Cloud Gateway进行二次封装，作为平台统一的对外出入口，主要有服务路由、鉴权、流量控制等管理功能。
- [hzero-oauth](https://github.com/open-hand/hzero-oauth)  - hzero-oauth 服务是基于 Spring Security、Spring OAuth2、JWT 实现的统一认证服务中心，登录基于 spring security 的标准登录流程。客户端授权支持 oauth2.0 的四种授权模式：授权码模式、简化模式、密码模式、客户端模式，授权流程跟标准的 oauth2 流程一致。web 端采用简化模式(implicit)登录系统，移动端可使用密码模式(password)登录系统。同时还支持基于 Spring Social 的三方账号登录方式，如微信/QQ、支付宝、微博等，并提供拓展模式，支持更多三方渠道。
- [hzero-swagger](https://github.com/open-hand/hzero-swagger)  - 用于对平台开发测试的API文档进行管理以及接口调试
- [hzero-admin](https://github.com/open-hand/hzero-admin)  - 管理服务，基础服务之一，把路由、限流、熔断等功能易用化，集中在管理服务来管控，提供自动化的路由刷新、权限刷新、swagger信息刷新服务，提供界面化的服务、配置、路由、限流、熔断管理功能以及Spring Boot Admin控制台。
- [hzero-platform](https://github.com/open-hand/hzero-platform)  - 平台基础功能服务，主要包含系统基础设置，如：系统配置、配置管理等；开发管理，如：值集管理、个性化管理、数据源管理等。
- [hzero-iam](https://github.com/open-hand/hzero-iam)  - 权限管理服务，平台统一的权限体系架构，用于管理角色、菜单、子账户等。
- [hzero-file](https://github.com/open-hand/hzero-file)  - 文件管理服务，提供简单易用的文件存储功能，具备对接多种云对象存储服务的能力且易于拓展，同时支持服务器ftp协议文件上传，支持大文件断点续传、文件预览、word在线编辑、pdf水印等。
- [hzero-message](https://github.com/open-hand/hzero-message) - 消息管理服务，支持短信、邮箱、企业微信、钉钉、电话语音、Webhook、站内消息发送，并能够灵活管理消息模板和对接云平台支持的微服务。
- [hzero-scheduler](https://github.com/open-hand/hzero-scheduler) - 分布式调度服务-Quartz服务端负责任务调度，任务的执行由执行器来完成。该服务具体包含执行器管理、并发任务管理、日志管理以及并发请求等。
- [hzero-report](https://github.com/open-hand/hzero-report) - 报表服务，通过配置数据集，执行SQL或者查询URL，获取数据以渲染平面报表、单据报表、图形报表的服务。
- [hzero-import](https://github.com/open-hand/hzero-import) - 通用导入服务，支持Excel、csv数据导入，支持自定义渲染Excel模板，自定义数据校验，自定义数据导入。Excel读取永远不会内存溢出。

### 服务插件
- [hzero-plugin-parent](https://github.com/open-hand/hzero-plugin-parent) - 服务可插拔功能管理服务，针对服务可插拔功能统一进行管理，服务需要用到时通过POM依赖的方式进行部署。
- [hzero-gateway-helper](https://github.com/open-hand/hzero-gateway-helper)  - 网关鉴权组件，提供鉴权的顶层接口端点以及鉴权过滤器接口，产品或项目完全可以自定义鉴权逻辑或者加入特定的鉴权逻辑。

### 服务客户端
- [hzero-boot-parent](https://github.com/open-hand/hzero-boot-parent) - 服务客户端管理服务，针对HZERO使用频率较高的功能从相应服务中抽取出客户端组件，便于服务中使用和日后的维护，服务中需调用时仅需引入相应客户端依赖并注入对应的客户端入口类即可。例如，在服务中需使用值集相关的API则直接引入平台服务客户端组件依赖并注入值集客户端入口类即可。

### 通用开发组件
- [hzero-starter-parent](https://github.com/open-hand/hzero-starter-parent) - HZERO通用开发组件，主要提供了HZERO服务内部的一些基础能力，包含通用mapper组件、通用redis工具组件、动态JDBC组件、SQL解析器组件等。

## 演示环境

您还可以体验HZERO的[试用申请](http://open.hand-china.com/market-home/detail/29?from=myProduct)。

## 支持

如果您有任何疑问并需要我们的支持，可以在[汉得开放平台](http://open.hand-china.com/)提交反馈或提交Issue。

您也可以通过发送邮件到 openhand@vip.hand-china.com 邮箱联系到我们。

## 关注公众号

您可以关注我们『四海汉得』微信公众号，了解最新动态信息。
<p align="left">
    <img src="https://developer.open.hand-china.com/static/world-hand.48c87276.png" width="200">
</p>
