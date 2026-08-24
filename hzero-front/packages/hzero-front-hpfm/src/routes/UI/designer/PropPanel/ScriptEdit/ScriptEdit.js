/**
 * ScriptEdit.js
 * @date 2018/11/7
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

import React from 'react';
import { Controlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/mode/clike/clike';
import 'codemirror/mode/javascript/javascript';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/dracula.css';
import { Bind } from 'lodash-decorators';
import { isFunction } from 'lodash';

export default class ScriptEdit extends React.Component {
  editor;

  @Bind()
  handleCodeMirrorRef(editor) {
    this.editor = editor;
    editor.setSize('540px', '340px');
  }

  @Bind()
  handleChange(editor, data, value) {
    const { onChange } = this.props;
    if (isFunction(onChange)) {
      onChange(value);
    }
  }

  render() {
    // 只有 onBeforeChange 才能 更新
    const { value, options, onChange, ...otherProps } = this.props;
    return (
      <CodeMirror
        {...otherProps}
        autoScroll
        value={value}
        onBeforeChange={this.handleChange}
        options={{
          mode: 'javascript',
          lineNumbers: true,
          autoFocus: true,
          cursorHeight: 0.85,
          viewportMargin: Infinity,
          ...options,
        }}
        editorDidMount={this.handleCodeMirrorRef}
      />
    );
  }
}
