import React, { useState } from 'react';
import { Col, Form, Input, Modal, Row, Spin, Button, Table, Divider } from 'hzero-ui';
import uuid from 'uuid/v4';

import Lov from 'components/Lov';
import Switch from 'components/Switch';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { MODAL_FORM_ITEM_LAYOUT, EDIT_FORM_CLASSNAME } from 'utils/constants';

import CreatePoint from './CreatePoint';

const onCell = () => {
  return {
    style: {
      overflow: 'hidden',
      textOverflow: 'ellipsis',
      whiteSpace: 'nowrap',
    },
    onClick: (e) => {
      const { target } = e;
      if (target.style.whiteSpace === 'normal') {
        target.style.whiteSpace = 'nowrap';
      } else {
        target.style.whiteSpace = 'normal';
      }
    },
  };
};

function Editor(props) {
  const [selectedPointRowKeys, setSelectedPointRowKeys] = useState([]);
  const [selectPointRows, setSelectPointRows] = useState([]);
  const [selectedRuleRowKeys, setSelectedRuleRowKeys] = useState([]);
  const [selectRuleRows, setSelectRuleRows] = useState([]);
  const [createVisible, setCreateVisible] = useState(false);
  const pointColumns = [
    {
      title: intl.get('hpfm.customize.model.customize.point.serviceName').d('服务名'),
      dataIndex: 'serviceName',
      width: 150,
    },
    {
      title: intl.get('hpfm.customize.model.customize.point.packageName').d('包名'),
      dataIndex: 'packageName',
      onCell,
      width: 150,
    },
    {
      title: intl.get('hpfm.customize.model.customize.point.className').d('类名'),
      dataIndex: 'className',
      onCell,
      width: 150,
    },
    {
      title: intl.get('hpfm.customize.model.customize.point.methodName').d('方法名'),
      dataIndex: 'methodName',
      onCell,
      width: 200,
    },
    {
      title: intl.get('hpfm.customize.model.customize.point.methodArgs').d('方法参数列表'),
      dataIndex: 'methodArgs',
      onCell,
      width: 200,
    },
  ];
  const ruleColumns = [
    {
      title: intl.get('hpfm.customize.model.customize.rule.ruleCode').d('规则编码'),
      dataIndex: 'ruleCode',
      onCell,
      width: 200,
    },
    {
      title: intl.get('hpfm.customize.model.customize.rule.ruleName').d('规则名称'),
      dataIndex: 'ruleName',
      onCell,
      width: 200,
    },
  ];
  const {
    form,
    dispatch,
    initData = {},
    title = '',
    isSiteFlag,
    pointToRangeList = [],
    ruleToRangeList = [],
    visible = false,
    loading = false,
    initLoading = false,
    deletePointLoading = false,
    deleteRuleLoading = false,
    onCancel = (e) => e,
  } = props;
  const { getFieldDecorator } = form;
  const { tenantId, tenantName, description, enabledFlag = 1 } = initData;

  const handleOk = () => {
    const { onOk = (e) => e } = props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk({ ...initData, ...fieldsValue });
      }
    });
  };

  const handleCancel = () => {
    onCancel();
  };

  const handleSelectPointTable = (keys, rows) => {
    setSelectedPointRowKeys(keys);
    setSelectPointRows(rows);
  };

  const handleSelectRuleTable = (keys, rows) => {
    setSelectedRuleRowKeys(keys);
    setSelectRuleRows(rows);
  };

  const fetchPointListToRange = () => {
    dispatch({
      type: 'customize/fetchPointListToRange',
      payload: { rangeId: initData.rangeId },
    });
  };

  const fetchRuleListToRange = () => {
    dispatch({
      type: 'customize/fetchRuleListToRange',
      payload: { rangeId: initData.rangeId },
    });
  };

  const hasCheck = (list, data) => {
    const flag = list.some((item) => {
      return (
        item.className === data.className &&
        item.serviceName === data.serviceName &&
        item.methodName === data.methodName &&
        item.packageName === data.packageName &&
        item.methodArgs === data.methodArgs
      );
    });
    if (flag) {
      return notification.warning({
        message: intl.get('hzero.common.message.dataExists').d('数据已存在'),
      });
    }
  };

  const handleCreatePoint = (data) => {
    hasCheck(pointToRangeList, data);
    dispatch({
      type: 'customize/updateState',
      payload: {
        pointToRangeList: [...pointToRangeList, data],
      },
    });
    setCreateVisible(false);
  };

  const deletePoint = () => {
    const createList = selectPointRows.filter((item) => item.isCreate);
    const filterList = selectPointRows.filter((item) => !item.isCreate);
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk: () => {
        if (createList.length > 0) {
          const deleteList = pointToRangeList.filter((item) => {
            return !selectedPointRowKeys.includes(item.rangePointId);
          });
          dispatch({
            type: 'customize/updateState',
            payload: {
              pointToRangeList: deleteList,
            },
          });
        }
        if (filterList.length > 0) {
          dispatch({
            type: 'customize/deletePointToRange',
            payload: { rangeId: initData.rangeId, list: filterList },
          }).then((res) => {
            if (res) {
              notification.success();
              setSelectedPointRowKeys([]);
              fetchPointListToRange();
            }
          });
        }
      },
    });
  };

  const deleteRule = () => {
    const createList = selectRuleRows.filter((item) => item.isCreate);
    const filterList = selectRuleRows.filter((item) => !item.isCreate);
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk: () => {
        if (createList.length > 0) {
          const deleteList = ruleToRangeList.filter((item) => {
            return !selectedRuleRowKeys.includes(item.rangeRuleId);
          });
          dispatch({
            type: 'customize/updateState',
            payload: {
              ruleToRangeList: deleteList,
            },
          });
        }
        if (filterList.length > 0) {
          dispatch({
            type: 'customize/deleteRuleToRange',
            payload: { rangeId: initData.rangeId, list: filterList },
          }).then((res) => {
            if (res) {
              notification.success();
              setSelectedRuleRowKeys([]);
              fetchRuleListToRange();
            }
          });
        }
      },
    });
  };

  const createRule = (val, record) => {
    const flag = ruleToRangeList.some((item) => {
      return item.ruleCode === record.ruleCode && item.ruleName === record.ruleName;
    });
    if (flag) {
      return notification.warning({
        message: intl.get('hzero.common.message.dataExists').d('数据已存在'),
      });
    }
    dispatch({
      type: 'customize/updateState',
      payload: {
        ruleToRangeList: [
          ...ruleToRangeList,
          {
            isCreate: true,
            rangeRuleId: uuid(),
            ruleId: record.ruleId,
            ruleCode: record.ruleCode,
            ruleName: record.ruleName,
          },
        ],
      },
    });
  };

  const pointRowSelection = {
    selectedPointRowKeys,
    onChange: handleSelectPointTable,
  };

  const ruleRowSelection = {
    selectedRuleRowKeys,
    onChange: handleSelectRuleTable,
  };

  return (
    <Modal
      destroyOnClose
      wrapClassName="ant-modal-sidebar-right"
      transitionName="move-right"
      title={title}
      width="820px"
      visible={visible}
      confirmLoading={loading}
      onCancel={handleCancel}
      onOk={handleOk}
    >
      <Spin spinning={initLoading}>
        <Form className={EDIT_FORM_CLASSNAME}>
          <Row>
            {isSiteFlag && (
              <Col span={12}>
                <Form.Item
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hzero.common.model.tenantName').d('租户')}
                  style={{ marginBottom: '12px' }}
                >
                  {getFieldDecorator('tenantId', {
                    initialValue: tenantId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hzero.common.model.tenantName').d('租户'),
                        }),
                      },
                    ],
                  })(<Lov code="HPFM.TENANT" textValue={tenantName} />)}
                </Form.Item>
              </Col>
            )}
            <Col span={12}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hpfm.dataGroup.model.dataGroup.name').d('名称')}
              >
                {getFieldDecorator('description', {
                  initialValue: description,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.dataGroup.model.dataGroup.name').d('名称'),
                      }),
                    },
                    {
                      max: 480,
                      message: intl.get('hzero.common.validation.max', {
                        max: 480,
                      }),
                    },
                  ],
                })(<Input trim />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hzero.common.status.enable').d('启用')}
              >
                {getFieldDecorator('enabledFlag', {
                  initialValue: enabledFlag,
                })(<Switch />)}
              </Form.Item>
            </Col>
          </Row>
        </Form>
        <Row>
          <Col>
            <Divider orientation="left">
              {intl.get('hpfm.customize.view.title.point').d('个性化切入点')}
            </Divider>
            <div className="table-operator">
              <Button
                disabled={selectedPointRowKeys.length === 0}
                onClick={deletePoint}
                loading={deletePointLoading}
              >
                {intl.get('hzero.common.button.delete').d('删除')}
              </Button>
              <Button onClick={() => setCreateVisible(true)}>
                {intl.get('hzero.common.button.create').d('新建')}
              </Button>
            </div>
            <Table
              bordered
              rowKey="rangePointId"
              rowSelection={pointRowSelection}
              loading={false}
              dataSource={pointToRangeList}
              columns={pointColumns}
              pagination={false}
            />
            <CreatePoint
              visible={createVisible}
              pointToRangeList={pointToRangeList}
              onCancel={() => setCreateVisible(false)}
              onOk={handleCreatePoint}
            />
          </Col>
        </Row>
        <Row>
          <Col>
            <Divider orientation="left">
              {intl.get('hpfm.customize.view.title.rule').d('个性化规则')}
            </Divider>
            <div className="table-operator">
              <Button
                disabled={selectedRuleRowKeys.length === 0}
                onClick={deleteRule}
                loading={deleteRuleLoading}
              >
                {intl.get('hzero.common.button.delete').d('删除')}
              </Button>
              <Lov
                isButton
                code="HPFM.CUSTOMIZE_RULE"
                onChange={createRule}
                disabled={form.getFieldValue('tenantId') === undefined}
                queryParams={{
                  tenantId: form.getFieldValue('tenantId'),
                  rangeId: initData.rangeId,
                }}
              >
                {intl.get('hzero.common.button.create').d('新建')}
              </Lov>
            </div>
            <Table
              bordered
              rowKey="rangeRuleId"
              rowSelection={ruleRowSelection}
              loading={false}
              dataSource={ruleToRangeList}
              columns={ruleColumns}
              pagination={false}
            />
          </Col>
        </Row>
      </Spin>
    </Modal>
  );
}

export default Form.create({ fieldNameProp: null })(Editor);
