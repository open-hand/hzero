import React from 'react';
import { Card, Form, Input, Select, Spin, Row, Col, Button } from 'hzero-ui';
import { connect } from 'dva';
import { isEmpty, map, isUndefined, isEqual, findIndex, forIn } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import { Header, Content } from 'components/Page';
import Switch from 'components/Switch';
import Lov from 'components/Lov';
import TLEditor from 'components/TLEditor';
import { Button as ButtonPermission } from 'components/Permission';
import EditTable from 'components/EditTable';
import { operatorRender } from 'utils/renderer';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import {
  isTenantRoleLevel,
  getCurrentOrganizationId,
  getResponse,
  filterNullValueObject,
  encryptPwd,
  getEditTableData,
} from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import notification from 'utils/notification';
import {
  EDIT_FORM_ITEM_LAYOUT,
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';
import DynamicFormRender from '@/components/DynamicFormRender';

@Form.create({ fieldNameProp: null })
@connect(({ dataSource, loading }) => ({
  dataSource,
  fetchDetailLoading: loading.effects['dataSource/fetchDataSourceDetail'],
  saving:
    loading.effects['dataSource/editDataSource'] || loading.effects['dataSource/createDataSource'],
  testing: loading.effects['dataSource/testConnection'],
  isSiteFlag: !isTenantRoleLevel(),
  currentTenantId: getCurrentOrganizationId(),
}))
@formatterCollections({
  code: ['hpfm.ruleEngine', 'entity.tenant', 'hpfm.dataSource', 'hpfm.reportDataSource'],
})
export default class Detail extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      dbTypeDisabled: true,
      driverIdRequired: false,
      dataSource: [],
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    const lovCodes = {
      dataSourceClass: 'HPFM.DATASOURCE_CLASS', // 数据源分类值集
      dataSourceType: 'HPFM.DATABASE_TYPE', // 数据源类型值集
      dbPoolType: 'HPFM.DB_POOL_TYPE', // 连接池类型值集
      dsPurposeCode: 'HPFM.DATASOURCE_PURPOSE', // 数据源用途值集
      driverTypeCode: 'HPFM.DATASOURCE_DRIVER_TYPE', // 数据源用途值集
    };
    // 初始化 值集
    dispatch({
      type: `dataSource/batchCode`,
      payload: {
        lovCodes,
      },
    });
    this.fetchDetail();
    this.fetchPublicKey();
  }

  @Bind()
  fetchDetail() {
    const { dispatch, match } = this.props;
    const {
      params: { datasourceId },
    } = match;
    if (datasourceId !== 'create') {
      dispatch({
        type: 'dataSource/fetchDataSourceDetail',
        payload: { datasourceId },
      }).then((res) => {
        if (res) {
          dispatch({
            type: 'dataSource/fetchFormParams',
            payload: {
              formCode: res.dbType,
            },
          }).then(() => {
            this.handleConfig();
          });
        }
      });
    } else {
      dispatch({
        type: 'dataSource/updateState',
        payload: { dataSourceDetail: {}, dbPoolParams: {} },
      });
    }
  }

  /**
   * 请求公钥
   */
  @Bind()
  fetchPublicKey() {
    const { dispatch = () => {} } = this.props;
    dispatch({
      type: 'dataSource/getPublicKey',
    });
  }

  @Bind()
  handleConfig() {
    const {
      dataSource: { dataSourceDetail = {}, extConfigs = [] },
    } = this.props;
    const { extConfig = '{}' } = dataSourceDetail;
    const columnList = [];
    // 获取参数
    forIn(JSON.parse(extConfig), (value, key) => {
      if (
        findIndex(extConfigs, (o) => {
          return o.itemCode === key;
        }) === -1
      ) {
        columnList.push({
          name: key,
          value,
          columnId: uuid(),
          _status: '',
        });
      }
    });
    this.setState({
      dataSource: columnList,
    });
  }

  @Bind()
  handleOk() {
    const { form, onOk } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        onOk(values);
      }
    });
  }

  /**
   * 获取连接池参数
   */
  @Bind()
  changeDbPoolType(value) {
    const { dispatch, dataSourceDetail = {} } = this.props;
    if (dataSourceDetail.dbPoolParams !== value) {
      dispatch({
        type: 'dataSource/getDbPoolParams',
        payload: { dbPoolType: value },
      });
    }
  }

  @Bind()
  changeDbClass(value) {
    if (!isUndefined(value)) {
      this.setState({
        dbTypeDisabled: false,
      });
    }
  }

  /**
   * 改变数据源类型,获取驱动类和连接字符串
   */
  @Bind()
  changeDbType(value) {
    const { dispatch, form } = this.props;
    dispatch({
      type: 'dataSource/fetchFormParams',
      payload: {
        formCode: value,
      },
    });
    dispatch({
      type: 'dataSource/getDriverClass',
      payload: { dbType: value },
    }).then((res) => {
      if (res) {
        const formValues = {
          // driverClass: res.driverClass,
          datasourceUrl: res.datasourceUrl,
        };
        form.setFieldsValue(formValues);
      }
    });
  }

  @Bind()
  changeDriverType(value) {
    const { form } = this.props;
    if (isEqual(value, 'CUSTOMIZE')) {
      this.setState({
        driverIdRequired: true,
      });
    } else {
      this.setState({
        driverIdRequired: false,
      });
      form.resetFields(['driverId']);
    }
  }

  /**
   * 更新数据源
   */
  @Bind()
  handleUpdateDataSource() {
    const { dataSource = [] } = this.state;
    const {
      dispatch,
      form,
      history,
      match: { params: { datasourceId } } = {},
      dataSource: { dataSourceDetail, dbPoolParams = {}, extConfigs = [], publicKey },
    } = this.props;
    const changedList = getEditTableData(dataSource);
    const arr = changedList.map((item) => item.columnId);
    const paramsConfig = dataSource.map((item) => {
      if (arr.includes(item.columnId)) {
        return changedList.find((temp) => temp.columnId === item.columnId);
      } else {
        return item;
      }
    });
    form.validateFields((err, values) => {
      if (!err) {
        // 解析连接池参数
        const newOptions = {};
        Object.keys(dbPoolParams).forEach((item) => {
          newOptions[item] = values[item];
        });
        // 表单配置
        const newExtConfig = {};
        extConfigs.forEach((item) => {
          newExtConfig[item.itemCode] = values[item.itemCode];
        });
        paramsConfig.forEach((item) => {
          newExtConfig[item.name] = item.value;
        });
        const list = values.dsPurposeCode || [];
        const newValues = { ...values, dsPurposeCode: list.join(',') };
        if (values.passwordEncrypted) {
          newValues.passwordEncrypted = encryptPwd(values.passwordEncrypted, publicKey);
        }
        let temp = { ...newValues };
        if (values.password === dataSourceDetail.password) {
          const { password, ...other } = newValues;
          temp = other;
        }
        if (temp.driverId && dataSourceDetail.driverClass) {
          delete dataSourceDetail.driverClass;
        }
        dispatch({
          type: `dataSource/${datasourceId !== 'create' ? 'editDataSource' : 'createDataSource'}`,
          payload: {
            ...dataSourceDetail,
            ...temp,
            options: JSON.stringify(newOptions),
            extConfig: JSON.stringify(newExtConfig),
          },
        }).then((res) => {
          if (res) {
            notification.success();
            if (datasourceId === 'create') {
              history.push(`/hpfm/data-source/detail/${res.datasourceId}`);
            }
            this.fetchDetail();
          }
        });
      }
    });
  }

  // 测试
  @Bind()
  handleTestConnection(params) {
    const {
      form,
      dispatch,
      dataSource: { dataSourceDetail, dbPoolParams = {}, extConfigs = [], publicKey },
    } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        // 解析连接池参数
        const newOptions = {};
        Object.keys(dbPoolParams).forEach((item) => {
          newOptions[item] = values[item];
        });
        // 表单配置
        const newExtConfig = {};
        extConfigs.forEach((item) => {
          newExtConfig[item.itemCode] = values[item.itemCode];
        });
        const list = values.dsPurposeCode || [];
        let temp = { ...values, dsPurposeCode: list.join(',') };
        if (values.password === dataSourceDetail.password) {
          const { password, ...other } = values;
          temp = { ...other, dsPurposeCode: list.join(',') };
        }
        if (values.passwordEncrypted) {
          temp.passwordEncrypted = encryptPwd(values.passwordEncrypted, publicKey);
        }

        dispatch({
          type: 'dataSource/testConnection',
          payload: { ...params, ...filterNullValueObject(temp) },
        }).then((res) => {
          if (JSON.stringify(res) === '{}') {
            notification.success({
              message: intl.get('hpfm.dataSource.view.message.test.success').d('测试成功'),
            });
          } else {
            getResponse(res);
          }
        });
      }
    });
  }

  getColumns(pageDisabled) {
    return [
      {
        dataIndex: 'name',
        title: intl.get('hpfm.dataSource.model.dataSource.receiverName').d('名称'),
        width: 160,
        render: (value, record) => {
          if (record._status) {
            const { $form: form } = record;
            return (
              <>
                <Form.Item>
                  {form.getFieldDecorator('name', {
                    initialValue: value,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.dataSource.model.dataSource.receiverName').d('名称'),
                        }),
                      },
                    ],
                  })(<Input disabled={pageDisabled} />)}
                </Form.Item>
              </>
            );
          } else {
            return value;
          }
        },
      },
      {
        dataIndex: 'value',
        title: intl.get('hpfm.dataSource.model.dataSource.receiverValue').d('值'),
        width: 160,
        render: (value, record) => {
          if (record._status) {
            const { $form: form } = record;
            return (
              <>
                <Form.Item>
                  {form.getFieldDecorator('value', {
                    initialValue: value,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.dataSource.model.dataSource.receiverValue').d('值'),
                        }),
                      },
                    ],
                  })(<Input />)}
                </Form.Item>
              </>
            );
          } else {
            return value;
          }
        },
      },
      {
        key: 'action',
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        render: (_, record) => {
          const operators = [];
          if (record._status === '') {
            operators.push({
              key: 'edit',
              ele: (
                <a
                  onClick={() => {
                    this.editRecord(record, true);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          } else if (record._status !== 'create') {
            operators.push({
              key: 'cancel',
              ele: (
                <a
                  onClick={() => {
                    this.editRecord(record, false);
                  }}
                >
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            });
          }
          operators.push({
            key: 'delete',
            ele: (
              <a
                onClick={() => {
                  this.removeRecord(record);
                }}
              >
                {intl.get('hzero.common.button.delete').d('删除')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.delete').d('删除'),
          });
          return operatorRender(operators);
        },
      },
    ];
  }

  @Bind()
  handleAddBtnClick() {
    const { dataSource = [] } = this.state;
    this.setState({
      dataSource: [...dataSource, { columnId: uuid(), _status: 'create' }],
    });
  }

  @Bind()
  removeRecord(record) {
    const { dataSource = [] } = this.state;
    this.setState({
      dataSource: dataSource.filter((r) => r.columnId !== record.columnId),
    });
  }

  @Bind()
  editRecord(record, flag) {
    const { dataSource = [] } = this.state;
    this.setState({
      dataSource: dataSource.map((item) =>
        item.columnId === record.columnId ? { ...item, _status: flag ? 'update' : '' } : item
      ),
    });
  }

  @Bind()
  renderDbTypeSelect(pageDisabled) {
    const { dbTypeDisabled } = this.state;
    const {
      form: { getFieldValue = () => {} },
      dataSource: { dataSourceTypeList = [], dataSourceDetail = {} },
    } = this.props;
    const { datasourceId } = dataSourceDetail;
    const datasourceClass = getFieldValue('datasourceClass') || '';
    const filteredTypes = dataSourceTypeList.filter((item) => item.parentValue === datasourceClass);
    return (
      <Select
        allowClear={false}
        onChange={this.changeDbType}
        disabled={datasourceId !== undefined || dbTypeDisabled || pageDisabled}
      >
        {filteredTypes.map((item) => (
          <Select.Option key={item.value} value={item.value}>
            {item.meaning}
          </Select.Option>
        ))}
      </Select>
    );
  }

  @Bind()
  renderFormParams(item, newExtConfig, pageDisabled, createFlag) {
    const disabled = (item.updatableFlag === 0 && createFlag !== 'create') || pageDisabled;
    const renderProps = {
      disabled,
      required: item.requiredFlag,
    };
    return <DynamicFormRender data={item} config={newExtConfig} {...renderProps} />;
  }

  @Bind()
  getExtConfigRows(extConfigs) {
    const arr = [];
    for (let i = 0; i < extConfigs.length; i += 3) {
      arr.push(extConfigs.slice(i, i + 3));
    }
    return arr;
  }

  render() {
    const { driverIdRequired, dataSource } = this.state;
    const {
      isSiteFlag,
      saving = false,
      testing = false,
      fetchDetailLoading = false,
      form,
      currentTenantId,
      match: {
        params: { datasourceId: createFlag },
      },
      match,
      dataSource: {
        dataSourceDetail = {},
        dataSourceClassList = [],
        // dataSourceTypeList = [],
        dbPoolTypeList = [],
        dbPoolParams = {},
        dsPurposeCodeList = [],
        extConfigs = [],
        driverTypeCodeList = [],
      },
    } = this.props;
    const { getFieldDecorator } = this.props.form;
    const {
      datasourceId,
      dsPurposeCode,
      datasourceCode,
      username,
      passwordEncrypted,
      datasourceClass,
      dbType,
      dbPoolType,
      description,
      remark,
      tenantId,
      tenantName,
      driverId,
      driverName,
      driverType,
      // driverClass,
      datasourceUrl,
      enabledFlag = 1,
      extConfig = '{}',
      _token,
    } = dataSourceDetail;
    // 获取连接池参数
    const newDbPoolParams = map(dbPoolParams, (value, key) => ({ key, value }));
    const newExtConfig = JSON.parse(extConfig);
    const pageDisabled =
      datasourceId === undefined ? false : !isSiteFlag && currentTenantId !== tenantId;
    const columns = this.getColumns(pageDisabled);
    const extConfigRows = this.getExtConfigRows(extConfigs);
    return (
      <>
        <Header
          title={
            createFlag !== 'create'
              ? intl.get('hpfm.dataSource.model.dataSource.detail').d('数据源详情')
              : intl.get('hpfm.dataSource.model.dataSource.create').d('数据源新建')
          }
          backPath="/hpfm/data-source/list"
        >
          <ButtonPermission
            type="primary"
            loading={saving}
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '数据源-保存',
              },
            ]}
            disabled={testing || pageDisabled}
            onClick={this.handleUpdateDataSource}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <ButtonPermission
            loading={testing}
            onClick={() => this.handleTestConnection(dataSourceDetail)}
            permissionList={[
              {
                code: `${match.path}.button.test`,
                type: 'button',
                meaning: '数据源-测试连接',
              },
            ]}
            disabled={pageDisabled}
          >
            {intl.get('hpfm.dataSource.model.dataSource.testConnection').d('测试连接')}
          </ButtonPermission>
        </Header>
        <Content>
          <Spin spinning={fetchDetailLoading}>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              loading={false}
              title={
                <h3>
                  {intl
                    .get('hpfm.reportDataSource.model.reportDataSource.baseParams')
                    .d('基本参数')}
                </h3>
              }
            >
              <Form>
                <Row {...EDIT_FORM_ROW_LAYOUT} type="flex" justify="start">
                  {isSiteFlag && (
                    <Col {...FORM_COL_3_LAYOUT}>
                      <Form.Item
                        label={intl.get('entity.tenant.tag').d('租户')}
                        {...EDIT_FORM_ITEM_LAYOUT}
                      >
                        {getFieldDecorator('tenantId', {
                          rules: [
                            {
                              required: true,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: intl.get('entity.tenant.tag').d('租户'),
                              }),
                            },
                          ],
                          initialValue: tenantId,
                        })(
                          <Lov
                            code="HPFM.TENANT"
                            textValue={tenantName}
                            disabled={datasourceId !== undefined || pageDisabled}
                          />
                        )}
                      </Form.Item>
                    </Col>
                  )}
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hpfm.dataSource.model.dataSource.datasourceCode')
                        .d('数据源编码')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('datasourceCode', {
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hpfm.dataSource.model.dataSource.datasourceCode')
                                .d('数据源编码'),
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
                            message: intl.get('hzero.common.validation.max', {
                              max: 30,
                            }),
                          },
                        ],
                        initialValue: datasourceCode,
                      })(
                        <Input
                          trim
                          typeCase="upper"
                          inputChinese={false}
                          disabled={datasourceId !== undefined || pageDisabled}
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hpfm.ruleEngine.model.ruleEngine.description')
                        .d('数据源名称')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('description', {
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hpfm.ruleEngine.model.ruleEngine.description')
                                .d('数据源名称'),
                            }),
                          },
                          {
                            max: 600,
                            message: intl.get('hzero.common.validation.max', {
                              max: 600,
                            }),
                          },
                        ],
                        initialValue: description,
                      })(
                        <TLEditor
                          label={intl
                            .get('hpfm.ruleEngine.model.ruleEngine.description')
                            .d('数据源名称')}
                          field="description"
                          token={_token}
                          disabled={pageDisabled}
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hpfm.reportDataSource.model.reportDataSource.dsPurposeCode')
                        .d('数据源用途')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('dsPurposeCode', {
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hpfm.reportDataSource.model.reportDataSource.dsPurposeCode')
                                .d('数据源用途'),
                            }),
                          },
                        ],
                        initialValue: dsPurposeCode ? dsPurposeCode.split(',') : [],
                      })(
                        <Select
                          mode="multiple"
                          allowClear={false}
                          disabled={datasourceId !== undefined || pageDisabled}
                        >
                          {dsPurposeCodeList.map((item) => (
                            <Select.Option key={item.value} value={item.value}>
                              {item.meaning}
                            </Select.Option>
                          ))}
                        </Select>
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl.get('hpfm.ruleEngine.model.dataSource.class').d('数据库分类')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('datasourceClass', {
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hpfm.ruleEngine.model.dataSource.class')
                                .d('数据库分类'),
                            }),
                          },
                        ],
                        initialValue: datasourceClass,
                      })(
                        <Select
                          allowClear={false}
                          onChange={this.changeDbClass}
                          disabled={datasourceId !== undefined || pageDisabled}
                        >
                          {dataSourceClassList.map((item) => (
                            <Select.Option key={item.value} value={item.value}>
                              {item.meaning}
                            </Select.Option>
                          ))}
                        </Select>
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl.get('hpfm.ruleEngine.model.dataSource.dbType').d('数据库类型')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('dbType', {
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hpfm.ruleEngine.model.dataSource.dbType')
                                .d('数据库类型'),
                            }),
                          },
                        ],
                        initialValue: dbType,
                      })(this.renderDbTypeSelect(pageDisabled))}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl.get('hpfm.dataSource.model.dataSource.driverType').d('驱动类型')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('driverType', {
                        initialValue: driverType || 'DEFAULT',
                      })(
                        <Select
                          allowClear={false}
                          onChange={this.changeDriverType}
                          disabled={datasourceId !== undefined || pageDisabled}
                          // defaultValue='DEFAULT'
                        >
                          {driverTypeCodeList.map((item) => (
                            <Select.Option key={item.value} value={item.value}>
                              {item.meaning}
                            </Select.Option>
                          ))}
                        </Select>
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl.get('hpfm.dataSource.model.dataSource.driverId').d('数据源驱动')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('driverId', {
                        initialValue: driverId,
                        rules: [
                          {
                            required: driverIdRequired,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hpfm.dataSource.model.dataSource.driverId')
                                .d('数据源驱动'),
                            }),
                          },
                        ],
                      })(
                        <Lov
                          code="HPFM.DATASOURCE_DRIVER"
                          textValue={driverName}
                          disabled={!driverIdRequired || pageDisabled}
                          queryParams={
                            isSiteFlag
                              ? {
                                  tenantId: form.getFieldValue('tenantId'),
                                  databaseType: form.getFieldValue('dbType'),
                                }
                              : {
                                  tenantId: getCurrentOrganizationId(),
                                  databaseType: form.getFieldValue('dbType'),
                                }
                          }
                        />
                      )}
                    </Form.Item>
                  </Col>
                  {/* <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl.get('hpfm.dataSource.model.dataSource.driverClass').d('数据类')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('driverClass', {
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hpfm.dataSource.model.dataSource.driverClass')
                                .d('数据类'),
                            }),
                          },
                          {
                            max: 240,
                            message: intl.get('hzero.common.validation.max', {
                              max: 240,
                            }),
                          },
                        ],
                        initialValue: driverClass,
                      })(<Input disabled={pageDisabled} />)}
                    </Form.Item>
                  </Col> */}
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hpfm.dataSource.model.dataSource.datasourceUrl')
                        .d('URL地址')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('datasourceUrl', {
                        initialValue: datasourceUrl,
                        rules: [
                          {
                            max: 600,
                            message: intl.get('hzero.common.validation.max', {
                              max: 600,
                            }),
                          },
                        ],
                      })(<Input disabled={pageDisabled} />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hpfm.reportDataSource.model.reportDataSource.dbPoolType')
                        .d('连接池类型')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('dbPoolType', {
                        initialValue: dbPoolType,
                      })(
                        <Select
                          allowClear={false}
                          onChange={this.changeDbPoolType}
                          disabled={pageDisabled}
                        >
                          {dbPoolTypeList.map((item) => (
                            <Select.Option key={item.value} value={item.value}>
                              {item.meaning}
                            </Select.Option>
                          ))}
                        </Select>
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl.get('hpfm.dataSource.model.dataSource.user').d('用户')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('username', {
                        initialValue: username,
                        rules: [
                          {
                            max: 100,
                            message: intl.get('hzero.common.validation.max', {
                              max: 100,
                            }),
                          },
                        ],
                      })(<Input disabled={pageDisabled} autocomplete="off" />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl.get('hpfm.dataSource.model.dataSource.password').d('密码')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('passwordEncrypted', {
                        initialValue: passwordEncrypted,
                        rules: [
                          {
                            max: 110,
                            message: intl.get('hzero.common.validation.max', {
                              max: 110,
                            }),
                          },
                        ],
                      })(
                        <Input
                          autocomplete="new-password"
                          disabled={pageDisabled}
                          type="password"
                          placeholder={
                            datasourceId !== undefined
                              ? intl.get('hzero.common.validation.notChange').d('未更改')
                              : ''
                          }
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl.get('hzero.common.remark').d('备注')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('remark', {
                        initialValue: remark,
                        rules: [
                          {
                            max: 240,
                            message: intl.get('hzero.common.validation.max', {
                              max: 240,
                            }),
                          },
                        ],
                      })(<Input disabled={pageDisabled} />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl.get('hzero.common.status.enable').d('启用')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('enabledFlag', {
                        initialValue: enabledFlag,
                      })(<Switch disabled={pageDisabled} />)}
                    </Form.Item>
                  </Col>
                </Row>
              </Form>
            </Card>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              loading={false}
              title={
                <h3>{intl.get('hpfm.dataSource.model.dataSource.params.setting').d('参数配置')}</h3>
              }
            >
              <div className="table-operator">
                <Button onClick={this.handleAddBtnClick} disabled={pageDisabled}>
                  {intl.get('hzero.common.button.add').d('新增')}
                </Button>
              </div>
              <EditTable
                bordered
                pagination={false}
                rowKey="columnId"
                dataSource={dataSource}
                columns={columns}
                // rowSelection={rowSelection}
              />
            </Card>
            {extConfigs.length > 0 && (
              <Card
                bordered={false}
                className={DETAIL_CARD_CLASSNAME}
                loading={false}
                title={
                  <h3>
                    {intl
                      .get('hpfm.reportDataSource.model.reportDataSource.formParams')
                      .d('表单配置')}
                  </h3>
                }
              >
                <Row {...EDIT_FORM_ROW_LAYOUT}>
                  {extConfigRows.map((temp) => {
                    return (
                      <Row {...EDIT_FORM_ROW_LAYOUT}>
                        {temp.map((item) => {
                          const rules = [
                            {
                              required: item.requiredFlag === 1,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: item.itemName,
                              }),
                            },
                          ];
                          if (item.valueConstraint) {
                            const { length } = item.valueConstraint;
                            rules.push({
                              pattern: new RegExp(
                                item.valueConstraint.slice(
                                  1,
                                  item.valueConstraint[length] !== '/' ? length - 2 : length - 1
                                ),
                                item.valueConstraint[length] !== '/'
                                  ? item.valueConstraint[length]
                                  : undefined
                              ),
                              message: intl
                                .get('hzero.common.validation.format')
                                .d('数据格式校验不通过'),
                            });
                          }
                          let initValue;
                          let textValue;
                          if (
                            newExtConfig[item.itemCode] &&
                            (item.itemTypeCode.toLowerCase() === 'lov' ||
                              item.itemTypeCode.toLowerCase() === 'lov_view') &&
                            JSON.parse(newExtConfig[item.itemCode])
                          ) {
                            textValue = JSON.parse(newExtConfig[item.itemCode]);
                          }
                          switch (item.itemTypeCode.toLowerCase()) {
                            case 'lov':
                              initValue = textValue?.value;
                              break;
                            case 'lov_view':
                              initValue = textValue?.value;
                              break;
                            default:
                              initValue = newExtConfig[item.itemCode];
                              break;
                          }
                          return (
                            <Col {...FORM_COL_3_LAYOUT}>
                              <Form.Item
                                label={`${item.itemName}(${item.itemCode})`}
                                {...EDIT_FORM_ITEM_LAYOUT}
                                key={`${item.formLineId}`}
                              >
                                {getFieldDecorator(`${item.itemCode}`, {
                                  initialValue: initValue || item.defaultValue,
                                  rules,
                                })(
                                  this.renderFormParams(
                                    item,
                                    newExtConfig,
                                    pageDisabled,
                                    createFlag
                                  )
                                )}
                              </Form.Item>
                            </Col>
                          );
                        })}
                      </Row>
                    );
                  })}
                </Row>
              </Card>
            )}
            {!isEmpty(newDbPoolParams) && (
              <Card
                bordered={false}
                className={DETAIL_CARD_CLASSNAME}
                loading={false}
                title={
                  <h3>
                    {intl
                      .get('hpfm.reportDataSource.model.reportDataSource.dbPoolParams')
                      .d('连接池参数')}
                  </h3>
                }
              >
                <Row {...EDIT_FORM_ROW_LAYOUT}>
                  {newDbPoolParams.map((item) => (
                    <Col {...FORM_COL_3_LAYOUT}>
                      <Form.Item
                        label={`${item.key}`}
                        {...EDIT_FORM_ITEM_LAYOUT}
                        key={`${item.key}`}
                      >
                        {getFieldDecorator(`${item.key}`, {
                          initialValue: `${item.value}`,
                        })(<Input disabled={pageDisabled} />)}
                      </Form.Item>
                    </Col>
                  ))}
                </Row>
              </Card>
            )}
          </Spin>
        </Content>
      </>
    );
  }
}
