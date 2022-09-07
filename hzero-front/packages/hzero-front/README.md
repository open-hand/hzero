---
title: hzero-front-runtime
date: 2019-03-01
tag: HZero react dva webpack runtime lerna package yarn
version: 1.0.0
---

HZero Front Runtime
===

本项目是基于`React`的构建页面的`JavaScript`UI库以及轻量级前端数据模型/状态管理框架`dva`, 并使用`webpack 4.x`构建本项目.

本项目主要为基于hzero-front创建的项目提供基础运行/开发环境/配置等,支持通过`lerna`工具,将本项目作为`monorepo`(多模块,多`package`管理模式)模式作为多模块项目的核心子项目

我们提供了一套方案可以基于本项目快速构建全新的`monorepo`项目,配合[`HZero Front Cli`](https://code.choerodon.com.cn/hzero-hzero/hzero-front-cli)工具即可轻松搭建全新的基于`HZero Front`的前端工程

## 目录

* [介绍](##介绍)
* [使用](##使用)
* [项目目录](##项目目录)
* [Author](##Author)
* [Contributing](##Contributing)

## 介绍

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

* yarn: 推荐使用yarn管理本项目

  > 执行如下命令全局安装yarn
  > ```
  > $ npm install --global yarn 
  > ```
  > 
  > 关于`yarn`请参考 [https://yarnpkg.com](https://yarnpkg.com)

* lerna: 用于管理具有多个`package`的`JavaScript`项目的工具。

  > A tool for managing JavaScript projects with multiple packages.
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

### 安装runtime

执行如下命令

```
$ npm install hzero-front-runtime --registry=http://nexus.saas.hand-china.com/content/groups/hzero-npm-group/
```

或者

```
$ yarn add hzero-front-runtime --registry=http://nexus.saas.hand-china.com/content/groups/hzero-npm-group/
```

### 下载/Clone

您可以使用如下命令下载本项目

```shell
git clone https://code.choerodon.com.cn/hzero-hzero/hzero-front-runtime.git
cd hzero-front-runtime
```

**本项目依赖于`packages`,需要默认安装`HZero Front`才能执行如下操作**

### 初始化项目

执行如下命令

```shell
$ yarn 
```

若安装了`packages`,则执行如下命令

```
$ lerna bootstrap
```

`build dll`: 本项目开启`webpack dll`插件,所以在执行`启动/build`操作之前,`请务必执行如下命令`

```shell
$ yarn dll
```

### 使用子模块

由于在项目中使用了 git submodule 进行子模块管理，需要在首次拉取代码后进行初始化操作。

```shell
git submodule update --init --recursive
```

### 启动项目/开发者模式

在执行完`build dll`操作之后,执行如下命令

```shell
$ yarn start
```

```shell
cross-env BASE_PATH=/ CLIENT_ID=localhost BPM_HOST=http://192.168.12.103:8330 API_HOST=http://hzeronb.saas.hand-china.com WEBSOCKET_HOST=ws://172.20.0.202:8260 HARD_SOURCE=none node --max_old_space_size=4096 ./node_modules/roadhog/bin/roadhog.js dev
```

* **start 会设置几个环境变量, 您可以改变他们来适应自己的项目**
* BASE_PATH: 部署在子目录时需要改变。 例如 部署在 /demo/ 下; 则该变量的值为 /demo/
* CLIENT_ID: 是hzero后端所需要的客户端参数
* BPM_HOST: 工作流的接口地址
* API_HOST: 请求接口的地址
* WEBSOCKET_HOST: websocket 地址

启动成功后,请访问如下地址即可

```url
http://localhost:8000
```

### 构建

在执行完`build dll`操作之后,执行如下命令即可构建用于生产环境的项目

```shell
$ yarn build
```

最终静态文件会生成至如下目录

```shell
/dist
```

**build 会设置几个环境变量, 您可以改变他们来适应自己的项目。**

变量名及含义如下表所示：

| 变量名           | 含义                                                         | 构建后需要替换 |
| ---------------- | ------------------------------------------------------------ | -------------- |
| BASE_PATH        | 部署在子目录时需要改变。 例如 部署在 /demo/ 下; 则该变量的值为 /demo/ | 是             |
| CLIENT_ID        | hzero 后端进行 OAUTH 认证所需要的客户端参数                  | 是             |
| BPM_HOST         | 工作流的接口地址                                             | 是             |
| API_HOST         | 请求接口的地址                                               | 是             |
| WEBSOCKET_HOST   | websocket 地址                                               | 是             |
| ESLINT           | 由于在提交的时候已经校验过了 所以这里不执行校验以提升打包速度 | 否             |
| PLATFORM_VERSION | 系统是OP版还是SAAS版                                         | 是             |

#### 替换操作

如果采用 jenkins 进行构建，则需要在 build 结束后手动执行 `./docker/enterpoint.sh` 进行变量替换，因而需要在脚本中填写实际的变量值。

而如果采用 gitlab CI 进行构建，这些变量的值配置于 `/charts/hzero-front/values.yaml` 内，在构建时会将这些变量设置到当前执行环境，在脚本执行时便可读取以进行替换。

### Nginx 配置

文件 `docker/default.conf` 为默认的项目 Nginx 配置文件，如需修改 Nginx 配置需要在此更改，在构建 Docker 镜像时，Dockerfile 中的指令会将该文件复制到 nginx 配置目录下。

由于基础镜像暂未开启 `gzip`，在本项目 Dockerfile 中存在指令来更改默认的 Nginx 配置文件，以使 gzip 生效，如下：

```shell
RUN sed -i 's/\#gzip/gzip/g' /etc/nginx/nginx.conf;
```

如需关闭 gzip，将该行注释即可。

### lerna的用法

执行如下命令即可创建/管理packages

#### 初始化项目

```
$ lerna init
```

#### 安装packages和主体项目依赖

```
$ lerna bootstrap
```

#### 运行子项目/主体脚本

```
$ lerna run [script]
```

#### 导入package

```
$ lerna import <pathToRepo>
```

#### 创建package

```
$ lerna create <package name> packages
```

### 更多可执行脚本

* `precommit`: 执行`git commit`操作之前做`lint-staged`代码检查
* `lint`: 执行`eslint`代码检查和`stylelint`样式检查
* `lint:fix`: 执行`eslint`代码检查并修复和`stylelint`样式检查并修复
* `lint-staged`: 执行`lint-staged`代码检查
* `lint-staged:js`: 执行`eslint` `JavaScript`代码检查
* `lint:style`: 执行`stylelint`样式检查并修复
* `test`: 执行单元测试命令
* `changelog`: 执行变更日志CHANGELOG.md文件生成
* `prettier`: 执行`prettier`用于美化代码
* `tree`: 查看项目目录结构,该命令windows系统支持有限

## 项目目录

```shell
.
├── CHANGELOG.md                                          // 项目变更日志
├── README.md                                             // 项目说明
├── charts                                                // gitlab配置文件
├── config                                                // 项目基本配置,包含webpakc相关/路由相关/测试相关/样式相关
│   ├── env.js                                            // node.js环境变量配置
│   ├── jest                                              // jest单元测试工具配置文件
│   │   ├── cssTransform.js
│   │   └── fileTransform.js
│   ├── paths.js                                          // 静态文件路径配置文件
│   ├── routers.js                                        // 项目菜单路由配置文件
│   ├── theme.js                                          // 默认样式配置文件
│   ├── webpack.config.js                                 // webpack默认配置文件
│   ├── webpack.dll.config.js                             // webpack dll插件配置文件
│   └── webpackDevServer.config.js                        // webpack dev server开发者模式配置文件
├── dist                                                  // 可用于生产环境的静态文件目录
├── dll                                                   // build dll生成的dll文件
├── docker                                                // docker镜像配置相关
│   └── enterpoint.sh
├── jsconfig.json                                         // 开发工具(Visual Studio Code)代码兼容性配置文件
├── lerna.json                                            // lerna多package JavaScript项目管理配置文件
├── lib                                                   // babel用于开发者模块化编译生成目录
├── mock                                                  // mock静态数据服务配置相关
│   ├── ...
│   └── index.js
├── package.json                                          // 本项目配置node.js 配置文件
├── packages                                              // 子项目package相关
│   └── ...
├── public                                                // 公共静态资源目录
│   ├── error.html
│   ├── favicon.ico
│   ├── favicon.png
│   ├── hzero-logo.svg
│   ├── images
│   │   ├── ...
│   ├── index.html
│   ├── lib
│   │   ├── es6-sham.min.js
│   │   ├── es6-shim.min.js
│   │   └── tinymce
│   ├── manifest.json
│   ├── suggestBrowser.html
│   └── tinymce
│       ├── langs
│       └── skins
├── scripts                                                // 本项目脚本文件包括webpack/模块化编译等 
│   ├── build.js                                           // 生产环境编译脚本文件
│   ├── build.lib.js                                       // babel模块化编译脚本文件 
│   ├── start.js                                           // 项目开发者模式dev server启动脚本文件 
│   └── test.js                                            // 单元测试脚本文件
├── src                                                    // 工作目录,包含项目业务逻辑代码等
│   ├── index.js                                           // 项目入口文件
│   ├── index.less                                         // 项目全局样式
│   ├── models                                             // 项目数据模型
│   │   └── global.js                                      // 全局数据模型
│   ├── router.js                                          // 项目核心业务逻辑/页面 
│   ├── routes
│   │   └── index.js                                       // 入口文件 
│   ├── serviceWorker.js                                   // 静态缓存service worker 
│   ├── setupProxy.js                                      // mock静态数据代理服务器配置文件
│   └── utils                                              // 项目业业务逻辑通用方法 
│       └── router.js                                      // 路由工具 
└── yarn.lock                                              // 项目yarn node.js模块配置文件 
```

## Author

@中台技术中心·HZero技术团队

## Contributing

### 使用与开发指引

[core-develop-guide](https://rdc.hand-china.com/gitlab/hzero/hzero-fe/tree/master/docs/antd-develop-guide)

### 发布

将本项目发布到`nexus npm`私有源仓库 

#### 生成 `auth hash`

执行如下命令

```
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

#### Clone

```shell
git clone https://code.choerodon.com.cn/hzero-hzero/hzero-front-runtime.git
cd hzero-front-runtime
```

#### Git global setup

```shell
git config --global user.name "yourname"
git config --global user.email "youremail@hand-china.com"
```

#### Commit & push

```shell
git add yourfile
git commit -m "your commit message"
git push -u origin master
```

#### Commit guide

本项目采用如下规则

```shell
[操作][:][空格][commit内容]
```

`[commit内容]`请详细填写具体的文件新增/修改/删除操作过程

例如

```shell
fix: 修复查询功能bug
```

* 操作标识符

```shell
fix：修复bug
update：更新文件
add：新增文件
modify：重命名
delete：删除文件
```
