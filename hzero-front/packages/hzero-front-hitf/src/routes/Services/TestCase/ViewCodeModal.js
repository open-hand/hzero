/**
 * ViewCodeModal - 代码预览弹窗
 * @date: 2019/6/12
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Button, Modal, Select, Spin } from 'hzero-ui';
import classnames from 'classnames';
import { Controlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/mode/javascript/javascript'; // javascript/json 样式
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/dracula.css';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import styles from './index.less';

const { Option } = Select;

/**
 * 代码预览弹窗
 * @extends {Component} - Component
 * @reactProps {boolean} visible - 是否显示代码预览弹窗
 * @reactProps {boolean} loading - 加载中标志
 * @reactProps {array} codeTypes - 代码类型值集
 * @reactProps {string} code - 显示的代码
 * @reactProps {Function} onCancel - 关闭代码预览弹窗
 * @reactProps {Function} onChangeType - 切换下拉框选项
 * @return React.element
 */
export default class ViewCodeModal extends Component {
  state = {
    type: 'JAVA',
  };

  /**
   * 切换下拉框选项
   * @param {string} value - 选中值
   */
  @Bind()
  changeCodeType(value) {
    this.setState(
      {
        type: value,
      },
      () => {
        const { onChangeType } = this.props;
        onChangeType(value);
      }
    );
  }

  /**
   * 关闭模态框
   */
  @Bind()
  closeModal() {
    const { onCancel } = this.props;
    this.setState(
      {
        type: 'JAVA',
      },
      () => {
        onCancel();
      }
    );
  }

  render() {
    const { onCancel, visible, codeTypes, code, loading } = this.props;
    const { type } = this.state;
    return (
      <Modal
        width={700}
        visible={visible}
        className={classnames(styles['hitf-testcase-code-modal'])}
        destroyOnClose
        maskClosable
        title={intl.get('hitf.services.view.button.view.code').d('代码预览')}
        onCancel={this.closeModal}
        footer={[
          <Button key="cancel" onClick={() => onCancel()}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>,
        ]}
      >
        <Spin spinning={loading}>
          <Select
            className={classnames(styles['type-select'])}
            onChange={this.changeCodeType}
            value={type}
          >
            {codeTypes.length &&
              codeTypes.map(({ value, meaning }) => (
                <Option key={value} value={value}>
                  {meaning}
                </Option>
              ))}
          </Select>
          <div>
            <CodeMirror
              autoScroll
              className={styles['hzero-codemirror']}
              value={code}
              readOnly
              options={{
                mode: 'javascript',
                lineNumbers: true,
              }}
            />
          </div>
        </Spin>
      </Modal>
    );
  }
}
