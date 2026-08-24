#!/bin/bash

mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8

cd hzero-starter-parent
mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8

cd ../hzero-starter-sso-parent
mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8

cd ../hzero-starter-file-parent
mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8

cd ../hzero-starter-social-parent
mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8

cd ../hzero-starter-integrate-parent
mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8

cd ../hzero-starter-sms-parent
mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8

cd ../hzero-starter-call-parent
mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8

cd ../hzero-template-parent
mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8

cd ../hzero-boot-parent
mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8

cd ../hzero-gateway-helper
mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8

cd ../hzero-plugin-parent
mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8
