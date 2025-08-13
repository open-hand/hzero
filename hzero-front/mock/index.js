const delay = require('mocker-api/utils/delay');
const user = require('./hpfm/user');
const menu = require('./hpfm/menu');

const mock = {
    user,
    menu,
};

// 是否禁用代理
const noProxy = process.env.NO_PROXY === 'true';

let allMock = {};
if (!noProxy) {
    Object.keys(mock).forEach((mockKey) => {
        allMock = Object.assign(allMock, mock[mockKey]);
    });
}
module.exports = noProxy ? allMock : delay(allMock, 1000);