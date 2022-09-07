#!/usr/bin/env bash

# jenkins 脚本文件

set -e # 报错不继续执行

export TEMP_DIR=./.cache/
mkdir -p $TEMP_DIR

export REQUIRE_RE_NPM_INSTALL=false
export YARN_LOCK_CHANGE_LOG=""
if [ -f "$TEMP_DIR/.last_build_parent_git_head" ]; then
  export LAST_BUILD_PARENT_GIT_HEAD=`cat $TEMP_DIR/.last_build_parent_git_head` # 获取上一次 build 父项目时的 提交代码版本
  export CURRENT_GIT_HEAD=`git log -1 --pretty=format:"%H"` # 获取当前提交代码版本
  export YARN_LOCK_CHANGE_LOG=`git diff $LAST_BUILD_PARENT_GIT_HEAD $CURRENT_GIT_HEAD  --shortstat -- yarn.lock` # 对比两次提交版本中的 yarn.lock 是否变化
  if [[ -n "$YARN_LOCK_CHANGE_LOG" ]] ;then # 如果 yarn.lock 发生变化, 需要更新缓存。
    echo -e "gitlab-ci -- yarn.lock 发生变化, 需要清除之前编译时留下来的缓存。\n\t $YARN_LOCK_CHANGE_LOG"
    export REQUIRE_RE_NPM_INSTALL=true
  fi
else
  export REQUIRE_RE_NPM_INSTALL=true
fi
if  [[ $REQUIRE_RE_NPM_INSTALL =~ "true" ]] ;then
  rm -rf dll dist node_modules
  yarn install
  echo `git log -1 --pretty=format:"%H"` > $TEMP_DIR/.last_build_parent_git_head  # 保存当前 build 父项目时的 提交代码版本
fi

cat > src/config/.env.production.local.yml  << END

BASE_PATH: BUILD_BASE_PATH
PLATFORM_VERSION: BUILD_PLATFORM_VERSION
CLIENT_ID: BUILD_CLIENT_ID
GENERATE_SOURCEMAP: false
API_HOST: BUILD_API_HOST
WEBSOCKET_HOST: BUILD_WEBSOCKET_HOST
SKIP_TS_CHECK_IN_START: false
SKIP_ESLINT_CHECK_IN_START: false

END

yarn run build:production

rm -rf ./html
cp -r ./dist ./html

export BUILD_BASE_PATH=/
export BUILD_API_HOST=http://backend.hft.jajabjbj.top
export BUILD_CLIENT_ID=localhost
export BUILD_WFP_EDITOR=""
export BUILD_WEBSOCKET_HOST=ws://ws.hft.jajabjbj.top
export BUILD_PLATFORM_VERSION=SAAS
export BUILD_BPM_HOST=http://bpm.jd1.jajabjbj.top
export BUILD_IM_WEBSOCKET_HOST=ws://im.ws.jd1.jajabjbj.top

find ./html -name '*.js' | xargs sed -i "s BUILD_BASE_PATH $BUILD_BASE_PATH g"
find ./html -name '*.js' | xargs sed -i "s BUILD_API_HOST $BUILD_API_HOST g"
find ./html -name '*.js' | xargs sed -i "s BUILD_CLIENT_ID $BUILD_CLIENT_ID g"
find ./html -name '*.js' | xargs sed -i "s BUILD_BPM_HOST $BUILD_BPM_HOST g"
find ./html -name '*.js' | xargs sed -i "s BUILD_WFP_EDITOR $BUILD_WFP_EDITOR g"
find ./html -name '*.js' | xargs sed -i "s BUILD_WEBSOCKET_HOST $BUILD_WEBSOCKET_HOST g"
find ./html -name '*.js' | xargs sed -i "s BUILD_PLATFORM_VERSION $BUILD_PLATFORM_VERSION g"

# 这里实现你的部署逻辑 deploy ./html

export CICD_EXECUTION_SEQUENCE=${BUILD_NUMBER:-1}
docker build . -t  hzero-front-sample:${CICD_EXECUTION_SEQUENCE}
docker rm -f hzero-front-sample 2>/dev/null
docker run --rm -it --name hzero-front-sample hzero-front-sample:${CICD_EXECUTION_SEQUENCE}
