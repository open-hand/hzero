/*
 * module - 分组编辑
 * @author: YXY <xinyu.ye@hand-china.com>
 * @date: 2019-10-16
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Header, Content } from 'components/Page';
import { DataSet, Table, Button, TextField, CheckBox, Select } from 'choerodon-ui/pro';
import { routerRedux } from 'dva/router';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import intl from 'utils/intl';
import notification from 'utils/notification';
import querystring from 'querystring';
import { saveNode } from '@/utils/saveNode';
import GroupDS from '../stores/GroupDS';

@connect()
export default class GroupPage extends Component {
  groupDS = new DataSet({
    ...GroupDS(),
    events: {
      update: ({ dataSet, name, value }) => {
        if (name === 'groupComponentName') {
          dataSet.forEach(record => {
            record.set('groupComponentName', value);
          });
        }
      },
      submit: ({ data }) => {
        const newData = data.filter(val => {
          return val.groupFlag === 'Y' || val.aggregationType;
        });
        if (newData && newData.length === 0) {
          notification.error({
            message: intl.get('hres.grouping.view.message.validate.setGroup').d('请设置分组条件！'),
          });
          return false;
        }
      },
    },
  });

  submitFalg = true;

  componentDidMount() {
    const {
      location: { search },
      match,
    } = this.props;
    const { id, code } = match.params;
    const { componentName, pageType } = querystring.parse(search.substring(1));
    this.flag = pageType === 'view';
    this.groupDS.setQueryParameter('ruleCode', code);
    this.groupDS.setQueryParameter('groupComponentName', componentName);
    this.groupDS.setQueryParameter('id', id);
    this.groupDS.query();
  }

  /**
   * 确认
   */
  @Bind()
  async handleSure() {
    const isModified = this.groupDS.isModified();
    if (!(await this.groupDS.validate(false, false))) {
      notification.error({
        message: intl.get('hres.common.notification.required').d('存在必输字段未填写'),
      });
      return false;
    }
    if (!isModified) {
      notification.warning({
        message: intl.get('hres.common.notification.unmodified').d('表单未做修改'),
      });
      return;
    }
    const res = await this.groupDS.submit();
    if (!isEmpty(res) && !res.status) {
      this.submitFalg = false;
      this.groupDS.query();
      const {
        dispatch,
        location: { search },
        match,
      } = this.props;
      const { code, id } = match.params;
      const { pageType, ruleName } = querystring.parse(search.substring(1));
      await saveNode(res.content[0].processNode, id);
      const pathname = `/hres/rules/flow/detail/${code}`;
      dispatch(
        routerRedux.push({
          pathname,
          search: querystring.stringify({ pageType, ruleName }),
        })
      );
    }
  }

  /**
   * 是否编辑
   */
  @Bind()
  handleEditor(record, name) {
    switch (name) {
      case 'groupFlag':
        if (!record.get('aggregationType') && this.submitFalg && !this.flag) {
          return <CheckBox />;
        }
        return false;
      case 'aggregationType':
        if (
          record.get('groupFlag') !== 'Y' &&
          this.submitFalg &&
          !this.flag &&
          record.get('fieldType') !== 'DATE' &&
          record.get('fieldType') !== 'STRING'
        ) {
          return <Select />;
        }
        return false;
      default:
        return false;
    }
  }

  /**
   * 获取名称name
   */
  @Bind()
  handleName() {
    const {
      location: { search },
    } = this.props;
    const { componentName } = querystring.parse(search.substring(1));
    return componentName ? 'fieldName' : 'fullName';
  }

  get columns() {
    return [
      { name: this.handleName() },
      { name: 'fieldType' },
      { name: 'groupFlag', editor: this.handleEditor },
      { name: 'aggregationType', editor: this.handleEditor },
    ];
  }

  render() {
    const {
      location: { search },
      match,
    } = this.props;
    const { code } = match.params;
    const { componentName, pageType, ruleName } = querystring.parse(search.substring(1));
    const frozenFlag = pageType === 'view';
    const isChange = this.groupDS.current ? this.groupDS.current.dirty : false;

    return (
      <React.Fragment>
        <Header
          title={`${intl.get('hres.grouping.view.title.edit').d('分组组件')}_${ruleName}`}
          backPath={`/hres/rules/flow/detail/${code}?pageType=${pageType}&ruleName=${ruleName}`}
          isChange={isChange}
        >
          {!frozenFlag && (
            <Button color="primary" icon="save" onClick={this.handleSure}>
              {intl.get('hzero.common.button.save').d('保存')}
            </Button>
          )}
        </Header>
        <Content>
          {intl.get('hres.grouping.view.title.grouping').d('分组名称：')}
          <TextField
            restrict="\S"
            dataSet={this.groupDS}
            name="groupComponentName"
            disabled={componentName || !this.submitFalg}
          />
          <Table
            style={{ marginTop: 18 }}
            dataSet={this.groupDS}
            columns={this.columns}
            queryBar="none"
            pagination={false}
          />
        </Content>
      </React.Fragment>
    );
  }
}
