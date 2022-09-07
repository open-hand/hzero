HZero前端工程改进方案操作说明
===

## 环境变量

* node.js: v8.x(>= v8.10.x) or v10.x
* 内存: 
  * 开发者模式运行内存: >4GB
  * 生产环境编译运行内存: >4GB
* npm: v6.x
* yarn: 全局安装`yarn`,安装命令如下

  ```bash
  $ npm install -g yarn
  ```

  > 关于`yarn`,请参考: [https://yarnpkg.com](https://yarnpkg.com)

* lerna: 全局安装`lerna`,安装命令如下:

  ```bash
  $ npm install -g lerna
  ```

  或者

  ```bash
  $ yarn global add lerna
  ```

  > 关于`lerna`,请参考: [https://lernajs.io/](https://lernajs.io/)

* 开发工具: 推荐使用`Visual Studio Code`

  > 关于`Visual Studio Code`,请参考[https://code.visualstudio.com/](https://code.visualstudio.com/)

  * `Visual Studio Code`常用插件

    * 格式化代码风格:   EditorConfig for VS Code 
    * 校验代码:              Eslint
    * git流程:                 GitLens — Git supercharged
    * yaml文件支持:      YAML, Bracket Pair 
    * 括号高亮:              Braclet Pair Colorizer2
    * 单词拼写检查:       Code Spell Checker
    * React代码片段:     ES7 React/Redux/GraphQL/React-Native snippets
    * 高亮 TODO: 等:    TODO Highlight
    * 文件图标:             Vscode-icons

## HZero Front Cli工具的使用

HZero Front Cli工具是用于创建/更新基于HZero Front的前端工程的命令行工具,项目地址: [https://code.choerodon.com.cn/hzero-hzero/hzero-front-cli](https://code.choerodon.com.cn/hzero-hzero/hzero-front-cli)

### 安装

执行如下命令安装cli(HZero Front Cli以下简称Cli)

使用`npm`

```bash
$ npm install -g hzero-front-cli --registry=http://nexus.saas.hand-china.com/content/groups/hzero-npm-group/
$ hzero-front-cli -v
```

### 主要命令

安装好`HZero Front Cli`工具后执行如下命令即可查看`hzero-front-cli`的所有命令

```bash
$ hzero-front-cli -h
```

| 命令           | 描述         | 用法 |
| ----------------| ------------ | -------------- |
| `[dir]`         | 创建新项目  | `$ hzero-front-cli [项目路径/名称dir]` example: `$ hzero-front-cli hzero-front-app` |
| `-v, --version` | 查看`Cli`版本号 | `$ hzero-front-cli -v` 或者 `$ hzero-front-cli --version` |
| `-i, ignore [files]` | 忽略需要被更新的框架(hzero-front-runtime)文件 | `$ hzero-front-cli hzero-front-app -i 'config/routers.js .eslintrc.js'` |
| `-h, --help`  | 查看`Cli`帮助命令 | `$ hzero-front-cli -h` 或者 `$ hzero-front-cli --help` |

## 创建基于HZero Front的新项目

关于`HZero Front`请参考[https://code.choerodon.com.cn/hzero-hzero/hzero-front](https://code.choerodon.com.cn/hzero-hzero/hzero-front)

安装好`HZero Front Cli`工具后执行如下命令,创建一个名为`hzero-front-app`的前端工程项目

```bash 
$ hzero-front-cli hzero-front-app
```

等待所有的操作执行完成后,执行如下命令

```bash
$ cd hzero-front-app
$ yarn start
```


**请注意`dll`文件是默认必须的,所以若第一次启动项目`dll`文件不存在或者有全新的node modules依赖,请先执行如下命令**

```bash
$ yarn dll
```


即可启动项目,启动成功后,请访问如下地址即可

```url
http://localhost:8000
```

项目目录结构如下

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
│   ├── hzero-front                                       // HZero Front package
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

### 更新`hzero-front`相关依赖

执行如下命令

```bash
$ cd your-hzero-front-app
$ yarn upgrade hzero-front --registry http://nexus.saas.hand-china.com/content/groups/hzero-npm-group/
```

## 开发流程

默认支持当前(原有)`hzero-platform-fe`开发模式,但目前对`hzero-front`代码做了一些改进

* 路由: `src/common/router.js` => `config/routers.js`

具体配置做了简化处理

```js
// 原有配置

'/spfm/supplier-kpi-indicator': {
  component: dynamicWrapper(app, ['spfm/supplierKpiIndicator'], () =>
    import('../routes/spfm/SupplierKpiIndicator')
  ),
  authorized: true,
},
```

改进后

```js
// 新的配置方式

{
  path: '/sslm/supplier-kpi-indicator',
  component: 'sslm/SupplierKpiIndicator',
  models: ['sslm/supplierKpiIndicatorOrg'],
},
```

* 国际化: 调整`@prompt` => `@formatterCollections`

### 代码迁移

与`hzero-platform-fe`代码结构相同

* models: 处理业务/组件功能数据逻辑
* services: 处理业务功能数据接口
* routes: 业务逻辑功能页面代码
* components: 基本组件
* utils: 公共通用方法

请注意路由的注册

### 更新项目代码(框架结构,hzero-front-runtime)

HZero Front Cli支持更新项目代码,执行如下命令

```bash
# 切换到项目目录下
$ cd hzero-front-app
$ hzero-front-cli .
```

执行如下命令可以忽略不需要更新的文件

```bash
# 切换到项目目录下
$ cd hzero-front-app
$ hzero-front-cli . -i 'config/routers.js package.json'
```

### 发布

将基于`hzero-front`前端项目作为模块(即模块化)发布到`nexus npm`私有源仓库

### 编译用于发布的版本

编译用于发布的版本仅需要用`babel`直接编译工程源码即可,需要使用`babel cli`相关命令,建议在`package.json`中的`script`加入下内容即可

```
"build:lib": "npx cross-env NODE_ENV=production BABEL_ENV=production node ./node_modules/@babel/cli/bin/babel.js src --out-dir lib --copy-files --ignore 'src/**/*.spec.js','src/**/*.test.js'"
```

执行如下命令,即可输出编译后的目录`lib`

```bash
$ yarn build:lib
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

### 子项目管理,packages

`HZero Front`子项目管理采用`lerna`管理

执行如下命令创建子项目`package`

```bash
$ lerna create <package name> packages 
```

执行如下命令安装项目依赖`node modules`,无需切换到各子项目下安装依赖

```bash
$ lerna bootstrap
```

执行如下命令来`build packages`

```bash
$ lerna run transpile
```

#### 子项目git管理

请注意,子项目多`package`代码仓库管理,默认使用`git`管理,请注意`git`子项目的配置和管理

