/**
 * Drawer - 消息模板明细模板参数
 * @date: 2019-10-9
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import React from 'react';
import { Form, Input, Modal, Button, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import EditTable from 'components/EditTable';
import TLEditor from 'components/TLEditor';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { getEditTableData } from 'utils/utils';

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.PureComponent {
  @Bind()
  handleSearch(params = {}) {
    const { onSearch = e => e } = this.props;
    onSearch(params);
  }

  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  handleInit() {
    const { onInit = e => e } = this.props;
    onInit();
  }

  @Bind()
  handleDelete(record) {
    const { onDelete = e => e } = this.props;
    onDelete(record);
  }

  @Bind()
  handleCreate() {
    const { onCreate = e => e } = this.props;
    onCreate({
      _status: 'create',
    });
  }

  @Bind()
  handleSave(record) {
    const { onOk = e => e, dataSource } = this.props;
    const params = getEditTableData(dataSource).filter(item => item.argId === record.argId);
    onOk(params[0]);
  }

  @Bind()
  handlePagination(pagination) {
    this.handleSearch({
      page: pagination,
    });
  }

  render() {
    const {
      onCancel = e => e,
      onEdit = e => e,
      fetchLoading = false,
      loading = false,
      visible = false,
      dataSource = [],
      pagination = {},
      path,
      form,
    } = this.props;
    const columns = [
      {
        title: intl.get('hmsg.messageTemplate.model.template.argName').d('参数名称'),
        dataIndex: 'argName',
        width: 150,
      },
      {
        title: intl.get('hmsg.messageTemplate.model.template.description').d('参数描述'),
        dataIndex: 'description',
        render: (val, record) => {
          if (record._status === 'update') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('description', {
                  initialValue: val,
                })(
                  <TLEditor
                    label={intl
                      .get('hmsg.messageTemplate.model.template.description')
                      .d('参数描述')}
                    field="description"
                    // eslint-disable-next-line
                    token={record._token}
                  />
                )}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        render: (_, record) => {
          if (record._status === 'update') {
            return (
              <span className="action-link">
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.cancelEdit`,
                      type: 'button',
                      meaning: '模板参数-取消编辑',
                    },
                  ]}
                  onClick={() => onEdit(record, false)}
                >
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </ButtonPermission>
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.save`,
                      type: 'button',
                      meaning: '模板参数-保存',
                    },
                  ]}
                  onClick={() => this.handleSave(record)}
                >
                  {intl.get('hzero.common.button.save').d('保存')}
                </ButtonPermission>
              </span>
            );
          } else if (record._status !== 'update') {
            return (
              <span className="action-link">
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '模板参数-编辑',
                    },
                  ]}
                  onClick={() => onEdit(record, true)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>

                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => this.handleDelete(record)}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.delete`,
                        type: 'button',
                        meaning: '模板参数-删除',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </Popconfirm>
              </span>
            );
          }
        },
      },
    ];
    return (
      <Modal
        width="620px"
        destroyOnClose
        transitionName="move-right"
        wrapClassName="ant-modal-sidebar-right"
        title={intl.get('hmsg.messageTemplate.view.message.title.detail.para').d('模板参数')}
        visible={visible}
        onCancel={onCancel}
        footer={null}
      >
        <Form layout="inline">
          <Form.Item label={intl.get('hmsg.messageTemplate.model.template.argName').d('参数名称')}>
            {form.getFieldDecorator('argName')(<Input />)}
          </Form.Item>
          <Form.Item>
            <Button onClick={this.handleReset} style={{ marginRight: '8px' }}>
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
            <Button type="primary" htmlType="submit" onClick={() => this.handleSearch()}>
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
          </Form.Item>
        </Form>
        <div
          style={{ display: 'flex', justifyContent: 'flex-end' }}
          className="table-list-operator"
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.init`,
                type: 'button',
                meaning: '模板参数-初始化',
              },
            ]}
            type="primary"
            disabled={loading}
            onClick={this.handleInit}
          >
            {intl.get('hmsg.messageTemplate.model.template.init').d('初始化')}
          </ButtonPermission>
        </div>
        <EditTable
          bordered
          rowKey="argId"
          columns={columns}
          loading={fetchLoading || loading}
          dataSource={dataSource}
          pagination={pagination}
          onChange={this.handlePagination}
        />
      </Modal>
    );
  }
}
