/**
 * 二级域名模板分配--模板分配
 * @date: 2019-7-11
 * @author: XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Row, Col, Icon, Switch, Card, Spin, Popconfirm } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import queryString from 'querystring';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { BKT_PLATFORM } from 'utils/config';
import { getAttachmentUrl, getCurrentOrganizationId } from 'utils/utils';

import style from './index.less';
import AssignTemplate from './AssignTemplate';

@connect(({ loading, ssoConfig }) => ({
  ssoConfig,
  getTemplateAssignListLoading: loading.effects['ssoConfig/getTemplateAssignList'],
  getAssignableListLoading: loading.effects['ssoConfig/getAssignableList'],
  savingLoading: loading.effects['ssoConfig/templateAssignCreate'],
  organizationId: getCurrentOrganizationId(),
}))
@formatterCollections({
  code: ['hiam.ssoConfig', 'hiam.common', 'hiam.portalAssign'],
})
export default class Template extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      templateName: '',
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'ssoConfig/updateState',
      payload: {
        templateAssignList: [],
      },
    });
    this.setState({
      templateName: '',
    });
    this.fetchConfigDetail();
  }

  @Bind()
  handleCancel() {
    this.setState({
      modalVisible: false,
    });
  }

  // 批量分配模板
  @Bind()
  handleOk(selectedRows) {
    const {
      dispatch,
      match: {
        params: { tenantId, domainId },
      },
    } = this.props;
    if (selectedRows.length > 0) {
      dispatch({
        type: 'ssoConfig/templateAssignCreate',
        payload: selectedRows.map((item) => ({
          ...item,
          tenantId,
          sourceKey: domainId,
          sourceType: 'SSO',
        })),
      }).then((res) => {
        if (res) {
          notification.success();
          this.fetchConfigDetail();
          this.setState({
            modalVisible: false,
          });
        }
      });
    } else {
      notification.warning({
        message: intl.get(`hzero.common.message.confirm.selected.atLeast`).d('请至少选择一行数据'),
      });
    }
  }

  // 删除模板
  @Bind()
  handleDelete(item) {
    const {
      dispatch,
      match: {
        params: { tenantId },
      },
    } = this.props;
    dispatch({
      type: 'ssoConfig/templateAssignDelete',
      payload: {
        ...item,
        tenantId,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchConfigDetail();
      }
    });
  }

  // 获取单点登录明细和分配模板列表
  fetchConfigDetail() {
    const {
      dispatch,
      match: {
        params: { domainId, tenantId },
      },
    } = this.props;
    dispatch({
      type: 'ssoConfig/getTemplateAssignList',
      payload: {
        sourceKey: domainId,
        sourceType: 'SSO',
        tenantId,
      },
    }).then((res) => {
      if (res && res.length > 0) {
        const res1 = res.filter((item) => item.defaultFlag === 1);
        if (res1.length > 0) {
          this.setState({
            templateName: res1[0].templateName,
          });
        }
      }
    });
  }

  // 点击设置跳转到模板分配配置界面
  @Bind()
  templateConfigSetting(item) {
    const {
      history,
      dispatch,
      match: {
        params: { domainId },
        path,
      },
      ssoConfig: { templateConfigPagination = {} },
      location: { search },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    dispatch({
      type: 'ssoConfig/getTemplateConfigList',
      payload: {
        templateAssignId: item.templateAssignId,
        tenantId: item.tenantId,
        page: templateConfigPagination,
      },
    });
    const router = `/hiam/domain-config/template/edit/${domainId}/${item.tenantId}/${
      item.templateAssignId
    }/${encodeURIComponent(item.templateName)}/${encodeURIComponent(item.templatePath)}`;
    history.push({
      pathname: path.indexOf('/private') === 0 ? `/private${router}` : `${router}`,
      search: path.indexOf('/private') === 0 ? `?access_token=${accessToken}` : '',
    });
  }

  @Bind()
  onGetAssignTemplate(params = {}) {
    const {
      dispatch,
      match: {
        params: { domainId, tenantId },
      },
      ssoConfig: { assignablePagination = {} },
    } = this.props;
    dispatch({
      type: 'ssoConfig/updateState',
      payload: {
        assignableList: [],
      },
    });
    this.setState({
      modalVisible: true,
    });
    dispatch({
      type: 'ssoConfig/getAssignableList',
      payload: {
        tenantId,
        sourceKey: domainId,
        sourceType: 'SSO',
        page: assignablePagination,
        ...params,
      },
    });
  }

  // 点击分配模板打开模态框获取可分配模板列表
  @Bind()
  showAssignTemplate() {
    this.onGetAssignTemplate();
  }

  // 设置模板启用
  @Bind()
  handleEnabledTemplate(item) {
    const { dispatch } = this.props;
    dispatch({
      type: 'ssoConfig/templateAssignDefault',
      payload: {
        templateAssignId: item.templateAssignId,
      },
    }).then((res) => {
      if (res) {
        this.setState({
          templateName: item.templateName,
        });
        notification.success();
        this.fetchConfigDetail();
      }
    });
  }

  // 重置表单
  @Bind()
  handleResetBtn(form) {
    form.resetFields();
  }

  @Bind()
  handleTableChange(assignablePagination = {}) {
    this.onGetAssignTemplate({
      page: assignablePagination,
    });
  }

  // 查询表单
  @Bind()
  handleSearchBtn(form) {
    const {
      dispatch,
      match: {
        params: { tenantId, domainId },
      },
    } = this.props;
    const fieldsValue = form.getFieldsValue();
    dispatch({
      type: 'ssoConfig/getAssignableList',
      payload: {
        ...fieldsValue,
        tenantId,
        sourceKey: domainId,
        sourceType: 'SSO',
      },
    });
  }

  render() {
    const {
      ssoConfig: { templateAssignList = [], assignableList = [], assignablePagination = {} },
      match: { path },
      getTemplateAssignListLoading,
      getAssignableListLoading,
      savingLoading,
      organizationId,
      location: { search },
    } = this.props;
    const { modalVisible, templateName } = this.state;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    return (
      <>
        <Header
          title={intl.get('hiam.portalAssign.view.button.assignTemplate').d('分配模板')}
          backPath={
            path.indexOf('/private') === 0
              ? `/private/hiam/domain-config/list?access_token=${accessToken}`
              : '/hiam/domain-config/list'
          }
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.assignTemplate`,
                type: 'button',
                meaning: '分配模板-分配模板',
              },
            ]}
            type="primary"
            icon="plus"
            onClick={this.showAssignTemplate}
          >
            {intl.get('hiam.portalAssign.view.button.assignTemplate').d('分配模板')}
          </ButtonPermission>
        </Header>

        <Content>
          <Spin spinning={getTemplateAssignListLoading}>
            <table className={style['template-table']}>
              <tbody>
                <tr>
                  <td className={style['template-label']}>
                    {intl.get('hiam.common.view.message.title.customTemplate').d('所选模板')}
                  </td>
                  <td>{templateName}</td>
                </tr>
              </tbody>
            </table>
            {templateAssignList.length > 0 && (
              <Row gutter={24}>
                <div style={{ fontSize: '16px', padding: '24px 24px 0 12px' }}>
                  {intl.get('hiam.common.view.message.title.allTemplate').d('所有模板:')}
                </div>
                {templateAssignList.map((item) => (
                  <Col key={item.templateId} span={8} style={{ marginTop: 20 }}>
                    <Card
                      hoverable
                      cover={
                        <div style={{ height: 300, overflow: 'hidden' }}>
                          <img
                            src={getAttachmentUrl(
                              item.templateAvatar,
                              BKT_PLATFORM,
                              organizationId,
                              'hpfm03'
                            )}
                            alt="logo"
                            width="100%"
                          />
                        </div>
                      }
                      actions={[
                        <Icon
                          type="setting"
                          onClick={() => {
                            this.templateConfigSetting(item);
                          }}
                        />,
                        <Switch
                          type="primary"
                          disabled={item.defaultFlag === 1}
                          checked={item.defaultFlag === 1}
                          onClick={() => this.handleEnabledTemplate(item)}
                        />,
                        <Popconfirm
                          title={intl
                            .get('hiam.common.view.message.confirm.delete')
                            .d('是否删除此模板？')}
                          onConfirm={() => this.handleDelete(item)}
                        >
                          <ButtonPermission
                            type="text"
                            permissionList={[
                              {
                                code: `${path}.button.delete`,
                                type: 'button',
                                meaning: '分配模板-删除',
                              },
                            ]}
                            disabled={item.defaultFlag === 1}
                          >
                            {intl.get('hzero.common.button.delete').d('删除')}
                          </ButtonPermission>
                        </Popconfirm>,
                      ]}
                    >
                      <Card.Meta
                        title={
                          <div style={{ display: 'flex', justifyContent: 'space-around' }}>
                            <span>
                              {intl
                                .get('hiam.common.view.message.title.templateCode')
                                .d('模板代码:')}
                              {item.templateCode}
                            </span>
                            <span>
                              {intl
                                .get('hiam.common.view.message.title.templateName')
                                .d('模板名称:')}
                              {item.templateName}
                            </span>
                          </div>
                        }
                      />
                    </Card>
                  </Col>
                ))}
              </Row>
            )}
            <AssignTemplate
              confirmLoading={savingLoading}
              modalVisible={modalVisible}
              onOk={this.handleOk}
              onCancel={this.handleCancel}
              assignableList={assignableList}
              assignablePagination={assignablePagination}
              search={this.handleSearchBtn}
              reset={this.handleResetBtn}
              getAssignableListLoading={getAssignableListLoading}
              onTableChange={this.handleTableChange}
            />
          </Spin>
        </Content>
        {templateAssignList.length === 0 && !getTemplateAssignListLoading && (
          <h3 style={{ color: 'gray', marginTop: '10%', textAlign: 'center' }}>
            {intl
              .get('hiam.ssoConfig.model.ssoConfig.noTemplateAssignList')
              .d('当前暂无可配置的模板，请点击分配模板按钮进行分配')}
          </h3>
        )}
      </>
    );
  }
}
