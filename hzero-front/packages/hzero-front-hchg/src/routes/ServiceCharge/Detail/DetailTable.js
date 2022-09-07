/**
 * DetailTable - 服务计费配置-详情列表
 * @date: 2019-8-28
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { dateRender } from 'utils/renderer';

import RuleDrawer from './RuleDrawer';
import ServiceListModal from './ServiceListModal';

const promptCode = 'hchg.serviceCharge.model.serviceCharge';

/**
 * 详情列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Boolean} isCreate - 是否为新建
 * @reactProps {string} groupStatus - 计费组状态
 * @reactProps {number} chargeGroupId - 计费组ID
 * @reactProps {object} rowSelection - 行选择配置
 * @reactProps {object} ruleDetail - 规则行详情
 * @reactProps {array} chargeScopeList - 服务范围列表
 * @reactProps {string} lovCode - 创建服务范围查询code
 * @reactProps {string} type - 计费类型
 * @reactProps {Boolean} loading - 查询计费规则列表加载标志
 * @reactProps {array} dataSource - 计费规则列表
 * @reactProps {object} pagination - 计费规则分页
 * @reactProps {Boolean} deleteChargeRuleLoading - 删除计费规则加载标志
 * @reactProps {Boolean} fetchRuleDetailLoading - 查询计费规则详情加载标志
 * @reactProps {Boolean} createChargeRuleLoading - 创建计费规则加载标志
 * @reactProps {Boolean} updateChargeRuleLoading - 更新计费规则加载标志
 * @reactProps {Boolean} chargeScopeLoading - 服务范围加载标志
 * @reactProps {array} chargeMethodTypes - 计费方式值集
 * @reactProps {array} chargeTypes - 计费类型值集
 * @reactProps {array} measureBasisTypes - 计量单位值集
 * @reactProps {array} chargeBasisTypes - 计费单位值集
 * @reactProps {array} intervalCycleTypes - 阶梯周期值集
 * @reactProps {array} intervalMeasureTypes - 阶梯单位值集
 * @reactProps {Function} fetchRuleDetail - 查询计费规则详情
 * @reactProps {Function} fetchPreList - 查询预付费规则
 * @reactProps {Function} fetchPostList - 查询后付费规则
 * @reactProps {Function} createChargeRule - 创建计费规则
 * @reactProps {Function} updateChargeRule - 更新计费规则
 * @reactProps {Function} deleteChargeRule - 删除计费规则
 * @reactProps {Function} createChargeScope - 创建服务范围
 * @reactProps {Function} deleteChargeScope - 删除计费范围
 * @reactProps {Function} queryChargeScope - 查询计费范围
 * @reactProps {Function} updateChargeRuleLineList - 更新阶梯行
 * @reactProps {Function} cleanData - 清空数据
 * @return React.element
 */

export default class DetailTable extends PureComponent {
  state = {
    isShowModal: false, // 是否显示计费规则侧滑
    isShowServiceList: false, // 是否显示服务列表弹窗
    currentChargeRule: {}, // 选中的计费规则行
    currentServiceRule: {}, // 选中的计费规则行
  };

  /**
   * 显示规则侧滑
   */
  @Bind()
  handleOpenDrawer(record = {}) {
    this.setState({
      isShowModal: true,
      currentChargeRule: record,
    });
  }

  /**
   * 关闭规则弹窗
   */
  @Bind()
  handleCloseDrawer() {
    const { cleanData = () => {} } = this.props;
    this.setState(
      {
        isShowModal: false,
        currentChargeRule: {},
      },
      () => {
        cleanData();
      }
    );
  }

  /**
   * 创建或修改计费规则
   * @param {string} type - 付费类型
   */
  @Bind()
  handleSaveChargeRule(type) {
    const { fetchPreList = () => {}, fetchPostList = () => {}, cleanData = () => {} } = this.props;
    this.setState(
      {
        isShowModal: false,
        currentChargeRule: {},
      },
      () => {
        if (type === 'PRE') {
          fetchPreList();
        } else {
          fetchPostList();
        }
        cleanData();
      }
    );
  }

  /**
   * 显示服务列表弹窗
   */
  @Bind()
  handleOpenModal(record = {}) {
    this.setState({
      isShowServiceList: true,
      currentServiceRule: record,
    });
  }

  /**
   * 关闭服务列表弹窗
   */
  @Bind()
  handleCloseModal() {
    const { cleanData = () => {} } = this.props;
    this.setState(
      {
        isShowServiceList: false,
        currentServiceRule: {},
      },
      () => {
        cleanData();
      }
    );
  }

  /**
   * 切换分页
   * @param {obejct} page - 分页
   */
  @Bind()
  handleChangePage(page = {}) {
    const { type, fetchPreList = () => {}, fetchPostList = () => {} } = this.props;
    if (type === 'PRE') {
      fetchPreList(page);
    } else {
      fetchPostList(page);
    }
  }

  render() {
    const {
      type,
      lovCode,
      dataSource,
      pagination,
      chargeGroupId,
      rowSelection = {},
      groupStatus,
      ruleDetail,
      chargeScopeList,
      chargeMethodTypes,
      chargeTypes,
      measureBasisTypes,
      chargeBasisTypes,
      intervalCycleTypes,
      intervalMeasureTypes,
      loading,
      deleteChargeRuleLoading,
      fetchRuleDetailLoading,
      createChargeRuleLoading,
      updateChargeRuleLoading,
      chargeScopeLoading,
      deleteChargeRule = () => {},
      fetchRuleDetail = () => {},
      createChargeRule = () => {},
      updateChargeRule = () => {},
      queryChargeScope = () => {},
      createChargeScope = () => {},
      deleteChargeScope = () => {},
      updateChargeRuleLineList = () => {},
    } = this.props;
    const { isShowModal, isShowServiceList, currentChargeRule, currentServiceRule } = this.state;
    const { selectedRowKeys } = rowSelection;
    const isPublished = groupStatus === 'PUBLISHED';
    const columns = [
      {
        title: intl.get(`${promptCode}.scope`).d('服务范围'),
        dataIndex: 'displayMask',
        fixed: 'left',
        width: 100,
        render: (_, record) => (
          <span className="action-link">
            <a onClick={() => this.handleOpenModal(record)}>
              {intl.get('hchg.serviceCharge.view.title.modal.serviceList').d('服务列表')}
            </a>
          </span>
        ),
      },
      {
        title: intl.get(`${promptCode}.priority`).d('优先级'),
        dataIndex: 'priority',
        width: 120,
      },
      {
        title: intl.get(`${promptCode}.ruleName`).d('说明'),
        dataIndex: 'ruleName',
        width: 120,
      },
      {
        title: intl.get(`${promptCode}.chargeMethod`).d('计费方式'),
        dataIndex: 'chargeMethodMeaning',
        width: 120,
      },
      {
        title: intl.get(`${promptCode}.chargeType`).d('计费类型'),
        dataIndex: 'chargeTypeMeaning',
        width: 120,
      },
      {
        title: intl.get(`${promptCode}.measureBasis`).d('计量单位'),
        dataIndex: 'measureBasisMeaning',
        width: 120,
      },
      {
        title: intl.get(`${promptCode}.quantity`).d('计量'),
        dataIndex: 'quantity',
        width: 120,
      },
      {
        title: intl.get(`${promptCode}.chargeBasis`).d('计费单位'),
        dataIndex: 'chargeBasisMeaning',
        width: 120,
      },
      {
        title: intl.get(`${promptCode}.charge`).d('计费'),
        dataIndex: 'charge',
        width: 120,
      },
      type === 'PRE' && {
        title: intl.get(`${promptCode}.discountLineId`).d('折扣行'),
        dataIndex: 'discountLineName',
        width: 120,
      },
      {
        title: intl.get(`${promptCode}.startDate`).d('生效日期从'),
        dataIndex: 'startDate',
        width: 150,
        render: dateRender,
      },
      {
        title: intl.get(`${promptCode}.endDate`).d('生效日期至'),
        dataIndex: 'endDate',
        width: 150,
        render: dateRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 110,
        key: 'edit',
        fixed: 'right',
        render: (_, record) => (
          <span className="action-link">
            <a onClick={() => this.handleOpenDrawer(record)}>
              {isPublished
                ? intl.get('hzero.common.button.detail').d('查看详情')
                : intl.get('hzero.common.button.edit').d('编辑')}
            </a>
          </span>
        ),
      },
    ].filter(Boolean);

    const drawerProps = {
      visible: isShowModal,
      isPublished,
      type,
      currentChargeRule,
      chargeGroupId,
      ruleDetail,
      chargeMethodTypes,
      chargeTypes,
      measureBasisTypes,
      chargeBasisTypes,
      intervalCycleTypes,
      intervalMeasureTypes,
      fetchRuleDetailLoading,
      createChargeRuleLoading,
      updateChargeRuleLoading,
      onCreate: createChargeRule,
      onEdit: updateChargeRule,
      onCancel: this.handleCloseDrawer,
      onFetchDetail: fetchRuleDetail,
      onOk: this.handleSaveChargeRule,
      onUpdateList: updateChargeRuleLineList,
    };
    const serviceListModalProps = {
      lovCode,
      isPublished,
      chargeGroupId,
      chargeScopeList,
      visible: isShowServiceList,
      currentServiceRule,
      onCancel: this.handleCloseModal,
      onCreate: createChargeScope,
      onDelete: deleteChargeScope,
      onFetch: queryChargeScope,
      loading: chargeScopeLoading,
    };
    return (
      <>
        {(groupStatus === 'CANCELLED' || groupStatus === 'NEW') && (
          <div className="table-list-operator" style={{ textAlign: 'right' }}>
            <Button
              loading={deleteChargeRuleLoading}
              disabled={!selectedRowKeys.length}
              onClick={() => deleteChargeRule(type)}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </Button>
            <Button type="primary" onClick={() => this.handleOpenDrawer({})}>
              {intl.get('hzero.common.button.create').d('新建')}
            </Button>
          </div>
        )}
        <Table
          bordered
          loading={loading}
          rowKey="chargeRuleId"
          columns={columns}
          dataSource={dataSource}
          rowSelection={rowSelection}
          scroll={{ x: tableScrollWidth(columns) }}
          pagination={pagination}
          onChange={(page) => this.handleChangePage(page)}
        />
        <ServiceListModal {...serviceListModalProps} />
        <RuleDrawer {...drawerProps} />
      </>
    );
  }
}
