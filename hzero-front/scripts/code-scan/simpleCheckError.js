/**
 * simple check locale error
 */

const { readYmlAsync } = require('hzero-front-util/bin/buildin/ymlUtils');
const { readdirAsync, existsSync } = require('hzero-front-util/bin/buildin/fileUtils');
const { resolveBase } = require('hzero-front-util/bin/buildin/pathUtils');
const { formatString } = require('hzero-front-util/bin/buildin/stringUtils');

// const resolveBase = (...paths) => {
//   return resolve(__dirname, '../../', ...paths);
// };

const excludeModules = ['hzero-front-demo'];

async function main() {
  const modules = await readdirAsync(resolveBase('packages'));
  await Promise.all(
    modules
      .filter(moduleName => existsSync(resolveBase('packages', moduleName, 'package.json')))
      .filter(moduleName => !excludeModules.includes(moduleName))
      .map(async moduleName => {
        const validIntlPrefix = [
          'hzero',
          'entity',
          'hzeroUI',
          moduleName.substr('hzero-front-'.length),
        ];
        const intl = await readYmlAsync(
          resolveBase('packages', moduleName, 'locale', 'code-scan', 'process', 'locale.yml'),
        );
        debugger;
        Object.keys(intl).forEach(intlModule => {
          if (!validIntlPrefix.includes(intlModule)) {
            console.error(formatString('%6s: error intl-prefix______%s', moduleName, intlModule));
          }
        });
      }),
  );
}

main().catch(console.error);
