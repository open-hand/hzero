import templatePath from './index';
/**
 * require.extensions 扩展注册函数
 * 使用动态编译机制
 * @param {Object} module
 * @param {string} flnm
 */
export default function extension(_module, flnm) {
  const filename = flnm || _module.filename;
  const imports = `var template=require(${JSON.stringify(templatePath)})`;
  const options = JSON.stringify({
    filename,
  });

  _module._compile(`${imports}\n module.exports = template.compile(${options});`, filename);
}
