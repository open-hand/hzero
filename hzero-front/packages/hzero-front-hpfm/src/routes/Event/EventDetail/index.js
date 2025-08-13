/**
 * Event - 事件编辑界面
 * @date: 2018-6-20
 * @author: niujiaqing <njq.niu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Card, Col, Form, Modal, Row, Table } from 'hzero-ui';
import { connect } from 'dva';
import { omit } from 'lodash';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';
import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { enableRender, yesOrNoRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { getCurrentOrganizationId, isTenantRoleLevel, tableScrollWidth } from 'utils/utils';
import {
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT_COL_2,
  FORM_COL_3_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

import EventRuleForm from './EventRuleForm';

@connect(({ event, loading }) => ({
  event,
  isSiteFlag: !isTenantRoleLevel(),
  tenantId: getCurrentOrganizationId(),
  fetchLoading: loading.effects['event/getEvent'],
  eventSaving: loading.effects['event/updateEvent'],
  ruleSaving: loading.effects['event/updateRule'],
}))
@formatterCollections({
  code: 'hpfm.event',
})
@Form.create({ fieldNameProp: null })
export default class EditForm extends React.Component {
  state = {
    selectedRowKeys: [],
    event: {},
    rule: {},
    selectedRows: [],
  };

  eventRuleForm;

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'event/init',
    });
    this.loadEvent();
  }

  @Bind()
  loadEvent() {
    const { form, dispatch, match, tenantId } = this.props;
    dispatch({
      type: 'event/getEvent',
      payload: { id: match.params.id, tenantId },
    }).then((res) => {
      if (res) {
        this.setState({
          event: res,
        });
        const formValues = {
          eventCode: res.eventCode,
          eventDescription: res.eventDescription,
          enabledFlag: res.enabledFlag,
        };
        form.setFieldsValue(formValues);
      }
    });
  }

  @Bind()
  showCreateModal() {
    const { match } = this.props;
    this.showEditModal({
      eventRuleId: '',
      eventId: match.params.id,
      callType: 'M',
    });
  }

  @Bind()
  deleteEventRule() {
    const { dispatch, match, tenantId } = this.props;
    const { selectedRows } = this.state;
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk: () => {
        dispatch({
          type: 'event/action',
          method: 'removeRule',
          payload: {
            tenantId,
            selectedRows,
            eventId: match.params.id,
          },
        }).then((res) => {
          if (res) {
            this.setState({
              selectedRowKeys: [],
              selectedRows: [],
            });
            this.loadEvent();
            notification.success();
          }
        });
      },
    });
  }

  @Bind()
  showEditModal(rule) {
    this.setState({
      rule,
    });
    this.showModal();
  }

  @Bind()
  saveEvent() {
    const { form, dispatch, tenantId } = this.props;
    const { event } = this.state;
    if (event.eventId) {
      dispatch({
        type: 'event/updateEvent',
        payload: {
          tenantId,
          ...omit(event, ['ruleList']),
          ...form.getFieldsValue(),
        },
      }).then((response) => {
        if (response) {
          notification.success();
          this.loadEvent();
        }
      });
    }
  }

  @Bind()
  handleRowSelectChange(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  @Bind()
  showModal() {
    this.handleModalVisible(true);
  }

  @Bind()
  hideModal() {
    const { ruleSaving } = this.props;
    if (!ruleSaving) {
      this.handleModalVisible(false);
      // this.setState({
      //   rule:{}
      // })
    }
  }

  handleModalVisible(flag) {
    const { dispatch } = this.props;
    if (flag === false && this.eventRuleForm) {
      this.eventRuleForm.resetForm();
    }

    dispatch({
      type: 'event/updateState',
      payload: {
        ruleModalVisible: !!flag,
      },
    });
  }

  @Bind()
  handleAdd(fieldsValue) {
    const { dispatch, tenantId } = this.props;
    const { rule = {} } = this.state;
    const data = {
      tenantId,
      ...rule,
      ...fieldsValue,
    };
    dispatch({
      type: 'event/updateRule',
      payload: data,
    }).then((res) => {
      if (res) {
        this.loadEvent();
        this.hideModal();
        notification.success();
      }
    });
  }

  render() {
    const {
      match,
      form,
      isSiteFlag,
      tenantId: currentTenantId,
      event: { ruleModalVisible, apiList, typeList },
      eventSaving = false,
      ruleSaving = false,
      fetchLoading = false,
    } = this.props;

    const { event, selectedRowKeys, rule } = this.state;
    const { ruleList = [], tenantName, tenantId: currentDetailTenantId, _token } = event;

    const basePath = match.path.substring(0, match.path.indexOf('/detail'));

    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
    };

    // 是否预定义按钮控制
    const isPredefined = currentTenantId !== currentDetailTenantId;
    const editControl = !isSiteFlag ? isPredefined : false;
    const columns = [
      {
        title: intl.get('hpfm.event.model.eventRule.rule').d('匹配规则'),
        dataIndex: 'matchingRule',
      },
      {
        title: intl.get('hpfm.event.model.eventRule.callType').d('调用类型'),
        align: 'center',
        width: 100,
        dataIndex: 'callTypeMeaning',
      },
      {
        title: intl.get('hpfm.event.model.eventRule.syncFlag').d('是否同步'),
        dataIndex: 'syncFlag',
        align: 'center',
        width: 100,
        render: yesOrNoRender,
      },
      {
        title: intl.get('hpfm.event.model.eventRule.orderSeq').d('顺序'),
        align: 'center',
        width: 75,
        dataIndex: 'orderSeq',
      },
      {
        title: intl.get('hpfm.event.model.eventRule.ruleDescription').d('规则描述'),
        width: 240,
        align: 'center',
        dataIndex: 'ruleDescription',
      },
      {
        title: intl.get('hpfm.event.model.eventRule.resultFlag').d('返回结果'),
        dataIndex: 'resultFlag',
        align: 'center',
        width: 100,
        render: yesOrNoRender,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        align: 'center',
        width: 75,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        fixed: 'right',
        align: 'center',
        width: 75,
        render: (_, record) => {
          const operators = [];
          if (isSiteFlag || !isPredefined) {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '事件编辑界面-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.showEditModal(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
            return operatorRender(operators);
          }
        },
      },
    ];
    return (
      <React.Fragment>
        <Header
          title={intl.get('hpfm.event.view.detail.title').d('事件规则')}
          backPath={`${basePath}/list`}
        >
          <ButtonPermission
            loading={eventSaving}
            disabled={editControl || fetchLoading}
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '事件编辑界面-保存',
              },
            ]}
            onClick={this.saveEvent}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
        <Content>
          <Card
            key="event-header"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hpfm.event.view.title.event').d('事件规则')}</h3>}
            loading={fetchLoading}
          >
            <Form>
              <Row {...SEARCH_FORM_ROW_LAYOUT}>
                {isSiteFlag && (
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hzero.common.model.common.tenantId').d('租户')}
                    >
                      {tenantName}
                    </Form.Item>
                  </Col>
                )}
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hpfm.event.model.event.code').d('事件编码')}
                  >
                    {form.getFieldValue('eventCode')}
                  </Form.Item>
                </Col>
                <Col span={4} offset={2}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hzero.common.status.enable').d('启用')}
                  >
                    {form.getFieldDecorator('enabledFlag')(<Switch disabled={editControl} />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...SEARCH_FORM_ROW_LAYOUT}>
                <Col span={12}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT_COL_2}
                    label={intl.get('hpfm.event.model.event.description').d('事件描述')}
                  >
                    {form.getFieldDecorator('eventDescription')(
                      <TLEditor
                        label={intl.get('hcuz.custButton.view.title.description').d('事件描述')}
                        field="eventDescription"
                        disabled={editControl}
                        token={_token}
                      />
                    )}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Card>
          <Card
            key="event-rule"
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            title={<h3>{intl.get('hpfm.event.view.title.eventRule').d('匹配规则')}</h3>}
          >
            {(isSiteFlag || !isPredefined) && (
              <div className="table-list-operator">
                <ButtonPermission
                  type="primary"
                  permissionList={[
                    {
                      code: `${match.path}.button.create`,
                      type: 'button',
                      meaning: '事件编辑界面-新建规则',
                    },
                  ]}
                  onClick={this.showCreateModal}
                >
                  {intl.get('hpfm.event.view.detail.button.create').d('新建规则')}
                </ButtonPermission>
                <ButtonPermission
                  disabled={selectedRowKeys.length === 0}
                  permissionList={[
                    {
                      code: `${match.path}.button.remove`,
                      type: 'button',
                      meaning: '事件编辑界面-删除规则',
                    },
                  ]}
                  onClick={this.deleteEventRule}
                >
                  {intl.get('hpfm.event.view.detail.button.remove').d('删除规则')}
                </ButtonPermission>
              </div>
            )}
            <Table
              bordered
              rowKey="eventRuleId"
              pagination={false}
              rowSelection={rowSelection}
              dataSource={ruleList}
              columns={columns}
              scroll={{ x: tableScrollWidth(columns, 200) }}
            />
          </Card>
        </Content>
        {ruleModalVisible && (
          <EventRuleForm
            title={
              rule.eventRuleId
                ? intl.get('hpfm.event.view.detail.button.edit').d('编辑规则')
                : intl.get('hpfm.event.view.detail.button.create').d('新建规则')
            }
            eventRule={rule}
            onRef={(ref) => {
              this.eventRuleForm = ref;
            }}
            apiList={apiList}
            typeList={typeList}
            handleAdd={this.handleAdd}
            confirmLoading={ruleSaving}
            modalVisible={ruleModalVisible}
            hideModal={this.hideModal}
          />
        )}
      </React.Fragment>
    );
  }
}
