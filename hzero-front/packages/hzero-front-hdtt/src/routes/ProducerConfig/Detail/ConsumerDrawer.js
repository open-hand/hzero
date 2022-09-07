/**
 * ConsumerDrawer - 数据消费生产消费配置详情页-消费配置
 * @date: 2019/4/16
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Modal, Spin, Button } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, cloneDeep, filter, uniqBy } from 'lodash';
import uuidv4 from 'uuid/v4';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { getEditTableData, addItemToPagination, delItemToPagination } from 'utils/utils';
import ConsumerDrawerForm from './ConsumerDrawerForm';
import ConsumerDrawerTable from './ConsumerDrawerTable';

const promptCode = 'hdtt.producerConfig';

/**
 * 列信息-数据修改滑窗(抽屉)
 * @extends {Component} - React.Component
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onOk - 抽屉确定操作
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @reactProps {!Object} producerConfig - 数据源
 * @reactProps {boolean} fetchConsumerDetailLoading - 表格详情数据加载是否完成
 * @reactProps {boolean} savingDetailLoading - 新增和编辑页面是否在保存
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} currentConsumerDetail - 编辑时选中的当前行
 * @reactProps {number} tenantFlag - 是否按租户分发
 * @reactProps {Function} onCancel - 关闭滑窗
 * @reactProps {Function} onSave - 保存
 * @return React.element
 */
@connect(({ producerConfig, loading }) => ({
  producerConfig,
  tenantInitingLoading: loading.effects['producerConfig/initConsumer'],
  fetchConsumerDetailLoading:
    loading.effects['producerConfig/fetchConsumerTenantList'] ||
    loading.effects['producerConfig/initConsumer'] ||
    loading.effects['producerConfig/deleteTenantConsumer'] ||
    loading.effects['producerConfig/fetchConsumerDbConfig'],
  savingDetailLoading:
    loading.effects['producerConfig/createConsumerConfig'] ||
    loading.effects['producerConfig/updateConsumer'],
}))
export default class ConsumerDrawer extends Component {
  state = {
    tenantSelectedRowKeys: [],
    tenantSelectedRows: {},
    initDbCode: '',
    initDsId: '',
    serviceName: '',
  };

  /**
   * 获取消费租户选中行
   */
  @Bind()
  handleTenantRowSelectChange(selectedRowKeys, selectedRows) {
    this.setState({
      tenantSelectedRowKeys: selectedRowKeys,
      tenantSelectedRows: selectedRows,
    });
  }

  /**
   * 新建行
   */
  @Bind()
  handleCreateTenantConsumer() {
    const {
      dispatch,
      producerConfig: { consumerTenantList, consumerTenantPagination },
    } = this.props;
    dispatch({
      type: 'producerConfig/updateState',
      payload: {
        consumerTenantList: [
          {
            consTenantConfigId: uuidv4(),
            rowId: uuidv4(),
            tenantId: '',
            consumerOffset: 0,
            processStatusMeaning: '',
            processTime: '',
            processMsg: '',
            _status: 'create',
          },
          ...consumerTenantList,
        ],

        consumerTenantPagination: addItemToPagination(
          consumerTenantList.length,
          consumerTenantPagination
        ),
      },
    });
  }

  /**
   * 取消或右上角关闭
   */
  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    this.setState(
      {
        tenantSelectedRowKeys: [],
        tenantSelectedRows: {},
      },
      () => {
        onCancel();
      }
    );
  }

  /**
   * 刷新租户维护数据
   */
  @Bind()
  handleRefreshConfig() {
    const {
      onRefreshConfig,
      fetchConsumerDetailLoading = false,
      producerConfig: { consumerDbConfig },
    } = this.props;
    if (fetchConsumerDetailLoading) {
      return;
    }
    this.handleFetchTenantList();
    onRefreshConfig(consumerDbConfig.consDbConfigId);
  }

  /**
   * 获取消费配置表格数据
   * @param {Object} fields - 分页参数
   */
  @Bind()
  handleFetchTenantList(fields = {}) {
    const {
      dispatch,
      producerConfig: { consumerDbConfig },
    } = this.props;
    dispatch({
      type: 'producerConfig/fetchConsumerTenantList',
      payload: {
        consDbConfigId: consumerDbConfig.consDbConfigId,
        page: isEmpty(fields) ? {} : fields,
      },
    });
  }

  /**
   * 删除租户消费配置
   */
  @Bind()
  handleDeleteTenant() {
    const { dispatch } = this.props;
    const { tenantSelectedRows } = this.state;
    const that = this;
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk() {
        // 选中的有ID的数据
        const savedRows = tenantSelectedRows.filter((item) => !('_status' in item));
        if (savedRows.length) {
          dispatch({
            type: 'producerConfig/deleteTenantConsumer', // 删除
            payload: savedRows,
          }).then((res) => {
            if (res) {
              // 从总列删除勾选了的列
              that.handleUpdateTenantData();
            }
          });
        } else {
          that.handleUpdateTenantData();
        }
      },
    });
  }

  /**
   * 删除后更新租户消费表格
   */
  @Bind()
  handleUpdateTenantData() {
    const {
      dispatch,
      producerConfig: { consumerTenantList, consumerTenantPagination },
    } = this.props;
    const { tenantSelectedRowKeys } = this.state;
    const tempConsumerTenantList = filter(
      consumerTenantList,
      (item) => !tenantSelectedRowKeys.includes(item.consTenantConfigId)
    );
    this.setState({
      tenantSelectedRows: {},
      tenantSelectedRowKeys: [],
    });
    dispatch({
      type: 'producerConfig/updateState',
      payload: {
        consumerTenantList: tempConsumerTenantList,
        consumerTenantPagination: delItemToPagination(
          tempConsumerTenantList.length,
          consumerTenantPagination
        ),
      },
    });
    notification.success();
  }

  /**
   * 初始化租户维度的消费者
   * @param {Object} record - 消费租户表格行数据
   */
  @Bind()
  handleInitTenantConsumer(record) {
    const {
      producerConfig: { consumerDbConfig },
      dispatch,
      onRefreshConfig,
    } = this.props;
    const tempCurrentConsumerDetail = cloneDeep(consumerDbConfig);
    tempCurrentConsumerDetail.consTenantConfigList = [record];
    dispatch({
      type: 'producerConfig/initConsumer',
      payload: { ...tempCurrentConsumerDetail },
    }).then((res) => {
      if (res) {
        onRefreshConfig(consumerDbConfig.consDbConfigId);
        this.handleFetchTenantList();
        notification.success();
      }
    });
  }

  /**
   *  保存消费配置详情
   */
  @Bind()
  handleSave() {
    const {
      tenantFlag,
      fetchConsumerDetailLoading = false,
      savingDetailLoading = false,
      producerConfig: { consumerTenantList },
    } = this.props;
    if (savingDetailLoading || fetchConsumerDetailLoading) {
      return;
    }
    // 收集头部表单值
    const formValues = this.formRef.getFormValue();
    let consTenantConfigList = [];
    let flag = true;
    if (tenantFlag) {
      // 判断是否有新增的行
      const isHasCreate = consumerTenantList.some((item) => '_status' in item);
      // 按租户分发，至少包含一条租户消费配置
      if (!consumerTenantList.length) {
        notification.warning({
          message: intl
            .get(`${promptCode}.view.message.tenant.warning`)
            .d('按租户分发时，必须至少有一个租户消费设置'),
        });
        flag = false;
      }

      if (isHasCreate) {
        // 收集表格值
        let tenantList = consumerTenantList.filter((item) => !('_status' in item));
        consTenantConfigList = getEditTableData(consumerTenantList, ['_status']);
        tenantList = [...tenantList, ...consTenantConfigList];
        if (uniqBy(tenantList, 'tenantName').length !== tenantList.length) {
          notification.warning({
            message: intl.get(`${promptCode}.view.message.tenant.repeat`).d('不可选择重复租户'),
          });
          flag = false;
        } else if (consTenantConfigList.length) {
          // 如果有新增行，但新增行数据为空数组。说明报错了
          // 删除新增的数据中，为了配合表格rowKey而添加的属性
          consTenantConfigList = consTenantConfigList.map((item) => {
            const tempItem = cloneDeep(item);
            if ('rowId' in item) {
              delete tempItem.consTenantConfigId;
              delete tempItem.rowId;
            }
            return tempItem;
          });
        } else if (!consTenantConfigList.length) {
          flag = false;
        }
      }
    }

    if (flag && !isEmpty(formValues)) {
      const payload = {
        ...formValues,
        consTenantConfigList,
      };
      this.save(payload);
    }
  }

  /**
   * 保存配置
   * @param {object} payload -请求体
   */
  @Bind()
  save(payload) {
    const {
      dispatch,
      producerConfigId,
      onRefreshConfig,
      onRefreshTenant,
      tenantFlag,
      producerConfig: { consumerDbConfig },
    } = this.props;
    const isEdit = !isEmpty(consumerDbConfig);
    let totalPayload;
    let type;
    if (isEdit) {
      type = 'producerConfig/updateConsumer';
      totalPayload = {
        payload: {
          ...payload,
          _token: consumerDbConfig._token,
          objectVersionNumber: consumerDbConfig.objectVersionNumber,
          producerConfigId,
        },
        consDbConfigId: consumerDbConfig.consDbConfigId,
      };
    } else {
      type = 'producerConfig/createConsumerConfig';
      totalPayload = {
        payload,
        producerConfigId,
      };
    }
    dispatch({
      type,
      payload: { ...totalPayload },
    }).then((res) => {
      if (res) {
        notification.success();
        onRefreshConfig(res.consDbConfigId);
        if (tenantFlag) {
          onRefreshTenant(res.consDbConfigId);
        } else {
          this.handleCancel();
        }
      }
    });
  }

  /**
   * 修改服务
   * @param {string} val - 选中值
   */
  @Bind()
  handleChangeService(val) {
    this.setState({
      serviceName: val,
      initDbCode: null,
      initDsId: null,
    });
  }

  /**
   * 修改租户LOV
   * @param {number} initDbCode - DbCode
   * @param {number} initDsId - DsId
   */
  @Bind()
  handleChangeDb(initDbCode, initDsId) {
    this.setState({ initDbCode, initDsId });
  }

  render() {
    const {
      producerConfig: { consumerTenantList = [], consumerTenantPagination, consumerDbConfig },
      anchor,
      visible,
      tenantFlag,
      tenantInitingLoading = false,
      savingDetailLoading = false,
      fetchConsumerDetailLoading = false,
    } = this.props;
    const { tenantSelectedRowKeys, initDbCode, initDsId, serviceName } = this.state;
    const isEdit = !isEmpty(consumerDbConfig);
    const consumerTenantRowSelection = {
      selectedRowKeys: tenantSelectedRowKeys,
      onChange: this.handleTenantRowSelectChange,
    };
    const formProps = {
      consumerDbConfig,
      dataSource: consumerTenantList,
      onChangeDb: this.handleChangeDb,
      onChangeService: this.handleChangeService,
      wrappedComponentRef: (form) => {
        this.formRef = form;
      },
    };
    const listProps = {
      loading: false,
      dataSource: consumerTenantList,
      rowSelection: consumerTenantRowSelection,
      pagination: consumerTenantPagination,
      isEdit,
      initDbCode,
      initDsId,
      isTenantIniting: tenantInitingLoading,
      consumerDbConfig,
      wrappedComponentRef: (node) => {
        this.editTable = node;
      },
      onChange: this.handleFetchTenantList,
      onTenantInit: this.handleInitTenantConsumer,
    };

    return (
      <Modal
        destroyOnClose
        title={
          isEdit
            ? intl.get(`${promptCode}.view.message.edit.consumer`).d('编辑消费配置')
            : intl.get(`${promptCode}.view.message.create.consumer`).d('新建消费配置')
        }
        width={650}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={this.handleSave}
        onCancel={this.handleCancel}
        confirmLoading={savingDetailLoading}
      >
        <Spin spinning={fetchConsumerDetailLoading || savingDetailLoading}>
          <ConsumerDrawerForm {...formProps} />
          <div
            className="table-list-operator"
            style={{ display: !tenantFlag ? 'none' : 'block', textAlign: 'right' }}
          >
            {isEdit && (
              <Button onClick={this.handleRefreshConfig}>
                {intl.get('hzero.common.button.refresh').d('刷新')}
              </Button>
            )}
            <Button disabled={isEmpty(tenantSelectedRowKeys)} onClick={this.handleDeleteTenant}>
              {intl.get('hzero.common.button.delete').d('删除')}
            </Button>
            <Button
              type="primary"
              onClick={this.handleCreateTenantConsumer}
              disabled={(!isEdit && !serviceName) || (!isEdit && !(initDbCode && initDsId))}
            >
              {intl.get('hzero.common.button.create').d('新建')}
            </Button>
          </div>
          {!!tenantFlag && <ConsumerDrawerTable {...listProps} />}
        </Spin>
      </Modal>
    );
  }
}
