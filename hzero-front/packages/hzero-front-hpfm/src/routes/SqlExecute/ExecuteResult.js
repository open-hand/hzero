/**
 * sqlExecute - SQL执行界面
 * @date: 2018-9-27
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Form, Table, Tabs } from 'hzero-ui';
import uuid from 'uuid/v4';
import { isEqual, max, min } from 'lodash';
import { Bind } from 'lodash-decorators';

import { createPagination, getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';

import style from './index.less';

const { TabPane } = Tabs;

/**
 * getWidthFromWord - 通过字符串确定宽度
 * @param {String} word - 字符串
 * @param {Number} minWidth - 最小宽度
 * @param {Number} maxWidth - 最大宽度
 * @param {Number} [defaultWidth=100] - 默认宽度
 * @param {Number} [fontWidth=14] - 每个字符的宽度
 * @param {Number} [paddingWidth=20] - 补偿额外宽度
 */
function getWidthFromWord({
  word,
  minWidth = 60,
  maxWidth,
  // defaultWidth = 100,
  fontWidth = 12,
  paddingWidth = 36,
}) {
  // if (isString(word)) {
  return min([max([(word.length * fontWidth) / 2, minWidth]), maxWidth]) + paddingWidth;
  // }
  // return defaultWidth;
}

@Form.create({ fieldNameProp: null })
@connect(({ sqlExecute, loading }) => ({
  sqlExecute,
  fetchResult: loading.effects['sqlExecute/fetchExecuteResult'],
  tenantId: getCurrentOrganizationId(),
}))
export default class ExecuteResult extends PureComponent {
  state = {
    paging: { page: 0, size: 10 }, // 分页参数
    defaultActiveKey: 'information', // 选中的Tab
    resultData: {}, // 结果渲染的数据
    results: [],
  };

  textArea;

  // eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    if (!isEqual(nextProps.sqlExecute.resultData, this.props.sqlExecute.resultData)) {
      this.setState({ resultData: nextProps.sqlExecute.resultData }, () => {
        const sqlList = this.state.resultData.results.map(item => item.sql);
        this.props.dispatch({
          type: 'sqlExecute/updateState',
          payload: {
            exportSql: sqlList.join(';'),
          },
        });
      });
    }

    // 当执行的SQL改变后，将Tab定位在【信息】
    if (!isEqual(nextProps.sqlExecute.executeSql, this.props.sqlExecute.executeSql)) {
      this.setState({ ...this.state, defaultActiveKey: 'information' });
    }
  }

  /**
   * 按条件查询
   */
  @Bind()
  loadData(payload) {
    const { dispatch, tenantId } = this.props;
    const { paging } = this.state;
    dispatch({
      type: 'sqlExecute/fetchExecuteResult',
      payload: { ...paging, ...payload, tenantId },
    });
  }

  @Bind()
  handleTableChange(item, pagination) {
    const { dispatch } = this.props;
    const { resultData } = this.state;
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
    };

    dispatch({
      type: 'sqlExecute/fetchSingleResult',
      payload: { ...params, sql: item.sql.trim() },
    }).then(res => {
      if (res) {
        const tempData = resultData;
        const replacement = res.results[0];
        tempData.results = tempData.results.map(result =>
          result.sql && result.sql.trim() === replacement.sql.trim() ? replacement : result
        );
        this.setState({
          paging: params,
          resultData: tempData,
        });

        const sqlList = tempData.results.map(re => re.sql);
        dispatch({
          type: 'sqlExecute/updateState',
          payload: {
            exportSql: sqlList.join(';'),
          },
        });
      }
    });
  }

  // 切换Tab
  @Bind()
  switchTab(activeKey) {
    this.setState({ defaultActiveKey: activeKey });
  }

  @Bind()
  renderTabs() {
    // const { sqlExecute: { resultData = {} }, fetchResult } = this.props;
    const { fetchResult } = this.props;
    const { defaultActiveKey, resultData } = this.state;
    const { results = [], information = [] } = resultData;

    const TabTableList = results.map((item, index) => {
      // const columns = this.renderColumns(item.result.content);
      const { colArray } = this.renderColumns(item.result.content);
      const columns = colArray.map(c => ({ ...c, width: 150 }));
      return (
        item.result.content.length > 0 && (
          <TabPane
            tab={intl
              .get('hpfm.sqlExecute.view.message.tab.result', { orderSeq: index + 1 })
              .d(`结果${index + 1}`)}
            key={item.sql && item.sql.trim()}
          >
            <Table
              rowKey={() => uuid()}
              loading={fetchResult}
              dataSource={item.result.content}
              columns={columns}
              bordered
              // scroll={{ x: columnsWidth > 800 ? '200%' : '100%', y: 250 }}
              scroll={{ x: columns.length * 150 }}
              pagination={createPagination(item.result)}
              onChange={(...args) => {
                this.handleTableChange(item, ...args);
              }}
            />
          </TabPane>
        )
      );
    });
    const informations = information.map(item => (
      <div key={uuid()}>
        {item.sql && <div>{item.sql.trim()}</div>}
        <div>{`> ${item.message}`}</div>
        <div style={{ marginBottom: 20 }}>
          {intl.get('hpfm.sqlExecute.view.message.time').d('> 时间：')}
          {item.time}s
        </div>
      </div>
    ));
    return (
      information.length > 0 && (
        <Tabs
          defaultActiveKey="information"
          type="card"
          onChange={this.switchTab}
          activeKey={defaultActiveKey}
        >
          <TabPane
            tab={intl.get('hpfm.sqlExecute.view.tab.information').d('信息')}
            key="information"
          >
            {informations}
          </TabPane>
          {TabTableList}
        </Tabs>
      )
    );
  }

  @Bind()
  renderColumns(list) {
    const colData = list[0];
    const colArray = [];
    let columnsWidth = 0;
    for (const key in colData) {
      if (Object.prototype.hasOwnProperty.call(colData, key)) {
        const columnWidth = getWidthFromWord({ word: key });
        const item = {
          title: key,
          dataIndex: key,
          width: columnWidth,
        };
        columnsWidth += columnWidth;
        colArray.push(item);
      }
    }
    return {
      columnsWidth,
      colArray,
    };
  }

  render() {
    return <div className={style['result-table']}>{this.renderTabs()}</div>;
  }
}
