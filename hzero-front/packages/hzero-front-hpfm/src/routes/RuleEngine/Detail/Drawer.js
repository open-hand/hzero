import React from 'react';
import { Button, Form, Icon, Input, Modal, Spin } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

import styles from './index.less';

const FormItem = Form.Item;
const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 6 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 17 },
  },
};

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.PureComponent {
  state = {
    keys: [], // 表单键
    uuid: 0, // 键
  };

  /**
   * 新增键,值
   */
  @Bind()
  handleAdd() {
    const { keys, uuid } = this.state;
    const nextKeys = keys.concat(uuid);
    this.setState({
      keys: nextKeys,
      uuid: uuid + 1,
    });
  }

  /**
   * 移除键，值
   * @param {*}
   */
  @Bind()
  handleDelete(k) {
    const { keys } = this.state;
    this.setState({
      keys: keys.filter(key => key !== k),
    });
  }

  @Bind()
  handleTest(e) {
    const { form, onTest } = this.props;
    const { keys } = this.state;
    e.preventDefault();
    form.validateFields((err, values) => {
      let params = [];
      let newParams = {};
      params = keys.map(item => ({ [values[`key${item}`]]: values[`value${item}`] }));
      params.forEach(element => {
        newParams = { ...newParams, ...element };
      });
      if (isEmpty(err)) {
        onTest(newParams);
      }
    });
  }

  /**
   * 关闭模态框
   */
  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    this.setState({
      keys: [],
      uuid: 0,
    });
    onCancel();
  }

  render() {
    const {
      visible,
      form: { getFieldDecorator, getFieldValue },
      testContent,
      testRuleEngineLoading = false,
    } = this.props;
    const { keys } = this.state;
    const formItems = keys.map(k => {
      return (
        <React.Fragment key={k}>
          <FormItem
            {...formItemLayout}
            label={intl.get('hpfm.ruleEngine.model.ruleEngine.paramsName').d('参数名称')}
            required={false}
            // key={k}
          >
            {getFieldDecorator(`key${k}`, {
              rules: [
                {
                  required: !isUndefined(getFieldValue(`value${k}`)),
                  whitespace: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.ruleEngine.model.ruleEngine.paramsName').d('参数名称'),
                  }),
                },
              ],
            })(<Input />)}
          </FormItem>
          <FormItem
            {...formItemLayout}
            label={intl.get('hpfm.ruleEngine.model.ruleEngine.paramsValue').d('参数值')}
            required={false}
            // key={k}
          >
            {getFieldDecorator(`value${k}`, {
              rules: [
                {
                  required: false,
                  whitespace: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.ruleEngine.model.ruleEngine.paramsValue').d('参数值'),
                  }),
                },
              ],
            })(<Input />)}
          </FormItem>
          {keys.length >= 1 ? (
            <Icon
              className={styles.iconButton}
              type="minus-circle-o"
              onClick={() => this.handleDelete(k)}
            />
          ) : null}
        </React.Fragment>
      );
    });
    return (
      <Modal
        destroyOnClose
        width={550}
        visible={visible}
        onCancel={this.handleCancel}
        title={[
          <Button
            type="primary"
            icon="plus"
            style={{ marginRight: '16px' }}
            onClick={this.handleAdd}
            key="1"
            disabled={testRuleEngineLoading}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>,
          <Button
            type="default"
            icon="check-circle"
            htmlType="submit"
            onClick={this.handleTest}
            key="11"
            loading={testRuleEngineLoading}
          >
            {intl.get('hpfm.ruleEngine.view.button.check').d('校验')}
          </Button>,
        ]}
        footer={
          <Button type="default" onClick={this.handleCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
        }
      >
        <Spin spinning={testRuleEngineLoading}>
          <Form layout="inline">{formItems}</Form>
          <FormItem label={intl.get('hpfm.ruleEngine.model.ruleEngine.testResult').d('测试结果')}>
            <Input.TextArea
              autosize={{ minRows: 6 }}
              value={isUndefined(testContent) ? '' : JSON.stringify(testContent)}
            />
          </FormItem>
        </Spin>
      </Modal>
    );
  }
}
