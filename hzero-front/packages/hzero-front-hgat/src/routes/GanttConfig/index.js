/**
 * ganttConfig 标签管理
 * @date: 2020-3-23
 * @author: jmy <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React from 'react';
import { Table, Modal, ModalContainer, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import { HZERO_PLATFORM } from 'utils/config';

import { initDS, drawerDS } from '../../stores/ganttConfigDS';
import Drawer from './Drawer';

@formatterCollections({ code: ['hgat.ganttConfig'] })
export default class GanttConfig extends React.Component {
  initDs = new DataSet(initDS());

  get columns() {
    return [
      {
        name: 'ganttCode',
        width: 200,
      },
      {
        name: 'ganttName',
        width: 200,
      },
      {
        name: 'remark',
      },
      {
        name: 'enabledFlag',
        width: 100,
        renderer: ({ value }) => enableRender(value),
      },
      {
        name: 'action',
        width: 160,
        renderer: ({ record }) => {
          const { data = {} } = record;
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${this.props.match.path}.button.edit`,
                    type: 'button',
                    meaning: '甘特图配置-编辑',
                  },
                ]}
                onClick={() => this.handleUpdate(false, data)}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          operators.push({
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${this.props.match.path}.button.config`,
                    type: 'button',
                    meaning: '甘特图配置-编辑',
                  },
                ]}
                onClick={() => this.handleEdit(data)}
              >
                {intl.get('hgat.ganttConfig.view.message.detailTitle').d('甘特图配置')}
              </ButtonPermission>
            ),
            len: 5,
            title: intl.get('hgat.ganttConfig.view.message.detailTitle').d('甘特图配置'),
          });
          return operatorRender(operators);
        },
      },
    ];
  }

  /**
   * 新建/编辑知识类别
   * @param {boolean} isCreate
   * @param {object} [record={}]
   * @memberof ganttConfig
   */
  @Bind()
  handleUpdate(isCreate, record = {}) {
    const { ganttId } = record;
    this.drawerDs = new DataSet(drawerDS());
    this.drawerDs.create({}, 0);
    Modal.open({
      closable: true,
      key: 'gantt-config',
      title: isCreate
        ? intl.get('hgat.ganttConfig.view.message.crete').d('新建甘特图配置')
        : intl.get('hgat.ganttConfig.view.message.edit').d('编辑甘特图配置'),
      drawer: true,
      style: {
        width: 500,
      },
      children: <Drawer ds={this.drawerDs} id={ganttId} isCreate={isCreate} />,
      onOk: this.handleOk,
    });
  }

  /**
   * 保存知识类别数据
   */
  @Bind()
  async handleOk() {
    const validate = await this.drawerDs.submit();
    if (!validate) {
      return false;
    }
    this.initDs.query();
  }

  /**
   * 保存知识类别数据
   */
  @Bind()
  async handleEdit(record = {}) {
    const { ganttId, ganttCode } = record;
    const { history } = this.props;
    history.push(`${HZERO_PLATFORM}/gantt/detail/${ganttId || 'create'}/${ganttCode}`);
  }

  render() {
    const { location, match } = this.props;
    return (
      <>
        <Header title={intl.get('hgat.ganttConfig.view.message.title.ganttConfig').d('甘特图管理')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '甘特图配置-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.handleUpdate(true)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table
            dataSet={this.initDs}
            highLightRow={false}
            columns={this.columns}
            queryFieldsLimit={3}
          />
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }
}
