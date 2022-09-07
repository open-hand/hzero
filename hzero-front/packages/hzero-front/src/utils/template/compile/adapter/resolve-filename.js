const detectNode = typeof window === 'undefined';
const LOCAL_MODULE = /^\.+\//;

/**
 * 获取模板的绝对路径
 * @param   {string} filename
 * @param   {Object} options
 * @return  {string}
 */
const resolveFilename = (filename, options) => {
  /* istanbul ignore else  */
  if (detectNode) {
    const path = require('path');
    const { root, extname } = options;

    if (LOCAL_MODULE.test(filename)) {
      const from = options.filename;
      const self = !from || filename === from;
      const base = self ? root : path.dirname(from);
      // eslint-disable-next-line no-param-reassign
      filename = path.resolve(base, filename);
    } else {
      // eslint-disable-next-line no-param-reassign
      filename = path.resolve(root, filename);
    }

    if (!path.extname(filename)) {
      // eslint-disable-next-line no-param-reassign
      filename += extname;
    }
  }

  return filename;
};

export default resolveFilename;
