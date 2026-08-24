#!/usr/bin/env bash

# jenkins部署脚本

set -e # 报错不继续执行

rm -f deploy.tar.gz
yarn install

export DIST_PATH=dist
export COMMON_PACKAGE=hzero-front

if [[ "$HZERO_BUILD_MODULE" == "all" ]]; then

	echo 全量编译中....
  yarn transpile
  yarn build:dll
	yarn build
	tar -czf deploy.tar.gz $DIST_PATH

elif [[ ! -n "$HZERO_BUILD_MODULE" ]] || [[ "$HZERO_BUILD_MODULE" =~ "_check_git" ]]; then

	echo HZERO_BUILD_MODULE 自动监测
	node .jenkins/deploy-util.js -e $DIST_PATH -c $COMMON_PACKAGE

	if [[ -f ".jenkins/changed-packages.txt" ]]; then

		echo changed-packages.txt存在
		PACAKGES=$(cat .jenkins/changed-packages.txt)
		npx hzero-cli transpile $PACAKGES
		echo running npx cross-env BUILD_DIST_PATH=./dist BUILD_SKIP_PARENT=true npx hzero-cli build --only-build-micro $PACAKGES
		npx cross-env BUILD_DIST_PATH=$DIST_PATH BUILD_PUBLIC_MS=true BUILD_SKIP_PARENT=true npx hzero-cli build --only-build-micro $PACAKGES

	fi

elif [[ "$HZERO_BUILD_MODULE" =~ "master" ]]; then

	echo 编译父工程中....
	if [[ -d "$DIST_PATH" ]] && [[ -d "$DIST_PATH/packages" ]]; then
		cp -r $DIST_PATH/packages dist_backup
	fi
	yarn run build --only-build-parent
	cp -r $DIST_PATH deploy
	rm -rf deploy/packages
	tar -czf deploy.tar.gz deploy
	if [ -d "dist_backup" ]; then
		echo dist_backup 存在。复制中...
		rm -rf $DIST_PATH/packages
		cp -r dist_backup $DIST_PATH/packages
	fi
	rm -rf deploy dist_backup

elif [[ "$HZERO_BUILD_MODULE" =~ "dll" ]]; then

	echo 构建dll中....
	yarn build:dll
  cat > .jenkins/__dll_changed.txt << END
    changed
END

else

	echo "编译子模块: $HZERO_BUILD_MODULE"
	npx cross-env BUILD_DIST_PATH=$DIST_PATH BUILD_PUBLIC_MS=true BUILD_SKIP_PARENT=true npx hzero-cli build --only-build-micro $HZERO_BUILD_MODULE
	cat > .jenkins/changed-packages.txt << END
		$HZERO_BUILD_MODULE
END

fi

if [[ -f ".jenkins/changed-packages.txt" ]]; then
  echo 打包文件

	if [[ -d "deploy" ]]; then
		rm -rf deploy
	fi

	mkdir deploy
	mkdir deploy/packages

	for item in $(cat .jenkins/changed-packages.txt | sed 's/,/ /g'); do
		if [[ ! "$item" == "$COMMON_PACKAGE" ]]; then
			cp -r $DIST_PATH/packages/$item deploy/packages/$item
		fi
	done
	cp $DIST_PATH/packages/microConfig.js deploy/packages/microConfig.js
	cp $DIST_PATH/packages/microConfig.json deploy/packages/microConfig.json
	# cp -r $DIST_PATH/static deploy/
	tar -czf deploy.tar.gz deploy
	rm -rf deploy

fi

rm -f .jenkins/changed-packages.txt
