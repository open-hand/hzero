@echo off

call mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8

call mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8  -f ./hzero-starter-parent

call mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -f ./hzero-starter-sso-parent

call mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -f ./hzero-starter-file-parent

call mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -f ./hzero-starter-social-parent

call mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -f ./hzero-starter-integrate-parent

call mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -f ./hzero-starter-sms-parent

call mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -f ./hzero-starter-call-parent

call mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -f ./hzero-template-parent

call mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -f ./hzero-boot-parent

call mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -f ./hzero-gateway-helper

call mvn clean install -Dmaven.springboot.skip=true -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -f ./hzero-plugin-parent