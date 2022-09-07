/**
 * SQL组件
 * @Author: NJQ <jiangqi.nan@hand-china.com>
 * @Date: 2019-10-18
 * @LastEditTime: 2019-10-18
 * @Copyright: Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { DataSet, Button, Form, TextArea, TextField, Lov, Spin } from 'choerodon-ui/pro';
import { Table, Layout } from 'hzero-ui';
// import { observer } from 'mobx-react';
import { connect } from 'dva';
import { isEmpty, isUndefined } from 'lodash';
import querystring from 'querystring';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId } from 'utils/utils';
import { routerRedux } from 'dva/router';
import { Header } from 'components/Page';
import notification from 'utils/notification';
import { saveNode } from '@/utils/saveNode';
import { queryTreeData } from '@/services/formulaService';
import SqlContentDS from '../stores/SqlContentDS';
import styles from './index.less';

/**
 * SQL组件
 * @extends {Component} - Component
 * @reactProps {Object} [history={}]
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 *
 */
@connect()
// @observer
@formatterCollections({ code: ['hres.flow', 'hres.common'] })
export default class ListPage extends Component {
  SqlContentDS = new DataSet(SqlContentDS);

  state = {
    treeData: false,
  };

  async componentDidMount() {
    const {
      match,
      location: { search },
    } = this.props;
    const { code, id } = match.params;
    const { componentName } = querystring.parse(search.substring(1));
    const res = await queryTreeData(code);
    if (!isEmpty(res) && res.failed && res.message) {
      return false;
    } else if (!isEmpty(res)) {
      if (res.children) {
        this.setState({
          treeData: res.children,
        });
      }
    }
    if (isEmpty(componentName)) {
      this.SqlContentDS.create({
        id,
        ruleCode: code,
        tenantId: getCurrentOrganizationId(),
      });
    } else {
      this.SqlContentDS.setQueryParameter('ruleCode', code);
      this.SqlContentDS.setQueryParameter('sqlName', componentName);
      this.SqlContentDS.setQueryParameter('tenantId', getCurrentOrganizationId());
      this.SqlContentDS.query();
    }
  }

  /**
   * 确认提交
   *
   */
  @Bind()
  async handlerClick() {
    const {
      dispatch,
      location: { search },
      match,
    } = this.props;
    const { code, id } = match.params;
    const { pageType, ruleName } = querystring.parse(search.substring(1));
    const res = await this.SqlContentDS.submit();
    if (!isUndefined(res) && !res.status && !isEmpty(res)) {
      await saveNode(res.content[0].processNode, id);
      const pathname = `/hres/rules/flow/detail/${code}`;
      dispatch(
        routerRedux.push({
          pathname,
          search: querystring.stringify({ pageType, ruleName }),
        })
      );
      return false;
    } else if (res === false) {
      notification.error({
        message: intl.get('hres.common.notification.required').d('存在必输字段未填写'),
      });
      return false;
    } else {
      notification.warning({
        message: intl.get('hres.common.notification.unmodified').d('表单未做修改'),
      });
      return false;
    }
  }

  /**
   * 点击树添加进SQL编辑框
   * @param record
   */
  onClick = (record) => {
    const { children = [], fullName = undefined } = record;
    const inps = document.getElementsByTagName('TextArea');
    const startPos = inps[0].selectionStart || 0;
    const endPos = inps[0].selectionEnd || 0;
    if (isEmpty(children)) {
      const tempValue = this.SqlContentDS.current.get('sqlText') || '';
      const fullNames = `{${fullName}}`;
      const final =
        tempValue.substring(0, startPos) +
        fullNames +
        tempValue.substring(endPos, tempValue.length);
      this.SqlContentDS.current.set('sqlText', final);
    }
  };

  render() {
    const {
      location: { search },
      match,
    } = this.props;
    const { code } = match.params;
    const { treeData } = this.state;
    const { componentName, pageType, ruleName } = querystring.parse(search.substring(1));
    const frozenFlag = pageType === 'view';
    const isChange = this.SqlContentDS.current ? this.SqlContentDS.current.dirty : false;
    const columns = [
      {
        dataIndex: 'data',
        key: 'fullName',
      },
    ];
    return (
      <React.Fragment>
        <Header
          title={`${intl.get('hres.flow.model.flow.sqlCmp').d('Sql组件')}_${ruleName}`}
          backPath={`/hres/rules/flow/detail/${code}?pageType=${pageType}&ruleName=${ruleName}`}
          isChange={isChange}
        >
          <Button icon="save" color="primary" onClick={() => this.handlerClick()}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Layout className={styles['layout-wrapper']}>
          <Layout.Sider width={300}>
            {treeData ? (
              <Table
                className={styles['tree-wrapper']}
                size="small"
                defaultExpandAllRows
                showHeader={false}
                pagination={false}
                columns={columns}
                dataSource={treeData}
                onRow={(record) => {
                  return {
                    onClick: this.onClick.bind(this, record),
                  };
                }}
                rowKey="data"
              />
            ) : (
              <Spin />
            )}
          </Layout.Sider>
          <Layout>
            <Layout.Header>
              <Form dataSet={this.SqlContentDS} disabled={frozenFlag} columns={2}>
                <TextField
                  name="sqlName"
                  restrict="\S"
                  labelAlign="center"
                  disabled={componentName}
                />
                <Lov name="dataSourceCodelov" />
              </Form>
            </Layout.Header>
            <Layout.Content>
              <Form dataSet={this.SqlContentDS} disabled={frozenFlag} columns={2}>
                <TextArea name="sqlText" rows={10} newLine resize="vertical" colSpan={2} />
              </Form>
            </Layout.Content>
          </Layout>
        </Layout>
      </React.Fragment>
    );
  }
}
