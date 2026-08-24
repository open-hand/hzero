/**
 * Config - 系统配置
 * @date: 2018-10-24
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { Form, Spin, Tabs, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { VERSION_IS_OP } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

import System from './System.js';
import Iam from './Iam.js';
import Oauth from './Oauth.js';
import styles from './index.less';

@connect(({ config, loading, user, global }) => ({
  user,
  global,
  config,
  isSite: !isTenantRoleLevel(),
  queryTenantConfigLoading: loading.effects['config/queryTenantConfig'],
  queryOrganizationConfigLoading: loading.effects['config/queryOrganizationConfig'],
  updateTenantConfigLoading: loading.effects['config/updateTenantConfig'],
  updateOrganizationConfigLoading: loading.effects['config/updateOrganizationConfig'],
  organizationId: getCurrentOrganizationId(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hpfm.config'],
})
export default class ConfigDetail extends Component {
  constructor(props) {
    super(props);
    this.systemRef = React.createRef();
    this.iamRef = React.createRef();
    this.oauthRef = React.createRef();
    this.state = {
      activeKey: 'system',
    };
  }

  render() {
    const {
      config,
      match,
      queryTenantConfigLoading,
      queryOrganizationConfigLoading,
      updateTenantConfigLoading,
      updateOrganizationConfigLoading,
      isSite,
      global: { supportLanguage: languageList = [] },
    } = this.props;
    const { activeKey = 'system' } = this.state;

    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.config.view.message.title').d('系统配置')}>
          <ButtonPermission
            icon="save"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '系统配置-保存',
              },
            ]}
            onClick={this.handleSave}
            loading={isSite ? updateTenantConfigLoading : updateOrganizationConfigLoading}
            disabled={isSite ? queryTenantConfigLoading : queryOrganizationConfigLoading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
        <Content className={styles['config-content']}>
          <Spin spinning={isSite ? queryTenantConfigLoading : queryOrganizationConfigLoading}>
            <Tabs
              activeKey={activeKey}
              onChange={this.handleChange}
              animated={false}
              tabPosition="left"
              // className={styles['hpfm-config-tabs']}
            >
              <Tabs.TabPane
                tab={
                  <React.Fragment>
                    <div className={styles['config-content-tab-system']}>
                      <Icon className={styles['config-content-tab-system-icon']} />
                      <span>{intl.get('hpfm.config.view.title.system').d('基础数据配置')}</span>
                    </div>
                  </React.Fragment>
                }
                key="system"
              >
                <System
                  config={config}
                  wrappedComponentRef={this.systemRef}
                  languageList={languageList}
                />
              </Tabs.TabPane>
              <Tabs.TabPane
                tab={
                  <React.Fragment>
                    <div className={styles['config-content-tab-iam']}>
                      <Icon className={styles['config-content-tab-iam-icon']} />
                      <span>{intl.get('hpfm.config.view.title.iam').d('用户中心配置')}</span>
                    </div>
                  </React.Fragment>
                }
                key="iam"
              >
                <Iam
                  config={config}
                  wrappedComponentRef={this.iamRef}
                  isSite={isSite || VERSION_IS_OP}
                />
              </Tabs.TabPane>
              {(isSite || VERSION_IS_OP) && (
                <Tabs.TabPane
                  tab={
                    <React.Fragment>
                      <div className={styles['config-content-tab-iam']}>
                        <Icon className={styles['config-content-tab-oauth-icon']} />
                        <span>{intl.get('hpfm.config.view.title.oauth').d('登录首页配置')}</span>
                      </div>
                    </React.Fragment>
                  }
                  key="oauth"
                >
                  <Oauth
                    config={config}
                    wrappedComponentRef={this.oauthRef}
                    languageList={languageList}
                  />
                </Tabs.TabPane>
              )}
            </Tabs>
          </Spin>
        </Content>
      </React.Fragment>
    );
  }

  componentDidMount() {
    this.queryConfig();
    this.init();
  }

  componentWillUnmount() {
    this.props.dispatch({
      type: 'config/updateState',
      payload: {
        data: [],
      },
    });
  }

  /**
   * 查询系统配置信息
   */
  @Bind()
  queryConfig() {
    const { dispatch, organizationId, isSite } = this.props;
    dispatch({
      type: isSite ? 'config/queryTenantConfig' : 'config/queryOrganizationConfig',
      payload: organizationId,
    });
  }

  @Bind()
  handleChange(value) {
    this.setState({ activeKey: value });
  }

  @Bind()
  handleSave() {
    const { dispatch, organizationId, isSite } = this.props;
    const { activeKey } = this.state;
    let fieldValues = [];
    let errorFlag = false;
    if (this.systemRef.current && activeKey === 'system') {
      this.systemRef.current.props.form.validateFields((err, fieldsValue) => {
        if (!err) {
          fieldValues = fieldsValue;
        } else {
          errorFlag = true;
        }
      });
    }
    if (this.iamRef.current && activeKey === 'iam') {
      this.iamRef.current.props.form.validateFields((err, fieldsValue) => {
        if (!err) {
          fieldValues = { ...fieldValues, ...fieldsValue };
        } else {
          errorFlag = true;
        }
      });
    }
    if (this.oauthRef.current && activeKey === 'oauth') {
      this.oauthRef.current.props.form.validateFields((err, fieldsValue) => {
        if (!err) {
          fieldValues = { ...fieldValues, ...fieldsValue };
        } else {
          errorFlag = true;
        }
      });
    }
    const values = this.updateData(fieldValues);
    if (!errorFlag) {
      dispatch({
        type: isSite ? 'config/updateTenantConfig' : 'config/updateOrganizationConfig',
        payload: { values, organizationId },
      }).then((res) => {
        if (res) {
          if (this.systemRef.current) {
            this.systemRef.current.props.form.resetFields();
          }
          if (this.iamRef.current) {
            this.iamRef.current.props.form.resetFields();
          }
          if (this.oauthRef.current) {
            this.oauthRef.current.props.form.resetFields();
          }
          notification.success();
          this.queryConfig();
          if (res.length > 0 && activeKey === 'system') {
            const title = { ...res.filter((item) => item.configCode === 'TITLE')[0] };
            const logo = { ...res.filter((item) => item.configCode === 'LOGO')[0] };
            const favicon = { ...res.filter((item) => item.configCode === 'FAVICON')[0] };
            // const menuLayout = res.filter(item => item.configCode === 'MENU_LAYOUT');
            const menuLayoutTheme = {
              ...res.filter((item) => item.configCode === 'MENU_LAYOUT_THEME')[0],
            };
            const roleMergeFlag = { ...res.filter((item) => item.configCode === 'ROLE_MERGE')[0] };
            dispatch({
              type: 'user/updateCurrentUser',
              payload: {
                title: title.configValue,
                logo: logo.configValue,
                favicon: favicon.configValue,
                // menuLayout: newMenuLayout.configValue,
                menuLayoutTheme: menuLayoutTheme.configValue,
                roleMergeFlag: roleMergeFlag.configValue,
              },
            });
          }
        }
      });
    }
  }

  @Bind()
  updateData(obj) {
    const {
      config: { data = [] },
      organizationId,
    } = this.props;
    const values = [];
    if (!isEmpty(obj)) {
      Object.keys(obj).forEach((item) => {
        switch (item) {
          case 'title':
            values.push({
              category: 'system',
              configCode: 'TITLE',
              tenantId: organizationId,
              ...this.findConfig('TITLE', data),
              _tls: obj._tls,
              configValue: obj.title,
            });
            break;
          case 'logo':
            values.push({
              category: 'system',
              configCode: 'LOGO',
              tenantId: organizationId,
              ...this.findConfig('LOGO', data),
              configValue: obj.logo,
            });
            break;
          case 'favicon':
            values.push({
              category: 'system',
              configCode: 'FAVICON',
              tenantId: organizationId,
              ...this.findConfig('FAVICON', data),
              configValue: obj.favicon,
            });
            break;
          case 'menuLayout':
            values.push({
              category: 'system',
              configCode: 'MENU_LAYOUT',
              tenantId: organizationId,
              ...this.findConfig('MENU_LAYOUT', data),
              configValue: obj.menuLayout,
            });
            break;
          case 'menuLayoutTheme':
            values.push({
              category: 'system',
              configCode: 'MENU_LAYOUT_THEME',
              tenantId: organizationId,
              ...this.findConfig('MENU_LAYOUT_THEME', data),
              configValue: obj.menuLayoutTheme,
            });
            break;
          case 'roleMergeFlag':
            values.push({
              category: 'system',
              configCode: 'ROLE_MERGE',
              tenantId: organizationId,
              ...this.findConfig('ROLE_MERGE', data),
              configValue: obj.roleMergeFlag,
            });
            break;
          case 'defaultLanguage':
            values.push({
              category: 'system',
              configCode: 'TENANT_DEFAULT_LANGUAGE',
              tenantId: organizationId,
              ...this.findConfig('TENANT_DEFAULT_LANGUAGE', data),
              configValue: obj.defaultLanguage,
            });
            break;
          case 'watermark':
            values.push({
              category: 'system',
              configCode: 'WATERMARK',
              tenantId: organizationId,
              ...this.findConfig('WATERMARK', data),
              configValue: obj.watermark,
            });
            break;
          case 'password':
            values.push({
              category: 'iam',
              configCode: 'HIAM.IF_SEND_MODIFY_PASSWORD',
              tenantId: organizationId,
              ...this.findConfig('HIAM.IF_SEND_MODIFY_PASSWORD', data),
              configValue: obj.password,
            });
            break;
          case 'sendFlag':
            values.push({
              category: 'iam',
              configCode: 'HIAM.IF_SEND_CREATE_USER',
              tenantId: organizationId,
              ...this.findConfig('HIAM.IF_SEND_CREATE_USER', data),
              configValue: obj.sendFlag,
            });
            break;
          case 'url':
            values.push({
              category: 'iam',
              configCode: 'HIAM.INDEX_URL',
              tenantId: organizationId,
              ...this.findConfig('HIAM.INDEX_URL', data),
              configValue: obj.url,
            });
            break;
          case 'loginTitle':
            values.push({
              category: 'oauth',
              configCode: 'HOTH.TITLE',
              tenantId: organizationId,
              ...this.findConfig('HOTH.TITLE', data),
              configValue: obj.loginTitle,
            });
            break;
          case 'copyright':
            values.push({
              category: 'oauth',
              configCode: 'HOTH.COPYRIGHT',
              tenantId: organizationId,
              ...this.findConfig('HOTH.COPYRIGHT', data),
              configValue: obj.copyright,
            });
            break;
          case 'loginLogo':
            values.push({
              category: 'oauth',
              configCode: 'HOTH.LOGO_URL',
              tenantId: organizationId,
              ...this.findConfig('HOTH.LOGO_URL', data),
              configValue: obj.loginLogo,
            });
            break;
          case 'languageFlag':
            values.push({
              category: 'oauth',
              configCode: 'HOTH.SHOW_LANGUAGE',
              tenantId: organizationId,
              ...this.findConfig('HOTH.SHOW_LANGUAGE', data),
              configValue: obj.languageFlag,
            });
            break;
          case 'language':
            values.push({
              category: 'oauth',
              configCode: 'HOTH.DEFAULT_LANGUAGE',
              tenantId: organizationId,
              ...this.findConfig('HOTH.DEFAULT_LANGUAGE', data),
              configValue: obj.language,
            });
            break;
          default:
            break;
        }
      });
    }
    return values;
  }

  @Bind()
  updateRestData(arr) {
    const {
      config: { data = [] },
    } = this.props;
    const values = [];
    if (data.length > 0) {
      // 复制更新数据
      data.forEach((item) => {
        if (!this.findConfigField(item.configCode, arr)) {
          values.push(item);
        }
      });
    }
    return values;
  }

  /**
   * 从配置列表查找配置项
   * @param {Number|String} field 查询配置字段的 ID 或 Code
   * @param {Array} data 获取到的原配置数组
   */
  @Bind()
  findConfigField(field, data) {
    if (data.length > 0) {
      const dataFilter = data.find((item) => {
        return item.configCode === field;
      });
      return dataFilter ? dataFilter.configValue : null;
    }
  }

  @Bind()
  findConfig(field, data) {
    if (data.length > 0) {
      const dataFilter = data.find((item) => {
        return item.configCode === field;
      });
      return dataFilter;
    }
  }

  // 上传图片成功
  @Bind()
  onUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        logo: file.response,
      });
    }
  }

  // 删除图片成功
  @Bind()
  onCancelSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        logo: '',
      });
    }
  }

  /**
   * 上传 favicon 成功
   */
  @Bind()
  handleFaviconUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        favicon: file.response,
      });
    }
  }

  /**
   * 删除 favicon 成功
   */
  @Bind()
  handleCancelFaviconUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        favicon: '',
      });
    }
  }

  /**
   * 页面初始化， 调用 model 中的初始化 查询值集
   */
  init() {
    const { dispatch } = this.props;
    dispatch({ type: 'config/init' });
  }
}
