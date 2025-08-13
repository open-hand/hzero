import render from './render';
import compile from './compile';
import defaults from './defaults';

/**
 * 避免html-minifier被打到生产包
 * 关闭html-minifier功能，注释相关代码
 */

/**
 * 模板引擎
 * @param   {string}            filename 模板名
 * @param   {Object|string}     content  数据或模板内容
 * @return  {string|function}            如果 content 为 string 则编译并缓存模板，否则渲染模板
 */
const template = (filename, content) =>
  content instanceof Object
    ? render({ filename }, content)
    : compile({ filename, source: content });

template.render = render;
template.compile = compile;
template.defaults = defaults;

export default template;
