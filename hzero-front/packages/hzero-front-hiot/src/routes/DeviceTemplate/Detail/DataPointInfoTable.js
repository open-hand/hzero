/**
 * 设备模板——添加数据点组模板
 */
import React, { PureComponent } from 'react';
import { Table, Modal, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import intl from 'utils/intl';
import { Button as ButtonPermission } from 'components/Permission';

import { dataPointInfoTableDS, dataPointModalDS } from '@/stores/deviceTemplateDS';

const modalKay = Modal.key();
const prefix = 'hiot.deviceTemplate';

export default class DataPointInfoTable extends PureComponent {
  constructor(props) {
    super(props);
    const { DS, headDs, actionDs } = props;
    this.tableDS = DS;
    this.headDs = headDs;
    this.actionDs = actionDs;
    this.dataPointModalDS = new DataSet(dataPointModalDS());
  }

  @Bind()
  handleOpenModal(type) {
    const { thingModelId } = this.props;
    this.dataPointModalDS.setQueryParameter('type', type);
    this.dataPointModalDS.setQueryParameter('thingModelId', thingModelId);
    this.dataPointModalDS.query();
    const columns = [
      {
        name: 'propertyModelName',
        type: 'string',
        label: intl.get('hiot.common.name').d('名称'),
      },
      {
        name: 'propertyModelCode',
        type: 'string',
        label: intl.get('hiot.common.code').d('编码'),
      },
      {
        name: 'description',
        type: 'string',
        label: intl.get('hzero.common.explain').d('说明'),
      },
    ];
    Modal.open({
      key: modalKay,
      title:
        type === 'group'
          ? intl.get(`${prefix}.view.button.addDataPointGroup`).d('添加数据点组模板')
          : intl.get('hiot.common.view.add.data.point.template').d('添加数据点模板'),
      closable: true,
      destroyOnClose: true,
      style: {
        width: 800,
        top: 10,
      },
      children: <Table queryFieldsLimit={2} columns={columns} dataSet={this.dataPointModalDS} />,
      onOk: () => {
        this.handleOk();
      },
      onClose: () => {
        this.dataPointModalDS.clearCachedSelected();
        this.dataPointModalDS.unSelectAll();
      },
      onCancel: () => {
        this.dataPointModalDS.clearCachedSelected();
        this.dataPointModalDS.unSelectAll();
      },
    });
  }

  @Bind()
  async handleOk() {
    const { selected } = this.dataPointModalDS;
    if (!isEmpty(selected)) {
      const propertyModelList = this.dataPointModalDS.selected.map((item) =>
        item.get('propertyModelId')
      );
      const { __dirty, _token, ...thingModel } = this.headDs.current.toData();
      this.actionDs.create(
        {
          thingModel,
          propertyModelList,
        },
        0
      );
      await this.actionDs.submit();
      this.tableDS.query();
    }
  }

  /**
   * 删除
   */
  @Bind()
  async handleDelete(record) {
    const { __dirty, _token, ...thingModel } = this.headDs.current.toData();
    record.set('deletePropertyModelList', [record.get('propertyModelId')]);
    record.set('thingModel', thingModel);
    await this.tableDS.delete(record);
    this.tableDS.query();
  }

  render() {
    const { isReadOnly, isReferred, path, registerFlag } = this.props;
    const columns = [...dataPointInfoTableDS().fields.map((item) => ({ name: item.name }))];
    if (!isReadOnly && !isReferred && !registerFlag) {
      columns.push({
        name: 'action',
        width: 100,
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        renderer: ({ record }) => {
          return (
            <span className="action-link">
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.delete`,
                    type: 'button',
                    meaning: '数据点信息-删除',
                  },
                ]}
                onClick={() => this.handleDelete(record)}
              >
                {intl.get('hzero.common.button.delete').d('删除')}
              </ButtonPermission>
            </span>
          );
        },
      });
    }
    const buttons = [
      <ButtonPermission
        type="c7n-pro"
        permissionList={[
          {
            code: `${path}.button.template`,
            type: 'button',
            meaning: '数据点信息-添加数据点组模版',
          },
        ]}
        key="addDataPoint"
        icon="add"
        onClick={() => this.handleOpenModal('single')}
      >
        {intl.get('hiot.common.view.add.data.point.template').d('添加数据点模板')}
      </ButtonPermission>,
    ];
    return (
      <Table
        mode="tree"
        columns={columns}
        buttons={isReadOnly || isReferred ? [] : buttons}
        dataSet={this.tableDS}
      />
    );
  }
}
