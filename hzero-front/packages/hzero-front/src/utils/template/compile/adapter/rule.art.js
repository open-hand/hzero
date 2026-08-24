/* eslint-disable no-case-declarations */
/* eslint-disable no-param-reassign */
/**
 * 简洁模板语法规则
 */
const artRule = {
  test: /{{([@#]?)[ \t]*(\/?)([\w\W]*?)[ \t]*}}/,
  use(match, raw, close, code) {
    const compiler = this;
    const { options } = compiler;
    const esTokens = compiler.getEsTokens(code);
    const values = esTokens.map(token => token.value);
    const result = {};

    let group;
    let output = raw ? 'raw' : false;
    let key = close + values.shift();

    // 旧版语法升级提示
    const warn = (oldSyntax, newSyntax) => {
      console.warn(
        `${options.filename || 'anonymous'}:${match.line + 1}:${match.start + 1}\n` +
          `Template upgrade: {{${oldSyntax}}} -> {{${newSyntax}}}`
      );
    };

    // v3 compat: #value
    if (raw === '#') {
      warn('#value', '@value');
    }

    switch (key) {
      case 'set':
        code = `var ${values.join('').trim()}`;
        break;

      case 'if':
        code = `if(${values.join('').trim()}){`;

        break;

      case 'else':
        const indexIf = values.indexOf('if');

        // eslint-disable-next-line no-bitwise
        if (~indexIf) {
          values.splice(0, indexIf + 1);
          code = `}else if(${values.join('').trim()}){`;
        } else {
          code = `}else{`;
        }

        break;

      case '/if':
        code = '}';
        break;

      case 'each':
        group = artRule._split(esTokens);
        group.shift();

        if (group[1] === 'as') {
          // ... v3 compat ...
          warn('each object as value index', 'each object value index');
          group.splice(1, 1);
        }

        const object = group[0] || '$data';
        const value = group[1] || '$value';
        const index = group[2] || '$index';

        code = `$each(${object},function(${value},${index}){`;

        break;

      case '/each':
        code = '})';
        break;

      case 'block':
        group = artRule._split(esTokens);
        group.shift();
        code = `block(${group.join(',').trim()},function(){`;
        break;

      case '/block':
        code = '})';
        break;

      case 'echo':
        key = 'print';
        warn('echo value', 'value');
        break;
      case 'print':
      case 'include':
      case 'extend':
        if (
          values
            .join('')
            .trim()
            .indexOf('(') !== 0
        ) {
          // 执行函数省略 `()` 与 `,`
          group = artRule._split(esTokens);
          group.shift();
          code = `${key}(${group.join(',')})`;
          break;
        }
        break;
      default:
        // eslint-disable-next-line no-bitwise
        if (~values.indexOf('|')) {
          const v3split = ':'; // ... v3 compat ...

          // 将过滤器解析成二维数组
          const _group = esTokens
            .reduce((group_, token) => {
              const { _value, type } = token;
              if (_value === '|') {
                group_.push([]);
              } else if (type !== `whitespace` && type !== `comment`) {
                if (!group_.length) {
                  group_.push([]);
                }
                if (_value === v3split && group_[group_.length - 1].length === 1) {
                  warn('value | filter: argv', 'value | filter argv');
                } else {
                  group_[group_.length - 1].push(token);
                }
              }
              return group_;
            }, [])
            .map(g => artRule._split(g));

          // 将过滤器管道化
          code = _group.reduce(
            (accumulator, filter) => {
              const name = filter.shift();
              filter.unshift(accumulator);

              return `$imports.${name}(${filter.join(',')})`;
            },
            _group
              .shift()
              .join(` `)
              .trim()
          );
        }

        output = output || 'escape';
    }

    result.code = code;
    result.output = output;

    return result;
  },

  // 将多个 javascript 表达式拆分成组
  // 支持基本运算、三元表达式、取值、运行函数，不支持 `typeof value` 操作
  // 只支持 string、number、boolean、null、undefined 这几种类型声明，不支持 function、object、array
  _split: esTokens => {
    esTokens = esTokens.filter(({ type }) => type !== `whitespace` && type !== `comment`);

    let current = 0;
    let lastToken = esTokens.shift();
    const punctuator = `punctuator`;
    const close = /\]|\)/;
    const group = [[lastToken]];

    while (current < esTokens.length) {
      const esToken = esTokens[current];

      if (
        esToken.type === punctuator ||
        (lastToken.type === punctuator && !close.test(lastToken.value))
      ) {
        group[group.length - 1].push(esToken);
      } else {
        group.push([esToken]);
      }

      lastToken = esToken;

      current++;
    }

    return group.map(g => g.map(_g => _g.value).join(``));
  },
};
export default artRule;
