/* eslint-disable no-nested-ternary */
import React from 'react';
import {
  Divider,
  Form,
  Input,
  InputNumber,
  Select,
  Spin,
  Table,
  Button,
  Popconfirm,
} from 'hzero-ui';
import { cloneDeep, isArray, isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuidv4 from 'uuid/v4';

import ModalForm from 'components/Modal/ModalForm';
import Lov from 'components/Lov';
import Switch from 'components/Switch';
import { Button as ButtonPermission } from 'components/Permission';
import EditTable from 'components/EditTable';
import TLEditor from 'components/TLEditor';

import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';
import { isTenantRoleLevel, getEditTableData } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';

@Form.create({ fieldNameProp: null })
export default class EditForm extends ModalForm {
  constructor(props) {
    super(props);
    const { data = {} } = props;
    this.state = {
      data,
      deleteDocTypeAssigns: [], // 删除的租户列表
      addDocTypeAssigns: [], // 新增的租户列表
      selectedRowKeys: [],
      selectedRow: {}, // 选中的租户
      deleteSqlIdLine: [], // sqlid 删除的行
    };
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { data: prevData } = prevState;
    const { data } = nextProps;
    if (data !== prevData) {
      return {
        ...prevState,
        data,
      };
    } else {
      return null;
    }
  }

  /**
   * 保存编辑数据
   */
  @Bind()
  onOk() {
    const { deleteDocTypeAssigns = [], addDocTypeAssigns = [], data, deleteSqlIdLine } = this.state;
    const { form, handleSave, docTypeSqlidList } = this.props;
    form.validateFields({ force: true }, (err, fieldsValue) => {
      if (err) return;
      const initData = docTypeSqlidList.filter(
        (v) => v._status !== 'create' && v._status !== 'update' && v._status !== 'delete'
      );
      const sqlIdList = getEditTableData(docTypeSqlidList, ['tableId']);
      const params = {
        ...data,
        ...fieldsValue,
        orderSeq: fieldsValue.orderSeq || 0,
        docTypeAssigns: [...deleteDocTypeAssigns, ...addDocTypeAssigns],
        docTypeSqlidList: [...initData, ...sqlIdList, ...deleteSqlIdLine],
      };
      handleSave(params);
    });
  }

  /**
   * 租户维护新增租户
   * @param {*} _
   * @param {*} record - 选择行记录
   */
  @Bind()
  addTenant(_, record) {
    const {
      addDocTypeAssigns = [],
      deleteDocTypeAssigns = [],
      data: { docTypeAssigns = [], docTypeId } = {},
    } = this.state;
    const sourceIndex = isArray(docTypeAssigns)
      ? docTypeAssigns.findIndex((item) => item.tenantNum === record.tenantNum)
      : -1;
    const addIndex = isArray(addDocTypeAssigns)
      ? addDocTypeAssigns.findIndex((item) => item.tenantNum === record.tenantNum)
      : -1;
    const deleteIndex = isArray(deleteDocTypeAssigns)
      ? deleteDocTypeAssigns.findIndex((item) => item.tenantNum === record.tenantNum)
      : -1;

    // 避免重复添加
    if (sourceIndex === -1 && addIndex === -1) {
      const { tenantNum, tenantName, tenantId } = record;
      const newAddDocTypeAssigns = [
        ...addDocTypeAssigns,
        {
          assignValueId: tenantId,
          docTypeId,
          actionType: 1,
          tenantNum,
          tenantName,
        },
      ];
      this.setState({
        addDocTypeAssigns: newAddDocTypeAssigns,
      });
    } else if (deleteIndex !== -1) {
      const newDeleteDocTypeAssigns = cloneDeep(deleteDocTypeAssigns);
      newDeleteDocTypeAssigns.splice(deleteIndex, 1);
      this.setState({
        deleteDocTypeAssigns: newDeleteDocTypeAssigns,
      });
    }
    this.setState({
      selectedRow: {},
      selectedRowKeys: [],
    });
  }

  /**
   * 点击表格行时将当前行设为点击行数据
   * @param {Object} record - 单据权限行数据
   */
  @Bind()
  onRow(record) {
    this.setState({
      selectedRowKeys: [record.tenantNum],
      selectedRow: record,
    });
  }

  /**
   * 租户级租户维护删除租户
   */
  @Bind()
  deleteTenant() {
    const {
      deleteDocTypeAssigns = [],
      addDocTypeAssigns = [],
      selectedRowKeys = [],
      data: { docTypeAssigns = [] } = {},
    } = this.state;

    if (!isEmpty(selectedRowKeys)) {
      let newDeleteDocTypeAssigns = cloneDeep(deleteDocTypeAssigns);
      const newAddDocTypeAssigns = cloneDeep(addDocTypeAssigns);
      selectedRowKeys.forEach((element) => {
        const sourceIndex = isArray(docTypeAssigns)
          ? docTypeAssigns.findIndex((item) => item.tenantNum === element)
          : -1;
        const addIndex = newAddDocTypeAssigns.findIndex((item) => item.tenantNum === element);
        if (sourceIndex !== -1) {
          newDeleteDocTypeAssigns = [
            ...newDeleteDocTypeAssigns,
            {
              ...docTypeAssigns[sourceIndex],
              actionType: 0,
            },
          ];
        } else if (addIndex !== -1) {
          newAddDocTypeAssigns.splice(addIndex, 1);
        }
      });
      // console.log(newDeleteDocTypeAssigns, newAddDocTypeAssigns);
      this.setState({
        deleteDocTypeAssigns: newDeleteDocTypeAssigns,
        addDocTypeAssigns: newAddDocTypeAssigns,
        selectedRowKeys: [],
      });
    } else {
      this.setState({
        selectedRowKeys: [],
      });
    }
  }

  @Bind()
  addSqlIds() {
    const { docTypeSqlidList, dispatch } = this.props;
    const arr = docTypeSqlidList;
    arr.unshift({ _status: 'create', tableId: uuidv4() });
    dispatch({
      type: 'docType/updateState',
      payload: {
        docTypeSqlidList,
      },
    });
  }

  @Bind()
  handleDeleteSqlId(record) {
    const { docTypeSqlidList, dispatch } = this.props;
    const { deleteSqlIdLine } = this.state;
    deleteSqlIdLine.push({ ...record, _status: 'delete' });
    const newList = docTypeSqlidList.filter((item) => item.tableId !== record.tableId);
    dispatch({
      type: 'docType/updateState',
      payload: {
        docTypeSqlidList: newList,
      },
    });
    this.setState({ deleteSqlIdLine });
  }

  @Bind()
  handleCleanLine(record) {
    const { docTypeSqlidList, dispatch } = this.props;
    const newList = docTypeSqlidList.filter((item) => item.tableId !== record.tableId);
    dispatch({
      type: 'docType/updateState',
      payload: {
        docTypeSqlidList: newList,
      },
    });
  }

  @Bind()
  handleEditLine(record, flag) {
    const { docTypeSqlidList, dispatch } = this.props;
    const newList = docTypeSqlidList.map((item) =>
      item.tableId === record.tableId ? { ...item, _status: flag ? 'update' : '' } : item
    );
    dispatch({
      type: 'docType/updateState',
      payload: {
        docTypeSqlidList: newList,
      },
    });
  }

  /**
   * 侧栏隐藏时清空状态
   */
  @Bind()
  resetState() {
    this.setState({
      data: {},
      deleteDocTypeAssigns: [], // 删除的租户列表
      addDocTypeAssigns: [], // 新增的租户列表
      selectedRowKeys: [],
      selectedRow: {}, // 选中的租户
    });
  }

  renderForm() {
    const {
      deleteDocTypeAssigns = [],
      addDocTypeAssigns = [],
      selectedRowKeys = [],
      data = {},
    } = this.state;
    const {
      path,
      isSiteFlag,
      loading,
      form: { getFieldDecorator, getFieldsValue },
      docTypeLevelCode = [],
      typeList = [],
      isNewDocType,
      docTypeSqlidList = [],
    } = this.props;
    const {
      docTypeCode,
      docTypeName,
      sourceServiceName,
      // sourceDataEntity,
      levelCode,
      authControlType,
      orderSeq,
      description,
      enabledFlag = 0,
      docTypeAssigns = [],
      docTypePermissions,
      _token,
    } = data;
    const columns = [
      {
        title: intl.get('hiam.docType.model.docType.tenantNum').d('租户编号'),
        dataIndex: 'tenantNum',
      },
      {
        title: intl.get('hiam.docType.model.docType.tenantName').d('租户名称'),
        dataIndex: 'tenantName',
      },
    ];
    const ruleColumns = [
      {
        title: intl.get('hzero.common.model.common.tenantId').d('租户'),
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hiam.docType.model.docType.ruleCode').d('数据屏蔽规则代码'),
        dataIndex: 'ruleCode',
      },
      {
        title: intl.get('hiam.docType.model.docType.ruleName').d('数据屏蔽规则名称'),
        dataIndex: 'ruleName',
      },
    ];
    const tenantDataSource = isArray(docTypeAssigns)
      ? [
          ...docTypeAssigns.filter(
            (item) => deleteDocTypeAssigns.findIndex((e) => e.tenantNum === item.tenantNum) === -1
          ),
          ...addDocTypeAssigns,
        ]
      : addDocTypeAssigns;

    const rowSelection = {
      selectedRowKeys,
      onChange: (keys, rows) => {
        this.setState({
          selectedRowKeys: keys,
          selectedRow: rows[0],
        });
      },
    };

    const sqlIdColumns = [
      {
        title: intl.get('hiam.docType.model.docType.sqlid').d('Sql Id'),
        dataIndex: 'sqlid',
        render: (_, record) =>
          record._status === 'create' || record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`sqlid`, {
                initialValue: record.sqlid,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.docType.model.docType.sqlid').d('Sql Id'),
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          ) : (
            <span>{record.sqlid}</span>
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
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
                  onConfirm={() => this.handleDeleteSqlId(record)}
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
      <Spin spinning={loading}>
        <Form.Item style={{ fontSize: 14, color: '#333' }}>
          {intl.get('hiam.docType.view.title.basicSection').d('单据权限类型基本信息')}
        </Form.Item>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hiam.docType.model.docType.docTypeCode').d('类型编码')}
        >
          {getFieldDecorator('docTypeCode', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hiam.docType.model.docType.docTypeCode').d('类型编码'),
                }),
              },
              {
                pattern: CODE_UPPER,
                message: intl
                  .get('hzero.common.validation.codeUpper')
                  .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
              },
              {
                max: 30,
                message: intl.get('hzero.common.validation.max', { max: 30 }),
              },
            ],
            initialValue: docTypeCode,
          })(<Input trim inputChinese={false} typeCase="upper" disabled={!!docTypeCode} />)}
        </Form.Item>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hiam.docType.model.docType.docTypeName').d('类型名称')}
        >
          {getFieldDecorator('docTypeName', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hiam.docType.model.docType.docTypeName').d('类型名称'),
                }),
              },
              {
                max: 80,
                message: intl.get('hzero.common.validation.max', { max: 80 }),
              },
            ],
            initialValue: docTypeName,
          })(
            <TLEditor
              label={intl.get('hiam.docType.model.docType.docTypeName').d('类型名称')}
              field="docTypeName"
              token={_token}
            />
          )}
        </Form.Item>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hiam.docType.model.docType.sourceServiceName').d('来源微服务')}
        >
          {getFieldDecorator('sourceServiceName', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hiam.docType.model.docType.sourceServiceName').d('来源微服务'),
                }),
              },
              {
                max: 30,
                message: intl.get('hzero.common.validation.max', { max: 30 }),
              },
            ],
            initialValue: sourceServiceName,
          })(
            <Lov
              textValue={sourceServiceName}
              code={isSiteFlag ? 'HADM.ROUTE.SERVICE_CODE' : 'HADM.ROUTE.SERVICE_CODE.ORG'}
            />
          )}
        </Form.Item>
        {/* <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hiam.docType.model.docType.sourceDataEntity').d('来源数据实体')}
        >
          {getFieldDecorator('sourceDataEntity', {
            rules: [
              {
                max: 120,
                message: intl.get('hzero.common.validation.max', { max: 30 }),
              },
            ],
            initialValue: sourceDataEntity,
          })(<Input />)}
        </Form.Item> */}
        {!isTenantRoleLevel() && (
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hiam.docType.model.docType.levelCode').d('层级')}
          >
            {getFieldDecorator('levelCode', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hiam.docType.model.docType.levelCode').d('层级'),
                  }),
                },
              ],
              initialValue: levelCode,
            })(
              <Select style={{ width: '100%' }}>
                {docTypeLevelCode.map((m) => (
                  <Select.Option key={m.value} value={m.value}>
                    {m.meaning}
                  </Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
        )}
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hiam.docType.model.docType.authControlType').d('权限控制类型')}
        >
          {getFieldDecorator('authControlType', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hiam.docType.model.docType.authControlType').d('权限控制类型'),
                }),
              },
            ],
            initialValue: authControlType,
          })(
            <Select style={{ width: '100%' }}>
              {typeList.map((m) => (
                <Select.Option key={m.value} value={m.value}>
                  {m.meaning}
                </Select.Option>
              ))}
            </Select>
          )}
        </Form.Item>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hiam.docType.model.docType.orderSeq').d('排序号')}
        >
          {getFieldDecorator('orderSeq', {
            initialValue: orderSeq || 0,
          })(<InputNumber style={{ width: 150 }} />)}
        </Form.Item>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hiam.docType.model.docType.description').d('描述')}
        >
          {getFieldDecorator('description', {
            initialValue: description,
            rules: [
              {
                max: 80,
                message: intl.get('hzero.common.validation.max', { max: 80 }),
              },
            ],
          })(<Input />)}
        </Form.Item>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hzero.common.status.enable').d('启用')}
        >
          {getFieldDecorator('enabledFlag', {
            initialValue: enabledFlag,
          })(<Switch />)}
        </Form.Item>
        {!isNewDocType && (
          <Form.Item>
            <Table
              columns={ruleColumns}
              dataSource={docTypePermissions}
              pagination={false}
              bordered
            />
          </Form.Item>
        )}
        <>
          <div style={{ marginBottom: 10, textAlign: 'right' }}>
            <Button icon="plus" type="primary" onClick={this.addSqlIds}>
              {intl.get('hzero.common.button.create').d('新建')}
            </Button>
          </div>
          <EditTable
            bordered
            rowKey="tableId"
            pagination={false}
            dataSource={docTypeSqlidList}
            columns={sqlIdColumns}
          />
        </>
        {/* 层级为租户时出现租户维护列表 */}
        {getFieldsValue().levelCode === 'TENANT' ? (
          <>
            <Divider orientation="left">
              {intl.get('hiam.docType.view.title.tenantSection').d('租户维护')}
            </Divider>
            <Lov isButton icon="plus" code="HPFM.TENANT" onChange={this.addTenant}>
              {intl.get('hzero.common.button.create').d('新建')}
            </Lov>
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.delete`,
                  type: 'button',
                  meaning: '单据权限类型-删除租户',
                },
              ]}
              icon="delete"
              onClick={this.deleteTenant}
              style={{ marginLeft: 8 }}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </ButtonPermission>
            <Table
              bordered
              rowKey="tenantNum"
              pagination={false}
              columns={columns}
              dataSource={tenantDataSource}
              style={{
                marginTop: 20,
              }}
              onRow={(record) => ({
                onClick: () => this.onRow(record),
              })}
              rowSelection={rowSelection}
            />
          </>
        ) : null}
      </Spin>
    );
  }
}
