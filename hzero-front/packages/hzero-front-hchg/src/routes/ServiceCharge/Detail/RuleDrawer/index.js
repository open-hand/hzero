/**
 * RuleDrawer - 新建/编辑/查询计费规则侧滑
 * @date: 2019/9/5
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Modal, Form, Spin, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import intl from 'utils/intl';
import { getEditTableData } from 'utils/utils';
import { DETAIL_DEFAULT_CLASSNAME, DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import notification from 'utils/notification';

import RuleForm from './RuleForm';
import RuleTable from './RuleTable';

/**
 * 新建/编辑实例弹窗
 * @extends {Component} - React.Component
 * @reactProps {Boolean} visible - 是否可见
 * @reactProps {Boolean} isPublished - 计费组是否已发布
 * @reactProps {string} type - 付费类型
 * @reactProps {Object} currentChargeRule - 当前选中规则行
 * @reactProps {number} chargeGroupId - 计费组ID
 * @reactProps {object} ruleDetail - 计费规则详情
 * @reactProps {array} chargeMethodTypes - 计费方式值集
 * @reactProps {array} chargeTypes - 计费类型值集
 * @reactProps {array} measureBasisTypes - 计量单位值集
 * @reactProps {array} chargeBasisTypes - 计费单位值集
 * @reactProps {array} intervalCycleTypes - 阶梯周期值集
 * @reactProps {array} intervalMeasureTypes - 阶梯单位值集
 * @reactProps {Boolean} fetchRuleDetailLoading - 查询计费规则详情加载标志
 * @reactProps {Boolean} createChargeRuleLoading - 创建计费规则加载标志
 * @reactProps {Boolean} updateChargeRuleLoading - 更新计费规则加载标志
 * @reactProps {function} onCreate - 创建计费规则
 * @reactProps {function} onEdit - 编辑计费规则
 * @reactProps {function} onCancel - 关闭侧滑
 * @reactProps {function} onFetchDetail - 查询计费规则
 * @reactProps {function} onOk - 保存计费规则
 * @reactProps {function} onUpdateList - 更新规则行
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class RuleDrawer extends Component {
  state = {
    chargeType: null,
    chargeMethod: null,
  };

  getSnapshotBeforeUpdate(prevProps) {
    const { visible, currentChargeRule } = this.props;
    const { chargeRuleId } = currentChargeRule;
    return (
      visible &&
      !isEmpty(chargeRuleId) &&
      chargeRuleId !== (prevProps.currentChargeRule || {}).chargeRuleId
    );
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      this.handleFetchRuleDetail();
    }
  }

  // 查询实例详情
  @Bind()
  handleFetchRuleDetail() {
    const { onFetchDetail = () => {}, currentChargeRule = {} } = this.props;
    onFetchDetail(currentChargeRule.chargeRuleId);
  }

  /**
   * 关闭侧滑
   */
  @Bind()
  handleCancel() {
    const { onCancel = () => {} } = this.props;
    onCancel();
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.ruleForm = (ref.props || {}).form;
  }

  /**
   * 创建/更新规则
   */
  @Bind()
  handleSave() {
    const { validateFields = (e) => e } = this.ruleForm.props.form;
    const {
      onCreate = () => {},
      onEdit = () => {},
      onOk = () => {},
      currentChargeRule = {},
      type,
      chargeGroupId,
      ruleDetail = {},
      createChargeRuleLoading,
      updateChargeRuleLoading,
    } = this.props;
    if (createChargeRuleLoading || updateChargeRuleLoading) return;
    const { chargeRuleLineList = [], ...rest } = ruleDetail;
    // 找到保存过的被删除了的数据，因为getEditTableData方法会把_status非固定两种以外的数据排除
    const deletedLines = chargeRuleLineList.filter((line) => line._status === 'delete');
    let nextList = getEditTableData(chargeRuleLineList, ['chargeLineId']);
    nextList = nextList.concat(deletedLines);
    validateFields((err, values) => {
      if (!err) {
        const nextValues = { ...values };
        nextValues.startDate =
          nextValues.startDate && nextValues.startDate.format(DEFAULT_DATETIME_FORMAT);
        nextValues.endDate =
          nextValues.endDate && nextValues.endDate.format(DEFAULT_DATETIME_FORMAT);
        let payload = {
          ...nextValues,
          chargeGroupId,
          chargeRuleLineList: nextList,
          paymentMethod: type,
        };
        // 创建
        if (isEmpty(currentChargeRule)) {
          onCreate(payload).then((res) => {
            if (res) {
              notification.success();
              onOk(type);
            }
          });
          // 更新
        } else {
          payload = {
            ...rest,
            ...payload,
          };
          onEdit(payload).then((res) => {
            if (res) {
              notification.success();
              onOk(type);
            }
          });
        }
      }
    });
  }

  @Bind()
  setChargeInfo(chargeMethod, chargeType) {
    this.setState({
      chargeMethod,
      chargeType,
    });
  }

  render() {
    const {
      visible,
      ruleDetail = {},
      fetchRuleDetailLoading,
      createChargeRuleLoading,
      updateChargeRuleLoading,
      chargeMethodTypes,
      chargeTypes,
      measureBasisTypes,
      chargeBasisTypes,
      intervalCycleTypes,
      intervalMeasureTypes,
      currentChargeRule,
      chargeGroupId,
      type,
      isPublished,
      onUpdateList = () => {},
    } = this.props;
    const { chargeType, chargeMethod } = this.state;
    const isCreate = isEmpty(currentChargeRule);
    const formProps = {
      type,
      isCreate,
      isPublished,
      ruleDetail,
      chargeGroupId,
      chargeMethodTypes,
      chargeTypes,
      measureBasisTypes,
      chargeBasisTypes,
      intervalCycleTypes,
      intervalMeasureTypes,
      onChargeTypeChange: this.setChargeType,
      onChargeInfoChange: this.setChargeInfo,
      wrappedComponentRef: (form) => {
        this.ruleForm = form;
      },
    };
    const currentChargeType = chargeType || ruleDetail.chargeType;
    const currentChargeMethod = chargeMethod || ruleDetail.chargeMethod;
    const tableProps = {
      isPublished,
      ruleDetail,
      chargeGroupId,
      currentChargeRule,
      currentChargeMethod,
      isCreate,
      onUpdateList,
    };
    const modalProps = {
      destroyOnClose: true,
      width: 750,
      /* eslint-disable no-nested-ternary */
      title: isPublished
        ? intl.get('hchg.serviceCharge.view.title.rule.view').d('查看计费规则')
        : isCreate
        ? intl.get('hchg.serviceCharge.view.title.rule.create').d('新建计费规则')
        : intl.get('hchg.serviceCharge.view.title.rule.edit').d('编辑计费规则'),
      wrapClassName: 'ant-modal-sidebar-right',
      transitionName: 'move-right',
      visible,
      onOk: this.handleSave,
      onCancel: this.handleCancel,
      confirmLoading: createChargeRuleLoading || updateChargeRuleLoading,
    };
    if (isPublished) {
      modalProps.footer = [
        <Button key="cancel" onClick={this.handleCancel}>
          {intl.get('hzero.common.button.close').d('关闭')}
        </Button>,
      ];
    }
    return (
      <Modal {...modalProps}>
        <Spin spinning={fetchRuleDetailLoading} wrapperClassName={DETAIL_DEFAULT_CLASSNAME}>
          <RuleForm {...formProps} />
          {currentChargeType === 'INTERVAL' && <RuleTable {...tableProps} />}
        </Spin>
      </Modal>
    );
  }
}
