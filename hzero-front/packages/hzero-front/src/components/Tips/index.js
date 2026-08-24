import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Tooltip } from 'hzero-ui';

import { queryToolTip } from '../../services/api';

import StaticTextEditor from './StaticTextEditor';
import styles from './index.less';

export default class Tips extends Component {
  constructor(props) {
    super(props);
    this.staticTextEditor = React.createRef();
    this.state = { prevContent: '', prevTitle: '', visible: false };
  }

  componentDidMount() {
    const { code, lang } = this.props;
    queryToolTip({ textCode: code, lang }).then(res => {
      if (res) {
        this.setState({ prevContent: res.text, prevTitle: res.title });
      } else {
        this.setState({ prevContent: '', prevTitle: '' });
      }
    });
  }

  @Bind()
  handleVisible() {
    const { visible } = this.state;
    this.setState({ visible });
  }

  @Bind()
  renderRichEditor() {
    const { prevContent, prevTitle } = this.state;
    const { title } = this.props;
    return (
      <>
        <div style={{ textAlign: 'center' }}>{title || prevTitle}</div>
        <StaticTextEditor
          content={prevContent}
          readOnly
          onRef={staticTextEditor => {
            this.staticTextEditor = staticTextEditor;
          }}
        />
      </>
    );
  }

  render() {
    const { children, title, code, lang, ...others } = this.props;
    return (
      <Tooltip
        trigger="click"
        overlayClassName={styles['editor-tooltip']}
        {...others}
        title={this.renderRichEditor()}
      >
        {children}
      </Tooltip>
    );
  }
}
