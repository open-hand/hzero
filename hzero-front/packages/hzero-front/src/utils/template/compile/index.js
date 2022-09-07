import Compiler from './compiler';
import defaults from './defaults';

import TemplateError from './error';

const debugRender = (error, options) => {
  options.onerror(error, options);
  const render = () => `{Template Error}`;
  render.mappings = [];
  render.sourcesContent = [];
  return render;
};

/**
 * 编译模版
 * @param {string|Object} source   模板内容
 * @param {?Object}       options  编译选项
 * @return {function}
 */
const compile = (source, options = {}) => {
  let newOptions = options;
  if (typeof source !== 'string') {
    newOptions = source;
  } else {
    newOptions.source = source;
  }

  // 合并默认配置
  newOptions = defaults.$extend(newOptions);
  let newSource = newOptions.source;

  // debug 模式
  /* istanbul ignore if */
  if (newOptions.debug === true) {
    newOptions.cache = false;
    newOptions.minimize = false;
    newOptions.compileDebug = true;
  }

  if (newOptions.compileDebug) {
    newOptions.minimize = false;
  }

  // 转换成绝对路径
  if (newOptions.filename) {
    newOptions.filename = newOptions.resolveFilename(newOptions.filename, newOptions);
  }

  const { filename } = newOptions;
  const { cache } = newOptions;
  const { caches } = newOptions;

  // 匹配缓存
  if (cache && filename) {
    const render = caches.get(filename);
    if (render) {
      return render;
    }
  }

  // 加载外部模板
  if (!newSource) {
    try {
      newSource = newOptions.loader(filename, newOptions);
      newOptions.newSource = newSource;
    } catch (e) {
      const error = new TemplateError({
        name: 'CompileError',
        path: filename,
        message: `template not found: ${e.message}`,
        stack: e.stack,
      });

      if (newOptions.bail) {
        throw error;
      } else {
        return debugRender(error, newOptions);
      }
    }
  }

  let fn;
  const compiler = new Compiler(newOptions);

  try {
    fn = compiler.build();
  } catch (error) {
    const err = new TemplateError(error);
    if (newOptions.bail) {
      throw err;
    } else {
      return debugRender(err, newOptions);
    }
  }

  const render = (data, blocks) => {
    try {
      return fn(data, blocks);
    } catch (error) {
      // 运行时出错以调试模式重载
      if (!newOptions.compileDebug) {
        newOptions.cache = false;
        newOptions.compileDebug = true;
        return compile(newOptions)(data, blocks);
      }

      const err = new TemplateError(error);

      if (newOptions.bail) {
        throw err;
      } else {
        return debugRender(err, newOptions)();
      }
    }
  };

  render.mappings = fn.mappings;
  render.sourcesContent = fn.sourcesContent;
  render.toString = () => fn.toString();

  if (cache && filename) {
    caches.set(filename, render);
  }

  return render;
};

compile.Compiler = Compiler;

export default compile;
