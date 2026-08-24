import React from 'react';
import { Controlled as ControlledCodeMirror } from 'react-codemirror2';
import classnames from 'classnames';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/dracula.css';
import style from './index.less';

export default class CodeMirror extends React.PureComponent {
  constructor(props) {
    super(props);
    const { codeMirrorProps: { value } } = props;
    this.editor = null;
    this.state = {
      code: value || '',
    };
  }

  // eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    const { code } = this.state;
    const { codeMirrorProps: { value } } = nextProps;
    if (code !== value) {
      this.setState({ code: value });
    }
  }

  render() {
    const { codeMirrorProps = {}, fetchCodeMirror = e => e } = this.props;
    const { options = {}, className } = codeMirrorProps;
    const initProps = {
      autoScroll: true,
      className: classnames(style['hzero-codemirror'], className),
      ...codeMirrorProps,
      value: this.state.code,
      options: {
        lineNumbers: true,
        mode: 'yaml',
        autoFocus: true,
        cursorHeight: 0.85,
        viewportMargin: Infinity,
        ...options,
      },
    };
    return (
      <ControlledCodeMirror
        {...initProps}
        onBeforeChange={(editor, data, value) => {
          this.setState({ code: value });
        }}
        editorDidMount={editor => {
          fetchCodeMirror(editor);
          this.editor = editor;
          editor.setSize('auto', '500px');
        }}
      />
    );
  }
}
