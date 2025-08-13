/**
 * Storage 文件存储配置页面
 * @date: 2018-7-25
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Radio } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import { Content, Header } from 'components/Page';
import { Button as ButtonPermission, FormItem } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { isTenantRoleLevel } from 'utils/utils';

import StorageDrawer from './StorageDrawer';
import StorageList from './StorageList';

const RadioGroup = Radio.Group;

@formatterCollections({ code: 'hfile.storage' })
@Form.create({ fieldNameProp: null })
@connect(({ loading, storage, user }) => ({
  fetchDefaultLoading: loading.effects['storage/fetchDefaultStorage'],
  fetchStorageLoading: loading.effects['storage/fetchStorage'],
  saveLoading: loading.effects['storage/updateStorage'],
  storage,
  user,
}))
export default class Storage extends React.Component {
  state = {
    editVisible: false,
    editData: {},
    storageType: '', // 当前文件存储类型
  };

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'storage/init' });
    this.fetchDefaultStorage();
  }

  /**
   * fetchDefaultStorage - 获取默认配置
   */
  @Bind()
  fetchDefaultStorage(params = {}) {
    const {
      dispatch,
      storage: { tenantId },
    } = this.props;
    dispatch({
      type: 'storage/fetchDefaultStorage',
      payload: { tenantId, ...params },
    }).then(res => {
      if (res && res.content) {
        const [defaultStorage = {}] = res.content || [];
        const storageType = `${defaultStorage.storageType || '1'}`;
        this.setState({ storageType });
      }
    });
  }

  /**
   * fetchStorage - 获取文件
   * @param {string} params.text - 选择的租户值
   * @param {object} params.record - 选择的租户行数据
   */
  @Bind()
  fetchStorage(params = {}) {
    const {
      dispatch,
      storage: { tenantId },
    } = this.props;
    const { storageType } = this.state;
    dispatch({
      type: 'storage/fetchStorage',
      payload: { tenantId, storageType, ...params },
    });
  }

  /**
   * handleChangeOrg - 租户切换
   * @param {string} text - 选择的租户值
   * @param {object} record - 选择的租户行数据
   */
  @Bind()
  handleChangeOrg(text, record) {
    const { dispatch, form } = this.props;
    form.resetFields();
    dispatch({
      type: 'storage/updateState',
      payload: { tenantId: record.tenantId },
    });
    this.fetchDefaultStorage({
      tenantId: record.tenantId,
    });
  }

  /**
   * handleSelectStorage - 监听不同的存储类型切换
   * @param {object} e - 事件对象
   */
  @Bind()
  handleSelectStorage(e) {
    const { dispatch, form } = this.props;
    form.resetFields();
    dispatch({
      type: 'storage/updateState',
      payload: {
        storageDataList: [],
      },
    });
    this.setState({ storageType: e.target.value });
    this.fetchStorage({ storageType: e.target.value });
  }

  @Bind()
  handleCancel() {
    this.setState({ editVisible: false, editData: {} });
  }

  @Bind()
  handleEdit(record = {}) {
    this.setState({ editVisible: true, editData: record });
  }

  @Bind()
  handleCreate() {
    this.setState({ editVisible: true, editData: {} });
  }

  @Bind()
  handleDelete(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'storage/deleteStorage',
      payload: record,
    }).then(res => {
      if (res) {
        notification.success();
        this.fetchStorage({ storageType: record.storageType });
      }
    });
  }

  /**
   * 保存文件服务配置
   */
  @Bind()
  handleSaveStorage(fieldsValue) {
    const { dispatch, storage } = this.props;
    const { tenantId } = storage;
    const { storageConfigId, endPoint, endPointBefore, ...others } = fieldsValue;
    const params = {
      ...others,
      endPoint: `${others.storageType === '3' ? endPointBefore : ''}${endPoint}`,
      storageConfigId: storageConfigId || '',
      accessControl: others.accessControl ? others.accessControl[1] : '',
      tenantId,
    };
    dispatch({
      type: 'storage/updateStorage',
      payload: params,
    }).then(res => {
      if (res) {
        notification.success();
        this.handleCancel();
        this.fetchStorage({ storageType: fieldsValue.storageType });
      }
    });
  }

  render() {
    const {
      form,
      match: { path },
      fetchDefaultLoading = false,
      fetchStorageLoading = false,
      saveLoading = false,
      user: {
        currentUser: { tenantName },
      },
      storage: {
        storageDataList = [],
        serverProviderList = [],
        prefixStrategyList = [],
        microsoftEndpointList = [],
        radioTypeList = [],
      },
    } = this.props;
    const { editVisible, editData = {}, storageType } = this.state;
    const storageListProps = {
      path,
      serverProviderList,
      storageDataList,
      type: storageType,
      loading: fetchDefaultLoading || fetchStorageLoading,
      onEdit: this.handleEdit,
      onDelete: this.handleDelete,
    };
    const { meaning = '' } = radioTypeList.find(item => storageType === item.value) || {};
    const StorageDrawerProps = {
      serverProviderList,
      prefixStrategyList,
      microsoftEndpointList,
      type: storageType,
      title:
        editData.storageConfigId !== undefined
          ? intl
              .get('hfile.storage.view.title.storageType.edit', { type: meaning })
              .d(`编辑${meaning}`)
          : intl
              .get('hfile.storage.view.title.storageType.create', { type: meaning })
              .d(`新建${meaning}`),
      loading: saveLoading,
      modalVisible: editVisible,
      initData: editData,
      onOk: this.handleSaveStorage,
      onCancel: this.handleCancel,
    };

    return (
      <>
        <Header title={intl.get('hfile.storage.view.message.title').d('文件存储配置')}>
          <ButtonPermission
            type="primary"
            icon="plus"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '文件存储配置-新建',
              },
            ]}
            onClick={this.handleCreate}
            disabled={fetchDefaultLoading}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission icon="sync" onClick={this.fetchStorage}>
            {intl.get('hzero.common.button.refresh').d('刷新')}
          </ButtonPermission>
          {!isTenantRoleLevel() && (
            <Lov
              className="page-head-operation"
              style={{ width: '200px' }}
              value="0"
              allowClear={false}
              textValue={tenantName}
              code="HPFM.TENANT"
              onChange={(text, record) => {
                this.handleChangeOrg(text, record);
              }}
            />
          )}
        </Header>
        <Content>
          <Form>
            <FormItem
              label={intl.get('hfile.storage.model.storage.storageType').d('存储类型')}
              labelCol={{ span: 2 }}
              wrapperCol={{ span: 20 }}
            >
              {form.getFieldDecorator('storageType', {
                initialValue: storageType,
              })(
                <RadioGroup onChange={this.handleSelectStorage}>
                  {radioTypeList.map(item => (
                    <Radio value={item.value}>{item.meaning}</Radio>
                  ))}
                </RadioGroup>
              )}
            </FormItem>
          </Form>
          <StorageList {...storageListProps} />
          {editVisible && <StorageDrawer {...StorageDrawerProps} />}
        </Content>
      </>
    );
  }
}
