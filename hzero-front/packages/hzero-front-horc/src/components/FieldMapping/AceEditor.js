import ReactAceEditor from 'react-ace';
import 'ace-builds/src-noconflict/ext-searchbox';
import 'ace-builds/src-noconflict/mode-java';
import 'ace-builds/src-noconflict/theme-github';
import React from 'react';

class AceEditor extends React.Component {
  render() {
    const { value, width, height, disabled = false, onChange } = this.props;
    return (
      <ReactAceEditor
        mode="java"
        readOnly={disabled}
        theme="github"
        name="app_code_editor"
        onChange={onChange}
        fontSize={12}
        showPrintMargin
        showGutter
        highlightActiveLine // 突出活动线
        enableSnippets // 启用代码段
        value={value}
        style={{ width, height }}
        editorProps={{ $blockScrolling: true }}
        setOptions={{
          enableBasicAutocompletion: true, // 启用基本自动完成功能
          enableLiveAutocompletion: true, // 启用实时自动完成功能 （比如：智能代码提示）
          enableSnippets: true, // 启用代码段
          showLineNumbers: true,
          tabSize: 2,
        }}
      />
    );
  }
}
export default AceEditor;
