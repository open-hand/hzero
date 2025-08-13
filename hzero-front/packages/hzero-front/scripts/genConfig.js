/**
 * TODO: 已经可以直接通过 changeRoute('/iam', '/iam-xxxxx') 来在运行时改变配置了, 需要将 .route 相关的代码注释取消 和注释之下的代码, 需要注意单引号问题, 已知问题, 不能有重复的 配置名
 * 通过 ../config/apiConfig 生成 utils/config 文件
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-22
 * @copyright ® HAND
 */
const {config, prefix, suffix} = require('../src/config/apiConfig');

const helpFuncMap = {};
const initLang = [];
const changeRouteSwitch = new Map(); // 存储 case 代码
const configChangeFunc = [];
const exportConfig = [];
// 不需要从 'HZERO_PLATFORM' -> '/hpfm' 的映射
// '/hpfm' -> 'HZERO_PLATFORM' 的映射
// const routeMapVar = {};

Object.entries(config).forEach(([key, value]) => {

  // #region exportConfig
  exportConfig.push(key);
  // #endregion

  // #region initConfig
  let varType = 'let';
  if (value.noChange) {
    varType = 'const';
  }

  //  init  lang
  if (value.init) {
    if (value.route) {
      // 路由 不能直接被环境变量更改, 所有 route: true 的 init 是直接返回可用的变量
      const routePath = value.init();
      // if (routeMapVar[routePath]) {
      //   throw new Error(formatString('route define has two config %12s, %12s has same value %8s', routeMapVar[routePath], key, routePath));
      // } else {
      //   routeMapVar[routePath] = key;
      // }
      // initLang.push(formatString('%8s %16s = %s;', varType, key, ` routeMap[${routePath}] || ${routePath}`));
      // 使用 后端服务合并, 前端 不做调整
      initLang.push(formatString('%8s %16s = %s;', varType, key, `${routePath}`));
    } else {
      // not route
      initLang.push(formatString('%8s %16s = %s;', varType, key, value.init()));
    }

  } else if (value.get) {
    //  get  config  return  body;
    // TODO: 初始化时也调用方法
    // const retBody = value.get.toString().match(/return ([\S\s]+);/);
    // initLang.push(`${varType} ${key} = ${(retBody && retBody[1] || '')};`);
    initLang.push(formatString('%8s %16s = %s;', varType, key, `change${key}(${value.deps.join(', ')})`));
  } else {
    // value 是 字符串
    initLang.push(formatString('%8s %16s = %s;', varType, key, `'${value}'`));
  }
  // #endregion

  if (value.noChange) {
    // noChange 不能在运行时更改
    return;
  }

  // #region help  func
  // const helpKey = value.route ? value.init() : key;
  const helpKey = key;
  const curHelp = helpFuncMap[helpKey] || {changeConfig: [], depBy: []};
  helpFuncMap[helpKey] = curHelp;
  if (value.deps) {
    value.deps.forEach((depKey) => {
      // const depHelpKey = (config[depKey] && config[depKey].route) ? config[depKey].init() : depKey;
      const depHelpKey = depKey;
      const depHelp = helpFuncMap[depHelpKey] || {changeConfig: [], depBy: []};
      helpFuncMap[depHelpKey] = depHelp;
      depHelp.changeConfig.push(helpKey);
      curHelp.depBy.push(helpKey);
    });
  }
  curHelp.changeConfig.push(helpKey);
  // #endregion

  // #region change  route  func
  // const caseKey = value.route ? value.init() : key;
  const caseKey = key;
  const curCase = changeRouteSwitch.get(caseKey) || [];
  changeRouteSwitch.set(caseKey, curCase);
  if (value.deps) {
    //  if  have  deps,  must  have  get
    value.deps.forEach((depKey, depIndex) => {
      const depCase = changeRouteSwitch.get(depKey) || [];
      changeRouteSwitch.set(depKey, depCase);
      const newDep = [...value.deps];
      newDep[depIndex] = 'value';
      depCase.push(`${key} = change${key}(${newDep.join(', ')});`);
    });
    const changeFuncLines = value.get.toString().split('\n');
    changeFuncLines[0] = `function change${key} (${value.deps.join(', ')}) {`;
    const lastLineNo = changeFuncLines.length - 1;
    configChangeFunc.push(
      changeFuncLines.map((line, lineNo) => {
        if (lineNo === 0 || lineNo === lastLineNo) {
          return line.trim();
        } else {
          return `  ${line.trim()}`;
        }
      }).join('\n'));
  }
  // every config can change it self
  curCase.push(`${key} = value;`);
  // #endregion

});

function renderChangeRouteSwitch() {
  const strs = [
    'window.changeRoute = function changeRoute(key, value)  {',
    '  if(key && value)  {',
    '    switch(key)  {',
  ];
  for ([key, value] of changeRouteSwitch.entries()) {
    if (value.noChange) {
      return;
    }
    const curCase = (value || []);
    strs.push(`      case '${key}':`,
      curCase.map(str => {
        return `        ${str}`;
      }).join('\n'),
      '        break;',
    );
  }
  strs.push('      default:');
  strs.push('        console.error(`${key} is not exists`);');
  strs.push('        helpMethod();');
  strs.push('        break;');
  strs.push('',
    '    }',
    '  } else {',
    '    helpMethod(key);',
    '  }',
    '};',
  );
  return strs.join('\n');
}

function renderHelpMethod() {
  const strs = [];
  strs.push(`const helpMethodAssist = ${JSON.stringify(helpFuncMap)};`,
    'function helpMethod(key) {',
    '  if(key && helpMethodAssist[key]) {',
    `     console.error(\`\${key} 会更改: [\${helpMethodAssist[key].changeConfig.join(', ')}], 被级连更改: [\${helpMethodAssist[key].depBy.join(', ')}]\`)`,
    '  } else {',
    `    console.error('使用 changeRoute() 查看可以更改的参数');`,
    `    console.error('使用 changeRoute("参数") 查看具体改变');`,
    `    console.error('使用 changeRoute("参数", "参数值") 更改参数');`,
    `    console.error(\`可以更改的配置: [\${Object.keys(helpMethodAssist).join(', ')}]\`);`,
    '  }',
    '}');
  return strs.join('\n');
}

function renderExportsConfig() {
  return ['export {', exportConfig.map(exportName => `  ${exportName},`).join('\n'), '};'].join('\n');
}

const configData = [
  '/* eslint-disable no-shadow,camelcase,import/no-mutable-exports,no-console */',
  '// TODO: 自动生成的 src/utils/config 禁用了部分 eslint, 请查看 scripts/genConfig.js',
  prefix,
  // 使用后端服务合并 前端不再做调整
  // 'const routeMap = JSON.parse(process.env.routeMap || \'{}\');',
  // `const routeMapVar = ${JSON.stringify(routeMapVar)};`,
  '// #region initConfig',
  initLang.join('\n'),
  '// #endregion',
  '',
  '// #region changeConfig Funcs',
  configChangeFunc.join('\n'),
  '// #endregion',
  '',
  '// #region changeRoute',
  renderChangeRouteSwitch(),
  '// #endregion',
  '',
  '// #region helpMethod',
  renderHelpMethod(),
  '// #endregion',
  '',
  '// #regioin exportsConfig',
  renderExportsConfig(),
  '// #endregion',
  suffix,
];

require('fs').writeFileSync(
  require('path').resolve(__dirname, '../src/utils/config.js'),
  configData.join('\n'),
);


function formatString(format, ...args) {
  let argIndex = 0;
  return format.replace(/%(\d*)s/g, (testStr, match/* , start, all */) => {
    const padNum = +match;
    let ret;
    if (!padNum) {
      ret = args[argIndex]
    } else {
      ret = ("" + args[argIndex]).padEnd(padNum, ' ');
    }
    argIndex++;
    return ret;
  });
}
