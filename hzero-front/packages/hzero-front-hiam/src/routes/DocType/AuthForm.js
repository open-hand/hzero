/* eslint-disable no-nested-ternary */
/**
 * 单据权限 - 采购订单权限维度
 * TODO: 该表格全部给了宽度 所以 通用调整没有调整到
 */
import React from 'react';
import { Form, Input, Spin, Button, Modal, Popconfirm, Select, Tabs } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import uuidv4 from 'uuid/v4';
import EditTable from 'components/EditTable';
import Lov from 'components/Lov';
import intl from 'utils/intl';
import { getEditTableData, getEditTableForm } from 'utils/utils';

import MatchFieldDrawer from './MatchFieldDrawer';

@Form.create({ fieldNameProp: null })
export default class AuthForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      deleteLines: [],
      matchVisible: false,
      matchValue: '',
      editForm: {},
    };
  }

  componentDidMount() {
    const { onRef } = this.props;
    if (onRef) onRef(this);
  }

  /**
   * 保存提交当前权限维度
   */
  @Bind()
  onOk() {
    const { businessList, personalList, handleSave, docTypeId } = this.props;
    const { deleteLines } = this.state;
    const newDeleteLines = deleteLines.map((i) => {
      const { _status, ...res } = i;
      return res;
    });
    const err = [];
    getEditTableForm(businessList.concat(personalList)).map((item) =>
      item.validateFields((error) => error && err.push(error))
    );
    if (isEmpty(err)) {
      const arr = getEditTableData(businessList.concat(personalList), ['_status']).map((item) => {
        const { key, ...temp } = item;
        return { actionType: 1, docTypeId, ...temp };
      });
      handleSave([...newDeleteLines, ...arr]);
    }
  }

  @Bind()
  onCancel() {
    const { hideModal } = this.props;
    this.resetState();
    hideModal('auth');
  }

  /**
   * 侧栏隐藏时清除内部状态
   */
  @Bind()
  resetState() {
    this.setState({
      deleteLines: [],
    });
  }

  @Bind()
  addBusiness() {
    const { businessList, dispatch } = this.props;
    const arr = businessList;
    arr.unshift({ dimensionType: 'BIZ', _status: 'create', key: uuidv4() });
    dispatch({
      type: 'docType/updateState',
      payload: {
        businessList,
      },
    });
  }

  @Bind()
  addPersonal() {
    const { personalList, dispatch } = this.props;
    const arr = personalList;
    arr.unshift({ dimensionType: 'USER', _status: 'create', key: uuidv4() });
    dispatch({
      type: 'docType/updateState',
      payload: {
        personalList,
      },
    });
  }

  /**
   * 删除行
   * @param {Object} record - 删除的行
   */
  @Bind()
  handleDeleteLine(record, flag) {
    const { businessList, personalList, dispatch } = this.props;
    const { deleteLines } = this.state;
    const newList = (flag ? personalList : businessList).filter(
      (item) => item.authTypeCode !== record.authTypeCode
    );
    deleteLines.push({ ...record, actionType: 0 });
    dispatch({
      type: 'docType/updateState',
      payload: flag
        ? {
            personalList: newList,
          }
        : {
            businessList: newList,
          },
    });
    this.setState({ deleteLines });
  }

  /**
   * 清除新增行数据
   * @param {Object} record - 待清除的数据对象
   */
  @Bind()
  handleCleanLine(record, flag) {
    const { businessList, personalList, dispatch } = this.props;
    const newList = (flag ? personalList : businessList).filter((item) => item.key !== record.key);
    dispatch({
      type: 'docType/updateState',
      payload: flag
        ? {
            personalList: newList,
          }
        : {
            businessList: newList,
          },
    });
  }

  /**
   * 编辑行
   * @param {Object} record - 备选值行数据
   * @param {Boolean} flag - 编辑/取消标记
   */
  @Bind()
  handleEditLine(record, flag, flag2) {
    const { businessList, personalList, dispatch } = this.props;
    const newList = (flag2 ? personalList : businessList).map((item) =>
      item.authTypeCode === record.authTypeCode ? { ...item, _status: flag ? 'update' : '' } : item
    );
    dispatch({
      type: 'docType/updateState',
      payload: flag2
        ? {
            personalList: newList,
          }
        : {
            businessList: newList,
          },
    });
  }

  @Bind()
  editMatchField(type, value, editForm) {
    this.setState({ matchVisible: true, matchValue: value, editForm });
  }

  @Bind()
  cancelMatch() {
    this.setState({ matchVisible: false });
  }

  @Bind()
  saveMatch({ sourceMatchField = '' }) {
    const { editForm } = this.state;
    editForm.setFieldsValue({ sourceMatchField });
    this.setState({ matchVisible: false });
  }

  render() {
    const {
      form,
      loading,
      businessList,
      personalList,
      confirmLoading,
      modalVisible,
      title,
      ruleTypeList = [],
    } = this.props;
    const { matchVisible, matchValue } = this.state;
    const businessColumns = [
      {
        title: intl.get('hiam.docType.model.docType.bizMeaning').d('限定业务范围'),
        width: 100,
        dataIndex: 'authTypeCode',
        render: (_, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`authTypeCode`, {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.docType.model.docType.bizMeaning').d('限定业务范围'),
                    }),
                  },
                ],
                initialValue: record.authTypeCode,
              })(
                <Lov
                  code="HIAM.DOC_TYPE.DIMENSION"
                  queryParams={{ dimensionType: 'BIZ' }}
                  lovOptions={{ displayField: 'dimensionName' }}
                />
              )}
            </Form.Item>
          ) : (
            <span>{record.dimensionName}</span>
          ),
      },
      {
        title: intl.get('hiam.docType.model.docType.sourceMatchTable').d('来源匹配表'),
        key: 'sourceMatchTable',
        width: 100,
        render: (_, record) =>
          record._status === 'create' || record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`sourceMatchTable`, {
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.docType.model.docType.sourceMatchTable').d('来源匹配表'),
                    }),
                  },
                ],
                initialValue: record.sourceMatchTable,
              })(<Input />)}
            </Form.Item>
          ) : (
            <span>{record.sourceMatchTable}</span>
          ),
      },
      {
        title: intl.get('hiam.docType.model.docType.ruleType').d('规则类型'),
        dataIndex: 'ruleType',
        width: 60,
        render: (value, record) =>
          record._status === 'create' || record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('ruleType', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.docType.model.docType.ruleType').d('规则类型'),
                    }),
                  },
                ],
                initialValue: record.ruleType,
              })(
                <Select style={{ width: '100%' }}>
                  {ruleTypeList.map((item) => (
                    <Select.Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          ) : (
            <span>{record.ruleTypeMeaning}</span>
          ),
      },
      {
        title: intl.get('hiam.docType.model.docType.value').d('来源匹配字段'),
        dataIndex: 'sourceMatchField',
        width: 100,
        render: (value, record) => {
          const validaterField = form.getFieldValue(`bizSupSourceMatchField`);
          return record._status === 'create' || record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('sourceMatchField', {
                rules: [
                  record.$form.getFieldValue('ruleType') !== 'SUB_SELECT' && {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                  {
                    required: !validaterField,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.docType.model.docType.value').d('来源匹配字段'),
                    }),
                  },
                ].filter(Boolean),
                initialValue: record.sourceMatchField,
              })(
                record.$form.getFieldValue('ruleType') === 'SUB_SELECT' ? (
                  <Button
                    onClick={() =>
                      this.editMatchField(
                        'biz',
                        record.$form.getFieldValue('sourceMatchField'),
                        record.$form
                      )
                    }
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </Button>
                ) : (
                  <Input />
                )
              )}
            </Form.Item>
          ) : (
            <span>{record.sourceMatchField}</span>
          );
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        render: (_, record) => (
          <span className="action-link">
            {record._status === 'create' ? (
              <>
                <a onClick={() => this.handleCleanLine(record)}>
                  {intl.get('hzero.common.button.clean').d('清除')}
                </a>
              </>
            ) : record._status === 'update' ? (
              <a onClick={() => this.handleEditLine(record, false)}>
                {intl.get('hzero.common.button.cancel').d('取消')}
              </a>
            ) : (
              <>
                <a onClick={() => this.handleEditLine(record, true)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  placement="topRight"
                  onConfirm={() => this.handleDeleteLine(record)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              </>
            )}
          </span>
        ),
      },
    ];
    const personalColumns = [
      {
        title: intl.get('hiam.docType.model.docType.userMeaning').d('限定个人用户'),
        width: 100,
        dataIndex: 'authTypeCode',
        render: (_, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`authTypeCode`, {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.docType.model.docType.userMeaning').d('限定个人用户'),
                    }),
                  },
                ],
                initialValue: record.authTypeCode,
              })(
                <Lov
                  code="HIAM.DOC_TYPE.DIMENSION"
                  queryParams={{ dimensionType: 'USER' }}
                  lovOptions={{ displayField: 'dimensionName' }}
                />
              )}
            </Form.Item>
          ) : (
            <span>{record.dimensionName}</span>
          ),
      },
      {
        title: intl.get('hiam.docType.model.docType.sourceMatchTable').d('来源匹配表'),
        key: 'sourceMatchTable',
        width: 100,
        render: (_, record) =>
          record._status === 'create' || record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`sourceMatchTable`, {
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.docType.model.docType.sourceMatchTable').d('来源匹配表'),
                    }),
                  },
                ],
                initialValue: record.sourceMatchTable,
              })(<Input />)}
            </Form.Item>
          ) : (
            <span>{record.sourceMatchTable}</span>
          ),
      },
      {
        title: intl.get('hiam.docType.model.docType.ruleType').d('规则类型'),
        dataIndex: 'ruleType',
        width: 60,
        render: (value, record) =>
          record._status === 'create' || record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('ruleType', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.docType.model.docType.ruleType').d('规则类型'),
                    }),
                  },
                ],
                initialValue: record.ruleType,
              })(
                <Select style={{ width: '100%' }}>
                  {ruleTypeList.map((item) => (
                    <Select.Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          ) : (
            <span>{record.ruleTypeMeaning}</span>
          ),
      },
      {
        title: intl.get('hiam.docType.model.docType.value').d('来源匹配字段'),
        dataIndex: 'sourceMatchField',
        width: 100,
        render: (value, record) => {
          const validaterField = form.getFieldValue(`bizSupSourceMatchField`);
          return record._status === 'create' || record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('sourceMatchField', {
                rules: [
                  record.$form.getFieldValue('ruleType') !== 'SUB_SELECT' && {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                  {
                    required: !validaterField,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.docType.model.docType.value').d('来源匹配字段'),
                    }),
                  },
                ],
                initialValue: record.sourceMatchField,
              })(
                record.$form.getFieldValue('ruleType') === 'SUB_SELECT' ? (
                  <Button
                    onClick={() =>
                      this.editMatchField(
                        'biz',
                        record.$form.getFieldValue('sourceMatchField'),
                        record.$form
                      )
                    }
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </Button>
                ) : (
                  <Input />
                )
              )}
            </Form.Item>
          ) : (
            <span>{record.sourceMatchField}</span>
          );
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        render: (_, record) => (
          <span className="action-link">
            {record._status === 'create' ? (
              <>
                <a onClick={() => this.handleCleanLine(record, true)}>
                  {intl.get('hzero.common.button.clean').d('清除')}
                </a>
              </>
            ) : record._status === 'update' ? (
              <a onClick={() => this.handleEditLine(record, false, true)}>
                {intl.get('hzero.common.button.cancel').d('取消')}
              </a>
            ) : (
              <>
                <a onClick={() => this.handleEditLine(record, true, true)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  placement="topRight"
                  onConfirm={() => this.handleDeleteLine(record, true)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              </>
            )}
          </span>
        ),
      },
    ];

    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        width={1200}
        title={title}
        visible={modalVisible}
        onCancel={this.onCancel}
        onOk={this.onOk}
        confirmLoading={confirmLoading}
      >
        <Spin spinning={loading}>
          <Tabs defaultActiveKey="biz" animated={false}>
            <Tabs.TabPane
              key="biz"
              tab={intl.get('hiam.docType.model.docType.bizMeaning').d('限定业务范围')}
            >
              <div style={{ marginBottom: 10, textAlign: 'right' }}>
                <Button icon="plus" type="primary" onClick={this.addBusiness}>
                  {intl.get('hzero.common.button.create').d('新建')}
                </Button>
              </div>
              <EditTable
                bordered
                rowKey="authTypeCode"
                pagination={false}
                dataSource={businessList}
                columns={businessColumns}
              />
            </Tabs.TabPane>
            <Tabs.TabPane
              key="user"
              tab={intl.get('hiam.docType.model.docType.userMeaning').d('限定个人用户')}
            >
              <div style={{ marginBottom: 10, textAlign: 'right' }}>
                <Button icon="plus" type="primary" onClick={this.addPersonal}>
                  {intl.get('hzero.common.button.create').d('新建')}
                </Button>
              </div>
              <EditTable
                bordered
                rowKey="authTypeCode"
                pagination={false}
                dataSource={personalList}
                columns={personalColumns}
              />
            </Tabs.TabPane>
          </Tabs>
        </Spin>
        <MatchFieldDrawer
          modalVisible={matchVisible}
          value={matchValue}
          validaterField={form.getFieldValue(`bizSupSourceMatchField`)}
          onCancel={this.cancelMatch}
          onOk={this.saveMatch}
        />
      </Modal>
    );
  }
}
