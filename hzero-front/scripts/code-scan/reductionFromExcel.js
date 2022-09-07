const { readExcelAsync } = require('hzero-front-util/bin/buildin/excelUtils');
const { resolve, dirname } = require('hzero-front-util/bin/buildin/pathUtils');
const { writeYmlAsync } = require('hzero-front-util/bin/buildin/ymlUtils');
const { mkdirpAsync } = require('hzero-front-util/bin/buildin/fileUtils');

const reductionMaps = [
  {
    from: 'hzero-platform-prompt-v1.1.xlsx',
    to: 'intl-versions/v1.1.yml',
  },
];

function makeSureModule(obj, module) {
  if (!obj[module]) {
    obj[module] = {};
  }
}

function makeSureFeature(obj, module, feature) {
  makeSureModule(obj, module);
  if (!obj[module][feature]) {
    obj[module][feature] = {};
  }
}

function addIntl(obj, module, feature, code, meaning) {
  makeSureFeature(obj, module, feature);
  obj[module][feature][code] = meaning;
}

async function main() {
  await Promise.all(
    reductionMaps.map(async ({ from, to }) => {
      const data = await readExcelAsync(resolve(__dirname, 'data', from));
      await mkdirpAsync(dirname(resolve(__dirname, 'data', to)));
      const ymlObject = {};
      await Promise.all(
        Object.keys(data).map(async sheetName => {
          const sheet = data[sheetName];
          sheet.rows.forEach(([, organizationId, mf, code, lang, meaning], index) => {
            if ([0, '0'].includes(organizationId) && lang === 'zh_CN') {
              const [module, feature] = mf.split('.');
              addIntl(ymlObject, module, feature, code, meaning);
            }
          });
          await writeYmlAsync(resolve(__dirname, 'data', to), ymlObject);
        })
      );
    })
  );
}

main().catch(console.error);
