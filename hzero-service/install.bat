@echo off

call mvn clean install

call mvn clean install -f ./hzero-starter-parent

call mvn clean install -f ./hzero-starter-sso-parent

call mvn clean install -f ./hzero-starter-file-parent

call mvn clean install -f ./hzero-starter-social-parent

call mvn clean install -f ./hzero-starter-integrate-parent

call mvn clean install -f ./hzero-starter-sms-parent

call mvn clean install -f ./hzero-starter-call-parent

call mvn clean install -f ./hzero-template-parent

call mvn clean install -f ./hzero-boot-parent

call mvn clean install -f ./hzero-gateway-helper

call mvn clean install -f ./hzero-plugin-parent