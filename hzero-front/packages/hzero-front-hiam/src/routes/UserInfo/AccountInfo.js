/**
 * 个人中心 基本信息
 * AccountInfo.js.js
 * @date 2018/11/23
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Avatar, Button, Form, Icon, Input, message, Select, Tooltip } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { map, some } from 'lodash';

import { dateRender } from 'utils/renderer';
import intl from 'utils/intl';
import defaultUserAvatar from 'hzero-front/lib/assets/logo-usercenter-default.png';

import editIcon from '../../assets/userInfo/edit.png';
import Main from './components/Main';
import Content from './components/Content';
import Item from './components/Item';
import AvatarUploadModal from './components/AvatarUploadModal';

import styles from './index.less';
import MaxLenItem from './components/MaxLenItem';

const editWidthStyle = { width: 240 };
const btnStyle = { marginLeft: 8 };

@Form.create({ fieldNameProp: null })
export default class AccountInfo extends React.Component {
  state = {
    realNameEditing: false,
    avatarUploadProps: {},
    defaultRoleProps: { editing: false },
    defaultCompanyProps: { editing: false },
    userAvatar: defaultUserAvatar,
  };

  componentDidMount() {
    const { initRoleDataSource, initCompanyDataSource } = this.props;
    initRoleDataSource();
    initCompanyDataSource();
    const userAvatar = this.props.userInfo && this.props.userInfo.imageUrl;
    if (userAvatar) {
      const img = new Image();
      img.onload = this.updateUserAvatar;
      img.onerror = this.setDefaultUserAvatar;
      img.src = userAvatar;
    }
  }

  componentDidUpdate(prevProps) {
    const prevUserAvatar = prevProps.userInfo && prevProps.userInfo.imageUrl;
    const nextUserAvatar = this.props.userInfo && this.props.userInfo.imageUrl;
    if (prevUserAvatar !== nextUserAvatar) {
      // 只有当 用户头像存在 才会设置 用户头像
      if (nextUserAvatar) {
        const img = new Image();
        img.onload = this.updateUserAvatar;
        img.onerror = this.setDefaultUserAvatar;
        img.src = nextUserAvatar;
      }
    }
  }

  @Bind()
  updateUserAvatar() {
    const nextUserAvatar = this.props.userInfo && this.props.userInfo.imageUrl;
    this.setState({
      userAvatar: nextUserAvatar,
    });
  }

  @Bind()
  setDefaultUserAvatar() {
    this.setState({
      userAvatar: defaultUserAvatar,
    });
  }

  render() {
    const {
      userInfo = {},
      imgFormData,
      uploadImgName,
      uploadImgPreviewUrl,
      imgUploadStatus,
      avatarLoading,
      organizationId,
      dispatch,
    } = this.props;

    const { realNameEditing, avatarUploadProps, userAvatar } = this.state;

    return (
      <div className={styles.account}>
        <Main title={intl.get('hiam.userInfo.view.title.subMain.baseInfo').d('基本信息')}>
          <Content className={styles['base-info']}>
            <div className={styles['base-info-left']}>
              <div className={styles['base-info-avatar']}>
                <div className={styles['avatar-box']} onClick={this.handleAvatarUploadShow}>
                  <Avatar className={styles['head-info-avatar']} src={userAvatar} size="large">
                    {userInfo.realName}
                  </Avatar>
                  <div className={styles['box-content']}>
                    <Icon type="camera-o" className={styles['avatar-uploader-trigger']} />
                  </div>
                </div>
              </div>
              <div className={styles['base-info-real-name']}>
                {realNameEditing ? this.renderRealNameEdit() : this.renderRealName()}
              </div>
            </div>
            <div className={styles['base-info-right']}>
              <Item label={intl.get('hiam.userInfo.model.user.loginName').d('账号')}>
                {userInfo.loginName}
              </Item>
              <Item
                className={styles['group-item']}
                label={intl.get('hiam.userInfo.model.user.groupName').d('所属集团')}
              >
                {userInfo.groupName}
              </Item>
              <Item label={intl.get('hzero.common.date.active.from').d('有效日期从')}>
                {dateRender(userInfo.startDateActive)}
                {userInfo.endDateActive && (
                  <>
                    &nbsp;
                    <span className={styles['head-info-content-label']}>
                      {intl.get('hiam.userInfo.model.user.endDateActive').d('至')}
                    </span>
                    &nbsp;
                    {dateRender(userInfo.endDateActive)}
                  </>
                )}
              </Item>
            </div>
          </Content>
        </Main>
        <Main
          title={intl.get('hiam.userInfo.view.title.subMain.groupInfo').d('默认集团信息')}
          className={styles.group}
        >
          <Content>
            {this.renderDefaultRole()}
            {this.renderDefaultCompany()}
          </Content>
        </Main>
        {avatarUploadProps.avatarVisible && (
          <AvatarUploadModal
            {...avatarUploadProps}
            imgFormData={imgFormData}
            uploadImgName={uploadImgName}
            uploadImgPreviewUrl={uploadImgPreviewUrl}
            imgUploadStatus={imgUploadStatus}
            loading={avatarLoading}
            organizationId={organizationId}
            userInfo={userInfo}
            dispatch={dispatch}
          />
        )}
      </div>
    );
  }

  renderRealNameEdit() {
    const { userInfo, form, updateRealNameLoading } = this.props;
    return (
      <>
        <div className={styles['base-info-real-name-edit']}>
          {form.getFieldDecorator('realName', {
            initialValue: userInfo.realName,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hiam.userInfo.model.user.realName').d('昵称'),
                }),
              },
              {
                max: 40,
                message: intl.get('hzero.common.validation.max', { max: 40 }),
              },
            ],
          })(<Input />)}
          <a
            onClick={this.handleRealNameEditCancel}
            style={{ fontSize: '12px', color: '#aaadba', marginLeft: '8px' }}
          >
            {intl.get('hzero.common.button.cancel').d('取消')}
          </a>
          <a className={styles['base-info-real-name-edit-save']} onClick={this.handleRealNameSave}>
            {updateRealNameLoading && <Icon type="loading" style={{ marginRight: '2px' }} />}
            {intl.get('hzero.common.button.save').d('保存')}
          </a>
        </div>
      </>
    );
  }

  renderRealName() {
    const { userInfo = {} } = this.props;
    return (
      <>
        <Tooltip
          title={userInfo.realName}
          placement="bottom"
          className={styles['base-info-real-name-content']}
        >
          <span className={styles['base-info-real-name-content']}>{userInfo.realName}</span>
        </Tooltip>
        <a onClick={this.handleRealNameEdit}>
          <img src={editIcon} alt="edit" width="14px" height="14px" />
        </a>
      </>
    );
  }

  renderDefaultRole() {
    const { userInfo = {}, roleDataSource = [], form, updateRoleLoading } = this.props;
    const {
      defaultRoleProps: { editing = false },
    } = this.state;
    let content;
    const comment = intl.get('hiam.userInfo.view.message.role').d('登录汉得云时默认使用的角色');
    const btns = [];
    if (editing) {
      // comment = '';
      const roleOptions = map(roleDataSource, role => (
        <Select.Option key={role.id} value={role.id}>
          {role.name}
        </Select.Option>
      ));
      content = (
        <>
          {form.getFieldDecorator('defaultRole', {
            initialValue: userInfo.defaultRoleId,
          })(
            <Select allowClear style={editWidthStyle}>
              {roleOptions}
            </Select>
          )}
          <Button style={btnStyle} onClick={this.handleDefaultRoleEditCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button
            type="primary"
            style={btnStyle}
            loading={updateRoleLoading}
            onClick={this.handleDefaultRoleUpdate}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </>
      );
    } else {
      content = userInfo.defaultRoleName || '';
      btns.push(
        <Button key="update" onClick={this.handleDefaultRoleEdit}>
          {intl.get('hzero.common.button.update').d('修改')}
        </Button>
      );
    }
    return (
      <MaxLenItem
        key="default-role"
        itemIcon={null}
        descriptions={intl.get('hiam.userInfo.model.user.defaultRole').d('默认角色')}
        content={content}
        comment={comment}
        btns={btns}
      />
    );
  }

  renderDefaultCompany() {
    const { userInfo = {}, companyDataSource = [], form, updateCompanyLoading } = this.props;
    const {
      defaultCompanyProps: { editing = false },
    } = this.state;
    const curCompanyCanAssign = some(
      companyDataSource,
      company => company.companyId === userInfo.defaultCompanyId
    );
    let content;
    const comment = intl
      .get('hiam.userInfo.view.message.company')
      .d('在汉得云平台内根据权限分配的公司中的默认公司选项');
    const btns = [];
    if (editing) {
      // comment = '';
      const companyOptions = map(companyDataSource, company => (
        <Select.Option key={company.companyId} value={company.companyId}>
          {company.companyName}
        </Select.Option>
      ));
      content = (
        <>
          {form.getFieldDecorator('defaultCompany', {
            initialValue: curCompanyCanAssign ? userInfo.defaultCompanyId : undefined,
          })(
            <Select allowClear style={editWidthStyle}>
              {companyOptions}
            </Select>
          )}
          <Button style={btnStyle} onClick={this.handleDefaultCompanyEditCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button
            type="primary"
            style={btnStyle}
            loading={updateCompanyLoading}
            onClick={this.handleDefaultCompanyUpdate}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </>
      );
    } else {
      content = curCompanyCanAssign ? userInfo.defaultCompanyName || '' : '';
      btns.push(
        <Button key="update" onClick={this.handleDefaultCompanyEdit}>
          {intl.get('hzero.common.button.update').d('修改')}
        </Button>
      );
    }
    return (
      <MaxLenItem
        key="default-company"
        itemIcon={null}
        descriptions={intl.get('hiam.userInfo.model.user.defaultCompany').d('默认公司')}
        content={content}
        comment={comment}
        btns={btns}
      />
    );
  }

  // role
  @Bind()
  handleDefaultRoleEdit() {
    this.setState({
      defaultRoleProps: { editing: true },
    });
  }

  @Bind()
  handleDefaultRoleEditCancel() {
    const { form } = this.props;
    form.resetFields(['defaultRole']);
    this.setState({
      defaultRoleProps: { editing: false },
    });
  }

  @Bind()
  handleDefaultRoleUpdate() {
    const { form, onDefaultRoleSave } = this.props;
    form.validateFields(['defaultRole'], (err, data) => {
      if (!err) {
        onDefaultRoleSave(data.defaultRole).then(res => {
          if (res) {
            form.resetFields(['defaultRole']);
            this.handleDefaultRoleEditCancel();
          }
        });
      }
    });
  }

  // company
  @Bind()
  handleDefaultCompanyEdit() {
    this.setState({
      defaultCompanyProps: { editing: true },
    });
  }

  @Bind()
  handleDefaultCompanyEditCancel() {
    this.setState({
      defaultCompanyProps: { editing: false },
    });
  }

  @Bind()
  handleDefaultCompanyUpdate() {
    const { form, onDefaultCompanySave } = this.props;
    form.validateFields(['defaultCompany'], (err, data) => {
      if (!err) {
        onDefaultCompanySave(data.defaultCompany).then(res => {
          if (res) {
            form.resetFields(['defaultCompany']);
            this.handleDefaultCompanyEditCancel();
          }
        });
      }
    });
  }

  // real-name

  @Bind()
  handleRealNameEdit() {
    this.setState({
      realNameEditing: true,
    });
  }

  @Bind()
  handleRealNameEditCancel() {
    this.setState({
      realNameEditing: false,
    });
  }

  @Bind()
  handleRealNameSave(e) {
    e.preventDefault();
    const { onSaveRealName, form } = this.props;
    form.validateFields(['realName'], (err, data) => {
      if (err) {
        let errorMessage = '';
        err.realName.errors.forEach(er => {
          errorMessage += er.message;
        });
        message.error(errorMessage);
      } else {
        onSaveRealName(data.realName).then(res => {
          if (res) {
            this.setState({
              realNameEditing: false,
            });
          }
        });
      }
    });
  }

  // avatar

  @Bind()
  handleAvatarUploadShow(e) {
    e.preventDefault();
    this.setState({
      avatarUploadProps: {
        avatarVisible: true,
        onOk: this.handleAvatarUploadHidden,
        onCancel: this.handleAvatarUploadHidden,
      },
    });
  }

  @Bind()
  handleAvatarUploadHidden() {
    const { dispatch } = this.props;
    this.setState({
      avatarUploadProps: {
        visible: false,
      },
    });
    dispatch({
      type: 'userInfo/updateState',
      payload: { uploadImgPreviewUrl: '', uploadImgName: '' },
    });
  }
}
