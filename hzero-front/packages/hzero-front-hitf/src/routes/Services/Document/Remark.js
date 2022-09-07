/**
 * Remark - 接口文档-详细说明（详细说明、请求说明、响应说明）
 * @date: 2019/6/4
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import classnames from 'classnames';
import uuid from 'uuid/v4';
import RichTextEditor from 'components/RichTextEditor';
import styles from './index.less';

/**
 * 接口文档-详细说明
 * @extends {Component} - React.Component
 * @reactProps {string} content - 说明内容
 * @return React.element
 */
export default class Remark extends Component {
  constructor(props) {
    super(props);
    // this.getValues = this.getValues.bind(this);
    this.staticTextEditor = React.createRef();
    this.state = {
      editorKey: uuid(),
      prevContent: props.content, // 保存用来比较编辑内容是否改变
    };
  }

  componentDidMount() {
    const { onRef } = this.props;
    onRef(this);
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { content } = nextProps;
    if (content !== prevState.prevContent) {
      return {
        editorKey: uuid(),
        prevContent: content || '',
      };
    }
    return null;
  }
  /**
   * 获取当前富文本编辑器的内容
   */
  // @Bind()
  // getValues() {
  //   return this.state.content;
  // }

  render() {
    const { prevContent, editorKey } = this.state;
    return (
      <div className={classnames(styles['hitf-remark-wrapper'])}>
        <RichTextEditor key={editorKey} content={prevContent} ref={this.staticTextEditor} />
      </div>
    );
  }
}
