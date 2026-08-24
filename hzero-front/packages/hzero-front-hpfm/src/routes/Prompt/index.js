/**
 * Prompt 平台多语言
 * @date: 2018-6-22
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Table, Popconfirm, Tag } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import queryString from 'querystring';

import { Content, Header } from 'components/Page';
import Lov from 'components/Lov';
import { Button as ButtonPermission } from 'components/Permission';

import { getCurrentOrganizationId, isTenantRoleLevel, getCurrentLanguage } from 'utils/utils';
import { VERSION_IS_OP, HZERO_PLATFORM } from 'utils/config';
import notification from 'utils/notification';
import intl from 'utils/intl';
import { openTab } from 'utils/menuTab';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';

import PromptDrawer from './PromptDrawer';
import FilterForm from './FilterForm';

const currentLanguage = getCurrentLanguage();

@connect(({ loading, prompt, user }) => ({
  prompt,
  user,
  fetchPromptLoading: loading.effects['prompt/fetchPromptList'],
  updatePromptLoading: loading.effects['prompt/updatePrompt'],
  saving: loading.effects['prompt/createPrompt'],
  fetchPromptDetailLoading: loading.effects['prompt/fetchPromptDetail'],
  isSiteFlag: !isTenantRoleLevel(),
  organizationId: getCurrentOrganizationId,
}))
@formatterCollections({ code: 'hpfm.prompt' })
export default class Prompt extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      tenantId: getCurrentOrganizationId(),
      modalVisible: false,
      promptFormData: {},
    };
  }

  form;

  componentDidMount() {
    this.fetchPromptList();
  }

  /**
   * 获取查询表单组件this对象
   * @param {object} ref - 查询表单组件this
   */
  @Bind()
  handleBindRef(ref) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 获取多语言数据
   * @param {object} params
   */
  fetchPromptList(params = {}) {
    const {
      dispatch,
      prompt: { pagination = {} },
    } = this.props;
    const { tenantId } = this.state;
    const filterValue = this.form === undefined ? {} : this.form.getFieldsValue();
    dispatch({
      type: 'prompt/fetchPromptList',
      payload: { ...filterValue, page: pagination, tenantId, ...params },
    });
  }

  /**
   * 查询多语言
   */
  @Bind()
  handleSearch() {
    this.fetchPromptList({ page: {} });
  }

  /**
   * 分页
   */
  @Bind()
  handleStandardTableChange(pagination) {
    this.fetchPromptList({
      page: pagination,
    });
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showModal() {
    const { dispatch } = this.props;
    this.setState({
      promptFormData: {},
    });
    dispatch({
      type: 'prompt/fetchLanguages',
    }).then(() => {
      this.handleModalVisible(true);
    });
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    const { saving = false } = this.props;
    if (!saving) {
      this.handleModalVisible(false);
    }
  }

  /**
   * 编辑打开模态框
   */
  @Bind()
  handleUpdatePrompt(record) {
    this.setState({
      promptFormData: record,
    });
    const { dispatch } = this.props;
    dispatch({
      type: 'prompt/fetchLanguages',
    }).then(() => {
      dispatch({
        type: 'prompt/fetchPromptDetail',
        payload: {
          tenantId: record.tenantId,
          promptKey: record.promptKey,
          promptCode: record.promptCode,
          lang: record.lang,
        },
      });
      this.handleModalVisible(true);
      dispatch({
        type: 'prompt/update',
        payload: {
          record: {},
        },
      });
    });
  }

  /**
   * 保存多语言
   * @param {object} fieldsValue - 编辑或新增的数据
   */
  @Bind()
  handleSavePrompt(fieldsValue, promptDetail) {
    const { dispatch, organizationId, isSiteFlag } = this.props;
    const { promptFormData, tenantId } = this.state;
    const { promptConfigs } = fieldsValue;
    let _promptConfigs;
    if (promptConfigs) {
      _promptConfigs = this.removeNull(promptConfigs);
    }
    const params =
      promptFormData.promptCode !== undefined
        ? {
            ...promptFormData,
            ...fieldsValue,
            lang: promptFormData.lang,
            tenantId: promptDetail.tenantId,
            promptConfigs: _promptConfigs,
          }
        : {
            ...fieldsValue,
            promptConfigs: _promptConfigs,
            tenantId: isSiteFlag ? tenantId : organizationId,
          };
    dispatch({
      type: promptFormData.promptCode !== undefined ? 'prompt/updatePrompt' : 'prompt/createPrompt',
      payload: params,
    }).then((res) => {
      if (res) {
        notification.success();
        this.hideModal();
        this.fetchPromptList();
      }
    });
  }

  @Bind()
  removeNull(obj) {
    const _obj = { ...obj };
    Object.keys(_obj).forEach((item) => {
      if (!_obj[item]) {
        delete _obj[item];
      }
    });
    return _obj;
  }

  /**
   * 删除
   */
  @Bind()
  handleDeletePrompt(record) {
    const { dispatch } = this.props;
    const { tenantId } = this.state;
    if (tenantId !== record.tenantId) {
      notification.warning({
        message: intl.get('hpfm.prompt.view.message.delete').d('请勿删除平台数据'),
      });
      return;
    }
    dispatch({
      type: 'prompt/deletePrompt',
      payload: { ...record, tenantId: record.tenantId || tenantId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchPromptList();
      }
    });
  }

  /**
   * 改变租户
   */
  @Bind()
  handleChangeOrg(text, record) {
    this.setState({ tenantId: record.tenantId });
    this.fetchPromptList({ tenantId: record.tenantId });
  }

  @Bind()
  handleRefresh() {
    const { dispatch } = this.props;
    dispatch({
      type: 'prompt/refresh',
    }).then((res) => {
      if (res) {
        notification.success();
      }
    });
  }

  /**
   * 导入多语言
   */
  @Bind()
  handleImport() {
    openTab({
      key: `/hpfm/prompt/import-data/HPFM.PROMPT`,
      title: 'hzero.common.button.import',
      search: queryString.stringify({
        action: 'hzero.common.button.import',
        prefixPatch: HZERO_PLATFORM,
      }),
    });
  }

  render() {
    const {
      isSiteFlag,
      match,
      fetchPromptLoading,
      saving,
      updatePromptLoading,
      fetchPromptDetailLoading,
      user: {
        currentUser: { tenantName },
      },
      prompt: { promptList = [], pagination = {}, languageList = [], promptDetail = {} },
    } = this.props;
    const { promptFormData, modalVisible, tenantId } = this.state;
    const promptColumns = [
      {
        title: intl.get('hpfm.prompt.model.prompt.promptKey').d('模板代码'),
        width: 200,
        dataIndex: 'promptKey',
      },
      {
        title: intl.get('hpfm.prompt.model.prompt.promptCode').d('代码'),
        width: 400,
        dataIndex: 'promptCode',
      },
      {
        title: intl.get('hpfm.prompt.model.prompt.description').d('描述'),
        dataIndex: 'description',
      },
      !isSiteFlag && {
        title: intl.get('hzero.common.source').d('来源'),
        width: 100,
        render: (_, record) =>
          tenantId === record.tenantId ? (
            <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
          ) : (
            <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        fixed: 'right',
        dataIndex: 'edit',
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '平台多语言(平台)-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.handleUpdatePrompt(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          if (isSiteFlag) {
            operators.push({
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => {
                    this.handleDeletePrompt(record);
                  }}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.delete`,
                        type: 'button',
                        meaning: '平台多语言(平台)-删除',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            });
          } else if (tenantId === record.tenantId) {
            operators.push({
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => {
                    this.handleDeletePrompt(record);
                  }}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.orgDelete`,
                        type: 'button',
                        meaning: '平台多语言(租户)-删除',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            });
          }
          return operatorRender(operators, record);
        },
      },
    ].filter(Boolean);
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.prompt.view.message.title').d('平台多语言')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '平台多语言(平台)-新建',
              },
            ]}
            onClick={this.showModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.Import`,
                type: 'button',
                meaning: '平台多语言-导入',
              },
            ]}
            icon="to-top"
            onClick={this.handleImport.bind(this)}
          >
            {intl.get('hzero.common.button.import').d('导入')}
          </ButtonPermission>
          {(isSiteFlag || VERSION_IS_OP) && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${match.path}.button.refreshCache`,
                  type: 'button',
                  meaning: '平台多语言-刷新缓存',
                },
              ]}
              icon="sync"
              onClick={this.handleRefresh}
            >
              {intl.get('hzero.common.button.refreshCache').d('刷新缓存')}
            </ButtonPermission>
          )}
          {isSiteFlag && (
            <Lov
              allowClear={false}
              style={{ width: '200px', marginLeft: '8px' }}
              textValue={tenantName}
              value={tenantId}
              code="HPFM.TENANT"
              onChange={(text, record) => this.handleChangeOrg(text, record)}
            />
          )}
        </Header>
        <Content>
          <FilterForm
            onSearch={this.handleSearch}
            onRef={this.handleBindRef}
            languageList={languageList}
          />
          <Table
            bordered
            rowKey="promptId"
            loading={fetchPromptLoading}
            dataSource={promptList}
            columns={promptColumns}
            pagination={pagination}
            onChange={this.handleStandardTableChange}
          />
          <PromptDrawer
            title={
              promptFormData.promptCode
                ? intl.get('hpfm.prompt.view.message.edit').d('编辑多语言')
                : intl.get('hpfm.prompt.view.message.create').d('新建多语言')
            }
            loading={saving}
            updatePromptLoading={updatePromptLoading}
            fetchPromptDetailLoading={fetchPromptDetailLoading}
            modalVisible={modalVisible}
            initData={promptFormData}
            promptDetail={promptDetail}
            languageList={languageList}
            onCancel={this.hideModal}
            onOk={this.handleSavePrompt}
            currentLanguage={currentLanguage}
          />
        </Content>
      </React.Fragment>
    );
  }
}
