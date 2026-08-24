/**
 * nodeRule - 节点组规则
 * @date: 2018-9-7
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Button, Table, Icon, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import cacheComponent from 'components/CacheComponent';
import { Header, Content } from 'components/Page';

import { enableRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { HZERO_ADM } from 'utils/config';
import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';

import FilterForm from './FilterForm';

@connect(({ loading, nodeRule }) => ({
  nodeRule,
  fetchRuleLoading: loading.effects['nodeRule/fetchNodeRule'],
  enableLoading: loading.effects['nodeRule/enabledNodeRule'],
  disableLoading: loading.effects['nodeRule/disabledNodeRule'],
}))
@formatterCollections({ code: 'hadm.nodeRule' })
@cacheComponent({ cacheKey: '/hadm/node-rule/list' })
export default class nodeRule extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      fieldsValue: {},
      actionNodeRuleId: '',
    };
  }

  componentDidMount() {
    this.fetchNodeRule();
  }

  /**
   * @function fetchNodeRule - 请求列表数据
   * @param {object} params - 参数
   */
  fetchNodeRule(params = {}) {
    const {
      dispatch,
      nodeRule: { pagination = {}, defaultProductEnv: defaultValue = [] },
    } = this.props;
    const { fieldsValue } = this.state;
    dispatch({
      type: 'nodeRule/fetchNodeRule',
      payload: {
        ...fieldsValue,
        productId: defaultValue[0],
        productEnvId: defaultValue[1],
        page: pagination,
        ...params,
      },
    });
  }

  /**
   * @function handleCreate - 新建
   */
  @Bind()
  handleCreate() {
    const { history } = this.props;
    history.push(`/hadm/node-rule/config/create`);
    // // 监控是否新建，保存产品/环境信息
    // dispatch({ type: 'nodeRule/updateState', payload: { isEditSave: true } });
  }

  /**
   * @function handleCreate - 新建
   */
  @Bind()
  handleEdit(record) {
    const { dispatch, history } = this.props;
    history.push(`${HZERO_ADM}/node-rule/config/${record.nodeRuleId}`);
    // 监控是否编辑，保存产品/环境信息
    dispatch({ type: 'nodeRule/updateState', payload: { isEditSave: true } });
  }

  /**
   * @function handleEnable - 启用或禁用
   * @param {boolean} flag - 启用标识
   * @param {object} record - 行数据
   */
  @Bind()
  handleEnable(flag, record) {
    const {
      dispatch,
      nodeRule: { defaultProductEnv: defaultValue = [] },
    } = this.props;
    const { nodeRuleId } = record;
    this.setState({ actionNodeRuleId: nodeRuleId });
    const type = flag ? 'nodeRule/enabledNodeRule' : 'nodeRule/disabledNodeRule';
    dispatch({
      type,
      payload: { ...record, productId: defaultValue[0], productEnvId: defaultValue[1] },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchNodeRule();
      }
    });
  }

  /**
   * @function handlePagination - 分页操作
   * @param {Object} pagination - 分页参数
   */
  @Bind()
  handlePagination(pagination) {
    this.fetchNodeRule({
      page: pagination,
    });
  }

  /**
   * @function handleDelete - 删除节点组规则
   * @param {object} record - 行数据
   */
  handleDelete(record) {
    const {
      dispatch,
      nodeRule: { defaultProductEnv: defaultValue = [] },
    } = this.props;
    dispatch({
      type: 'nodeRule/deleteNodeRule',
      payload: { ...record, productId: defaultValue[0], productEnvId: defaultValue[1] },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchNodeRule();
      }
    });
  }

  /**
   * 查询表单
   * @param {object} form - 查询表单
   */
  @Bind()
  handleSearch(form) {
    const fieldsValue = form.getFieldsValue();
    this.setState({ fieldsValue });
    this.fetchNodeRule({ ...fieldsValue, page: {} });
  }

  /**
   * 重置查询表单
   * @param {object} form - 查询表单
   */
  @Bind()
  handleResetSearch(form) {
    this.setState({ fieldsValue: {} });
    form.resetFields();
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      fetchRuleLoading,
      enableLoading,
      disableLoading,
      nodeRule: { nodeRuleList = [], pagination = {} },
    } = this.props;
    const { actionNodeRuleId } = this.state;
    const columns = [
      // {
      //   title: intl.get('hadm.nodeRule.model.nodeRule.nodeRuleId').d('规则ID'),
      //   width: 100,
      //   dataIndex: 'nodeRuleId',
      // },
      {
        title: intl.get('hadm.nodeRule.model.nodeRule.ruleName').d('规则名称'),
        dataIndex: 'ruleName',
      },
      {
        title: intl.get('hzero.common.priority').d('优先级'),
        width: 100,
        dataIndex: 'priority',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        render: (text, record) => {
          const operators = [
            {
              key: 'disabel-enable',
              ele: (
                <span className="action-link">
                  {record.enabledFlag === 1 ? (
                    <a onClick={() => this.handleEnable(false, record)}>
                      {enableLoading && record.nodeRuleId === actionNodeRuleId ? (
                        <Icon type="loading" />
                      ) : (
                        intl.get('hzero.common.button.disable').d('禁用')
                      )}
                    </a>
                  ) : (
                    <a onClick={() => this.handleEnable(true, record)}>
                      {disableLoading && record.nodeRuleId === actionNodeRuleId ? (
                        <Icon type="loading" />
                      ) : (
                        intl.get('hzero.common.button.enable').d('启用')
                      )}
                    </a>
                  )}
                </span>
              ),
              len: 2,
              title:
                // eslint-disable-next-line no-nested-ternary
                record.enabledFlag === 1
                  ? enableLoading && record.nodeRuleId === actionNodeRuleId
                    ? null
                    : intl.get('hzero.common.button.disable').d('禁用')
                  : intl.get('hzero.common.button.enable').d('启用'),
            },
            {
              key: 'edit',
              ele: (
                <span className="action-link">
                  <a onClick={() => this.handleEdit(record)}>
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </a>
                </span>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <span className="action-link">
                  <Popconfirm
                    title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                    onConfirm={() => this.handleDelete(record)}
                  >
                    <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                  </Popconfirm>
                </span>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <>
        <Header title={intl.get('hadm.nodeRule.view.message.title.list').d('节点组规则')}>
          <Button icon="plus" type="primary" onClick={this.handleCreate}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm search={this.handleSearch} reset={this.handleResetSearch} />
          </div>
          <Table
            bordered
            rowKey="nodeRuleId"
            loading={fetchRuleLoading}
            dataSource={nodeRuleList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={pagination}
            onChange={this.handlePagination}
          />
        </Content>
      </>
    );
  }
}
