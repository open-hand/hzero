import React, { useImperativeHandle, useRef } from 'react';
import RichTextEdit from './edit';
import RichtextView from './view';

/**
 * 富文本编辑/查看工具
 * @param {string} mode - 模式 'view' 'edit'
 * @param {string} content - 富文本格式的json字符串
 * @param {ReactDOM} footer - 显示在文本下的内容，用于自定义上一页、下一页功能
 */
const RichTextTool = React.forwardRef((props, ref) => {
  const { mode = 'view', ...rest } = props;
  const editorRef = useRef();

  const _props = {
    ...rest,
    ref: editorRef,
  };
  useImperativeHandle(ref, () => ({
    getContent,
  }));

  function getContent() {
    if (!editorRef.current) return '';
    return editorRef.current.getContent();
  }

  let component;
  switch (mode) {
    case 'view':
      component = RichtextView;
      break;
    case 'edit':
      component = RichTextEdit;
      break;
    default:
      component = null;
  }

  return <>{React.createElement(component, _props)}</>;
});

export default RichTextTool;
