/* eslint-disable no-nested-ternary */

const globalThis =
  typeof self !== 'undefined'
    ? self
    : typeof window !== 'undefined'
    ? window
    : typeof global !== 'undefined'
    ? global
    : {};

const runtime = Object.create(globalThis);
const ESCAPE_REG = /["&'<>]/;

/**
 * 编码模板输出的内容
 * @param  {any}        content
 * @return {string}
 */
runtime.$escape = content => xmlEscape(toString(content));

/**
 * 迭代器，支持数组与对象
 * @param {array|Object} data
 * @param {function}     callback
 */
runtime.$each = (data, callback) => {
  if (Array.isArray(data)) {
    for (let i = 0, len = data.length; i < len; i++) {
      callback(data[i], i);
    }
  } else {
    // eslint-disable-next-line guard-for-in
    for (const i in data) {
      callback(data[i], i);
    }
  }
};

// 将目标转成字符
function toString(value) {
  if (typeof value !== 'string') {
    if (value === undefined || value === null) {
      return '';
    } else if (typeof value === 'function') {
      return toString(value.call(value));
    } else {
      return JSON.stringify(value);
    }
  }

  return value;
}

// 编码 HTML 内容
function xmlEscape(content) {
  const html = `${content}`;
  const regexResult = ESCAPE_REG.exec(html);
  if (!regexResult) {
    return content;
  }

  let result = '';
  let i;
  let lastIndex;
  let char;
  for (i = regexResult.index, lastIndex = 0; i < html.length; i++) {
    switch (html.charCodeAt(i)) {
      case 34:
        char = '&#34;';
        break;
      case 38:
        char = '&#38;';
        break;
      case 39:
        char = '&#39;';
        break;
      case 60:
        char = '&#60;';
        break;
      case 62:
        char = '&#62;';
        break;
      // eslint-disable-next-line no-continue
      default:
        // eslint-disable-next-line no-continue
        continue;
    }

    if (lastIndex !== i) {
      result += html.substring(lastIndex, i);
    }

    lastIndex = i + 1;
    result += char;
  }

  if (lastIndex !== i) {
    return result + html.substring(lastIndex, i);
  } else {
    return result;
  }
}

export default runtime;
