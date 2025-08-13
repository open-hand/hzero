/* eslint-disable no-nested-ternary */
/**
 * PreferenceInfo.js
 * @date 2018/11/27
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Bind } from 'lodash-decorators';
import { Button, Form, Select } from 'hzero-ui';

import Lov from 'components/Lov';

import intl from 'utils/intl';

import Main from './components/Main';
import Content from './components/Content';
import MaxLenItem from './components/MaxLenItem';

import styles from './index.less';

import DateTimeFormat from './DateTimeFormat';

const itemContentStyle = { width: 240 };
const btnStyle = { marginLeft: 8 };

@Form.create({ fieldNameProp: null })
export default class PreferenceInfo extends React.Component {
  state = {
    timeZoneProps: { editing: false },
    languageProps: { editing: false },
    menuProps: { editing: false },
    roleMergeProps: { editing: false },
    reminderFlagProps: { editing: false },
  };

  componentDidMount() {
    const { initLanguageMap, initPreference } = this.props;
    initLanguageMap();
    initPreference();
  }

  render() {
    const {
      userInfo,
      dateMap,
      timeMap,
      onDateFormatUpdate,
      onTimeFormatUpdate,
      updateDateFormatLoading,
      updateTimeFormatLoading,
    } = this.props;
    return (
      <div className={styles.preference}>
        <Main title={intl.get('hiam.userInfo.view.title.main.preferenceSetting').d('偏好设置')}>
          <Content>
            {this.renderTimeZone()}
            {this.renderLanguage()}
            {this.renderMenuType()}
            {this.renderRoleMerge()}
            {this.renderReminderFlag()}
            {
              <DateTimeFormat
                userInfo={userInfo}
                dateMap={dateMap}
                timeMap={timeMap}
                onDateFormatUpdate={onDateFormatUpdate}
                onTimeFormatUpdate={onTimeFormatUpdate}
                updateDateFormatLoading={updateDateFormatLoading}
                updateTimeFormatLoading={updateTimeFormatLoading}
              />
            }
          </Content>
        </Main>
      </div>
    );
  }

  // time-zone
  renderTimeZone() {
    const { userInfo = {}, form, updateTimeZoneLoading } = this.props;
    const {
      timeZoneProps: { editing = false },
    } = this.state;
    let content = userInfo.timeZoneMeaning;
    const comment = intl
      .get('hiam.userInfo.view.message.timeZone')
      .d('时区首选项，用于用户切换时区');
    const btns = [];
    if (editing) {
      // comment = '';
      content = (
        <>
          {form.getFieldDecorator('timeZone', {
            initialValue: userInfo.timeZone,
          })(
            <Lov
              code="HIAM.TIME_ZONE"
              textValue={userInfo.timeZoneMeaning}
              textField="timeZoneMeaning"
              style={itemContentStyle}
              allowClear={false}
            />
          )}
          <Button
            type="primary"
            style={btnStyle}
            loading={updateTimeZoneLoading}
            onClick={this.handleTimeZoneUpdate}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          <Button style={btnStyle} onClick={this.handleTimeZoneEditCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
        </>
      );
    } else {
      btns.push(
        <Button key="update" onClick={this.handleTimeZoneEdit}>
          {intl.get('hzero.common.button.update').d('修改')}
        </Button>
      );
    }
    return (
      <MaxLenItem
        key="time-zone"
        itemIcon={null}
        description={intl.get('hiam.userInfo.model.user.timeZone').d('时区切换')}
        content={content}
        comment={comment}
        btns={btns}
      />
    );
  }

  @Bind()
  handleTimeZoneEdit() {
    this.setState({ timeZoneProps: { editing: true } });
  }

  @Bind()
  handleTimeZoneEditCancel() {
    this.setState({ timeZoneProps: { editing: false } });
  }

  @Bind()
  handleTimeZoneUpdate() {
    const { form } = this.props;
    form.validateFields(['timeZone', 'timeZoneMeaning'], (err, data) => {
      const { onTimeZoneUpdate, userInfo = {} } = this.props;
      let unUpdateMeaning = data.timeZoneMeaning;
      // 如果没有改变 Lov, Lov 里面的 timeZoneMeaning(textField) 会为空
      if (userInfo.timeZone === data.timeZone) {
        if (!data.timeZoneMeaning) {
          unUpdateMeaning = userInfo.timeZoneMeaning;
        }
      }
      onTimeZoneUpdate({
        ...data,
        timeZoneMeaning: unUpdateMeaning,
      }).then((res) => {
        if (res) {
          this.handleTimeZoneEditCancel();
        }
      });
    });
  }

  @Bind()
  findConfigField(field, data) {
    if (data.length > 0) {
      const dataFilter = data.find((item) => item.value === field);
      return dataFilter !== undefined ? dataFilter.meaning : null;
    }
  }

  // language
  renderLanguage() {
    const { userInfo = {}, languageMap = {}, form, updateLanguageLoading } = this.props;
    const {
      languageProps: { editing = false },
    } = this.state;
    let content = userInfo.languageName;
    const btns = [];
    const comment = intl
      .get('hiam.userInfo.view.message.language')
      .d('语言首选项，用于用户切换语言');
    if (editing) {
      // comment = '';
      content = (
        <>
          {form.getFieldDecorator('language', {
            initialValue: userInfo.language,
          })(
            <Select style={itemContentStyle} allowClear>
              {Object.values(languageMap).map((item) => (
                <Select.Option key={item.code} value={item.code}>
                  {item.name}
                </Select.Option>
              ))}
            </Select>
          )}
          <Button
            key="save"
            style={btnStyle}
            loading={updateLanguageLoading}
            onClick={this.handleLanguageUpdate}
            type="primary"
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          <Button key="cancel" style={btnStyle} onClick={this.handleLanguageEditCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
        </>
      );
    } else {
      btns.push(
        <Button key="update" onClick={this.handleLanguageEdit}>
          {intl.get('hzero.common.button.update').d('修改')}
        </Button>
      );
    }
    return (
      <MaxLenItem
        itemIcon={null}
        description={intl.get('hiam.userInfo.model.user.language').d('语言切换')}
        content={content}
        comment={comment}
        btns={btns}
      />
    );
  }

  /**
   * 菜单布局设置
   */
  @Bind()
  renderMenuType() {
    const { userInfo = {}, menuMap = {}, form, updateMenuLoading } = this.props;
    const {
      menuProps: { editing = false },
    } = this.state;
    let content = this.findConfigField(userInfo.menuLayout, menuMap);
    const comment = intl.get('hiam.userInfo.view.message.menu').d('菜单首选项，选择不同的菜单布局');
    const btns = [];
    if (editing) {
      // comment = '';
      content = (
        <>
          {form.getFieldDecorator('menuLayout', {
            initialValue: userInfo.menuLayout,
          })(
            <Select style={itemContentStyle} allowClear>
              {Object.values(menuMap).map((item) => (
                <Select.Option key={item.value} value={item.value}>
                  {item.meaning}
                </Select.Option>
              ))}
            </Select>
          )}
          <Button
            key="save"
            style={btnStyle}
            loading={updateMenuLoading}
            onClick={this.handleMenuUpdate}
            type="primary"
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          <Button key="cancel" style={btnStyle} onClick={this.handleMenuEditCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
        </>
      );
    } else {
      btns.push(
        <Button key="update" onClick={this.handleMenuEdit}>
          {intl.get('hzero.common.button.update').d('修改')}
        </Button>
      );
    }
    return (
      <MaxLenItem
        itemIcon={null}
        description={intl.get('hiam.userInfo.model.user.menu').d('菜单布局')}
        content={content}
        comment={comment}
        btns={btns}
      />
    );
  }

  /**
   * 角色合并设置
   */
  @Bind()
  renderRoleMerge() {
    const { userInfo = {}, roleMergeMap = {}, form, updateRoleMergeLoading } = this.props;
    const {
      roleMergeProps: { editing = false },
    } = this.state;
    let content = this.findConfigField(
      userInfo.roleMergeFlag === 1 ? '1' : userInfo.roleMergeFlag === 0 ? '0' : undefined,
      roleMergeMap
    );
    const comment = intl
      .get('hiam.userInfo.view.message.roleMerge')
      .d('角色合并首选项，选择是否角色合并');
    const btns = [];
    if (editing) {
      // comment = '';
      content = (
        <>
          {form.getFieldDecorator('roleMergeFlag', {
            initialValue:
              userInfo.roleMergeFlag === 1 ? '1' : userInfo.roleMergeFlag === 0 ? '0' : undefined,
          })(
            <Select style={itemContentStyle} allowClear>
              {Object.values(roleMergeMap).map((item) => (
                <Select.Option key={item.value} value={item.value}>
                  {item.meaning}
                </Select.Option>
              ))}
            </Select>
          )}
          <Button
            key="save"
            style={btnStyle}
            loading={updateRoleMergeLoading}
            onClick={this.handleRoleMergeUpdate}
            type="primary"
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          <Button key="cancel" style={btnStyle} onClick={this.handleRoleMergeEditCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
        </>
      );
    } else {
      btns.push(
        <Button key="update" onClick={this.handleRoleMergeEdit}>
          {intl.get('hzero.common.button.update').d('修改')}
        </Button>
      );
    }
    return (
      <MaxLenItem
        itemIcon={null}
        description={intl.get('hiam.userInfo.model.user.roleMerge').d('角色合并')}
        content={content}
        comment={comment}
        btns={btns}
      />
    );
  }

  /**
   * 首页弹窗提醒设置
   */
  @Bind()
  renderReminderFlag() {
    const { form, userInfo, reminderFlagMap = {}, updateReminderFlagLoading } = this.props;
    const {
      reminderFlagProps: { editing = false },
    } = this.state;
    let content = this.findConfigField(
      userInfo.popoutReminderFlag === 1 ? '1' : userInfo.popoutReminderFlag === 0 ? '0' : null,
      reminderFlagMap
    );
    const btns = [];
    const comment = intl
      .get('hiam.userInfo.view.message.reminderFlag')
      .d('首页消息弹窗首选项，选择是否开启首页消息弹窗提醒');
    if (editing) {
      content = (
        <>
          {form.getFieldDecorator('reminderFlag', {
            initialValue:
              userInfo.popoutReminderFlag === 1
                ? '1'
                : userInfo.popoutReminderFlag === 0
                ? '0'
                : undefined,
          })(
            <Select style={itemContentStyle}>
              {Object.values(reminderFlagMap).map((item) => (
                <Select.Option key={item.value} value={item.value}>
                  {item.meaning}
                </Select.Option>
              ))}
            </Select>
          )}
          <Button
            key="save"
            style={btnStyle}
            loading={updateReminderFlagLoading}
            onClick={this.handleReminderFlagUpdate}
            type="primary"
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          <Button key="cancel" style={btnStyle} onClick={this.handleReminderFlagEditCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
        </>
      );
    } else {
      btns.push(
        <Button key="update" onClick={this.handleReminderFlagEdit}>
          {intl.get('hzero.common.button.update').d('修改')}
        </Button>
      );
    }
    const description = <>{intl.get('hiam.userInfo.model.user.reminderFlag').d('首页消息弹窗')}</>;
    return (
      <MaxLenItem
        itemIcon={null}
        description={description}
        content={content}
        comment={comment}
        btns={btns}
      />
    );
  }

  @Bind()
  handleLanguageEdit() {
    this.setState({
      languageProps: { editing: true },
    });
  }

  @Bind()
  handleLanguageEditCancel() {
    this.setState({
      languageProps: { editing: false },
    });
  }

  @Bind()
  handleLanguageUpdate() {
    const { form } = this.props;
    form.validateFields(['language'], (err, data) => {
      if (!err) {
        const { onLanguageUpdate } = this.props;
        onLanguageUpdate(data.language).then((res) => {
          if (res) {
            this.handleLanguageEditCancel();
          }
        });
      }
    });
  }

  // 菜单
  @Bind()
  handleMenuEdit() {
    this.setState({
      menuProps: { editing: true },
    });
  }

  @Bind()
  handleMenuEditCancel() {
    this.setState({
      menuProps: { editing: false },
    });
  }

  @Bind()
  handleMenuUpdate() {
    const { form } = this.props;
    form.validateFields(['menuLayout'], (err, data) => {
      if (!err) {
        const { onMenuUpdate, onRefreshMenu } = this.props;
        onMenuUpdate(data.menuLayout).then((res) => {
          if (res) {
            onRefreshMenu(data.menuLayout);
            this.handleMenuEditCancel();
          }
        });
      }
    });
  }

  // 角色合并
  @Bind()
  handleRoleMergeEdit() {
    this.setState({
      roleMergeProps: { editing: true },
    });
  }

  @Bind()
  handleRoleMergeEditCancel() {
    this.setState({
      roleMergeProps: { editing: false },
    });
  }

  @Bind()
  handleRoleMergeUpdate() {
    const { form } = this.props;
    form.validateFields(['roleMergeFlag'], (err, data) => {
      if (!err) {
        const { onRoleMergeUpdate } = this.props;
        onRoleMergeUpdate(data.roleMergeFlag).then((res) => {
          if (res) {
            this.handleRoleMergeEditCancel();
          }
        });
      }
    });
  }

  /**
   * 修改首页消息弹窗设置
   */
  @Bind()
  handleReminderFlagEdit() {
    this.setState({
      reminderFlagProps: { editing: true },
    });
  }

  /**
   * 取消首页消息弹窗设置修改
   */
  @Bind()
  handleReminderFlagEditCancel() {
    this.setState({
      reminderFlagProps: { editing: false },
    });
  }

  /**
   * 更新首页消息弹窗设置
   */
  @Bind()
  handleReminderFlagUpdate() {
    const { form } = this.props;
    form.validateFields(['reminderFlag'], (err, data) => {
      if (!err) {
        const { onReminderFlagUpdate } = this.props;
        onReminderFlagUpdate(data.reminderFlag).then((res) => {
          if (res) {
            this.handleReminderFlagEditCancel();
          }
        });
      }
    });
  }
}
