/**
 * Detail - 流程监控 明细
 * @date: 2018-11-15
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Tabs, Row, Col, Spin } from 'hzero-ui';
import { connect } from 'dva';
import classNames from 'classnames';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';

import ApproveHistory from './ApproveHistory';
import ApproveForm from '../../components/ApproveForm';
import FlowChart from './FlowChart';
import styles from './index.less';

@connect(({ monitor, loading }) => ({
  monitor,
  fetchDetailLoading: loading.effects['monitor/fetchDetail'],
  fetchForecast: loading.effects['monitor/fetchForecast'],
  tenantId: getCurrentOrganizationId(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hwfp.monitor', 'hwfp.common', 'entity.position', 'entity.department'],
})
export default class Detail extends Component {
  approveFormChildren;

  /**
   * 生命周期函数
   *render调用后，获取页面展示数据
   */
  componentDidMount() {
    this.handleSearch();
  }

  @Bind()
  handleSearch() {
    const { dispatch, match, tenantId } = this.props;
    dispatch({
      type: 'monitor/fetchDetail',
      payload: {
        tenantId,
        id: match.params.id,
      },
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      dispatch,
      fetchDetailLoading,
      fetchForecast,
      tenantId,
      match,
      monitor: { [match.params.id]: { detail = {}, forecast = [], uselessParam } = {} },
    } = this.props;
    const { formKey = null } = detail;
    const historyProps = {
      detail,
      loading: fetchDetailLoading,
    };
    const flowProps = {
      dispatch,
      match,
      tenantId,
      forecast,
      detail,
      uselessParam,
      loading: fetchForecast,
    };
    const formProps = {
      disabled: true,
      detail,
      ref: ref => {
        this.approveFormChildren = ref;
      },
      onAction: this.taskAction,
    };

    const priority =
      detail.priority < 34
        ? intl.get('hzero.common.priority.low').d('低')
        : detail.priority > 66
        ? intl.get('hzero.common.priority.high').d('高')
        : intl.get('hzero.common.priority.medium').d('中');
    const name = `${detail.startUserName ? `${detail.startUserName}(${detail.startUserId})` : ''}`;

    return (
      <>
        <Header title={intl.get('hwfp.common.model.process.detail').d('流程明细')} />
        <Content>
          <Spin spinning={fetchDetailLoading}>
            {/* 审批事项 */}
            <div className={classNames(styles['label-col'])}>
              {intl.get('hwfp.common.model.approval.item').d('审批事项')}
            </div>
            <Row
              style={{ borderBottom: '1px dashed #dcdcdc', paddingBottom: 4, marginBottom: 20 }}
              type="flex"
              justify="space-between"
              align="bottom"
            >
              <Col md={8}>
                <Row>
                  <Col md={6} style={{ color: '#999' }}>
                    {intl.get('hwfp.common.model.process.name').d('流程名称')}:
                  </Col>
                  <Col md={16}> {detail.processName}</Col>
                </Row>
              </Col>
              <Col md={8}>
                <Row>
                  <Col md={6} style={{ color: '#999' }}>
                    {intl.get('hwfp.common.model.process.ID').d('流程标识')}:
                  </Col>
                  <Col md={16}> {detail.id}</Col>
                </Row>
              </Col>
              <Col md={8}>
                <Row>
                  <Col md={6} style={{ color: '#999' }}>
                    {intl.get('hwfp.common.model.apply.owner').d('申请人')}:
                  </Col>
                  <Col md={16}> {name}</Col>
                </Row>
              </Col>
            </Row>
            <Row
              style={{ borderBottom: '1px dashed #dcdcdc', paddingBottom: 4, marginBottom: 40 }}
              type="flex"
              justify="space-between"
              align="bottom"
            >
              <Col md={8}>
                <Row>
                  <Col md={6} style={{ color: '#999' }}>
                    {intl.get('hwfp.common.model.apply.time').d('申请时间')}:
                  </Col>
                  <Col md={16}> {detail.startTime}</Col>
                </Row>
              </Col>
              <Col md={8}>
                <Row>
                  <Col md={6} style={{ color: '#999' }}>
                    {intl.get('hzero.common.priority').d('优先级')}:
                  </Col>
                  <Col md={16}> {priority}</Col>
                </Row>
              </Col>
              <Col md={8}>
                <Row>
                  <Col md={6} style={{ color: '#999' }}>
                    {intl.get('hwfp.common.model.process.description').d('流程描述')}:
                  </Col>
                  <Col md={16}> {detail.description}</Col>
                </Row>
              </Col>
            </Row>

            {formKey && (
              <>
                <div className={classNames(styles['label-col'])}>
                  {intl.get('hwfp.common.model.approval.form').d('审批表单')}
                </div>
                <ApproveForm {...formProps} />
              </>
            )}

            <Tabs defaultActiveKey="1" animated={false}>
              <Tabs.TabPane
                tab={intl.get('hwfp.common.model.approval.history').d('审批历史')}
                key="1"
              >
                <ApproveHistory {...historyProps} />
              </Tabs.TabPane>
              <Tabs.TabPane tab={intl.get('hwfp.common.model.process.graph').d('流程图')} key="2">
                <FlowChart {...flowProps} />
              </Tabs.TabPane>
            </Tabs>
          </Spin>
        </Content>
      </>
    );
  }
}
