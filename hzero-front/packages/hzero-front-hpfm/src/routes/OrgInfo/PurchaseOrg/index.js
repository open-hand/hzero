import React, { PureComponent } from 'react';
import { Form, Input } from 'hzero-ui';
import { connect } from 'dva';
import uuid from 'uuid/v4';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import Checkbox from 'components/Checkbox';
import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { enableRender, operatorRender } from 'utils/renderer';
import {
  getCurrentOrganizationId,
  getEditTableData,
  addItemToPagination,
  delItemToPagination,
} from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';

import FilterForm from './FilterForm';

const FormItem = Form.Item;
@formatterCollections({ code: 'hpfm.purchaseOrg' })
@Form.create({ fieldNameProp: null })
@connect(({ loading, purchaseOrg }) => ({
  purchaseOrg,
  saveOrgLoading:
    loading.effects['purchaseOrg/savePurchaseOrg'] ||
    loading.effects['purchaseOrg/fetchPurchaseOrgList'],
  fetchPurchaseOrgLoading: loading.effects['purchaseOrg/fetchPurchaseOrgList'],
}))
export default class PurchaseOrg extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      formValues: {},
      orgId: getCurrentOrganizationId(),
    };
  }

  componentDidMount() {
    this.fetchDataList();
  }

  /**
   * 获取采购组织列表数据
   * @param {object} params - 请求参数
   */
  fetchDataList(params = {}) {
    const {
      dispatch,
      purchaseOrg: { pagination = {} },
    } = this.props;
    const { orgId: organizationId, formValues } = this.state;
    dispatch({
      type: 'purchaseOrg/fetchPurchaseOrgList',
      payload: { page: pagination, ...formValues, organizationId, ...params },
    });
  }

  /**
   * handlePagination - 分页设置
   * @param {object} pagination - 分页对象
   */
  @Bind()
  handlePagination(pagination) {
    this.fetchDataList({ page: pagination });
  }

  /**
   * handleSearchOrg - 搜索采购组织
   * @param {object} fieldsValue - 搜索条件
   * @param {string} fieldsValue.organizationCode - 组织编码
   * @param {string} fieldsValue.organizationName - 组织名称
   */
  @Bind()
  handleSearchOrg(fieldsValue) {
    this.setState(
      {
        formValues: fieldsValue,
      },
      () => {
        this.fetchDataList({ page: {} });
      }
    );
  }

  /**
   * handleResetSearch - 重置搜索表单和搜索条件缓存
   * @param {object} form - 表单对象
   */
  @Bind()
  handleResetSearch(form) {
    form.resetFields();
    this.setState({
      formValues: {},
    });
  }

  /**
   * handleOrgEdit - 设置行数据的编辑状态
   * @param {object} record - 行数据
   * @param {boolean} flag - 编辑标识
   */
  @Bind()
  handleOrgEdit(record, flag) {
    const {
      purchaseOrg: { purchaseOrgList = [] },
      dispatch,
    } = this.props;
    const newList = purchaseOrgList.map(item => {
      if (record.purchaseOrgId === item.purchaseOrgId) {
        return { ...item, _status: flag ? 'update' : '' };
      } else {
        return item;
      }
    });
    dispatch({
      type: 'purchaseOrg/updateState',
      payload: { purchaseOrgList: newList },
    });
  }

  /**
   * handleRemoveOrg - 删除未保存的行，分页减少1
   * @param {object} record - 行数据
   */
  @Bind()
  handleRemoveOrg(record) {
    const {
      dispatch,
      purchaseOrg: { purchaseOrgList = [], pagination },
    } = this.props;
    const newList = purchaseOrgList.filter(item => item.purchaseOrgId !== record.purchaseOrgId);
    dispatch({
      type: 'purchaseOrg/updateState',
      payload: {
        purchaseOrgList: newList,
        pagination: delItemToPagination(purchaseOrgList.length, pagination),
      },
    });
  }

  /**
   * 批量保存采购组织数据
   */
  @Bind()
  handleSaveOrg() {
    const {
      dispatch,
      purchaseOrg: { purchaseOrgList = [] },
    } = this.props;
    const params = getEditTableData(purchaseOrgList, ['purchaseOrgId']);
    if (Array.isArray(params) && params.length > 0) {
      dispatch({
        type: 'purchaseOrg/savePurchaseOrg',
        payload: {
          organizationId: this.state.orgId,
          purchaseOrgList: params,
        },
      }).then(res => {
        if (res) {
          notification.success();
          this.fetchDataList();
        }
      });
    }
  }

  /**
   * 新建采购组织
   */
  @Bind()
  handleCreateOrg() {
    const {
      dispatch,
      commonSourceCode,
      commonExternalSystemCode,
      purchaseOrg: { purchaseOrgList = [], pagination },
    } = this.props;
    dispatch({
      type: 'purchaseOrg/updateState',
      payload: {
        purchaseOrgList: [
          {
            _status: 'create',
            enabledFlag: 1,
            organizationCode: '',
            organizationName: '',
            purchaseOrgId: uuid(),
            sourceCode: commonSourceCode,
            externalSystemCode: commonExternalSystemCode,
          },
          ...purchaseOrgList,
        ],
        pagination: addItemToPagination(purchaseOrgList.length, pagination),
      },
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      saveOrgLoading,
      fetchPurchaseOrgLoading,
      commonSourceCode,
      match,
      purchaseOrg: { purchaseOrgList = [], pagination },
    } = this.props;
    const save = purchaseOrgList.filter(item => {
      return item._status === 'create' || item._status === 'update';
    });
    const columns = [
      {
        title: intl.get('hpfm.purchaseOrg.model.org.organizationCode').d('采购组织编码'),
        width: 150,
        dataIndex: 'organizationCode',
        render: (val, record) => {
          if (record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('organizationCode', {
                  initialValue: record.organizationCode,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hpfm.purchaseOrg.model.org.organizationCode')
                          .d('采购组织编码'),
                      }),
                    },
                    {
                      max: 30,
                      message: intl.get('hzero.common.validation.max', {
                        max: 30,
                      }),
                    },
                    {
                      pattern: CODE_UPPER,
                      message: intl
                        .get('hzero.common.validation.codeUpper')
                        .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                    },
                  ],
                })(
                  <Input
                    disabled={record.sourceCode !== commonSourceCode}
                    typeCase="upper"
                    inputChinese={false}
                  />
                )}
              </FormItem>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('hpfm.purchaseOrg.model.org.organizationName').d('采购组织名称'),
        dataIndex: 'organizationName',
        render: (val, record) => {
          if (record._status === 'update' || record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('organizationName', {
                  initialValue: record.organizationName,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hpfm.purchaseOrg.model.org.organizationName')
                          .d('采购组织名称'),
                      }),
                    },
                    {
                      max: 60,
                      message: intl.get('hzero.common.validation.max', {
                        max: 60,
                      }),
                    },
                  ],
                })(<Input disabled={record.sourceCode !== commonSourceCode} />)}
              </FormItem>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('hpfm.purchaseOrg.model.org.sourceCode').d('数据来源'),
        align: 'center',
        width: 100,
        dataIndex: 'sourceCode',
      },
      {
        title: intl.get('hpfm.purchaseOrg.model.org.externalSystemCode').d('来源系统'),
        align: 'center',
        width: 100,
        dataIndex: 'externalSystemCode',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        align: 'center',
        width: 80,
        render: (val, record) => {
          if (record._status === 'update' || record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('enabledFlag', {
                  initialValue: record.enabledFlag,
                })(<Checkbox />)}
              </FormItem>
            );
          } else {
            return enableRender(val);
          }
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        width: 80,
        render: (val, record) => {
          const operators = [];
          if (record._status === 'update') {
            operators.push({
              key: 'cancel',
              ele: (
                <a onClick={() => this.handleOrgEdit(record, false)}>
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            });
          } else if (record._status === 'create') {
            operators.push({
              key: 'cancel',
              ele: (
                <a onClick={() => this.handleRemoveOrg(record)}>
                  {intl.get('hzero.common.button.delete').d('删除')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            });
          } else {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '采购组织-编辑',
                    },
                  ]}
                  onClick={() => this.handleOrgEdit(record, true)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          return operatorRender(operators);
        },
      },
    ];
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.purchaseOrg.view.message.title').d('采购组织')}>
          <ButtonPermission
            type="primary"
            icon="save"
            disabled={isEmpty(save)}
            loading={saveOrgLoading && !isEmpty(save)}
            onClick={this.handleSaveOrg}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <ButtonPermission
            icon="plus"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '采购组织-新建',
              },
            ]}
            onClick={this.handleCreateOrg}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content noCard>
          <div className="table-list-search">
            <FilterForm onSearch={this.handleSearchOrg} onReset={this.handleResetSearch} />
          </div>
          <EditTable
            bordered
            loading={fetchPurchaseOrgLoading}
            rowKey="purchaseOrgId"
            columns={columns}
            dataSource={purchaseOrgList}
            pagination={pagination}
            onChange={this.handlePagination}
          />
        </Content>
      </React.Fragment>
    );
  }
}
