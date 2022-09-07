import React from 'react';
import { Form, Modal, Icon, Select, Popconfirm, Input } from 'hzero-ui';
import intl from 'utils/intl';
import { getEditTableData } from 'utils/utils';
import EditTable from 'components/EditTable';
import { Bind } from 'lodash-decorators';
import styles from './index.less';

const FormItem = Form.Item;
const { Option } = Select;

export default class ConditionModal extends React.Component {
  constructor(props) {
    super(props);
    const { paramList = [] } = props;
    this.state = {
      // loading: false,
      paramList: paramList.map((i) => ({ ...i, _status: 'update' })),
    };
  }

  onDelete(deleteId) {
    const { paramList } = this.state;
    this.setState({
      paramList: paramList.filter(({ _status, _id, paramKey }) =>
        _status === 'create' ? deleteId !== _id : deleteId !== paramKey
      ),
    });
  }

  @Bind()
  addParam() {
    const { paramList } = this.state;
    paramList.push({ _status: 'create', _id: paramList.length + 1 });
    this.setState({ paramList });
  }

  @Bind()
  onOk() {
    const { paramList } = this.state;
    const { onSave, onClose, readOnly } = this.props;
    if (!readOnly) {
      const newParams = getEditTableData(paramList, ['_id', '_status']) || [];
      if (paramList.length > 0 && newParams.length === 0) {
        return;
      }
      // eslint-disable-next-line no-unused-expressions
      typeof onSave === 'function' && onSave(newParams);
    }
    onClose();
  }

  getParamValueByType(type, form) {
    const { readOnly } = this.props;
    if (type === 'fixed') {
      return <Input trim disabled={readOnly} />;
    }
    if (type === 'context') {
      return (
        <Select
          style={{ width: '100%' }}
          onChange={(v) => form.setFieldsValue({ paramKey: v })}
          dropdownClassName={styles['param-dropdown']}
          optionLabelProp="title"
          disabled={readOnly}
        >
          <Option
            value="organizationId"
            title={intl.get('hpfm.customize.common.organizationId').d('采购方租户')}
          >
            <div className="option-title">
              {intl.get('hpfm.customize.common.organizationId').d('采购方租户')}
            </div>
            <div className="option-value">organizationId</div>
          </Option>
          <Option
            value="tenantId"
            title={intl.get('hpfm.customize.common.tenantId').d('供应商租户')}
          >
            <div className="option-title">
              {intl.get('hpfm.customize.common.tenantId').d('供应商租户')}
            </div>
            <div className="option-value">tenantId</div>
          </Option>
        </Select>
      );
    }
    return <span />;
  }

  autoCompleteFieldKey(id, form) {
    const { fieldList = {} } = this.props;
    const fieldObj = (fieldList[form.getFieldValue(`paramUnitId`)] || []).find(
      (i) => i.modelFieldId === id
    );
    form.setFieldsValue({ paramKey: fieldObj.unitFieldCode });
  }

  render() {
    const { paramList } = this.state;
    const { visible, onClose, readOnly, unitList = [], fieldList = {}, id } = this.props;
    const columns = [
      {
        title: intl.get('hpfm.customize.common.paramKey').d('参数名'),
        dataIndex: 'paramKey',
        width: 210,
        render: (val, record) => {
          if (['create', 'update'].includes(record._status)) {
            const { $form } = record;
            return (
              <FormItem>
                {$form.getFieldDecorator('paramKey', {
                  initialValue: val,
                  rules: [
                    {
                      required: !readOnly && true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.individual.common.paramKey').d('参数名'),
                      }),
                    },
                  ],
                })(<Input trim disabled={readOnly} />)}
              </FormItem>
            );
          }
          return val;
        },
      },
      {
        title: intl.get('hpfm.customize.common.paramType').d('参数类型'),
        dataIndex: 'paramType',
        width: 210,
        render: (val, record) => {
          if (['create', 'update'].includes(record._status)) {
            const { $form } = record;
            return (
              <FormItem wrapperCol={{ span: 24 }}>
                {$form.getFieldDecorator('paramType', {
                  initialValue: record.paramType || 'fixed',
                })(
                  <Select style={{ width: '100%' }} disabled={readOnly}>
                    <Option value="context">
                      {intl.get('hpfm.customize.common.contextParam').d('上下文参数')}
                    </Option>
                    <Option value="url">
                      {intl.get('hpfm.customize.common.urlParam').d('url参数')}
                    </Option>
                    <Option value="fixed">
                      {intl.get('hpfm.customize.common.fixed').d('固定值')}
                    </Option>
                    <Option value="unit">
                      {intl.get('hpfm.customize.common.unit').d('表单字段')}
                    </Option>
                  </Select>
                )}
              </FormItem>
            );
          }
          return val;
        },
      },
      {
        title: intl.get('hpfm.customize.common.paramValue').d('参数值'),
        dataIndex: 'paramValue',
        width: 420,
        render: (val, record) => {
          if (['create', 'update'].includes(record._status)) {
            const { $form } = record;
            const paramType = $form.getFieldValue('paramType');
            return (
              <div style={{ width: '100%', display: 'flex' }}>
                <FormItem
                  style={{
                    display: paramType === 'unit' ? 'inline-block' : 'none',
                  }}
                >
                  {$form.getFieldDecorator('paramUnitId', {
                    initialValue: record.paramUnitId,
                    rules: [
                      {
                        required: !readOnly && paramType === 'unit',
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.individual.common.paramUnit').d('所属单元'),
                        }),
                      },
                    ],
                  })(
                    <Select
                      style={{ width: '100%' }}
                      placeholder={intl.get('hpfm.individual.common.paramUnit').d('所属单元')}
                      optionLabelProp="title"
                    >
                      {unitList.map((unit) => (
                        <Option
                          value={unit.unitId}
                          title={unit.unitName}
                          disabled={unit.unitType === 'GRID' && unit.unitId !== id}
                        >
                          <div className="option-title">{unit.unitName}</div>
                          <div className="option-value">{unit.unitCode}</div>
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
                <FormItem
                  style={{
                    display: paramType !== 'unit' ? 'inline-block' : 'none',
                  }}
                >
                  {$form.getFieldDecorator('paramValue', {
                    initialValue: record.paramValue,
                  })(this.getParamValueByType(paramType, $form))}
                </FormItem>
                <FormItem
                  style={{
                    display: paramType === 'unit' ? 'inline-block' : 'none',
                  }}
                >
                  {$form.getFieldDecorator('paramFieldId', {
                    initialValue: record.paramFieldId,
                    rules: [
                      {
                        required: !readOnly && paramType === 'unit',
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.individual.common.paramField').d('字段编码'),
                        }),
                      },
                    ],
                  })(
                    <Select
                      style={{ width: '100%' }}
                      optionLabelProp="title"
                      placeholder={intl.get('hpfm.individual.common.paramField').d('字段编码')}
                      disabled={readOnly}
                      onChange={(v) => this.autoCompleteFieldKey(v, $form)}
                    >
                      {(fieldList[$form.getFieldValue(`paramUnitId`)] || []).map((f1) => (
                        <Option value={f1.modelFieldId} title={f1.unitFieldName}>
                          <div className="option-title">{f1.unitFieldName}</div>
                          <div className="option-value">{f1.unitFieldCode}</div>
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </div>
            );
          }
          return val;
        },
      },
      !readOnly && {
        title: intl.get('hpfm.customize.common.op').d('操作'),
        dataIndex: 'op',
        width: 60,
        render: (val, record) => (
          <Popconfirm
            title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
            okText={intl.get('hzero.common.status.yes').d('是')}
            cancelText={intl.get('hzero.common.status.no').d('否')}
            onConfirm={() =>
              this.onDelete(record._status === 'create' ? record._id : record.paramKey)
            }
          >
            <Icon type="delete" style={{ cursor: 'pointer' }} />
          </Popconfirm>
        ),
      },
    ].filter(Boolean);
    return (
      <Modal
        destroyOnClose
        maskClosable
        width={1007}
        visible={visible}
        onCancel={onClose}
        onOk={this.onOk}
        bodyStyle={{ padding: '12px' }}
        className={styles['params-modal-container']}
        title={intl.get('hpfm.individual.view.message.title.valueParamsConfig').d('值集参数配置')}
      >
        <header className="header">
          <div className={styles.title}>
            {intl.get('hzero.common.button.paramList').d('参数列表')}
          </div>
          {/* eslint-disable-next-line jsx-a11y/click-events-have-key-events */}
          <div
            onClick={this.addParam}
            style={{
              display: readOnly ? 'none' : 'flex',
              lineHeight: '18px',
              height: '28px',
              alignItems: 'center',
              marginLeft: '8px',
              cursor: 'pointer',
            }}
          >
            <Icon
              type="plus-circle-o"
              style={{ fontSize: '18px', color: '#34a6f8', marginRight: '8px' }}
            />
            {intl.get('hzero.common.button.addParam').d('添加参数')}
          </div>
        </header>
        <div className="params-list-area">
          <EditTable bordered columns={columns} dataSource={paramList} pagination={false} />
        </div>
      </Modal>
    );
  }
}
