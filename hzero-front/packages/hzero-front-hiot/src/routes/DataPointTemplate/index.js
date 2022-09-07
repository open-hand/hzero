/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-09-27 16:00:07
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 数据点模板管理列表界面
 */
import React from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import intl from 'utils/intl';

import { dataPointTemplateDS } from '@/stores/dataPointTemplateDS';
import { EDIT_ACTION, NEW_ACTION } from '@/utils/constants';

@formatterCollections({ code: ['hiot.dataPointTemplate', 'hiot.common'] })
export default class DataPointTemplate extends React.Component {
  constructor(props) {
    super(props);
    this.dataPointTemplateDS = new DataSet(dataPointTemplateDS());
  }

  componentDidMount() {
    this.dataPointTemplateDS.setQueryParameter('propertyModelId', undefined);
    this.dataPointTemplateDS.query();
  }

  /**
   * 跳转到对应数据模板详情/编辑页面的页面
   * @param templateId 模板主键id
   * @param type edit: 编辑 detail: 详情
   */
  @Bind()
  handleDetailOrCreate(type, templateId = '') {
    const path =
      type === NEW_ACTION
        ? `/hiot/data-point/template/${NEW_ACTION}`
        : `/hiot/data-point/template/${type}/${templateId}`;
    this.props.history.push(path);
  }

  /**
   * 删除数据点模板
   */
  @Bind()
  async handleDataPointTemplateDelete() {
    if (this.dataPointTemplateDS.selected.length > 0) {
      const res = await this.dataPointTemplateDS.delete(this.dataPointTemplateDS.selected);
      if (!res) {
        return false;
      } else {
        await this.dataPointTemplateDS.query();
      }
    } else {
      notification.warning({
        message: intl.get('hiot.common.view.message.pleaseSelectItem').d('请先选择数据'),
      });
    }
  }

  // 数据点模板table列
  get columns() {
    const {
      match: { path },
    } = this.props;
    return [
      { name: 'propertyModelCode', sortable: true },
      { name: 'propertyModelName' },
      { name: 'dataTypeValue' },
      { name: 'unitCode' },
      { name: 'minValue' },
      { name: 'maxValue' },
      { name: 'description' },
      { name: 'isReferred' },
      {
        name: 'operation',
        width: 80,
        renderer: ({ record }) => {
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
                      meaning: '数据点模型管理-编辑',
                    },
                  ]}
                  onClick={() =>
                    this.handleDetailOrCreate(EDIT_ACTION, record.get('propertyModelId'))
                  }
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
  }

  render() {
    const {
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={intl.get('hiot.dataPointTemplate.view.title.DataPointModel').d('数据点')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: '数据点模版管理-删除',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.handleDetailOrCreate(NEW_ACTION)}
            style={{ marginLeft: 8 }}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: '数据点模版管理-删除',
              },
            ]}
            icon="delete"
            onClick={this.handleDataPointTemplateDelete}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.dataPointTemplateDS} columns={this.columns} queryFieldsLimit={3} />
        </Content>
      </>
    );
  }
}
