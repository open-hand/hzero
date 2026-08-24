const paths = require('hzero-webpack-scripts/config/paths');
const path = require('path');

// 除hzero-front外的alias走lib
let aliasConfig = {
  // '@': path.resolve(paths.appRootPath, 'node_modules', 'hzero-front/lib'),
  // '@/assets': 'hzero-front/lib/assets',
  // '@/assets': path.resolve(paths.appPath, 'src/assets'),
  '@/assets': path.resolve(paths.appRootPath, 'src/assets'),
  '@': path.resolve(paths.appPath, 'src'),

  components: 'hzero-front/lib/components/',
  utils: 'hzero-front/lib/utils/',
  services: 'hzero-front/lib/services/',
};

if (
  paths.appPath.endsWith('hzero-front') &&
  process.cwd().indexOf('packages') > -1 &&
  process.env.NODE_ENV === 'development'
) {
  aliasConfig = Object.assign(aliasConfig, {
    components: 'hzero-front/src/components/',
    utils: 'hzero-front/src/utils/',
    services: 'hzero-front/src/services/',
    'hzero-front/lib': 'hzero-front/src',
  });
}

module.exports = aliasConfig;
