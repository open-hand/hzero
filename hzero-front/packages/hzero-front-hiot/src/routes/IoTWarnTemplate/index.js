/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-09 09:32:36
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: iot告警模板-列表页
 */
import React from 'react';
import { Table, CheckBox, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';

import { EDIT_ACTION, DETAIL_ACTION, NEW_ACTION } from '@/utils/constants';
import { iotWarnTemplateDS } from '@/stores/iotWarnTemplateDS';

@formatterCollections({ code: ['hiot.dataPointTemplate', 'hiot.iotTemplate', 'hiot.common'] })
export default class IoTWarnTemplate extends React.Component {
  constructor(props) {
    super(props);
    this.iotWarnTemplateDS = new DataSet(iotWarnTemplateDS());
  }

  componentDidMount() {
    this.iotWarnTemplateDS.setQueryParameter('templateId', undefined);
    this.iotWarnTemplateDS.query();
  }

  /**
   * 跳转到对应iot告警模板详情/编辑页面的页面
   * @param templateId 模板主键id
   * @param type edit: 编辑 detail: 详情
   */
  @Bind()
  handleDetailOrCreate(type, templateId = '') {
    const path =
      type === NEW_ACTION
        ? `/hiot/iot-warn/template/${NEW_ACTION}`
        : `/hiot/iot-warn/template/${type}/${templateId}`;
    this.props.history.push(path);
  }

  /**
   * 删除iot告警模板
   */
  @Bind()
  async handleIotWarnTemplateDelete() {
    if (this.iotWarnTemplateDS.selected.length > 0) {
      const res = await this.iotWarnTemplateDS.delete(this.iotWarnTemplateDS.selected);
      if (!res) {
        return false;
      } else {
        await this.iotWarnTemplateDS.query();
      }
    } else {
      notification.warning({
        message: intl.get('hiot.common.view.message.pleaseSelectItem').d('请先选择数据'),
      });
    }
  }

  /**
   * 表格列
   */
  get columns() {
    const {
      match: { path },
    } = this.props;
    return [
      { name: 'alertModelCode' },
      { name: 'alertModelName' },
      { name: 'alertLevelMeaning' },
      {
        name: 'enabled',
        renderer: ({ record }) => (
          <CheckBox defaultChecked={record.get('enabled') === 1} disabled />
        ),
      },
      { name: 'templateName' },
      { name: 'isReferred' },
      {
        name: 'operation',
        renderer: ({ record }) => (
          <span className="action-link">
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.detail`,
                  type: 'button',
                  meaning: 'IOT报警模版-详情',
                },
              ]}
              onClick={() => this.handleDetailOrCreate(DETAIL_ACTION, record.get('alertModelId'))}
              style={{ marginLeft: 10, marginRight: 10 }}
            >
              {intl.get('hzero.common.button.detail').d('详情')}
            </ButtonPermission>
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.edit`,
                  type: 'button',
                  meaning: 'IOT报警模版-编辑',
                },
              ]}
              onClick={() => this.handleDetailOrCreate(EDIT_ACTION, record.get('alertModelId'))}
            >
              {intl.get('hzero.common.button.edit').d('编辑')}
            </ButtonPermission>
          </span>
        ),
      },
    ];
  }

  render() {
    const {
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={intl.get('hiot.iotTemplate.view.title.header').d('IOT告警模板')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: 'IOT告警模版-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.handleDetailOrCreate(NEW_ACTION)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: 'IOT报警模版-删除',
              },
            ]}
            icon="delete"
            onClick={this.handleIotWarnTemplateDelete}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.iotWarnTemplateDS} columns={this.columns} queryFieldsLimit={3} />
        </Content>
      </>
    );
  }
}
