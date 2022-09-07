/**
 * InstanceDrawer - 新建/编辑实例弹窗
 * @date: 2019/8/27
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Modal, Form, Button, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';
import intl from 'utils/intl';
import { DETAIL_DEFAULT_CLASSNAME } from 'utils/constants';
import InstanceForm from './Form';
import ParamMapping from './ParamMapping';

/**
 * 新建/编辑实例弹窗
 * @extends {Component} - React.Component
 * @reactProps {Boolean} visible - 是否可见
 * @reactProps {object} currentInstance - 当前选中行
 * @reactProps {Boolean} confirmLoading - 保存中标识
 * @reactProps {Object} instanceDetail - 实例详情
 * @reactProps {Boolean} fetchInstanceDetailLoading - 请求实例详情加载标志
 * @reactProps {Boolean} refreshInstanceLoading - 刷新实例配置加载标志
 * @reactProps {Boolean} isCreate - 是否为新建
 * @reactProps {Function} onRefresh - 刷新实例配置
 * @reactProps {Function} onFetchDetail - 查询实例详情
 * @reactProps {Function} onCancel - 关闭弹窗
 * @reactProps {Function} onSaveInstance - 新建/更新实例
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class InstanceDrawer extends Component {
  getSnapshotBeforeUpdate(prevProps) {
    const { visible, currentInstance = {} } = this.props;
    const { applicationInstId } = currentInstance;

    return (
      visible &&
      !isUndefined(applicationInstId) &&
      applicationInstId !== (prevProps.currentInstance || {}).applicationInstId
    );
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      this.handleFetchInstanceDetail();
    }
  }

  /**
   * 查询实例详情
   */
  @Bind()
  handleFetchInstanceDetail() {
    const { onFetchDetail = () => {}, currentInstance = {} } = this.props;
    onFetchDetail(currentInstance.applicationInstId);
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
    this.instanceForm = (ref.props || {}).form;
  }

  /**
   * 创建应用实例
   */
  @Bind()
  handleSave() {
    const { isCreate } = this.props;
    if (this.instanceForm) {
      const mappingClass = this.instanceForm.getCurrentCode();
      this.instanceForm.props.form.validateFields((err, values) => {
        if (!err) {
          const { onSaveInstance = () => {} } = this.props;
          const payload = isCreate ? { ...values } : { ...values, mappingClass };
          onSaveInstance(payload);
        }
      });
    }
  }

  render() {
    const {
      visible,
      isCreate,
      interfaceId,
      composePolicy,
      confirmLoading = false,
      instanceDetail,
      currentInstance = {},
      fetchInstanceDetailLoading,
      refreshInstanceLoading,
      fetchMappingClassLoading,
      testMappingClassLoading,
      onEditLine = () => {},
      onRefresh = () => {},
      onFetchMappingClass = () => {},
      onTestMappingClass = () => {},
    } = this.props;
    const formProps = {
      instanceDetail,
      isCreate,
      composePolicy,
      fetchMappingClassLoading,
      testMappingClassLoading,
      onFetchMappingClass,
      onTestMappingClass,
      wrappedComponentRef: (form) => {
        this.instanceForm = form;
      },
    };
    const paramProps = {
      interfaceId,
      instanceDetail,
      onEditLine,
    };
    return (
      <Modal
        destroyOnClose
        width={1000}
        title={
          isCreate
            ? intl.get('hitf.typeDefinition.view.message.title.instance.create').d('新建实例配置')
            : intl.get('hitf.typeDefinition.view.message.title.instance.edit').d('编辑实例配置')
        }
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={visible}
        onOk={this.handleSave}
        onCancel={this.handleCancel}
        confirmLoading={confirmLoading}
      >
        <Spin spinning={fetchInstanceDetailLoading} wrapperClassName={DETAIL_DEFAULT_CLASSNAME}>
          <InstanceForm {...formProps} />
          <div className="table-operator">
            <Button
              type="primary"
              loading={refreshInstanceLoading}
              disabled={isEmpty(currentInstance)}
              onClick={() => onRefresh(currentInstance.applicationInstId)}
            >
              {intl.get('hitf.typeDefinition.view.message.title.button.refresh').d('刷新参数配置')}
            </Button>
          </div>
          <ParamMapping {...paramProps} />
        </Spin>
      </Modal>
    );
  }
}
