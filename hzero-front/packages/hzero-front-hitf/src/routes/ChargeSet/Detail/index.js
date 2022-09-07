/**
 * ChargeSet - 接口/服务计费详情
 * @date: 2020-2-17
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { DataSet, Table, Form, TextField, Select, DatePicker, Lov, Modal } from 'choerodon-ui/pro';
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
import { CHARGE_SET_STATUS_FIELDS, CHARGE_TYPE_FIELDS } from '@/constants/CodeConstants';
import ChargeSetHeaderDS from '../../../stores/ChargeSet/ChargeSetHeaderDS';
import ChargeSetLineDS from '../../../stores/ChargeSet/ChargeSetLineDS';

const organizationId = getCurrentOrganizationId();

/**
 * 接口/服务计费详情
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hitf.chargeSet'] })
export default class ChargeSetLine extends Component {
  constructor(props) {
    super(props);

    // 接口/服务计费设置行 数据源
    this.tableDS = new DataSet({
      ...ChargeSetLineDS(),
      autoQuery: false,
    });
    // 接口/服务计费设置头 数据源
    this.formDS = new DataSet({
      ...ChargeSetHeaderDS(),
      autoQuery: false,
      children: {
        chargeSetLineList: this.tableDS,
      },
    });

    this.state = {
      saveDisabled: false, // 保存按钮是否可点击
      publishDisabled: false, // 发布按钮是否可点击
      interfaceFlag: true, // 计费类型是否为接口
    };
  }

  async componentDidMount() {
    const { match } = this.props;
    // 查看/编辑页面
    if (match && match.params && match.params.setHeaderId) {
      // 设置查询参数
      this.formDS.queryParameter.setHeaderId = this.props.match.params.setHeaderId;
      this.tableDS.queryParameter.setHeaderId = this.props.match.params.setHeaderId;
      await this.formDS.query();

      this.setState({
        // 状态=发布/撤销 保存按钮不可点击
        saveDisabled:
          CHARGE_SET_STATUS_FIELDS.PUBLISHED === this.formDS.current.get('statusCode') ||
          CHARGE_SET_STATUS_FIELDS.CANCELLED === this.formDS.current.get('statusCode'),
        // 状态=发布 发布按钮不可点击
        publishDisabled:
          CHARGE_SET_STATUS_FIELDS.PUBLISHED === this.formDS.current.get('statusCode'),
        // 计费类型=接口
        interfaceFlag: CHARGE_TYPE_FIELDS.INTERFACE === this.formDS.current.get('typeCode'),
      });
    } else {
      // 新建页面
      this.formDS.create();
    }
  }

  /**
   * 保存
   */
  @Bind()
  async save() {
    const { match } = this.props;
    const { setHeaderId } = match.params;
    const isCreate = isUndefined(setHeaderId);

    // 先判断头行是否都填写完整
    if ((await this.formDS.validate()) && (await this.tableDS.validate())) {
      const result = await this.formDS.submit();
      if (getResponse(result)) {
        if (isCreate) {
          this.handleGotoDetail(result.content[0].setHeaderId);
        } else {
          await this.formDS.query();
        }
      }
    } else {
      notification.info({
        message: intl.get('hitf.chargeSet.view.message.validate').d('请先完善必输内容'),
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
        pathname: `/hitf/charge-set/line/${id}`,
      })
    );
  }

  /**
   * 发布
   */
  @Bind()
  async publish() {
    // 先判断数据是否已经保存或者数据是否存在修改未保存的
    if (
      !this.formDS.current ||
      !this.formDS.current.get('setHeaderId') ||
      (await this.formDS.isModified()) ||
      (await this.tableDS.isModified())
    ) {
      notification.info({
        message: intl.get('hitf.chargeSet.view.meaasge.confirm.save').d('请先保存数据'),
      });
      return;
    }
    // 发布时，计费规则至少有一条数据
    if (this.tableDS.data.length === 0) {
      notification.info({
        message: intl
          .get('hitf.chargeSet.view.message.chargeSetLine.limit')
          .d('至少维护一条计费规则'),
      });
      return;
    }
    Modal.confirm({
      title: intl.get('hitf.chargeSet.view.meaasge.confirm.publish').d('确定发布？'),
      onOk: async () => {
        const data = {
          setHeaderId: this.formDS.current.get('setHeaderId'),
          _token: this.formDS.current.get('_token'),
        };

        const url = isTenantRoleLevel()
          ? `${HZERO_HITF}/v1/${organizationId}/${this.formDS.current.get(
              'setHeaderId'
            )}/charge-set-headers/publish`
          : `${HZERO_HITF}/v1/charge-set-headers/${this.formDS.current.get('setHeaderId')}/publish`;
        try {
          const res = await axios.put(url, data);
          if (res && res.failed) {
            // intl.get('hitf.chargeSet.view.meaasge.publish.failed').d('发布失败')
            notification.error({
              message: res.message,
            });
          } else {
            notification.success({
              message: intl.get('hitf.chargeSet.view.meaasge.publish.success').d('发布成功'),
            });
            // 发布成功 刷新数据
            this.formDS.current.set('statusCode', res.statusCode);
            this.setState({
              // 状态=发布/撤销 保存按钮不可点击
              saveDisabled:
                CHARGE_SET_STATUS_FIELDS.PUBLISHED === res.statusCode ||
                CHARGE_SET_STATUS_FIELDS.CANCELLED === res.statusCode,
              // 状态=发布 发布按钮不可点击
              publishDisabled: CHARGE_SET_STATUS_FIELDS.PUBLISHED === res.statusCode,
              // 计费类型=接口
              interfaceFlag: CHARGE_TYPE_FIELDS.INTERFACE === res.typeCode,
            });
          }
        } catch (err) {
          notification.error({
            message: intl
              .get('hitf.chargeSet.view.meaasge.publish.wait')
              .d('发布失败，请稍后再试。'),
          });
        }
      },
    });
  }

  /**
   * 选择计费类型，控制接口ID/接口代码/接口名称字段是否必输
   */
  @Bind()
  changeTypeCode(value) {
    // 计费类型!=接口 接口Lov不可以选择
    const interfaceFlag = CHARGE_TYPE_FIELDS.INTERFACE === value;
    if (!interfaceFlag) {
      this.formDS.current.set('interfaceObject', null);
      this.formDS.current.set('interfaceId', null);
      this.formDS.current.set('interfaceName', null);
      this.formDS.current.set('interfaceCode', null);
    }
    this.setState({
      interfaceFlag,
    });
  }

  render() {
    const { saveDisabled, publishDisabled, interfaceFlag } = this.state;
    const {
      match: { path, params },
    } = this.props;
    const { setHeaderId } = params;
    const isNew = isUndefined(setHeaderId);
    return (
      <>
        <Header
          title={intl
            .get('hitf.chargeSet.view.message.title.chargeSet.detail')
            .d('接口计费设置详情')}
          backPath="/hitf/charge-set/list"
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
                {intl.get('hitf.chargeSet.view.message.title.basicInformation').d('基本信息')}
              </h3>
            }
          >
            <Form disabled={saveDisabled} dataSet={this.formDS} columns={3}>
              <TextField name="setCode" required restrict="a-zA-Z0-9-_./" />
              <TextField name="setName" required />
              <Select name="typeCode" required onChange={this.changeTypeCode} />
              <Lov name="serverObject" required />
              <TextField name="serverName" disabled />
              <Select name="statusCode" required disabled />
              <Lov name="interfaceObject" required={interfaceFlag} disabled={!interfaceFlag} />
              <TextField name="interfaceName" disabled />
              <DatePicker name="startDate" />
              <TextField name="remark" colSpan={3} />
            </Form>
          </Card>
          {this.tableDS && (
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={
                <h3>{intl.get('hitf.chargeSet.view.message.title.chargeRule').d('计费规则')}</h3>
              }
            >
              {!saveDisabled && (
                <>
                  <div style={{ textAlign: 'right' }}>
                    <ButtonPermission
                      permissionList={[
                        {
                          code: `${path}.button.create`,
                          type: 'button',
                          meaning: '接口计费设置详情-新建',
                        },
                      ]}
                      type="primary"
                      disabled={isNew || saveDisabled}
                      onClick={() => this.tableDS.create()}
                    >
                      {intl.get('hzero.common.button.create').d('新建')}
                    </ButtonPermission>
                  </div>
                  <br />
                </>
              )}
              <Table
                dataSet={this.tableDS}
                disabled={saveDisabled}
                columns={[
                  {
                    name: 'seqNumber',
                    lock: 'left',
                    align: 'center',
                    width: 70,
                    editor: true,
                  },
                  {
                    name: 'chargeRuleObject',
                    editor: true,
                  },
                  {
                    name: 'paymentModel',
                  },
                  {
                    name: 'settlementPeriod',
                    editor: true,
                  },
                  {
                    name: 'startDate',
                    align: 'center',
                    width: 130,
                  },
                  {
                    name: 'endDate',
                    align: 'center',
                    width: 130,
                  },
                  {
                    name: 'remark',
                    editor: true,
                  },
                  {
                    header: intl.get('hzero.common.button.action').d('操作'),
                    lock: 'right',
                    align: 'center',
                    width: 200,
                    renderer: ({ record }) => (
                      <span className="action-link">
                        <a onClick={() => this.tableDS.delete(record)}>
                          {intl.get('hzero.common.button.delete').d('删除')}
                        </a>
                      </span>
                    ),
                    hidden: saveDisabled,
                  },
                ]}
                queryBar="none"
                editMode={saveDisabled ? 'inline' : 'cell'}
              />
            </Card>
          )}
        </Content>
      </>
    );
  }
}
