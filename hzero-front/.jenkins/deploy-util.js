/* eslint-disable */

const gitUtils = require('hzero-webpack-scripts/utils/git-utils');
const fs = require('fs-extra');
const commander = require('commander');
const path = require('path');

commander
  .option('-e --ext <ext>', 'dist-ext')
  .option('-c --common <common>', '通用模块名称');

commander.parse(process.argv);

const ext = commander.ext || 'dist-ext';

const common = commander.common || 'hzero-front';

(async function start() {
  let changedPackages = [];
  const allPackages = (await fs.readdir(path.resolve(__dirname, '../packages'))).filter(v => !(v.endsWith('.json') || v.endsWith('.js')));
  // const dirs = await fs.readdir('dist-ext/packages');
  for (const package of allPackages) {
    try {
      // eslint-disable-next-line no-await-in-loop
      const json = await fs.readJson(path.resolve(__dirname, `../${ext}/packages/${package}/package.json`));
      const pattern = new RegExp(`${package}\\/.*\\.{1}\\w*$`);
      // eslint-disable-next-line no-await-in-loop
      if (await gitUtils.fileHasChangeFromCommit(pattern, json.commitHash)) {
        changedPackages.push(package);
      }
    } catch (e) {
      changedPackages.push(package);
    }
  }
  if (changedPackages.includes(common) || fs.existsSync(path.resolve(__dirname, '__dll_changed.txt'))) {
    changedPackages = allPackages;
    try {
      fs.remove(path.resolve(__dirname, '__dll_changed.txt'));
    } catch(e) {};
  }
  if (changedPackages.length) {
    try {
      await fs.writeFile(path.resolve(__dirname, `changed-packages.txt`), changedPackages.join(','), 'utf8');
    } catch (e) {}
  }
  
})();