import React from 'react';
import { Card, Button, Popconfirm, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import notification from 'utils/notification';

import { DETAIL_CARD_TABLE_CLASSNAME } from 'utils/constants';
import { operatorRender } from 'utils/renderer';

import ExpressionDrawer from './ExpressionDrawer';

export default class ExpressionParamter extends React.Component {
  state = {
    visible: false,
    currentRecord: {},
  };

  @Bind()
  handleDelete(record) {
    const { dispatch, parameterList = [] } = this.props;
    const filterList = parameterList.filter((item) => item.parameterId !== record.parameterId);
    dispatch({
      type: 'serviceDefinition/updateState',
      payload: { parameterList: filterList },
    });
    notification.success();
  }

  @Bind()
  showEditModal(flag, record = {}) {
    this.setState({ visible: flag, currentRecord: record });
  }

  @Bind
  handleParamterSave(data = {}) {
    const { dispatch, parameterList = [] } = this.props;
    const { currentRecord } = this.state;
    if (currentRecord.parameterId !== undefined) {
      const filterList = parameterList.map((item) => {
        if (item.parameterId === currentRecord.parameterId) {
          return { ...item, ...data };
        } else {
          return { ...item };
        }
      });
      dispatch({
        type: 'serviceDefinition/updateState',
        payload: { parameterList: filterList },
      });
    } else {
      dispatch({
        type: 'serviceDefinition/updateState',
        payload: { parameterList: [data, ...parameterList] },
      });
    }
    this.showEditModal(false);
  }

  @Bind()
  getColumns() {
    if (!this.columns) {
      this.columns = [
        {
          title: intl.get('hwfp.serviceDefinition.model.param.orderNumber').d('序号'),
          dataIndex: 'parameterName',
        },
        {
          title: intl.get('hwfp.serviceDefinition.model.param.leftParameterValue').d('左参数值'),
          dataIndex: 'parameterValue',
          width: 150,
        },
        {
          title: intl
            .get('hwfp.serviceDefinition.model.service.leftParameterSource')
            .d('左参数来源'),
          dataIndex: 'parameterSourceMeaning',
          width: 150,
        },
        {
          title: intl.get('hwfp.serviceDefinition.model.param.operator').d('操作符'),
          dataIndex: 'operator',
          width: 150,
        },
        {
          title: intl.get('hwfp.serviceDefinition.model.param.rightParameterValue').d('右参数值'),
          dataIndex: 'rightParameterValue',
          width: 150,
        },
        {
          title: intl
            .get('hwfp.serviceDefinition.model.service.rightParameterSource')
            .d('右参数来源'),
          dataIndex: 'rightParameterSourceMeaning',
          width: 150,
        },
        {
          title: intl.get('hzero.common.button.action').d('操作'),
          dataIndex: 'edit',
          width: 120,
          render: (val, record) => {
            const operators = [
              {
                key: 'edit',
                ele: (
                  <a
                    onClick={() => {
                      this.showEditModal(true, record);
                    }}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </a>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
              {
                key: 'delete',
                ele: (
                  <Popconfirm
                    placement="topRight"
                    title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                    onConfirm={() => this.handleDelete(record)}
                  >
                    <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                  </Popconfirm>
                ),
                len: 2,
                title: intl.get('hzero.common.button.delete').d('删除'),
              },
            ];
            return operatorRender(operators, record);
          },
        },
      ];
    }
    return this.columns;
  }

  @Bind()
  handleChangeSource(value) {
    const { onChangeSource = (e) => e } = this.props;
    onChangeSource(value);
  }

  render() {
    const {
      paramsLoading = false,
      parameterList = [],
      paramterSourceList = [],
      variableList = [],
      serviceOperatorList = [],
    } = this.props;
    const { visible, currentRecord } = this.state;
    const paramsProps = {
      modalVisible: visible,
      paramterSourceList,
      variableList,
      serviceOperatorList,
      onChangeSource: this.handleChangeSource,
      initData: currentRecord,
      onOk: this.handleParamterSave,
      onCancel: () => this.showEditModal(false, {}),
    };
    return (
      <>
        <Card
          bordered={false}
          className={DETAIL_CARD_TABLE_CLASSNAME}
          title={<h3>{intl.get('hwfp.serviceDefinition.view.title.parameter').d('参数')}</h3>}
        >
          <div className="table-list-operator">
            <Button
              type="primary"
              // disabled={!isSiteFlag ? isPredefined : false}
              onClick={() => this.showEditModal(true)}
              icon="plus"
            >
              {intl.get('hzero.common.button.create').d('新建')}
            </Button>
          </div>
          <Table
            bordered
            loading={paramsLoading}
            rowKey="parameterId"
            dataSource={parameterList}
            columns={this.getColumns()}
            pagination={false}
          />
          {visible && <ExpressionDrawer {...paramsProps} />}
        </Card>
      </>
    );
  }
}
