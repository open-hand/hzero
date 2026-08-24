/*
 * DetailForm - 限流明细表单
 * @date: 2018/10/13 11:08:03
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 *
 * @updateAt 2019-11-19
 * @update 更新维度编辑方式
 * @description
 *  1. 维度变为多选;
 *  2. 维度减少后, 需要调用接口判断维度是否能删除;
 *  3. 维度表格列 根据选择的维度
 */

import React from 'react';
import { Modal, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import EditFrom from './EditForm';
import DimensionConfigsEditForm from './DimensionConfigsEditForm';

/**
 * Zuul限流配置查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} handleSearch  搜索
 * @reactProps {Function} handleFormReset  重置表单
 * @reactProps {Function} toggleForm  展开查询条件
 * @reactProps {Function} renderAdvancedForm 渲染所有查询条件
 * @reactProps {Function} renderSimpleForm 渲染缩略查询条件
 * @return React.element
 */

@formatterCollections({
  code: 'hadm.zuulRateLimit',
})
export default class DetailForm extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      // 给维度规则编辑 使用
      dimensions: [], // 已经选择的维度
      dimensionEditModalVisible: false, // 模态框显示
      dimensionEditIsCreate: true, // 是否是新建
      dimensionConfig: {}, // 编辑的数据
    };

    this.dimensionConfigsEditFormInteractedRef = React.createRef();
    this.editFormInteractedRef = React.createRef();
  }

  @Bind()
  handleOk() {
    const { onOk = e => e } = this.props;
    if (this.editFormInteractedRef.current) {
      this.editFormInteractedRef.current.getValidateData().then(fieldsValue => {
        onOk(fieldsValue);
      });
    }
  }

  /**
   * 新建维度规则
   */
  @Bind()
  handleDimensionAdd(dimensions) {
    this.setState({
      dimensions,
      dimensionEditModalVisible: true,
      dimensionEditIsCreate: true,
      dimensionConfig: {},
    });
  }

  /**
   * 编辑维度规则
   */
  @Bind()
  handleDimensionEdit(dimensions, record) {
    this.setState({
      dimensions,
      dimensionEditModalVisible: true,
      dimensionEditIsCreate: false,
      dimensionConfig: {},
    });
    const { queryDimensionConfigsDetail } = this.props;
    queryDimensionConfigsDetail({
      rateLimitDimId: record.rateLimitDimId,
    }).then(res => {
      if (res) {
        this.setState({
          dimensionConfig: res,
        });
      }
    });
  }

  /**
   * 删除规则信息
   */
  @Bind()
  handleDimensionDelete(record) {
    // TODO: 提示用户是否删除(在表格中)
    const { deleteDimensionConfigs } = this.props;
    deleteDimensionConfigs({ dimensionConfig: record }).then(res => {
      if (res) {
        // 新建/更新维度成功, 重新查询维度信息
        const { refreshDimensionConfigs } = this.props;
        refreshDimensionConfigs();
        this.setState({
          dimensions: [], // 已经选择的维度
          dimensionEditModalVisible: false, // 模态框显示
          dimensionEditIsCreate: true, // 是否是新建
          dimensionConfig: {}, // 编辑的数据
        });
      }
    });
  }

  /**
   * Table 分页信息改变
   */
  @Bind()
  handleDimensionTableChange(page, filter, sort) {
    const { queryDimensionConfigs, initData } = this.props;
    queryDimensionConfigs({
      rateLimitLineId: initData.rateLimitLineId,
      pagination: { page, sort },
    });
  }

  /**
   * 保存规则信息
   */
  @Bind()
  handleDimensionSave() {
    if (this.dimensionConfigsEditFormInteractedRef.current) {
      const { getValidateData } = this.dimensionConfigsEditFormInteractedRef.current;
      getValidateData().then(
        values => {
          const {
            insertDimensionConfigs,
            updateDimensionConfigs,
            rateLimitId,
            initData,
          } = this.props;
          const { dimensionEditIsCreate, dimensionConfig } = this.state;
          const payload = {
            dimensionConfig: {
              rateLimitId,
              rateLimitLineId: initData.rateLimitLineId,
              ...dimensionConfig,
              rateLimitLine: initData,
              ...values,
            },
          };
          (dimensionEditIsCreate
            ? insertDimensionConfigs(payload)
            : updateDimensionConfigs(payload)
          ).then(res => {
            if (res) {
              // 新建/更新维度成功, 重新查询维度信息
              const { refreshDimensionConfigs } = this.props;
              refreshDimensionConfigs();
              this.setState({
                dimensions: [], // 已经选择的维度
                dimensionEditModalVisible: false, // 模态框显示
                dimensionEditIsCreate: true, // 是否是新建
                dimensionConfig: {}, // 编辑的数据
              });
            }
          });
        },
        () => {
          // 表单校验失败, 什么都不做
        }
      );
    }
  }

  /**
   * 关闭规则编辑模态框
   */
  @Bind()
  handleDimensionClose() {
    this.setState({
      dimensions: [], // 已经选择的维度
      dimensionEditModalVisible: false, // 模态框显示
      dimensionEditIsCreate: true, // 是否是新建
      dimensionConfig: {}, // 编辑的数据
    });
  }

  /**
   * 检查 维度是否可以编辑
   */
  dimensionAllowEdit() {}

  render() {
    const {
      initData,
      loading,
      modalVisible,
      onCancel,
      dimensionTypes,
      fetchLineDetailLoading = false,
      dimensionConfigsDataSource,
      dimensionConfigsPagination,
      dimensionAllowChange,
      queryDimensionConfigsLoading = false,
      queryGateWayRateLimitDimensionAllowChangeLoading = false,
      insertDimensionConfigsLoading = false,
      updateDimensionConfigsLoading = false,
      deleteDimensionConfigsLoading,
      queryDimensionConfigsDetailLoading = false,
      match,
    } = this.props;
    const {
      dimensions,
      dimensionEditIsCreate,
      dimensionEditModalVisible,
      dimensionConfig,
    } = this.state;
    const isCreate = initData.rateLimitLineId === undefined;
    return (
      <Modal
        destroyOnClose
        title={
          isCreate
            ? intl.get(`hadm.zuulRateLimit.view.message.detailAdd`).d('创建限流方式')
            : intl.get(`hadm.zuulRateLimit.view.message.detailEdit`).d('编辑限流方式')
        }
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        confirmLoading={loading}
        visible={modalVisible}
        onOk={this.handleOk}
        onCancel={onCancel}
        width={1000}
      >
        <Spin
          spinning={
            fetchLineDetailLoading ||
            queryDimensionConfigsLoading ||
            queryGateWayRateLimitDimensionAllowChangeLoading
          }
        >
          <EditFrom
            initData={initData}
            dimensionTypes={dimensionTypes}
            match={match}
            onRecordAdd={this.handleDimensionAdd}
            onRecordEdit={this.handleDimensionEdit}
            onRecordDelete={this.handleDimensionDelete}
            onTableChange={this.handleDimensionTableChange}
            dimensionConfigsDataSource={dimensionConfigsDataSource}
            dimensionConfigsPagination={dimensionConfigsPagination}
            dimensionAllowChange={dimensionAllowChange}
            isCreate={isCreate}
            interactedRef={this.editFormInteractedRef}
            deleteDimensionConfigsLoading={deleteDimensionConfigsLoading}
          />
          <Modal
            destroyOnClose
            title={
              dimensionEditIsCreate
                ? intl.get('hadm.zuulRateLimit.view.title.dimensionConfigsCreate').d('新建规则')
                : intl.get('hadm.zuulRateLimit.view.title.dimensionConfigsEdit').d('编辑规则')
            }
            visible={dimensionEditModalVisible}
            onOk={this.handleDimensionSave}
            onCancel={this.handleDimensionClose}
            confirmLoading={insertDimensionConfigsLoading || updateDimensionConfigsLoading}
          >
            <Spin spinning={queryDimensionConfigsDetailLoading}>
              <DimensionConfigsEditForm
                dimensionTypes={dimensionTypes}
                dimensions={dimensions}
                isCreate={dimensionEditIsCreate}
                dimensionConfig={dimensionConfig}
                interactedRef={this.dimensionConfigsEditFormInteractedRef}
                rateLimitLine={initData}
              />
            </Spin>
          </Modal>
        </Spin>
      </Modal>
    );
  }
}
