(async function() {
  const fs = require('fs-extra');
  const path = require('path');
  const hzero = require('../.hzerorc.js');

  const packages = hzero.packages.map(v => v.name);

  await fs.writeFile(path.resolve(__dirname, 'changed-packages.txt'), packages.join(','), 'utf8');
})();