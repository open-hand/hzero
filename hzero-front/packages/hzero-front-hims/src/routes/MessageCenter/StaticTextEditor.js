import React from 'react';
import uuid from 'uuid/v4';

import RichTextEditor from 'components/RichTextEditor';

export default class StaticTextEditor extends React.Component {
  constructor(props) {
    super(props);
    this.staticTextEditor = React.createRef();
    this.state = {
      prevContent: props.content,
      editorKey: uuid(),
    };
  }

  componentDidMount() {
    const { onRef } = this.props;
    onRef(this);
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { content } = nextProps;
    let newContent = content;
    const reg = /<img [A-Za-z0-9=:\\/@.'"-_\u4e00-\u9fa5]*?\/>/g; // 匹配<img />
    // const reg = new RegExp(`<img [A-Za-z0-9=:\\/@.' "-_\u4e00-\u9fa5]*?/>`, 'g');
    const heightReg = new RegExp(`height=[0-9'"]*`); // 匹配height
    const widthReg = new RegExp(`width=[0-9'"]*`); // 匹配width
    let arr;
    // eslint-disable-next-line no-cond-assign
    while ((arr = reg.exec(newContent)) !== null) {
      if (!(heightReg.test(arr[0]) || widthReg.test(arr[0]))) {
        const spiltArr = newContent.split(arr[0]);
        const n = arr[0].split(' ');
        n.splice(1, 0, `width="300"`); // 设置图片宽度
        spiltArr.splice(1, 0, n.join(' '));
        newContent = spiltArr.join('');
      }
    }
    if (newContent !== prevState.prevContent) {
      return {
        content: newContent || '',
        prevContent: newContent || '',
        editorKey: uuid(),
      };
    }
    return null;
  }

  render() {
    const { viewStatus } = this.props;
    const { editorKey, prevContent } = this.state;
    return (
      <RichTextEditor
        key={editorKey}
        ref={this.staticTextEditor}
        readOnly={viewStatus}
        content={prevContent}
      />
    );
  }
}
