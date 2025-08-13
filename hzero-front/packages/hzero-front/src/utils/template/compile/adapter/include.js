/**
 * 载入子模板
 * @param   {string}    filename
 * @param   {Object}    data
 * @param   {Object}    blocks
 * @param   {Object}    options
 * @return  {string}
 */
const include = (filename, data, blocks, options) => {
  // eslint-disable-next-line global-require
  const compile = require('../index');
  // eslint-disable-next-line no-param-reassign
  options = options.$extend({
    filename: options.resolveFilename(filename, options),
    bail: true,
    source: null,
  });
  return compile(options)(data, blocks);
};

export default include;
