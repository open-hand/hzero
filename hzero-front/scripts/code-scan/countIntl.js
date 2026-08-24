/**
 * count diff, newer, old information
 */

const { readYmlAsync } = require('hzero-front-util/bin/buildin/ymlUtils');
const { resolveBase } = require('hzero-front-util/bin/buildin/pathUtils');
const { formatString } = require('hzero-front-util/bin/buildin/stringUtils');

const readFileDir = resolveBase('locale', 'bak-code-scan-2019-11-07_16-07-33');

async function main() {
  await Promise.all(
    ['diff', 'newer', 'old'].map(async d => {
      const intl = await readYmlAsync(resolveBase(readFileDir, d + '.yml'));
      let count = 0;
      Object.keys(intl).forEach(m => {
        let moduleCount = 0;
        Object.keys(intl[m]).forEach(f => {
          count += Object.keys(intl[m][f]).length;
        });
        console.log(formatString('%6s %6s has %10s count', m, d, moduleCount));
      });
      console.log(formatString('%6s has %10s count', d, count));
    }),
  );
}

main().catch(console.error);
