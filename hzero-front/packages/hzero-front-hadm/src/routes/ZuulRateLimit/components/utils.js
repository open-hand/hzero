/**
 * 将 string 转为 受输入框支持的结构
 * @param {string} str
 * @returns {[]}
 */
export function transformStringToVariable(str) {
  // I do this because of value can't contains ?#{}&
  const vs = [];
  let lex = '';
  let isInVari = false;
  let itemId = 1001;
  for (let i = 0; i < str.length; i++) {
    const c = str[i];
    if (c === '{') {
      if (lex) {
        itemId += 1;
        vs.push({ type: 'c', value: lex, id: itemId });
        lex = '';
      }
      if (isInVari) {
        if (process.env.NODE_ENV === 'development') {
          throw new Error("variable can't be nested, that must be a data error");
        }
      } else {
        isInVari = true;
      }
    } else if (c === '}') {
      if (isInVari) {
        itemId += 1;
        vs.push({ type: 'v', id: itemId });
        isInVari = false;
      } else if (process.env.NODE_ENV === 'development') {
        throw new Error("} can't be used in constant, that must be a data error");
      }
    } else if (isInVari) {
      if (process.env.NODE_ENV === 'development') {
        if (!/\d/.test(c)) {
          throw new Error("letter can't in variable, that must be a data error");
        }
      }
    } else {
      lex += c;
    }
  }
  if (lex) {
    itemId += 1;
    vs.push({ type: 'c', value: lex, id: itemId });
    // lex = '';
  }
  return vs;
}

/**
 * 将 变量 转为 string
 * @param values
 * @returns {string}
 */
export function transformVariableToString(values) {
  const valueArr = [];
  let variIndex = 0;
  values.forEach(v => {
    if (v.type === 'c') {
      valueArr.push(v.value);
    } else if (v.type === 'v') {
      variIndex += 1;
      valueArr.push(`{${variIndex}}`);
    }
  });
  return valueArr.join('');
}

/**
 * 将 str 中的变量顺序 重排
 * @param str
 * @returns {string}
 */
export function refactorVariableSort(str) {
  return transformVariableToString(transformStringToVariable(str));
}

/**
 * @param args
 * @returns {string}
 */
export function transformArgsToString(args) {
  return (args || []).join(';');
}

/**
 * @param argsStr
 * @returns {string[]}
 */
export function transformStringToArgs(argsStr) {
  return (argsStr || '').split(';');
}

/**
 * @param urlVaris
 * @param args
 * @returns {string}
 */
export function renderVariable(urlVaris, args) {
  let argIndex = 0;
  return urlVaris
    .map(item => {
      if (item.type === 'c') {
        return item.value;
      } else if (item.type === 'v') {
        const v = args[argIndex] || '';
        argIndex += 1;
        return v;
      }
      // TODO: not reachable, may throw error
      return '';
    })
    .join('');
}

/**
 * @param {string} url
 * @param {string} [argsStr='']
 */
export function renderVariableWithString(url, argsStr) {
  return renderVariable(transformStringToVariable(url), transformStringToArgs(argsStr));
}
