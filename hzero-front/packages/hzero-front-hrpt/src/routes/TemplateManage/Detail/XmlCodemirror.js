import React from 'react';
import { Controlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/mode/xml/xml';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/dracula.css';
import style from './index.less';

export default class XmlCodemirror extends React.PureComponent {
  constructor(props) {
    super(props);
    const {
      codeMirrorProps: { value },
    } = props;
    this.editor = null;
    this.state = {
      code: value || '',
    };
  }

  // eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    const { code } = this.state;
    const {
      codeMirrorProps: { value },
    } = nextProps;
    if (code !== value) {
      this.setState({ code: value });
    }
  }

  render() {
    const { codeMirrorProps = {}, fetchCodeMirror = e => e } = this.props;
    const { options = {} } = codeMirrorProps;
    const initProps = {
      className: style['hzero-codemirror'],
      autoScroll: true,
      ...codeMirrorProps,
      value: this.state.code,
      options: {
        cursorHeight: 0.85,
        lineNumbers: true,
        lineWrapping: true,
        mode: 'application/xml',
        autoFocus: true,
        viewportMargin: Infinity,
        smartIndent: true,
        indentUnit: 2,
        ...options,
      },
    };
    return (
      <CodeMirror
        {...initProps}
        editorDidMount={editor => {
          fetchCodeMirror(editor);
          this.editor = editor;
          editor.setSize('auto', '400px');
        }}
      />
    );
  }
}
