import React from 'react';
import { Modal, Button } from 'hzero-ui';

import intl from 'utils/intl';
import SqlCodemirror from './SqlCodemirror';

/**
 * sql校验信息
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onOk - 确认之后的回调函数
 * @reactProps {Boolean} contentVisible - 收件人模态框是否可见
 * @return React.element
 */

export default class SqlDrawer extends React.PureComponent {
  codeMirrorEditor;

  /**
   * @function setCodeMirror - 获取CodeMirror实例
   * @param {object} editor - CodeMirror实例
   */
  setCodeMirror(editor) {
    this.codeMirrorEditor = editor;
  }

  render() {
    const { sqlDrawerVisible, onOk, sqlContent = {} } = this.props;
    const codeMirrorProps = {
      value: sqlContent.sql,
    };
    return (
      <Modal
        destroyOnClose
        title={intl.get('hrpt.reportDataSet.view.button.sqlValidate').d('SQL校验')}
        width={650}
        visible={sqlDrawerVisible}
        closable={false}
        footer={
          <Button type="primary" onClick={onOk}>
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>
        }
      >
        <SqlCodemirror
          codeMirrorProps={codeMirrorProps}
          fetchCodeMirror={editor => this.setCodeMirror(editor)}
        />
      </Modal>
    );
  }
}
