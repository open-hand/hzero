import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';

/**
 * JsonFormatter - 格式化JSON对象
 * @date: 2019-5-14
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
const tabSize = 2;
class JsonFormatter extends Component {
  /**
   * 根据count 返回空格数
   * @param {number} count 空格数
   */
  @Bind
  getSpace(count) {
    return ' '.repeat(count);
  }

  /**
   * 每一行包 span标签
   * @param {any} value json中的value
   * @param {string} className 针对不同数据类型，加上不同类名。可在页面上修改各种数据类型的展示颜色
   * @param {number} count 有多少前缀空格
   * @param {boolean} newLine 是否换行
   * @param {boolean} quote 是否给打引号
   */
  @Bind
  wrapper(value, className, count, newLine = false, quote = false) {
    const space = this.getSpace(count);
    const { renderItem } = this.props;
    const tag = (
      <span className={className}>
        {space}
        {quote && '"'}
        {value}
        {newLine && '\n'}
        {quote && '"'}
      </span>
    );
    return renderItem ? renderItem(tag) : tag;
  }

  /**
   * 处理json中的数组
   * @param {array} value 数组
   * @param {number} count 有多少前缀空格
   */
  @Bind
  withArray(value, count, space) {
    const { wrapper, process } = this;
    const html = [];
    const comments = this.comments(value);
    if (value.length) {
      html.push(wrapper('[', 'array', space, true));
      value.forEach((item, index) => {
        const values = [];
        if (comments[item] && comments[item][0]) {
          values.push(wrapper(comments[item], 'comment', count + tabSize, true));
        }
        values.push(process(item, count + tabSize));
        if (index !== value.length - 1) {
          values.push(',');
        }
        if (comments[item] && comments[item][1]) {
          values.push(wrapper(comments[item], 'comment', 1));
        }
        html.push(wrapper(values, 'items-wrapper', 0, true));
      });
      html.push(wrapper(']', 'array', count));
    } else {
      html.push(wrapper('[]', 'array', 1));
    }
    return html;
  }

  /**
   * 处理使用了类似hjson之类允许注释的JSON工具转换JSON对象后的注释
   * @param {object} obj json对象
   */
  @Bind
  comments(obj = {}) {
    if (obj.__COMMENTS__) {
      return obj.__COMMENTS__.c || {};
    }
    return {};
  }

  /**
   * 处理json中的对象
   * @param {object} value 对象
   * @param {number} count 有多少前缀空格
   */
  @Bind
  withObject(value, count, space) {
    const { wrapper, process } = this;
    const html = [];
    const comments = this.comments(value);
    const arr = Object.keys(value);
    if (arr.length) {
      html.push(wrapper('{', 'object', space, true));
      arr.forEach((item, index) => {
        const values = [];
        if (comments[item] && comments[item][0]) {
          values.push(wrapper(comments[item], 'comment', count + tabSize, true));
        }
        values.push(wrapper(item, 'item-key', count + tabSize, false, true));
        values.push(':');
        values.push(process(value[item], count + tabSize, 1));
        if (index !== arr.length - 1) {
          values.push(',');
        }
        if (comments[item] && comments[item][1]) {
          values.push(wrapper(comments[item], 'comment', 1));
        }
        html.push(wrapper(values, 'items-wrapper', 0, true));
      });
      html.push(wrapper('}', 'object', count));
    } else {
      html.push(wrapper('{}', 'object', 1));
    }
    return html;
  }

  /**
   * 处理JSON对象, 递归使用
   */
  @Bind
  process(value, count = 0, space = count) {
    const html = [];
    const type = typeof value;
    if (value === null) {
      html.push(this.wrapper('null', 'null', space));
    } else if (type === 'object' && value instanceof Array) {
      html.push(this.withArray(value, count, space));
    } else if (type === 'object' && value instanceof Date) {
      html.push(this.wrapper(value, 'date', space));
    } else if (type === 'object') {
      html.push(this.withObject(value, count, space));
    } else if (type === 'number') {
      html.push(this.wrapper(value, 'number', space));
    } else if (type === 'boolean') {
      html.push(this.wrapper(value ? 'true' : 'false', 'boolean', space));
    } else if (type === 'undefined') {
      html.push(this.wrapper('undefined', 'undefined', space));
    } else {
      html.push(this.wrapper(value, 'string', space, false, true));
    }
    return html;
  }
}

const defaultProps = {
  tabSize: 2,
};

const jsonFormat = new JsonFormatter(defaultProps);

export default jsonFormat.process;
