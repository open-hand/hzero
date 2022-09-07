import React from 'react';

/**
 * XmlFormatter - 格式化XML对象
 * @date: 2021-1-8
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2021, Hand
 */
class XmlFormatter extends React.Component {
  // 格式化xml代码
  process(xmlStr) {
    let text = xmlStr;
    // 使用replace去空格
    text = `\n${text
      .replace(/(<\w+)(\s.*?>)/g, function ($0, name, props) {
        return `${name} ${props.replace(/\s+(\w+=)/g, ' $1')}`;
      })
      .replace(/>\s*?</g, '>\n<')}`;
    // 处理注释
    text = text
      .replace(/\n/g, '\r')
      .replace(/<!--(.+?)-->/g, function ($0, text1) {
        const ret = `<!--${escape(text1)}-->`;
        return ret;
      })
      .replace(/\r/g, '\n');
    // 调整格式 以压栈方式递归调整缩进
    const rgx = /\n(<(([^?]).+?)(?:\s|\s*?>|\s*?(\/)>)(?:.*?(?:(?:(\/)>)|(?:<(\/)\2>)))?)/gm;
    const nodeStack = [];
    const span = '  '; // 缩进长度
    const output = text.replace(rgx, function (
      $0,
      all,
      name,
      isBegin,
      isCloseFull1,
      isCloseFull2,
      isFull1,
      isFull2
    ) {
      const isClosed =
        isCloseFull1 === '/' || isCloseFull2 === '/' || isFull1 === '/' || isFull2 === '/';
      let prefix = '';
      const output1 = [];
      if (isBegin === '!') {
        //! 开头
        for (let i = 0; i < nodeStack.length; ++i) {
          output1.push(span);
        }
        prefix = output1.join('');
      } else if (isBegin !== '/') {
        // /开头
        for (let i = 0; i < nodeStack.length; ++i) {
          output1.push(span);
        }
        prefix = output1.join('');
        if (!isClosed) {
          // 非关闭标签
          nodeStack.push(name);
        }
      } else {
        nodeStack.pop(); // 弹栈
        for (let i = 0; i < nodeStack.length; ++i) {
          output1.push(span);
        }
        prefix = output1.join('');
      }
      const ret = `\n${prefix}${all}`;
      return ret;
    });
    let outputText = output.substring(1);
    // 还原注释内容
    outputText = outputText
      .replace(/\n/g, '\r')
      .replace(/(\s*)<!--(.+?)-->/g, function ($0, prefix, text2) {
        let prefix1 = prefix;
        let text1 = text2;
        if (prefix1.charAt(0) === '\r') prefix1 = prefix1.substring(1);
        text1 = unescape(text1).replace(/\r/g, '\n');
        const ret = `\n${prefix1}<!--${text1.replace(/^\s*/gm, prefix1)}-->`;
        return ret;
      });
    outputText = outputText.replace(/\s+$/g, '').replace(/\r/g, '\r\n');
    return outputText;
  }
}

const xmlFormat = new XmlFormatter();

export default xmlFormat.process;
