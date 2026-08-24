/**
 * DynamicPage.js
 * @date 2018/11/7
 * @author WY yang.wang06@hand-china.com
 */

import React from 'react';

import { Button, Form } from 'hzero-ui';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';
import { isFunction } from 'lodash';

import PageScriptModal from '../ScriptEdit/PageScriptModal';
import styles from '../../index.less';

@Form.create({ fieldNameProp: null })
export default class DynamicPage extends React.Component {
  state = {
    // 页面方法的编辑
    pageScriptModalProps: {
      visible: false,
    },
  };

  render() {
    const { config = {} } = this.props;
    const { pageScriptModalProps } = this.state;
    return (
      <Form>
        <Form.Item className="ant-form-item-without-help">
          {intl.get('hpfm.ui.model.page.pageCode').d('页面编码')}: {config.pageCode}
        </Form.Item>
        <Form.Item className="ant-form-item-without-help">
          {intl.get('hpfm.ui.model.page.description').d('页面描述')}: {config.description}
        </Form.Item>
        <Form.Item className="ant-form-item-without-help">
          <Button type="primary" onClick={this.handleEditPageFunc} className={styles['full-width']}>
            {intl.get('hpfm.ui.model.page.edit').d('编辑页面方法')}
          </Button>
        </Form.Item>
        <PageScriptModal
          {...pageScriptModalProps}
          onOk={this.handleEditPageScriptSave}
          onCancel={this.handleEditPageFuncCancel}
        />
      </Form>
    );
  }

  @Bind()
  handleEditPageScriptSave(scripts) {
    const { onUpdateConfig, config = {} } = this.props;
    if (isFunction(onUpdateConfig)) {
      onUpdateConfig({
        ...config,
        scripts,
      });
    }
    this.setState({
      pageScriptModalProps: {
        visible: false,
      },
    });
  }

  @Bind()
  handleEditPageFuncCancel() {
    this.setState({
      pageScriptModalProps: {
        visible: false,
      },
    });
  }

  @Bind()
  handleEditPageFunc() {
    const { config = {} } = this.props;
    const { scripts = [] } = config;
    // Page
    this.setState({
      pageScriptModalProps: {
        visible: true,
        dataSource: scripts,
      },
    });
  }
}
