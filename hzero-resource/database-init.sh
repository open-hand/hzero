#!/usr/bin/env bash
if [ ! -f hzero-tool-data-install.jar ]
then
    curl http://nexus.saas.hand-china.com/content/repositories/Hzero-Release/org/hzero/tool/hzero-installer/0.2.1.RELEASE/hzero-installer-0.2.1.RELEASE.jar -o ./hzero-tool-data-install.jar
fi


echo -e "\nHZERO 1.5.RELEASE 更新开始..............\n\n"

echo "启动工具..."


java -Dinstaller.mapping=docs/mapping/service-mapping.xml -jar hzero-tool-data-install.jar

