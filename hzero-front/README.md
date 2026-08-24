---
title: hzero-front
date: 2019-03-01
tag: HZero react dva webpack 
version: 0.10.0
---

HZero Front
===

本项目是基于`React`的构建页面的`JavaScript`UI库以及轻量级前端数据模型/状态管理框架`dva`, 并使用`webpack 4.x`构建本项目.

本项目主要HZero平台前端核心组件/模块/服务,支持通过`lerna`工具,将本项目作为`monorepo`(多模块,多`package`管理模式)模式作为多模块项目的核心子项目

我们提供了一套方案可以基于本项目快速构建全新的`monorepo`项目,其中包含如下工具

* [HZero Front Runtime](https://code.choerodon.com.cn/hzero-hzero/hzero-front-runtime)
* [HZero Front Cli](https://code.choerodon.com.cn/hzero-hzero/hzero-front-cli)

## 目录

* [介绍](##介绍)
* [使用](##使用)
* [项目目录](##项目目录)
* [Author](##Author)
* [Contributing](##Contributing)

## 技术栈

### 关于React

React是用于构建用户界面的JavaScript库,本项目采用全新的react v16.8.x,其中包含一些全新的特性.且本项目会持续同步react版本.

更多请参考[React Github](https://github.com/facebook/react)或[React官网](https://reactjs.org/)

### 关于dva框架

dva是基于 redux、redux-saga 和 react-router 的轻量级前端框架。

请参考[dva Github](https://github.com/dvajs/dva)，相关问题可以在[dva Github issues](https://github.com/dvajs/dva/issues)咨询

### 关于webpack

用于构建/打包前端工程,本项目采用全新webpack v4.28.x,其中包含全新的特性/性能优化/社区最佳实践

请参考[webpack](https://webpack.js.org)

### 关于Create React App

本项目是基于Create React App脚手架创建,并执行了`yarn eject`命令

请参考[Create React App](https://github.com/facebook/create-react-app).

## 使用

下面是关于本项目的使用说明

### 环境变量

* node.js: v10.x or v8.x(>= v8.10)

  > 关于node.js请参考: [https://nodejs.org/en/](https://nodejs.org/en/)

* 内存: 
  * 开发者模式运行内存: >4GB
  * 生产环境编译运行内存: >4GB

* yarn: 推荐使用yarn管理本项目

  > 执行如下命令全局安装yarn
  > ```
  > $ npm install --global yarn 
  > ```
  > 
  > 关于`yarn`请参考 [https://yarnpkg.com](https://yarnpkg.com)

* lerna: 用于管理具有多个`package`的`JavaScript`项目的工具。

  > 执行如下命令全局安装
  > ```
  > $ npm install --global lerna
  > ```
  > 
  > 关于`lerna`请参考[https://lernajs.io/](https://lernajs.io/)

* 开发工具: 推荐使用Visual Studio Code编辑器

  > Visual Studio Code推荐插件:
  > * Chinese (Simplified) Language Pack for Visual Studio Code
  > * Debugger for Chrome
  > * EditorConfig for VS Code
  > * ESLint
  > * GitLens — Git supercharged
  > * YAML
  > * Code Spell Checker

## Contributing

关于本项目(hzero-front/hzero-front-*)开发方式/流程说明如下:

### 下载/Clone

使用如下命令clone本项目

```shell
git clone https://code.choerodon.com.cn/hzero-hzero/hzero-front.git
cd hzero-front
```

### 初始化本项目

**由于本项目使用lerna管理项目packages,所以初始化项目请务必执行如下初始化命令,确保主体项目和`packages`子项目依赖安装正确**

执行如下命令,安装本项目`packages`依赖(即初始化`workspace`)

```bash
$ lerna bootstrap --registry http://nexus.saas.hand-china.com/content/groups/hzero-npm-group/
```

执行如下命令,安装本项目依赖

```bash
$ yarn --registry http://nexus.saas.hand-china.com/content/groups/hzero-npm-group/
```

> 在开发模式下,可以执行如下命令可以跳过puppeteer安装过程中下载Chromium
> 
> ```bash
> $ export PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=1 #macos/linux
> # set PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=1 #windows
> ``````

`build dll`: 本项目开启`webpack dll`插件,所以在执行`启动/build`操作之前,`请务必执行如下命令`

```bash
$ yarn build:dll
```

### 启动项目/开发者模式

`hzero-front`主体工程依赖于`packages`下的各模块,所以需要编译`packages`下的各模块,执行如下命令

```bash
$ yarn transpile:prod
```

或者

```bash
$ lerna run transpile
```

接下来,确保`dll`操作已经执行成功后,执行如下命令,即可启动`hzero-front`主体工程

```bash
$ yarn start
```

启动成功后,请访问如下地址即可

```url
http://localhost:8000
```

#### 启动单个packages模块服务

本项目中`packages`模块均可独立编译/打包/部署,即支持`webpack-dev-server`,所以执行如下命令启动`packages`模块

```bash
$ cd packages/<hzero front module>
# cd packages/hzero-front-hiam
```

`packages`模块项目也开启`webpack dll`插件,所以在执行`启动/build`操作之前,`请务必执行如下命令`

```bash
$ yarn build:dll
```

再执行如下命令即可启动单个packages模块服务

```bash
$ yarn start
```

> 初始化子模块
>
> 若在项目中使用了`git submodule`进行子模块管理，需要在首次拉取代码后进行初始化操作。
>
> ```bash
> $ git submodule update --init --recursive
> ```

### 构建

在执行完`build dll`操作之后,执行如下命令即可构建用于生产环境的项目

```bash
$ yarn build
```

### Nginx 配置

文件 `docker/default.conf` 为默认的项目`Nginx`配置文件，如需修改`Nginx`配置需要在此更改，在构建 `Docker`镜像时,`Dockerfile`中的指令会将该文件复制到`nginx`配置目录下。

由于基础镜像暂未开启 `gzip`，在本项目`Dockerfile`中存在指令来更改默认的`Nginx`配置文件，以使`gzip`生效，如下：

```shell
RUN sed -i 's/\#gzip/gzip/g' /etc/nginx/nginx.conf;
```

如需关闭 gzip，将该行注释即可。

### 项目目录

```bash
.
├── CHANGELOG.md                                          // 项目变更日志
├── Dockerfile                                            // docker配置文件
├── README.md                                             // 项目说明
├── charts
├── config                                                // 项目基本配置,包含webpakc相关/路由相关/测试相关/样式相关
│   ├── alias.js                                          // webpack.config别名alias配置
│   ├── env.js                                            // node.js环境变量配置
│   ├── jest                                              // jest单元测试工具配置文件
│   │   ├── cssTransform.js
│   │   └── fileTransform.js
│   ├── paths.js                                          // 静态文件路径配置文件
│   ├── routers.js                                        // 项目菜单路由配置文件
│   ├── theme.js                                          // 默认样式配置文件
│   ├── webpack.config.js                                 // webpack默认配置文件
│   ├── webpack.dll.config.js                             // webpack dll插件配置文件
│   └── webpackDevServer.config.js                        // webpack dev server开发者模式配文件
├── docker                                                // docker镜像配置相关
│   ├── default.conf                                      // nginx 配置文件
│   └── enterpoint.sh
├── jsconfig.json                                         // 开发工具(Visual Studio Code)代码兼容性配置文件
├── lerna.json                                            // lerna多package JavaScript项目管理配置文件
├── mock                                                  // mock静态数据服务配置相关
│   ├── ...
│   └── index.js
├── package.json                                          // 本项目配置node.js 配置文件
├── packages
│   ├── hzero-front                                       // hzero-front模块包含hzero-front主要components/utils等
│   ├── hzero-front-hagd                                  // hzero-front-hagd 模块
│   ├── hzero-front-hcnf                                  // hzero-front-hcnf 模块
│   ├── hzero-front-hdtt                                  // hzero-front-hdtt 模块
│   ├── hzero-front-hfile                                 // hzero-front-hfile 模块
│   ├── hzero-front-hiam                                  // hzero-front-hiam 模块
│   ├── hzero-front-himp                                  // hzero-front-himp 模块
│   ├── hzero-front-hitf                                  // hzero-front-hitf 模块
│   ├── hzero-front-hmsg                                  // hzero-front-hmsg 模块
│   ├── hzero-front-hpfm                                  // hzero-front-hpfm 模块
│   ├── hzero-front-hptl                                  // hzero-front-hptl 模块
│   ├── hzero-front-hrpt                                  // hzero-front-hrpt 模块
│   ├── hzero-front-hsdr                                  // hzero-front-hsdr 模块
│   ├── hzero-front-hsgp                                  // hzero-front-hsgp 模块
│   └── hzero-front-hwfl                                  // hzero-front-hwfl 模块
├── public                                                // 公共静态资源目录
│   ├── ...
│   └── index.html                                        // 本项目主页面html文件
├── scripts                                               // 本项目脚本文件包括webpack/模块化编译等 
│   ├── build.js                                          // 生产环境编译脚本文件
│   ├── build.lib.js                                      // babel模块化编译脚本文件 
│   ├── start.js                                          // 项目开发者模式dev server启动脚本文件
├── src                                                   // 工作目录,包含项目业务逻辑代码等
│   ├── assets                                            // 静态小资源目录
│   ├── index.js                                          // 项目入口文件
│   ├── index.less                                        // 项目全局样式
│   ├── models                                            // 项目数据模型
│   │   └── global.js                                     // 全局数据模型
│   ├── router.js                                         // 路由管理逻辑文件
│   ├── routes                                            // 项目核心业务逻辑/页面 
│   │   └── index.js                                      // 入口文件
│   ├── serviceWorker.js                                  // 静态缓存service worker 
│   ├── services                                          // 项目接口逻辑层 
│   ├── setupProxy.js                                     // mock静态数据代理服务器配置文件
│   └── utils                                             // 项目业业务逻辑通用方法 
│       └── router.js                                     // 路由控制逻辑文件
└── yarn.lock                                             // 项目yarn node.js模块配置文件 
```

### 版本管理

本项目采用`conventional-changelog`和`standard-version`管理`CHANGELOG`和版本管理,包括`git tags`的管理

### 发布

将本项目发布到`nexus npm`私有源仓库

### 编译用于发布的版本

执行如下命令

```bash
$ cd packages/<hzero front module>
$ yarn transpile
```

#### 生成 `auth hash`

执行如下命令

```bash
echo -n 'username:password' | openssl base64
```

将生成的`auth hash`按照如下方式配置

```conf
email=yourname@hand-china.com
always-auth=true
_auth=yourbase64hashcode
```

执行如下命令将上面的配置加入到`node.js`全局环境变量配置文件`.npmrc`中

```bash
$ npm config edit 
```

再执行如下命令发布即可

```bash
$ npm publish --registry http://nexus.saas.hand-china.com/content/repositories/hzero-ui/
```

### Git使用规范

#### Git 配置

git配置中，用户名需使用本人姓名，邮箱使用公司邮箱。

```shell
git config --global user.name "yourname"
git config --global user.email "youremail@hand-china.com"
```

#### Git Commit guide

* 分支使用

1. 分支标识符

feature: 新功能开发/新服务开发
hotfix/bugfix: bug 修复

2. 开发使用

新功能/新服务开发，需要创建 `feature` 分支。

规范：

***feature-工号-服务***

例如：

***feature-13492-hiam***

3. 版本 bug 修复

对于已经发版的功能，bug 修复时，需要创建对应版本的分支。

规范：

***hotfix-vxxx-工号***

例如：

***hotfix-v1.0.x-15664***

* commit 操作标识符

```shell
fix：修复bug
feat：更新/新增文件/新特性
delete：删除文件
```

* commit 规范

```shell
[操作标识符][服务][空格]][功能模块][:][commit 内容]
```

例如：

```shell
fix(hzero-front-hitf) 应用类型: 修改测试映射类字段

feat(hzero-front-hchg) 服务计费: 添加服务计费功能

feat(hzero-front) im: 开发临时会话功能,优化消息类别处理
```

>备注：
> 1. 服务指的是：hzero-front-hpfm、hzero-front、hzero-front-hiam这些服务模块；
> 2. 功能模块指的是具体的功能，比如：角色管理，子账户管理；
> 3. 如果修改的是更具体的功能，与菜单无关的，比如，hzero-front的DefaultLayout布局，则功能模块指的就是DefaultLayout；

## Author

@中台技术中心·HZero技术团队
