import React from 'react';
import PropTypes from 'prop-types';
import { Form, Button, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class QueryForm extends React.Component {
  static propTypes = {
    onSearch: PropTypes.func.isRequired,
  };

  /**
   * 重置查询表单
   * @param {*} e
   */
  @Bind()
  handleResetBtnClick(e) {
    e.preventDefault();
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * 查询表单-查询
   * @param {*} e
   */
  @Bind()
  handleSearchBtnClick(e) {
    const { currentAddApprover } = this.props;
    e.preventDefault();
    if (currentAddApprover.needAppoint === 'Y') {
      const { onSearch } = this.props;
      onSearch();
    }
  }

  render() {
    const { form } = this.props;
    return (
      <Form layout="inline" style={{ marginBottom: '16px' }}>
        <Form.Item label={intl.get('entity.employee.employeeCode').d('员工编码')}>
          {form.getFieldDecorator('employeeCode')(<Input />)}
        </Form.Item>
        <Form.Item>
          <Button onClick={this.handleResetBtnClick} style={{ marginRight: '8px' }}>
            {intl.get('hzero.common.button.reset').d('重置')}
          </Button>
          <Button htmlType="submit" type="primary" onClick={this.handleSearchBtnClick}>
            {intl.get('hzero.common.button.search').d('查询')}
          </Button>
        </Form.Item>
      </Form>
    );
  }
}
