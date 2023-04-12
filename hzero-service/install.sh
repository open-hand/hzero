#!/bin/bash

mvn clean install

cd hzero-starter-parent
mvn clean install

cd ../hzero-starter-sso-parent
mvn clean install

cd ../hzero-starter-file-parent
mvn clean install

cd ../hzero-starter-social-parent
mvn clean install

cd ../hzero-starter-integrate-parent
mvn clean install

cd ../hzero-starter-sms-parent
mvn clean install

cd ../hzero-starter-call-parent
mvn clean install

cd ../hzero-template-parent
mvn clean install

cd ../hzero-boot-parent
mvn clean install

cd ../hzero-gateway-helper
mvn clean install

cd ../hzero-plugin-parent
mvn clean install
