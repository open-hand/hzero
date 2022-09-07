/**
 * 数据点类型管理
 */
import React, { Component } from 'react';
import { isEmpty } from 'lodash';
import { DataSet, Table } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import { dataPointTypeListDS } from '@/stores/dataPointTypeManagementDS';

@formatterCollections({ code: ['hiot.dataPointTypeManagement', 'hiot.common'] })
export default class DataPointTypeManagement extends Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet(dataPointTypeListDS());
  }

  /**
   * 处理新建、编辑、详情操作
   * @param record 操作的记录
   * @param operation
   */
  @Bind()
  handleOperation({ record: { pristineData: record } = {} }, operation) {
    const { history } = this.props;
    const path =
      operation === 'new' ? `/hiot/dptm/new` : `/hiot/dptm/${operation}/${record.typeId}`;
    history.push(path);
  }

  @Bind()
  async handleDelete() {
    try {
      const { selected } = this.tableDS;
      if (isEmpty(selected)) {
        notification.warning({
          message: intl.get('hzero.common.validation.atLeastOneRecord').d('请至少选择一条数据'),
        });
        return;
      }
      // 被引用的数据类型不能删除
      if (this.tableDS.toJSONData(true, true).some(({ isReferred }) => isReferred === 1)) {
        notification.warning({
          message: intl
            .get('hiot.dataPointManagement.message.error.selected.isReferred')
            .d('被引用的数据点类型不能删除！'),
        });
        return;
      }
      const resp = await this.tableDS.delete(selected);
      if (resp) {
        this.tableDS.query();
      }
    } catch (error) {
      //
    }
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const columns = [
      { name: 'typeCode' },
      { name: 'typeName' },
      { name: 'category' },
      { name: 'dataType' },
      { name: 'description' },
      { name: 'isReferred' },
      {
        name: 'operation',
        width: 80,
        header: <span>{intl.get('hzero.common.button.action').d('操作')}</span>,
        align: 'center',
        renderer: (record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '数据点类型管理-编辑',
                    },
                  ]}
                  onClick={() => this.handleOperation(record, 'edit')}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
    return (
      <>
        <Header
          title={intl.get('hiot.dataPointTypeManagement.view.title.dataPointType').d('数据点类型')}
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '数据点类型管理-新建',
              },
            ]}
            icon="add"
            color="primary"
            onClick={() => this.handleOperation({}, 'new')}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: '数据点类型管理-删除',
              },
            ]}
            icon="delete"
            onClick={this.handleDelete}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table columns={columns} dataSet={this.tableDS} queryFieldsLimit={3} />
        </Content>
      </>
    );
  }
}
