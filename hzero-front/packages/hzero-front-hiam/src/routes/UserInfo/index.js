/**
 * UserInfo.js
 * @date 2018/11/23
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import queryString from 'query-string';
import { Bind } from 'lodash-decorators';
import { Icon, Tabs } from 'hzero-ui';
import { connect } from 'dva';

import { Content as PageContent, Header } from 'components/Page';

import { isUndefined } from 'lodash';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { getCurrentOrganizationId } from 'utils/utils';
import { BKT_PUBLIC } from 'utils/config';

import styles from './index.less';

import AccountInfo from './AccountInfo';
import SafeInfo from './SafeInfo';
import PreferenceInfo from './PreferenceInfo';
import UserReceiveConfig from './UserReceiveConfig';

@connect(({ userInfo, loading, user, personalLoginRecord, login }) => ({
  user,
  userInfo,
  personalLoginRecord,
  login,
  avatarLoading: loading.effects['userInfo/saveAvatar'],
  editModalLoading:
    loading.effects['userInfo/validatePrePassword'] ||
    loading.effects['userInfo/validatePreValidate'] ||
    loading.effects['userInfo/validateNewEmail'] ||
    loading.effects['userInfo/validateNewPhone'] ||
    loading.effects['userInfo/updatePassword'],
  postCaptchaLoading: loading.effects['userInfo/postCaptcha'],
  updateRoleLoading: loading.effects['userInfo/updateRole'],
  updateCompanyLoading: loading.effects['userInfo/updateCompany'],
  updateRealNameLoading: loading.effects['userInfo/updateRealName'],
  updateTimeZoneLoading: loading.effects['userInfo/updateTimeZone'],
  updateLanguageLoading: loading.effects['userInfo/updateLanguage'],
  updateMenuLoading: loading.effects['userInfo/updateMenu'],
  updateRoleMergeLoading: loading.effects['userInfo/updateRoleMerge'],
  updateDateFormatLoading: loading.effects['userInfo/updateDateFormat'],
  updateTimeFormatLoading: loading.effects['userInfo/updateTimeFormat'],
  updateReminderFlagLoading: loading.effects['userInfo/updateReminderFlag'],
  organizationId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: ['hiam.userInfo', 'hiam.userReceiveConfig', 'hiam.login'] })
export default class UserInfo extends React.Component {
  componentDidMount() {
    this.init();
    // 检查绑定三方登陆的错误
    this.checkBindError();
  }

  checkBindError() {
    const {
      location: { hash = '' },
    } = this.props;
    const { social_error_message: bindErrorMessage } = queryString.parse(hash.slice(1));
    if (bindErrorMessage) {
      notification.warning({ message: decodeURIComponent(bindErrorMessage) });
    }
  }

  @Bind
  init() {
    const { organizationId, dispatch } = this.props;
    dispatch({
      type: 'userInfo/init',
      payload: { organizationId },
    });
  }

  render() {
    const {
      dispatch,
      organizationId,
      userInfo: {
        publicKey,
        passwordTipMsg,
        userInfo,
        roleDataSource,
        companyDataSource,
        imgFormData, // 图片表单数据
        uploadImgName, // 图片名称
        uploadImgPreviewUrl, // 图片上传预览
        imgUploadStatus, // 图片上传状态
        modalProps = {}, // modalForm 的额外数据
        openAccountList = [], // 第三方应用
        languageMap = {}, // 语言
        menuMap = {}, // 菜单
        roleMergeMap = {}, // 角色合并
        dateMap = {}, // 日期格式
        timeMap = {}, // 时间格式
        reminderFlagMap = {}, // 首页消息弹窗提醒
      },
      user: { currentUser },
      updateCompanyLoading = false,
      updateRoleLoading = false,
      updateRealNameLoading = false,
      avatarLoading = false,
      editModalLoading = false,
      updateTimeZoneLoading = false,
      updateLanguageLoading = false,
      updateMenuLoading = false,
      updateRoleMergeLoading = false,
      updateReminderFlagLoading = false,
      updateDateFormatLoading = false,
      updateTimeFormatLoading = false,
      postCaptchaLoading = false,
      location: { state: { _back } = {} },
      personalLoginRecord: { dataSource = [] } = {},
    } = this.props;
    return (
      <>
        <Header title={intl.get('hiam.userInfo.view.title').d('个人中心')} />
        <PageContent
          noCard
          className={styles['user-info-content']}
          style={{ minHeight: 'calc(100% - 16px)', marginBottom: '16px' }}
        >
          <Tabs
            animated={false}
            tabPosition="left"
            defaultActiveKey={isUndefined(_back) ? 'account' : 'safe'}
          >
            <Tabs.TabPane
              tab={
                <>
                  <div className={styles['user-info-content-tab-account']}>
                    <Icon className={styles['user-info-content-tab-account-icon']} />
                    <span className={styles['user-info-content-tab-account-info']}>
                      {intl.get('hiam.userInfo.view.title.main.accountInfo').d('账号信息')}
                    </span>
                  </div>
                </>
              }
              key="account"
            >
              <AccountInfo
                userInfo={userInfo}
                roleDataSource={roleDataSource}
                companyDataSource={companyDataSource}
                initRoleDataSource={this.initRoleDataSource}
                initCompanyDataSource={this.initCompanyDataSource}
                onSaveRealName={this.handleRealNameSave}
                updateRealNameLoading={updateRealNameLoading}
                onDefaultCompanySave={this.handleCompanySave}
                onDefaultRoleSave={this.handleRoleSave}
                updateRoleLoading={updateRoleLoading}
                updateCompanyLoading={updateCompanyLoading}
                // 头像
                getEnabledFile={this.getEnabledFile}
                imgFormData={imgFormData}
                uploadImgName={uploadImgName}
                uploadImgPreviewUrl={uploadImgPreviewUrl}
                imgUploadStatus={imgUploadStatus}
                avatarLoading={avatarLoading}
                organizationId={organizationId}
                dispatch={dispatch}
              />
            </Tabs.TabPane>
            <Tabs.TabPane
              tab={
                <div className={styles['user-info-content-tab-safeSetting']}>
                  <Icon className={styles['user-info-content-tab-safeSetting-icon']} />
                  <span className={styles['user-info-content-tab-safeSetting-info']}>
                    {intl.get('hiam.userInfo.view.title.main.safeSetting').d('安全设置')}
                  </span>
                </div>
              }
              key="safe"
            >
              <SafeInfo
                publicKey={publicKey}
                loginData={dataSource}
                passwordTipMsg={passwordTipMsg}
                userInfo={userInfo}
                modalProps={modalProps}
                onPasswordUpdate={this.handlePasswordUpdate}
                dispatch={dispatch}
                openAccountList={openAccountList}
                editModalLoading={editModalLoading}
                postCaptchaLoading={postCaptchaLoading}
              />
            </Tabs.TabPane>
            <Tabs.TabPane
              tab={
                <div className={styles['user-info-content-tab-preferenceSetting']}>
                  <Icon className={styles['user-info-content-tab-preferenceSetting-icon']} />
                  <span className={styles['user-info-content-tab-preferenceSetting-info']}>
                    {intl.get('hiam.userInfo.view.title.main.preferenceSetting').d('偏好设置')}
                  </span>
                </div>
              }
              key="preference"
            >
              <PreferenceInfo
                userInfo={userInfo}
                currentUser={currentUser}
                onTimeZoneUpdate={this.handleTimeZoneUpdate}
                initPreference={this.initPreference}
                initLanguageMap={this.initLanguageMap}
                dateMap={dateMap}
                timeMap={timeMap}
                languageMap={languageMap}
                menuMap={menuMap}
                roleMergeMap={roleMergeMap}
                reminderFlagMap={reminderFlagMap}
                onTimeFormatUpdate={this.handleTimeFormatUpdate}
                onDateFormatUpdate={this.handleDateFormatUpdate}
                onLanguageUpdate={this.handleLanguageUpdate}
                onRefreshMenu={this.handleRefresh}
                onMenuUpdate={this.handleMenuUpdate}
                onRoleMergeUpdate={this.handleRoleMergeUpdate}
                onReminderFlagUpdate={this.handleReminderFlagUpdate}
                updateTimeZoneLoading={updateTimeZoneLoading}
                updateLanguageLoading={updateLanguageLoading}
                updateMenuLoading={updateMenuLoading}
                updateRoleMergeLoading={updateRoleMergeLoading}
                updateDateFormatLoading={updateDateFormatLoading}
                updateTimeFormatLoading={updateTimeFormatLoading}
                updateReminderFlagLoading={updateReminderFlagLoading}
              />
            </Tabs.TabPane>
            <Tabs.TabPane
              tab={
                <div className={styles['user-info-content-tab-receiveSetting']}>
                  <Icon className={styles['user-info-content-tab-receiveSetting-icon']} />
                  <span className={styles['user-info-content-tab-receiveSetting-info']}>
                    {intl.get('hiam.userInfo.view.title.main.receiveSetting').d('接收设置')}
                  </span>
                </div>
              }
              key="receive"
            >
              <UserReceiveConfig />
            </Tabs.TabPane>
          </Tabs>
        </PageContent>
      </>
    );
  }

  @Bind()
  initRoleDataSource() {
    const { dispatch } = this.props;
    // 获取当前登陆用户所拥有的角色
    dispatch({
      type: 'userInfo/initRoleDataSource',
    });
  }

  @Bind()
  initCompanyDataSource() {
    // 获取当前登陆用户所拥有的角色
    const { organizationId, dispatch } = this.props;
    dispatch({
      type: 'userInfo/initCompanyDataSource',
      payload: { organizationId },
    });
  }

  @Bind()
  handleRealNameSave(realName) {
    const {
      dispatch,
      userInfo: { userInfo = {} },
    } = this.props;
    return dispatch({
      type: 'userInfo/updateRealName',
      payload: {
        realName,
        userInfo,
      },
    });
  }

  @Bind()
  handleCompanySave(defaultCompanyId) {
    const {
      userInfo: { companyMap = {}, userInfo = {} },
      dispatch,
      organizationId,
    } = this.props;
    return dispatch({
      type: 'userInfo/updateCompany',
      payload: {
        defaultCompanyId,
        defaultCompanyName:
          companyMap[defaultCompanyId] && companyMap[defaultCompanyId].companyName,
        userInfo,
        organizationId,
      },
    });
  }

  @Bind()
  handleRoleSave(defaultRoleId) {
    const {
      userInfo: { roleMap = {}, userInfo = {} },
      dispatch,
      organizationId,
    } = this.props;
    return dispatch({
      type: 'userInfo/updateRole',
      payload: {
        defaultRoleId,
        defaultRoleName: roleMap[defaultRoleId] && roleMap[defaultRoleId].name,
        userInfo,
        organizationId,
      },
    });
  }

  @Bind()
  getEnabledFile() {
    const { dispatch, organizationId } = this.props;
    return dispatch({
      type: 'userInfo/fetchEnabledFile',
      payload: {
        tenantId: organizationId,
        bucketName: BKT_PUBLIC,
        directory: 'hiam02',
      },
    });
  }

  // safe-info

  /**
   * 更新密码
   * @param {*} payload
   */
  @Bind()
  handlePasswordUpdate(payload) {
    const {
      dispatch,
      userInfo: { userInfo = {} },
    } = this.props;
    return dispatch({
      type: 'userInfo/updatePassword',
      payload: {
        userInfo,
        ...payload,
      },
    });
  }

  // preference-info

  @Bind()
  handleTimeZoneUpdate({ timeZone, timeZoneMeaning }) {
    const {
      dispatch,
      userInfo: { userInfo = {} },
    } = this.props;
    return dispatch({
      type: 'userInfo/updateTimeZone',
      payload: {
        timeZone,
        timeZoneMeaning,
        userInfo,
      },
    });
  }

  // todo 之前切换到输入模式都会查询，现在只会查询一次
  @Bind()
  initLanguageMap() {
    const { dispatch } = this.props;
    dispatch({
      type: 'userInfo/initLanguageDataSource',
      payload: {},
    });
  }

  @Bind()
  handleLanguageUpdate(language) {
    const {
      userInfo: { languageMap = {}, userInfo },
      dispatch,
    } = this.props;
    return dispatch({
      type: 'userInfo/updateLanguage',
      payload: {
        language,
        languageName: languageMap[language]?.name,
        userInfo,
      },
    });
  }

  @Bind()
  handleMenuUpdate(menuLayout) {
    const {
      userInfo: { userInfo = {} },
      dispatch,
    } = this.props;
    return dispatch({
      type: 'userInfo/updateMenuType',
      payload: {
        menuLayout,
        roleMergeFlag: userInfo.roleMergeFlag,
        userInfo,
      },
    });
  }

  @Bind()
  handleRefresh(menuLayout) {
    const { dispatch } = this.props;
    dispatch({
      type: 'user/updateCurrentUser',
      payload: { menuLayout },
    });
  }

  @Bind()
  handleRoleMergeUpdate(roleMergeFlag) {
    const {
      userInfo: { userInfo },
      dispatch,
    } = this.props;
    return dispatch({
      type: 'userInfo/updateRoleMerge',
      payload: {
        // eslint-disable-next-line no-nested-ternary
        roleMergeFlag: roleMergeFlag === '1' ? 1 : roleMergeFlag === '0' ? 0 : undefined,
        menuLayout: userInfo.menuLayout,
        userInfo,
      },
    });
  }

  @Bind()
  initPreference() {
    const { dispatch } = this.props;
    const lovCodes = {
      menuMap: 'HPFM.MENU_LAYOUT',
      roleMergeMap: 'HPFM.ENABLED_FLAG',
      dateMap: 'HIAM.DATE_FORMAT',
      timeMap: 'HIAM.TIME_FORMAT',
      reminderFlagMap: 'HPFM.ENABLED_FLAG',
    };
    dispatch({
      type: 'userInfo/initLovCode',
      payload: { lovCodes },
    });
  }

  /**
   * 变更当前用户的默认时间格式
   * @param {String} dateFormat
   * @memberof UserInfo
   */
  @Bind()
  handleDateFormatUpdate(dateFormat) {
    const {
      userInfo: { dateMap = {}, userInfo = {} },
      dispatch,
    } = this.props;
    return dispatch({
      type: 'userInfo/updateDateFormat',
      payload: {
        dateFormat,
        dateFormatMeaning: dateMap[dateFormat].meaning,
        userInfo,
      },
    });
  }

  /**
   * 变更当前用户的默认语言
   * @param {Object} timeFormat
   * @memberof UserInfo
   */
  @Bind()
  handleTimeFormatUpdate(timeFormat) {
    const {
      userInfo: { timeMap = {}, userInfo = {} },
      dispatch,
    } = this.props;
    return dispatch({
      type: 'userInfo/updateTimeFormat',
      payload: {
        timeFormat,
        timeFormatMeaning: timeMap[timeFormat].meaning,
        userInfo,
      },
    });
  }

  /**
   * 变更首页消息弹窗提醒设置
   * @param {Object} reminderFlag
   * @memberof UserInfo
   */
  @Bind()
  handleReminderFlagUpdate(reminderFlag) {
    const {
      userInfo: { userInfo },
      dispatch,
    } = this.props;
    return dispatch({
      type: 'userInfo/updateReminderFlag',
      payload: {
        popoutReminderFlag: reminderFlag === '1' ? 1 : 0,
        userInfo,
      },
    });
  }
}
