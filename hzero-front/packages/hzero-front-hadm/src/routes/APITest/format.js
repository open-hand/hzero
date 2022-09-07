import React, { Component } from 'react';

/**
 *  思路
 *  字符串转为json对象 或者直接就是个Json对象
 *  递归遍历这个json对象
 *  对key和value做操作
 */
const tabSize = 2;

class JsonFormatter extends Component {
  /**
   *  根据count 返回空格数
   */
  getSpace = (count) => ' '.repeat(count);

  /**
   * 每一行包 span标签
   * @param value
   * @param className
   * @param count 有多少前缀空格
   * @param newLine 是否换行
   * @param quote 是否给打引号
   * @returns {*}
   */
  wrapper = (value, className, count, newLine = false, quote = false) => {
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
  };

  withArray = (value, count, space) => {
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
  };

  comments = (obj = {}) => {
    /*  eslint-disable */
    if (obj.__COMMENTS__) {
      return obj.__COMMENTS__.c || {};
    }
    return {};
  };

  withObject = (value, count, space) => {
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
  };

  process = (value, count = 0, space = count) => {
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
  };
}

const defaultProps = {
  tabSize: 2,
};

const jsonFormat = new JsonFormatter(defaultProps);

export default jsonFormat.process;
