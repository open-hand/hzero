/**
 * ChargeGroup - 组合计费详情
 * @date: 2020-2-18
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { DataSet, Form, TextField, Select, Tabs, Modal } from 'choerodon-ui/pro';
import notification from 'utils/notification';
import { Button as ButtonPermission } from 'components/Permission';
import { Card } from 'choerodon-ui';
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';

import { isUndefined } from 'lodash';
import { routerRedux } from 'dva/router';
import { getResponse } from 'utils/utils';

import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import axios from 'axios';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import { CHARGE_GROUP_STATUS_FIELDS } from '@/constants/CodeConstants';
import ChargeGroupHeaderDS from '../../../stores/ChargeGroup/ChargeGroupHeaderDS';
import ChargeGroupLineDS from '../../../stores/ChargeGroup/ChargeGroupLineDS';
import ChargeGroupRuleDS from '../../../stores/ChargeGroup/ChargeGroupRuleDS';

import Server from '../Server';
import Rule from '../Rule';

const organizationId = getCurrentOrganizationId();

/**
 * 组合计费详情
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hitf.chargeGroup'] })
export default class ChargeGroupLine extends Component {
  constructor(props) {
    super(props);
    // 服务列表数据源
    this.serverListDS = new DataSet({
      ...ChargeGroupLineDS(),
      autoQuery: false,
    });
    // 计费规则数据源
    this.chargeGroupRuleDS = new DataSet({
      ...ChargeGroupRuleDS(),
      autoQuery: false,
    });
    // 组合计费设置头 数据源
    this.formDS = new DataSet({
      ...ChargeGroupHeaderDS(),
      autoQuery: false,
      children: {
        chargeGroupLineList: this.serverListDS,
        chargeGroupRuleList: this.chargeGroupRuleDS,
      },
    });

    this.state = {
      saveDisabled: false, // 保存按钮是否可点击
      publishDisabled: false, // 发布按钮是否可点击
      statusCode: null, // 状态
      activeTabKey: 'server', // Tab页Key
    };
  }

  async componentDidMount() {
    // 查看/编辑页面
    if (this.props.match.params.groupHeaderId) {
      // 设置查询参数
      this.formDS.queryParameter.groupHeaderId = this.props.match.params.groupHeaderId;
      this.serverListDS.queryParameter.groupHeaderId = this.props.match.params.groupHeaderId;
      this.chargeGroupRuleDS.queryParameter.groupHeaderId = this.props.match.params.groupHeaderId;
      await this.formDS.query();

      this.setState({
        // 状态=发布/撤销 保存按钮不可点击
        saveDisabled:
          CHARGE_GROUP_STATUS_FIELDS.PUBLISHED === this.formDS.current.get('statusCode') ||
          CHARGE_GROUP_STATUS_FIELDS.CANCELLED === this.formDS.current.get('statusCode'),
        // 状态=发布 发布按钮不可点击
        publishDisabled:
          CHARGE_GROUP_STATUS_FIELDS.PUBLISHED === this.formDS.current.get('statusCode'),
        statusCode: this.formDS.current.get('statusCode'),
      });
    } else {
      // 新建页面
      this.formDS.create();
      this.setState({
        statusCode: CHARGE_GROUP_STATUS_FIELDS.NEW,
      });
    }
  }

  /**
   * 保存
   */
  @Bind()
  async save() {
    const { match } = this.props;
    const { groupHeaderId } = match.params;
    const isCreate = isUndefined(groupHeaderId);
    // 先判断头行是否都填写完整

    if (
      (await this.formDS.validate()) &&
      (await this.serverListDS.validate()) &&
      (await this.chargeGroupRuleDS.validate())
    ) {
      const result = await this.formDS.submit();
      if (getResponse(result)) {
        if (isCreate) {
          this.handleGotoDetail(result.content[0].groupHeaderId);
        } else {
          await this.formDS.query();
        }
      }
    } else {
      notification.info({
        message: intl.get('hitf.chargeGroup.view.message.validate').d('请先完善必输内容'),
      });
    }
  }

  /**
   * 跳转到明细页面
   * @param {*} id
   */
  @Bind()
  handleGotoDetail(id) {
    const { dispatch = () => {} } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hitf/charge-group/line/${id}`,
      })
    );
  }

  /**
   * 发布
   */
  @Bind()
  async publish() {
    // 先判断数据是否已保存/数据是否修改过未保存
    if (
      !this.formDS.current ||
      !this.formDS.current.get('groupHeaderId') ||
      (await this.formDS.isModified()) ||
      (await this.serverListDS.isModified()) ||
      (await this.chargeGroupRuleDS.isModified())
    ) {
      notification.info({
        message: intl.get('hitf.chargeGroup.view.meaasge.confirm.save').d('请先保存数据'),
      });
      return;
    }
    // 发布 服务列表至少有一条数据
    if (this.serverListDS.data.length === 0) {
      notification.info({
        message: intl
          .get('hitf.chargeGroup.view.message.chargeSetLine.limit')
          .d('至少维护一条服务信息'),
      });
      return;
    }
    // 发布 计费规则至少有一条数据
    if (this.chargeGroupRuleDS.data.length === 0) {
      notification.info({
        message: intl
          .get('hitf.chargeGroup.view.message.chargeGroupRule.limit')
          .d('至少维护一条计费规则'),
      });
      return;
    }
    Modal.confirm({
      title: intl.get('hitf.chargeGroup.view.meaasge.confirm.publish').d('确定发布？'),
      onOk: async () => {
        // 请求参数
        const data = {
          groupHeaderId: this.formDS.current.get('groupHeaderId'),
          _token: this.formDS.current.get('_token'),
        };

        const url = isTenantRoleLevel()
          ? `${HZERO_HITF}/v1/${organizationId}/charge-group-headers/${this.formDS.current.get(
              'groupHeaderId'
            )}/publish`
          : `${HZERO_HITF}/v1/charge-group-headers/${this.formDS.current.get(
              'groupHeaderId'
            )}/publish`;
        try {
          const res = await axios.put(url, data);
          if (res && res.failed) {
            // intl.get(`hitf.chargeGroup.view.meaasge.publish.failed`).d('发布失败')
            notification.error({
              message: res.message,
            });
          } else {
            notification.success({
              message: intl.get('hitf.chargeGroup.view.meaasge.publish.success').d('发布成功'),
            });
            // 发布成功 数据数据
            this.formDS.current.set('statusCode', res.statusCode);
            this.setState({
              // 状态=发布/撤销 保存按钮不可点击
              saveDisabled:
                CHARGE_GROUP_STATUS_FIELDS.PUBLISHED === res.statusCode ||
                CHARGE_GROUP_STATUS_FIELDS.CANCELLED === res.statusCode,
              // 状态=发布 发布按钮不可点击
              publishDisabled: CHARGE_GROUP_STATUS_FIELDS.PUBLISHED === res.statusCode,
              statusCode: res.statusCode,
            });
          }
        } catch (err) {
          notification.error({
            message: intl
              .get('hitf.chargeGroup.view.meaasge.publish.wait')
              .d('发布失败，请稍后再试。'),
          });
        }
      },
    });
  }

  /**
   * tab切换变换
   */
  @Bind()
  handleTabChange(key) {
    this.setState({ activeTabKey: key });
  }

  /**
   * 新建 服务列表/计费规则
   */
  @Bind()
  handleAddLine() {
    const { activeTabKey } = this.state;
    // 先判断当前Tab页
    if (activeTabKey === 'server') {
      this.serverListDS.create();
    } else {
      this.chargeGroupRuleDS.create();
    }
  }

  render() {
    const { saveDisabled, publishDisabled, statusCode } = this.state;
    const {
      match: { path, params },
    } = this.props;
    const { groupHeaderId } = params;
    const isNew = isUndefined(groupHeaderId);
    return (
      <div>
        <Header
          title={intl.get('hitf.chargeGroup.view.message.title.detail').d('组合计费设置详情')}
          backPath="/hitf/charge-group/list"
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.publish`,
                type: 'button',
                meaning: '接口计费设置详情-发布',
              },
            ]}
            icon="check"
            type="default"
            onClick={this.publish}
            disabled={publishDisabled}
          >
            {intl.get('hzero.common.button.release').d('发布')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '接口计费设置详情-保存',
              },
            ]}
            icon="save"
            type="primary"
            onClick={this.save}
            disabled={saveDisabled}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>
                {intl.get('hitf.chargeGroup.view.message.title.basicInformation').d('基本信息')}
              </h3>
            }
          >
            <Form disabled={saveDisabled} dataSet={this.formDS} columns={3}>
              <TextField name="groupCode" required restrict="a-zA-Z0-9-_./" />
              <TextField name="groupName" required />
              <Select name="statusCode" required disabled />
              <TextField name="remark" colSpan={3} />
            </Form>
          </Card>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>
                {intl.get('hitf.chargeGroup.view.message.title.chargeGroupLine').d('设置明细')}
              </h3>
            }
          >
            <Tabs
              animated={false}
              tabBarExtraContent={
                <div style={{ lineHeight: '22px' }}>
                  <ButtonPermission
                    permissionList={[
                      {
                        code: `${path}.button.create`,
                        type: 'button',
                        meaning: '组合计费设置详情行-新增',
                      },
                    ]}
                    type="primary"
                    disabled={isNew || saveDisabled}
                    onClick={this.handleAddLine}
                  >
                    {intl.get('hzero.common.button.increase').d('新增')}
                  </ButtonPermission>
                </div>
              }
              onChange={this.handleTabChange}
            >
              <Tabs.TabPane
                tab={intl.get('hitf.chargeGroup.model.chargeGroupHeader.serverList').d('服务列表')}
                key="server"
              >
                {statusCode && <Server tableDS={this.serverListDS} statusCode={statusCode} />}
              </Tabs.TabPane>
              <Tabs.TabPane
                tab={intl
                  .get('hitf.chargeGroup.model.chargeGroupHeader.chargeGroupRule')
                  .d('计费规则')}
                key="rule"
              >
                {statusCode && <Rule tableDS={this.chargeGroupRuleDS} statusCode={statusCode} />}
              </Tabs.TabPane>
            </Tabs>
          </Card>
        </Content>
      </div>
    );
  }
}
