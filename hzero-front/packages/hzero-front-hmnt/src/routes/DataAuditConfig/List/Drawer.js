import React from 'react';
import { Form, Input, Modal, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import styles from './styles.less';

const FormItem = Form.Item;
const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.Component {
  /**
   * 模态框确认
   */
  @Bind()
  handleOK() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  }

  /**
   * 服务Lov改变时，审计接口Lov清空
   */
  @Bind()
  handleChangeServiceName() {
    this.props.form.resetFields('permissionId');
  }

  render() {
    const {
      form,
      initData,
      title,
      modalVisible,
      onCancel,
      isTenantRoleLevel,
      loading = false,
      aboutConfigAuditList = [],
      onAddConfigAudit = (e) => e,
      onDeleteConfigAudit = (e) => e,
    } = this.props;
    const {
      auditDataConfigId,
      tenantId,
      tenantName,
      serviceName,
      tableName,
      displayName,
    } = initData;
    const { getFieldDecorator } = form;
    const columns = [
      !isTenantRoleLevel && {
        title: intl.get('hzero.common.model.common.tenantId').d('租户'),
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hmnt.dataAuditConfig.model.dataAudit.auditTypeMeaning').d('审计类型'),
        dataIndex: 'auditTypeMeaning',
      },
      {
        title: intl.get('hmnt.dataAuditConfig.model.dataAudit.auditType').d('审计内容'),
        dataIndex: 'operationalContent',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        fixed: 'right',
        dataIndex: 'edit',
        render: (text, record) => {
          const operators = [
            {
              key: 'delete',
              ele: (
                <a onClick={() => onDeleteConfigAudit(record)}>
                  {' '}
                  {intl.get('hzero.common.button.delete').d('删除')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);

    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOK}
      >
        <Form>
          {!isTenantRoleLevel && (
            <FormItem
              {...formLayout}
              label={intl.get('hzero.common.model.common.tenantId').d('租户')}
            >
              {getFieldDecorator('tenantId', {
                initialValue: tenantId,
              })(<Lov code="HPFM.TENANT" textValue={tenantName} disabled />)}
            </FormItem>
          )}
          <FormItem
            {...formLayout}
            label={intl.get('hmnt.dataAuditConfig.model.dataAuditConfig.serviceName').d('服务名')}
          >
            {getFieldDecorator('auditType', {
              initialValue: serviceName,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hmnt.dataAuditConfig.model.dataAuditConfig.serviceName')
                      .d('服务名'),
                  }),
                },
              ],
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            {...formLayout}
            label={intl.get('hmnt.dataAuditConfig.model.dataAuditConfig.tableName').d('表名')}
          >
            {getFieldDecorator('tableName', {
              rules: [
                {
                  max: 480,
                  message: intl.get('hzero.common.validation.max', {
                    max: 480,
                  }),
                },
              ],
              initialValue: tableName,
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            {...formLayout}
            label={intl.get('hmnt.dataAuditConfig.model.dataAuditConfig.displayName').d('展示名称')}
          >
            {getFieldDecorator('displayName', {
              rules: [
                {
                  max: 320,
                  message: intl.get('hzero.common.validation.max', {
                    max: 320,
                  }),
                },
              ],
              initialValue: displayName,
            })(<Input />)}
          </FormItem>
        </Form>
        <div className={styles['data-audit-btn-warp']}>
          <Lov
            isButton
            code="HMNT.AUDIT_DATA.OP"
            onChange={(_, record) => onAddConfigAudit(record)}
            queryParams={{ tenantId, auditDataConfigId }}
          >
            {intl.get('hmnt.dataAuditConfig.view.button.aboutAudit').d('关联操作审计')}
          </Lov>
        </div>
        <Table bordered columns={columns} dataSource={aboutConfigAuditList} loading={loading} />
      </Modal>
    );
  }
}
