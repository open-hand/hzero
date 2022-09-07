/**
 * Workflow -采购方工作流
 * @date: 2019-08-26
 * @author: Wang Tao
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import { Row, Col, Card, Icon, Spin, Pagination, Avatar } from 'hzero-ui';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { openTab } from 'utils/menuTab';

import styles from './index.less';
import temporarily from '../../../assets/cards/temporarily-no-data.svg';

@connect(({ cardWorkflow, loading }) => ({
  cardWorkflow,
  workflowDataLoading: loading.effects['cardWorkflow/queryWorkflow'],
}))
@formatterCollections({ code: [] })
export default class Workflow extends React.Component {
  componentDidMount() {
    this.handleWorkflowSearch();
  }

  /**
   * 查询工作流
   * @param {number} [page = 0] - 当前页面
   * @param {number} [pageSize = 10] - 每页数据条数
   */
  @Bind()
  handleWorkflowSearch(page = 0, pageSize = 10) {
    const { dispatch } = this.props;
    dispatch({
      type: 'cardWorkflow/queryWorkflow',
      payload: { page, size: pageSize },
    });
  }

  /**
   * 查询某一页数据
   * @param {number} page - 当前页面
   * @param {number} pageSize - 每页数据条数
   */
  @Bind()
  onPageChange(page, pageSize) {
    this.handleWorkflowSearch(page - 1, pageSize);
  }

  /**
   * 页面数据条数改变,查询某一页数据
   * @param {number} page - 当前页面
   * @param {number} pageSize - 每页数据条数
   */
  @Bind()
  onShowSizeChange(current, pageSize) {
    this.handleWorkflowSearch(current - 1, pageSize);
  }

  /**
   * 跳转到我的待办事项
   */
  @Bind()
  handleGoToAllDetail() {
    openTab({
      title: 'hzero.common.view.todolist.mytodolist',
      key: `/hwfp/task`,
      path: `/hwfp/task`,
      icon: 'edit',
      closable: true,
    });
  }

  /**
   * 跳转到详情页
   * @param {number} id - 租户id
   * @param {number} processInstanceId - 流程id
   */
  @Bind()
  handleDetailPage(id, processInstanceId, processName, assigneeName) {
    openTab({
      title: assigneeName ? `${processName}-${assigneeName}` : `${processName}`,
      key: `/hwfp/task/detail/${id}/${processInstanceId}`,
      path: `/hwfp/task/detail/${id}/${processInstanceId}`,
      icon: 'edit',
      closable: true,
    });
  }

  render() {
    const {
      cardWorkflow: { workflowList = [], total, page, pageSize } = {},
      workflowDataLoading,
    } = this.props;
    const colorList = ['#0687ff', '#cb38ad', '#ffbc00', '#f02b2b'];
    return (
      <Card
        title={intl.get('hzero.common.view.todolist.workflow').d('工作流')}
        bordered={false}
        bodyStyle={{ padding: 0 }}
        className={styles.height}
        extra={
          <div style={{ fontSize: '12px' }}>
            <a onClick={this.handleGoToAllDetail}>
              {intl.get('hzero.common.view.workflow.allDetail').d('查看所有')}
              {'   '}
              <Icon type="double-right" />
            </a>
            <a
              onClick={() => this.handleWorkflowSearch(0, pageSize)}
              style={{ marginLeft: '16px' }}
            >
              {intl.get('hzero.common.button.reload').d('重新加载')}
              {'   '}
              <Icon type="reload" />
            </a>
          </div>
        }
      >
        <div style={{ height: '100%' }}>
          <div className={styles['content-styles']}>
            {!isEmpty(workflowList) ? (
              <Spin spinning={workflowDataLoading}>
                <div className={styles.workflow}>
                  {workflowList.map((item, index) => {
                    const styleIndex = index % 4;
                    return (
                      <Row
                        key={`todoList-item-${item.id}`}
                        type="flex"
                        justify="space-between"
                        align="middle"
                        className={styles['workflow-row']}
                        onClick={() => {
                          this.handleDetailPage(
                            item.id,
                            item.processInstanceId,
                            item.processName,
                            item.assigneeName
                          );
                        }}
                      >
                        <Col span={4}>
                          {item.assigneeName ? (
                            <Avatar style={{ backgroundColor: colorList[styleIndex] }}>
                              {item.assigneeName.slice(0, 1)}
                            </Avatar>
                          ) : (
                            <Avatar icon="user" />
                          )}
                        </Col>
                        <Col span={20}>
                          <Row style={{ marginBottom: '4px' }}>
                            <Col span={8} className={styles['workflow-title']}>
                              {item.processName}
                            </Col>
                            <Col span={8} className={styles['workflow-title']}>
                              {item.name}
                            </Col>
                            <Col span={8} className={styles['workflow-time']}>
                              {item.createTime}
                            </Col>
                          </Row>
                        </Col>
                      </Row>
                    );
                  })}
                </div>
              </Spin>
            ) : (
              <div style={{ textAlign: 'center' }}>
                <img src={temporarily} alt="" style={{ marginTop: '35px' }} />
              </div>
            )}
          </div>
          <div className={styles.pagination}>
            {!isEmpty(workflowList) ? (
              <Pagination
                showSizeChanger
                total={total}
                current={page}
                pageSize={pageSize}
                onChange={this.onPageChange}
                onShowSizeChange={this.onShowSizeChange}
              />
            ) : null}
          </div>
        </div>
      </Card>
    );
  }
}
