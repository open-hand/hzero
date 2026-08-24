const path = require('path');
const fs = require('fs');
const util = require('util');
const exec = util.promisify(require('child_process').exec);
const chalk = require('chalk');
const inquirer = require('inquirer');
const ora = require('ora');

const npmPublish = 'npm publish --registry http://nexus.saas.hand-china.com/content/repositories/hzero-ui/';
const packages = fs.readdirSync(path.resolve(__dirname, '../packages'));
const spinner = new ora();

/**
 * 获取模块的 package.json 数据
 * @param {string} module 子模块
 * @param {string?} key 指定获取的数据字段
 */
function getModuleInfo(module, key) {
  let package = {}
  try {
    package = JSON.parse(fs.readFileSync(path.resolve(__dirname, `../packages/${module}/package.json`), 'utf-8'));
  } catch(err) {
    throw err
  }
  return key ? package[key] : package;
}

/**
 * 显示发布加载信息
 * @param {array} modules 选择发布的模块
 */
async function releaseLoading(modules = []) {
  const textLoading = 'release...\n';
  const textModuleList = await modules.map((m) => {
    const version = getModuleInfo(m, 'version');
    return `${m}@${version}\n`;
  });
  spinner.color = 'yellow';
  spinner.text = `${chalk.yellow(`${textLoading}${textModuleList.join('')}`)}`;
  spinner.start();
}

/**
 * 发布版本
 * @param {array} module 选择发布的模块
 */
async function release(module, tag) {
  const command = `cd ${path.resolve(__dirname, `../packages/${module}`)}; yarn transpile; ${npmPublish} ${tag !== 'release' ? `--tag ${tag}` : ''};`
  await exec(command, (error, stdout) => {
    const version = getModuleInfo(module, 'version');
    if (error) {
      console.log(`${chalk.red(`\n${error}`)}`);
      spinner.fail(`${chalk.red(`${module}@${version} release failed !!! release stop.`)}`);
      return;
    }
    spinner.color = 'green';
    spinner.text = `${chalk.green(`${module}@${version} release succeed.`)}`;
    spinner.succeed();
  });
}

/**
 * 开始发布
 */
function releaseStart() {
  inquirer.prompt([
    {
      type: 'checkbox',
      name: 'modules',
      message: 'Please select the module to release ?',
      choices: packages,
    }
  ]).then(({ modules }) => {
    inquirer.prompt({
      type: 'list',
      name: 'tag',
      message: 'Please select tags?',
      default: 'release',
      choices: ['alpha', 'beta', 'rc', 'release'],
    }).then(({ tag }) => {
      releaseLoading(modules);
      for (const m of modules) {
        release(m, tag);
      }
    })
  })
}

releaseStart();
