/**
 * Detail - 数据消费生产消费配置详情页
 * @date: 2019/4/16
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Button, Row, Col, Input, Modal, Card, Icon, Tooltip } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { isEmpty, isUndefined, isEqual } from 'lodash';
import { Bind } from 'lodash-decorators';
import classNames from 'classnames';

import Switch from 'components/Switch';
import Lov from 'components/Lov';
import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { filterNullValueObject, isTenantRoleLevel } from 'utils/utils';
import {
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT,
} from 'utils/constants';

import style from './index.less';
import ConsumerTable from './ConsumerTable';
import ConsumerDrawer from './ConsumerDrawer';
import TopicMsgModal from '../List/TopicMsgModal';

const isTenant = isTenantRoleLevel();
const promptCode = 'hdtt.producerConfig';
const { TextArea } = Input;
const formLayout = {
  labelCol: {
    span: 3,
  },
  wrapperCol: {
    span: 21,
  },
};

/**
 * 数据生产消费-配置组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} producerConfig - 数据源
 * @reactProps {boolean} fetchProducerDetailLoading - 表单数据加载是否完成
 * @reactProps {boolean} fetchConsumerListLoading - 表格数据加载是否完成
 * @reactProps {!Object} saving - 表单是否保存完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: 'hdtt.producerConfig' })
@connect(({ producerConfig, loading }) => ({
  producerConfig,
  fetchProducerDetailLoading:
    loading.effects['producerConfig/fetchProducerDetail'] ||
    loading.effects['producerConfig/updateDdl'],
  fetchConsumerListLoading:
    loading.effects['producerConfig/fetchConsumerList'] ||
    loading.effects['producerConfig/deleteConsumer'] ||
    loading.effects['producerConfig/initConsumer'],
  saving:
    loading.effects['producerConfig/saveProducer'] ||
    loading.effects['producerConfig/updateProducer'],
  dbInitingLoading: loading.effects['producerConfig/initConsumer'],
}))
export default class Detail extends Component {
  /**
   * state初始化
   */
  state = {
    serviceCode: undefined, // 生产服务code
    serviceName: undefined, // 生产服务name
    initDbCode: undefined,
    initDsCode: undefined,
    initDsId: undefined,
    consumerSelectedRowKeys: [], // 消费服务列表选中行key集合
    consumerSelectedRows: {}, // 消费服务列表选中行集合
    consumerConfigDrawerVisible: false, // 显示滑窗
    isShowMessage: false, // 是否显示Topic消息模态框
  };

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    this.handleSearch();
  }

  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'producerConfig/updateState',
      payload: {
        producerDetail: {},
        consumerList: [], // 消费配置列表
        consumerListPagination: {}, // 消费配置列表分页
      },
    });
  }

  /**
   * 获取头行数据
   */
  @Bind()
  handleSearch() {
    const { dispatch, match, fetchProducerDetailLoading = false } = this.props;
    const { id } = match.params;
    if (fetchProducerDetailLoading) {
      return;
    }
    if (!isUndefined(id)) {
      dispatch({
        type: 'producerConfig/fetchProducerDetail',
        payload: {
          producerConfigId: id,
        },
      });
      this.handleFetchConsumerList();
    } else {
      dispatch({
        type: 'producerConfig/updateState',
        payload: {
          producerDetail: {},
          consumerList: [], // 消费配置列表
          consumerListPagination: {}, // 消费配置列表分页
        },
      });
    }
  }

  /**
   * 刷新初始化DDL
   */
  @Bind()
  handleRefreshDdl() {
    const {
      dispatch,
      match,
      producerConfig: { producerDetail = {} },
      fetchProducerDetailLoading = false,
    } = this.props;
    const { id } = match.params;
    if (fetchProducerDetailLoading) {
      return;
    }
    dispatch({
      type: 'producerConfig/updateDdl',
      payload: {
        producerConfigId: id,
        producerConfig: producerDetail,
      },
    });
  }

  /**
   * 获取消费配置表格数据
   * @param {Object} fields - 分页参数
   */
  @Bind()
  handleFetchConsumerList(fields = {}) {
    const { dispatch, match, fetchConsumerListLoading } = this.props;
    const { id } = match.params;
    if (fetchConsumerListLoading) {
      return;
    }
    dispatch({
      type: 'producerConfig/fetchConsumerList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        producerConfigId: id,
      },
    });
  }

  /**
   * 详情查询数据消息消费DB配置
   * @param {number} consDbConfigId - 消费配置ID
   */
  @Bind()
  handleFetchConsumerDbConfig(consDbConfigId) {
    const { dispatch } = this.props;
    dispatch({
      type: 'producerConfig/fetchConsumerDbConfig',
      payload: consDbConfigId,
    });
  }

  /**
   * 获取消费配置租户列表
   * @param {number} consDbConfigId - 消费配置ID
   */
  @Bind()
  handleFetchTenantList(consDbConfigId) {
    const { dispatch } = this.props;
    dispatch({
      type: 'producerConfig/fetchConsumerTenantList',
      payload: {
        consDbConfigId,
        page: {},
      },
    });
  }

  /**
   * 获取删除消费配置表格选中行
   */
  @Bind()
  handleConsumerRowSelectChange(selectedRowKeys, selectedRows) {
    this.setState({
      consumerSelectedRowKeys: selectedRowKeys,
      consumerSelectedRows: selectedRows,
    });
  }

  /**
   * 批量删除消费配置
   */
  @Bind()
  handleDeleteConsumer() {
    const { dispatch } = this.props;
    const { consumerSelectedRows } = this.state;
    const that = this;
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk() {
        dispatch({
          type: 'producerConfig/deleteConsumer', // 删除
          payload: consumerSelectedRows,
        }).then((res) => {
          if (res) {
            notification.success({
              message: intl.get('hzero.common.notification.success.delete').d('删除成功'),
            });
            that.setState(
              {
                consumerSelectedRowKeys: [],
                consumerSelectedRows: {},
              },
              () => {
                that.handleFetchConsumerList();
              }
            );
          }
        });
      },
    });
  }

  /**
   * 判断表单单个字段是否修改过
   * @param {string} name - 表单字段名称
   */
  @Bind()
  isModifiedField(name) {
    const {
      form: { getFieldValue },
      producerConfig: { producerDetail = {} },
    } = this.props;
    const value = getFieldValue(name);
    let initialValue = producerDetail[name];
    if (name === 'producerService') {
      initialValue = producerDetail.serviceName;
    }

    if (!value && !initialValue) {
      return false;
    }
    if (!isEqual(value, initialValue)) {
      return true;
    }
    return false;
  }

  /**
   * 判断表单是否修改过
   * @param {Object} values - 表单值
   */
  @Bind()
  isModifiedFields(values) {
    const fieldNames = Object.keys(values);
    for (const item of fieldNames) {
      if (this.isModifiedField(item)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 打开滑窗
   * @param status {string} - 触发弹窗打开的操作 create/edit
   * @param record {Object} - 表格列信息
   */
  @Bind()
  handleOpenDrawer(status, record = {}) {
    const {
      form,
      producerConfig: { producerDetail = {} },
      dispatch,
    } = this.props;
    const message = intl
      .get(`${promptCode}.view.message.delete.warning`)
      .d('请先修改后保存配置信息');
    form.validateFields((err, values) => {
      if (!err) {
        // 在侧滑打开弹窗之前必须保证头部信息已保存
        if (this.isModifiedFields(values)) {
          notification.warning({ message });
        } else {
          if (!isEmpty(record)) {
            this.handleFetchConsumerDbConfig(record.consDbConfigId);
            if (producerDetail.tenantFlag === 1) {
              this.handleFetchTenantList(record.consDbConfigId);
            }
          } else {
            dispatch({
              type: 'producerConfig/updateState',
              payload: {
                consumerDbConfig: {},
              },
            });
          }
          this.setState({
            consumerConfigDrawerVisible: true,
          });
        }
      } else {
        notification.warning({ message });
      }
    });
  }

  /**
   * 关闭滑窗
   */
  @Bind()
  handleCloseDrawer() {
    const { dispatch } = this.props;
    this.handleFetchConsumerList();
    this.setState(
      {
        consumerConfigDrawerVisible: false,
      },
      () => {
        dispatch({
          type: 'producerConfig/updateState',
          payload: {
            consumerTenantList: [], // 消费租户列表
            consumerTenantPagination: {}, // 消费租户列表分页
          },
        });
      }
    );
  }

  /**
   * 保存生产消费配置头部结构
   */
  @Bind()
  handleSaveConsumerConfig() {
    const {
      form,
      dispatch,
      match,
      producerConfig: { producerDetail = {} },
    } = this.props;
    const isEdit = !isUndefined(match.params.id);
    form.validateFields((err, values) => {
      if (!err) {
        if (!this.isModifiedFields(values)) {
          notification.warning({
            message: intl
              .get(`${promptCode}.view.message.delete.warning`)
              .d('请先修改后保存配置信息'),
          });
          return;
        }
        const {
          serviceCode = producerDetail.serviceCode,
          serviceName = producerDetail.serviceName,
          initDbCode = producerDetail.initDbCode,
          initDsCode = producerDetail.initDsCode,
          initDsId = producerDetail.initDsId,
        } = this.state;
        const fieldValues = isUndefined(this.props.form) ? {} : filterNullValueObject(values);
        fieldValues.initDdlSql = isEdit ? producerDetail.initDdlSql : null;
        const configData = {
          ...fieldValues,
          serviceCode,
          serviceName,
          initDbCode,
          initDsCode,
          initDsId,
        };
        if (isTenant) {
          configData.tenantFlag = 0;
        }
        if (!isEdit) {
          dispatch({
            type: 'producerConfig/saveProducer', // 新增
            payload: { ...configData },
          }).then((res) => {
            if (res) {
              notification.success();
              dispatch(
                routerRedux.push({
                  pathname: `/hdtt/producer-config/detail/${res.producerConfigId}`,
                })
              );
            }
          });
        } else {
          dispatch({
            type: 'producerConfig/updateProducer', // 编辑
            payload: {
              ...configData,
              producerConfigId: +match.params.id,
              objectVersionNumber: producerDetail.objectVersionNumber,
            },
          }).then((res) => {
            if (res) {
              notification.success();
              this.handleSearch();
            }
          });
        }
      }
    });
  }

  /**
   * 修改初始化DB
   * @param {string} val - 修改的值
   * @param {{initDbCode: string, initDsCode: string, initDsId: number}} record - 数据库相关值
   */
  @Bind()
  onChangeInitDB(val, { initDbCode, initDsCode, initDsId }) {
    this.setState({
      initDbCode,
      initDsCode,
      initDsId,
    });
  }

  /**
   * 修改生产者服务
   * @param {string} val - 修改的值
   * @param {{serviceCode: string, serviceName: string}} record - 服务相关值
   */
  @Bind()
  onChangeProducerService(val, { serviceCode, serviceName }) {
    const {
      form: { setFieldsValue },
    } = this.props;
    this.setState(
      {
        serviceCode,
        serviceName,
      },
      () => {
        setFieldsValue({ initDbCode: undefined });
      }
    );
  }

  /**
   * 初始化DB维度的消费者
   * @param {Object} record - 消费者表格行数据
   */
  @Bind()
  handleInitDbConsumer(record) {
    const {
      dispatch,
      form,
      producerConfig: { consumerListPagination },
    } = this.props;
    const message = intl
      .get(`${promptCode}.view.message.delete.warning`)
      .d('请先修改后保存配置信息');
    form.validateFields((err, values) => {
      if (!err) {
        // 初始化之前必须保证头部信息已保存
        if (this.isModifiedFields(values)) {
          notification.warning({ message });
        } else {
          dispatch({
            type: 'producerConfig/initConsumer',
            payload: { ...record, initByDbDimension: 1 },
          }).then((res) => {
            if (res) {
              this.handleFetchConsumerList(consumerListPagination);
              notification.success();
            }
          });
        }
      } else {
        notification.warning({ message });
      }
    });
  }

  /**
   * 显示Topic消息模态框
   */
  @Bind()
  handleOpenMessageModal() {
    this.setState({
      isShowMessage: true,
    });
  }

  /**
   * 关闭Topic消息内容模态框
   */
  @Bind()
  handleCloseMessageModal() {
    this.setState({
      isShowMessage: false,
    });
  }

  /**
   * 复制DDL语句
   */
  @Bind()
  handleCopy() {
    const {
      form: { getFieldValue },
    } = this.props;
    const ddl = document.getElementById('ddl');
    ddl.value = getFieldValue('initDdlSql');
    ddl.select();
    document.execCommand('copy');
    notification.success({
      message: intl.get(`${promptCode}.view.message.copy.success`).d('复制成功'),
    });
  }

  /**
   * 渲染头部表单
   */
  renderHeaderForm() {
    const {
      fetchProducerDetailLoading = false,
      form: { getFieldDecorator, getFieldValue },
      producerConfig: { producerDetail = {} },
      match,
    } = this.props;
    const isEdit = !isUndefined(match.params.id);
    const spinning = isUndefined(match.params.id) ? false : fetchProducerDetailLoading;
    return (
      <Form style={{ marginBottom: '30px' }}>
        <Card
          key="basic-config"
          bordered={false}
          title={<h3> {intl.get(`${promptCode}.view.message.basic.config`).d('基本配置')}</h3>}
          className={DETAIL_CARD_CLASSNAME}
          loading={spinning}
        >
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl.get(`${promptCode}.model.producerConfig.producerService`).d('生产服务')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('producerService', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get(`${promptCode}.model.producerConfig.producerService`)
                          .d('生产服务'),
                      }),
                    },
                  ],
                  initialValue: producerDetail.serviceName,
                })(
                  <Lov
                    code="HDTT.SERVICE"
                    textValue={producerDetail.serviceName || undefined}
                    onChange={this.onChangeProducerService}
                    disabled={isEdit}
                  />
                )}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl.get(`${promptCode}.model.producerConfig.tableName`).d('生产表名')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('tableName', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get(`${promptCode}.model.producerConfig.tableName`)
                          .d('生产表名'),
                      }),
                    },
                  ],
                  initialValue: producerDetail.tableName,
                })(<Input typeCase="lower" disabled={isEdit} />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl.get(`${promptCode}.model.producerConfig.description`).d('说明')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('description', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get(`${promptCode}.model.producerConfig.description`).d('说明'),
                      }),
                    },
                  ],
                  initialValue: producerDetail.description,
                })(<Input />)}
              </Form.Item>
            </Col>
          </Row>
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            {!isTenant && (
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl
                    .get(`${promptCode}.model.producerConfig.tenantFlag`)
                    .d('是否按照租户分发')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('tenantFlag', {
                    rules: [
                      {
                        required: true,
                        message: '',
                      },
                    ],
                    initialValue: isEdit ? producerDetail.tenantFlag : 0,
                  })(<Switch disabled={isEdit} />)}
                </Form.Item>
              </Col>
            )}
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl.get(`${promptCode}.model.producerConfig.enable`).d('是否启用')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('enabledFlag', {
                  initialValue: isEdit ? producerDetail.enabledFlag : 1,
                })(<Switch />)}
              </Form.Item>
            </Col>
            {isEdit && (
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl
                    .get(`${promptCode}.model.producerConfig.topicStatus`)
                    .d('Topic创建状态')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {producerDetail.topicGeneratedStatus === 'E' ? (
                    <a onClick={this.handleOpenMessageModal}>
                      {producerDetail.topicGeneratedStatusMeaning}
                    </a>
                  ) : (
                    producerDetail.topicGeneratedStatusMeaning
                  )}
                </Form.Item>
              </Col>
            )}
          </Row>
        </Card>
        <Card
          key="default-config"
          bordered={false}
          title={
            <h3>{intl.get(`${promptCode}.view.message.init.defaultConfig`).d('初始化默认配置')}</h3>
          }
          className={DETAIL_CARD_CLASSNAME}
          loading={spinning}
        >
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl.get(`${promptCode}.model.producerConfig.initDb`).d(`初始化DB`)}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('initDbCode', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get(`${promptCode}.model.producerConfig.initDb`).d(`初始化DB`),
                      }),
                    },
                  ],
                  initialValue: producerDetail.initDbCode,
                })(
                  <Lov
                    textField="initDbCode"
                    disabled={isEdit || !getFieldValue('producerService')}
                    code="HDTT.SERVICE_DATABASE"
                    queryParams={{ serviceName: getFieldValue('producerService') }}
                    onChange={this.onChangeInitDB}
                  />
                )}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl.get(`${promptCode}.model.producerConfig.initFlag`).d(`是否初始化数据`)}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('initImportDataFlag', {
                  initialValue: isEdit ? producerDetail.initImportDataFlag : 1,
                })(<Switch />)}
              </Form.Item>
            </Col>
          </Row>
          {isEdit && (
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col span={24} className={classNames(style.ddl)}>
                <div className="ddl-container">
                  <Form.Item
                    label={intl.get(`${promptCode}.model.producerConfig.initDdl`).d(`初始化DDL`)}
                    {...formLayout}
                  >
                    {getFieldDecorator('initDdlSql', {
                      initialValue: producerDetail.initDdlSql || undefined,
                    })(<TextArea style={{ height: 116, margin: '6px 0' }} rows={4} readOnly />)}
                  </Form.Item>
                  <TextArea id="ddl" />
                  <Tooltip title={intl.get('hzero.common.button.copy').d('复制')}>
                    <Icon type="copy" className="copy-icon" onClick={this.handleCopy} />
                  </Tooltip>
                </div>
              </Col>
            </Row>
          )}
        </Card>
      </Form>
    );
  }

  render() {
    const {
      match,
      fetchConsumerListLoading,
      dbInitingLoading = false,
      saving,
      producerConfig: { consumerList, consumerListPagination, producerDetail },
    } = this.props;
    const { consumerConfigDrawerVisible, consumerSelectedRowKeys, isShowMessage } = this.state;

    const consumerRowSelection = {
      selectedRowKeys: consumerSelectedRowKeys,
      onChange: this.handleConsumerRowSelectChange,
    };
    const consumerConfigListProps = {
      loading: fetchConsumerListLoading,
      pagination: consumerListPagination,
      dataSource: consumerList,
      isDbIniting: dbInitingLoading,
      rowSelection: consumerRowSelection,
      tenantFlag: producerDetail.tenantFlag,
      onEdit: this.handleOpenDrawer,
      onChange: this.handleFetchConsumerList,
      onInit: this.handleInitDbConsumer,
    };
    const consumerConfigDrawerProps = {
      anchor: 'right',
      visible: consumerConfigDrawerVisible,
      producerConfigId: match.params.id || null,
      tenantFlag: producerDetail.tenantFlag,
      onCancel: this.handleCloseDrawer,
      onRefreshConfig: this.handleFetchConsumerDbConfig,
      onRefreshTenant: this.handleFetchTenantList,
    };
    const topicModalProps = {
      topicGeneratedMsg: producerDetail.topicGeneratedMsg,
      topicGeneratedTime: producerDetail.topicGeneratedTime,
      visible: isShowMessage,
      onCancel: this.handleCloseMessageModal,
    };
    const isEdit = !isUndefined(match.params.id);
    return (
      <>
        <Header
          title={intl.get(`${promptCode}.view.message.title.config`).d('生产消费配置')}
          backPath="/hdtt/producer-config/list"
        >
          <Button
            icon="save"
            type="primary"
            onClick={this.handleSaveConsumerConfig}
            loading={saving}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          {isEdit && (
            <>
              <Button icon="sync" onClick={this.handleRefreshDdl}>
                {intl.get(`${promptCode}.model.producerConfig.refresh.init`).d('刷新初始化DDL')}
              </Button>
              <Button icon="sync" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.refresh').d('刷新')}
              </Button>
            </>
          )}
        </Header>
        <Content>
          {this.renderHeaderForm()}
          <div className="table-list-operator" style={{ textAlign: 'right' }}>
            {isEdit && (
              <Button onClick={this.handleFetchConsumerList}>
                {intl.get('hzero.common.button.refresh').d('刷新')}
              </Button>
            )}
            <Button disabled={isEmpty(consumerSelectedRowKeys)} onClick={this.handleDeleteConsumer}>
              {intl.get('hzero.common.button.delete').d('删除')}
            </Button>
            <Button type="primary" onClick={() => this.handleOpenDrawer('create')}>
              {intl.get('hzero.common.button.create').d('新建')}
            </Button>
          </div>
          <ConsumerTable {...consumerConfigListProps} />
          <ConsumerDrawer {...consumerConfigDrawerProps} />
          <TopicMsgModal {...topicModalProps} />
        </Content>
      </>
    );
  }
}
