const { writeYmlAsync } = require('hzero-front-util/bin/buildin/ymlUtils');
const { writeExcelAsync } = require('hzero-front-util/bin/buildin/excelUtils');
const { writeFileAsync } = require('hzero-front-util/bin/buildin/fileUtils');
const { resolve } = require('hzero-front-util/bin/buildin/pathUtils');

function transformUILocale(locale, name, propKey) {
  const thirdUIIntls = buildAllKeyOne(locale);
  const thirdUI = {
    hzero: {
      [propKey]: thirdUIIntls,
    },
  };
  const thirdUIExcelData = {
    locale: {
      rowHeaders: ['模块', '功能', '组合编码', 'zh_CN'],
      rows: [],
    },
  };

  const thirdUIWriteIntls = Object.keys(thirdUIIntls).map(code => {
    if (thirdUIIntls[code] === '确 定') {
      thirdUIIntls[code] = '确定';
    }
    thirdUIExcelData.locale.rows.push([`hzero.${propKey}`, code, `hzero.${propKey}.${code}`, thirdUIIntls[code]]);
    return `intl.get('hzero.${propKey}.${code}').d('${thirdUIIntls[code]}');`;
  });
  writeYmlAsync(resolve(__dirname, `${name}.locale.yml`), thirdUI);
  writeExcelAsync(resolve(__dirname, `${name}.locale.xlsx`), thirdUIExcelData);
  thirdUIWriteIntls.unshift('');
  thirdUIWriteIntls.unshift('const intl = require(\'utils/intl\');');
  writeFileAsync(
    resolve(__dirname, '../../src/locale', `${name}.locale.extra.js`),
    thirdUIWriteIntls.join('\n'),
  );
}

function buildAllKeyOne(obj, prefix = '') {
  const rt = {};
  Object.keys(obj)
    .forEach(k => {
      if (
        ['bigint', 'boolean', 'number', 'string', 'symbol', 'undefiend'].includes(typeof obj[k]) ||
        obj[k] === null
      ) {
        rt[prefix + k] = obj[k];
      } else {
        Object.assign(rt, buildAllKeyOne(obj[k], `${prefix + k}.`));
      }
    });
  return rt;
}

module.exports.transformUILocale = transformUILocale;
