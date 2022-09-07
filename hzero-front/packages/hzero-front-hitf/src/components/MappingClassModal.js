/**
 * MappingClassModal - 映射类预览弹窗
 * @date: 2019/9/16
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Button, Modal, Spin, Row, Col, Form } from 'hzero-ui';
import 'codemirror/mode/clike/clike'; // java 样式
import 'codemirror/mode/javascript/javascript'; // javascript/json 样式
import CodeMirror from 'components/CodeMirror';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

const FormItem = Form.Item;

/**
 * 映射类预览弹窗
 * @extends {Component} - Component
 * @reactProps {string} data - 映射类代码
 * @reactProps {boolean} loading - 加载中标志
 * @reactProps {boolean} testLoading - 测试中标志
 * @reactProps {boolean} visible - 是否显示代码预览弹窗
 * @reactProps {Function} onCancel - 关闭代码预览弹窗
 * @reactProps {Function} onTest - 测试映射类代码
 * @return React.element
 */

@Form.create({ fieldNameProp: null })
export default class MappingClassModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isShowLog: false,
      logMsg: '',
      status: 'success',
    };
  }

  /**
   * 关闭模态框
   */
  @Bind()
  handleCloseMappingClassModal() {
    const { onCancel } = this.props;
    if (this.codeMirrorEditor) {
      const content = this.codeMirrorEditor.getValue();
      onCancel(content);
    }
  }

  /**
   * 测试映射类代码
   */
  @Bind()
  handleTest() {
    const { onTest = () => {} } = this.props;
    if (this.codeMirrorEditor) {
      const content = this.codeMirrorEditor.getValue();
      onTest(content, this.handleOpenLogModal);
    }
  }

  /**
   * 显示测试日志
   * @param {object} res - 测试结果
   */
  @Bind()
  handleOpenLogModal(res) {
    const { logMsg = '', status = '' } = res;
    this.setState({
      isShowLog: true,
      logMsg,
      status,
    });
  }

  /**
   * 关闭测试日志弹窗
   */
  @Bind()
  handleCloseLogModal() {
    this.setState({
      isShowLog: false,
      logMsg: '',
    });
  }

  /**
   * @function setCodeMirror - 获取CodeMirror实例
   * @param {object} editor - CodeMirror实例
   */
  @Bind()
  setCodeMirror(editor) {
    this.codeMirrorEditor = editor;
  }

  /**
   * @function setLogMirror - 获取CodeMirror实例
   * @param {object} editor - CodeMirror实例
   */
  @Bind()
  setLogMirror(editor) {
    this.logMirrorEditor = editor;
  }

  render() {
    const { loading, testLoading, visible, data, readOnly } = this.props;
    const { isShowLog, logMsg, status } = this.state;
    const MODAL_FORM_ITEM_LAYOUT = {
      labelCol: {
        span: 2,
      },
      wrapperCol: {
        span: 22,
      },
    };
    return (
      <>
        <Modal
          width={750}
          visible={visible}
          destroyOnClose
          maskClosable
          title={intl.get('hitf.common.view.message.title.edit.mapping.class').d('查看编辑映射类')}
          onCancel={this.handleCloseMappingClassModal}
          footer={[
            <Button key="test" onClick={this.handleTest} loading={testLoading}>
              {intl.get('hitf.common.button.test').d('测试')}
            </Button>,
            <Button key="cancel" onClick={this.handleCloseMappingClassModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <Spin spinning={loading}>
            <div>
              <CodeMirror
                fetchCodeMirror={(editor) => this.setCodeMirror(editor)}
                codeMirrorProps={{
                  value: data,
                  options: {
                    mode: 'text/x-java',
                    lineNumbers: true,
                    readOnly: readOnly && 'nocursor',
                  },
                  readOnly,
                  disabled: readOnly,
                }}
              />
            </div>
          </Spin>
        </Modal>
        <Modal
          width={750}
          bodyStyle={{ padding: '0 24px' }}
          visible={isShowLog}
          destroyOnClose
          maskClosable
          title={intl.get('hitf.common.view.message.title.testLog').d('测试日志')}
          onCancel={this.handleCloseLogModal}
          footer={[
            <Button key="cancel" onClick={this.handleCloseLogModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <Row style={{ marginBottom: 0 }}>
            <Col>
              <FormItem
                label={intl.get('hitf.common.model.testResult').d('测试结果')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {status === 'success'
                  ? intl.get('hitf.common.model.success').d('成功')
                  : intl.get('hitf.common.model.failed').d('失败')}
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col>
              <FormItem
                label={intl.get('hitf.common.model.log').d('日志信息')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                <CodeMirror
                  fetchCodeMirror={(editor) => this.setLogMirror(editor)}
                  codeMirrorProps={{
                    value: logMsg,
                    options: {
                      mode: 'text/x-java',
                      lineNumbers: true,
                      readOnly: 'nocursor',
                    },
                  }}
                />
              </FormItem>
            </Col>
          </Row>
        </Modal>
      </>
    );
  }
}
