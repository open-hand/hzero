// const babelConfig = require('../../.babelrc.js');

// let hzeroFrontBabelConfig = babelConfig;
// hzeroFrontBabelConfig = babelConfig.overrideAlias(babelConfig)(moduleResolverConfig => {
//   return {
//     ...moduleResolverConfig,
//     alias: {
//       ...moduleResolverConfig.alias,
//       '@': './src',
//       components: './src/components',
//       utils: './src/utils',
//       services: './src/services',
//     },
//   };
// });
// module.exports = hzeroFrontBabelConfig;
module.exports = {
  extends: '../../.babelrc',
  "plugins": [
    ["module-resolver", {
      "root": ["./"],
      "alias": {
      '@': './src',
      components: './src/components',
      utils: './src/utils',
      services: './src/services',
      }
    }]
  ]
};
