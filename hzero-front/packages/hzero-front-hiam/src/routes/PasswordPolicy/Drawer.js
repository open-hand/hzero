import React from 'react';
import { Modal, Table, Form, Row, Col, Input, Checkbox } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import {
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

import { Button as ButtonPermission } from 'components/Permission';
import ValueList from 'components/ValueList';

/**
 * GroupModal-选择新的用户组 弹框
 * @reactProps {Boolean} visible 模态框是否显示
 * @reactProps {Boolean} dataSource 用户组的数据源
 * @reactProps {Function(selectedRows:Object[]):Promise|*} onSave 确认按钮的回调,接收选中的用户组,返回一个Promise对象或者任意值
 * @reactProps {Function} onCancel 取消按钮的回调
 */
@Form.create({ fieldNameProp: null })
export default class GroupModal extends React.Component {
  state = {
    selectedRowKeys: [],
  };

  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    this.handleUnSelectAll();
    onCancel();
  }

  @Bind()
  handleUnSelectAll() {
    this.setState({
      selectedRowKeys: [],
    });
  }

  @Bind()
  handleAdd(type) {
    const { onAdd, form } = this.props;
    const { selectedRowKeys } = this.state;
    this.setState({
      selectedRowKeys: [],
    });
    onAdd(selectedRowKeys, type, form.getFieldsValue(), this.handleUnSelectAll);
  }

  @Bind()
  handleDelete(type) {
    const { onDelete, form } = this.props;
    const { selectedRowKeys } = this.state;
    this.setState({
      selectedRowKeys: [],
    });
    onDelete(selectedRowKeys, type, form.getFieldsValue(), this.handleUnSelectAll);
  }

  @Bind()
  handleRowSelectionChange(selectedRowKeys) {
    this.setState({
      selectedRowKeys,
    });
  }

  @Bind()
  handleReset() {
    const {
      form: { resetFields },
    } = this.props;
    resetFields();
  }

  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    form.validateFields((_, value) => {
      this.setState({
        selectedRowKeys: [],
      });
      onSearch(value);
    });
  }

  render() {
    const {
      visible,
      path,
      dataSource,
      pagination,
      loading,
      onPagination,
      form,
      onAdd,
      onDelete,
    } = this.props;
    const { selectedRowKeys } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectionChange,
    };
    const columns = [
      {
        title: intl.get('hiam.passwordPolicy.model.passwordPolicy.loginName').d('账户'),
        dataIndex: 'loginName',
        width: 200,
      },
      {
        title: intl.get('hiam.passwordPolicy.model.passwordPolicy.realName').d('用户描述'),
        dataIndex: 'realName',
      },
      {
        title: intl
          .get('hiam.passwordPolicy.model.passwordPolicy.secCheckPhoneFlag')
          .d('启用手机校验码'),
        dataIndex: 'secCheckPhoneFlag',
        width: 150,
        render: (val, record) => (
          <Checkbox
            checked={val}
            onChange={(e) => {
              if (e.target.checked) {
                onAdd([record.id], 'phone', form.getFieldsValue(), this.handleUnSelectAll);
              } else {
                onDelete([record.id], 'phone', form.getFieldsValue(), this.handleUnSelectAll);
              }
            }}
          />
        ),
      },
      {
        title: intl
          .get('hiam.passwordPolicy.model.passwordPolicy.secCheckEmailFlag')
          .d('启用邮箱校验码'),
        dataIndex: 'secCheckEmailFlag',
        width: 150,
        render: (val, record) => (
          <Checkbox
            checked={val}
            onChange={(e) => {
              if (e.target.checked) {
                onAdd([record.id], 'email', form.getFieldsValue(), this.handleUnSelectAll);
              } else {
                onDelete([record.id], 'email', form.getFieldsValue(), this.handleUnSelectAll);
              }
            }}
          />
        ),
      },
    ].filter(Boolean);
    return (
      <Modal
        visible={visible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={this.handleCancel}
        width={900}
        title={intl.get('hiam.passwordPolicy.view.message.title.user').d('指定用户')}
        footer={null}
      >
        <Form className="more-fields-search-form">
          <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.passwordPolicy.model.passwordPolicy.loginName').d('用户名')}
              >
                {form.getFieldDecorator('loginName')(<Input />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl
                  .get('hiam.passwordPolicy.model.passwordPolicy.realName')
                  .d('用户真实名')}
              >
                {form.getFieldDecorator('realName')(<Input />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT} className="search-btn-more">
              <Form.Item>
                <ButtonPermission onClick={this.handleReset}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </ButtonPermission>
                <ButtonPermission type="primary" htmlType="submit" onClick={this.handleSearch}>
                  {intl.get('hzero.common.button.search').d('查询')}
                </ButtonPermission>
              </Form.Item>
            </Col>
          </Row>
          <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl
                  .get('hiam.passwordPolicy.model.passwordPolicy.secCheckPhoneFlag')
                  .d('启用手机校验码')}
              >
                {form.getFieldDecorator('secCheckPhoneFlag')(
                  <ValueList allowClear style={{ width: '100%' }} lovCode="HPFM.FLAG" />
                )}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl
                  .get('hiam.passwordPolicy.model.passwordPolicy.secCheckEmailFlag')
                  .d('启用邮箱校验码')}
              >
                {form.getFieldDecorator('secCheckEmailFlag')(
                  <ValueList allowClear style={{ width: '100%' }} lovCode="HPFM.FLAG" />
                )}
              </Form.Item>
            </Col>
          </Row>
        </Form>
        <Row style={{ marginBottom: 16 }}>
          <Col span={23}>
            <ButtonPermission
              disabled={selectedRowKeys.length === 0}
              type="primary"
              onClick={() => {
                this.handleAdd('phone');
              }}
              permissionList={[
                {
                  code: `${path}.button.enable.phone`,
                  type: 'button',
                  meaning: '密码策略-指定用户-启用手机',
                },
              ]}
              icon="plus"
            >
              {intl.get('hiam.passwordPolicy.button.enablePhone').d('启用手机')}
            </ButtonPermission>

            <ButtonPermission
              disabled={selectedRowKeys.length === 0}
              style={{ marginLeft: 8 }}
              onClick={() => {
                this.handleDelete('phone');
              }}
              permissionList={[
                {
                  code: `${path}.button.disable.phone`,
                  type: 'button',
                  meaning: '密码策略-指定用户-禁用手机',
                },
              ]}
              icon="delete"
            >
              {intl.get('hiam.passwordPolicy.button.disablePhone').d('禁用手机')}
            </ButtonPermission>
            <ButtonPermission
              disabled={selectedRowKeys.length === 0}
              type="primary"
              style={{ marginLeft: 8 }}
              onClick={() => {
                this.handleAdd('email');
              }}
              permissionList={[
                {
                  code: `${path}.button.enable.email`,
                  type: 'button',
                  meaning: '密码策略-指定用户-启用邮箱',
                },
              ]}
              icon="plus"
            >
              {intl.get('hiam.passwordPolicy.button.enableEmail').d('启用邮箱')}
            </ButtonPermission>

            <ButtonPermission
              disabled={selectedRowKeys.length === 0}
              style={{ marginLeft: 8 }}
              onClick={() => {
                this.handleDelete('email');
              }}
              permissionList={[
                {
                  code: `${path}.button.disable.email`,
                  type: 'button',
                  meaning: '密码策略-指定用户-禁用邮箱',
                },
              ]}
              icon="delete"
            >
              {intl.get('hiam.passwordPolicy.button.disableEmail').d('禁用邮箱')}
            </ButtonPermission>
          </Col>
        </Row>
        <Table
          bordered
          rowKey="id"
          loading={loading}
          dataSource={dataSource}
          pagination={pagination}
          columns={columns}
          style={{ marginTop: 14 }}
          onChange={onPagination}
          rowSelection={rowSelection}
          scroll={{ x: tableScrollWidth(columns) }}
        />
      </Modal>
    );
  }
}
