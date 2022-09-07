// const detectNode = typeof window === 'undefined';

/**
 * HTML 压缩器
 * @param  {string}     source
 * @param  {Object}     options
 * @return {string}
 */
const htmlMinifier = (source /* , options */) =>
  // if (detectNode) {
  //     const { minify } = require('html-minifier');
  //     const { htmlMinifierOptions } = options;
  //     const ignoreCustomFragments = options.rules.map(rule => rule.test);
  //     htmlMinifierOptions.ignoreCustomFragments.push(...ignoreCustomFragments);
  //     // eslint-disable-next-line no-param-reassign
  //     source = minify(source, htmlMinifierOptions);
  // }

  source;
export default htmlMinifier;
