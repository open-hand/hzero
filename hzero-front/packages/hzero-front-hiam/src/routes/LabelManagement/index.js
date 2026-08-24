/**
 * labelManagement 标签管理
 * @date: 2020-2-26
 * @author: jmy <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React from 'react';
import { Table, Modal, ModalContainer, DataSet, Tooltip } from 'choerodon-ui/pro';
import { Icon } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, yesOrNoRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';

import { initDS, drawerDS } from '../../stores/labelManagementDS';
import Drawer from './Drawer';

@formatterCollections({ code: ['hiam.labelManagement'] })
export default class labelManagement extends React.Component {
  initDs = new DataSet(initDS());

  drawerDs = null;

  get columns() {
    return [
      {
        name: 'name',
        width: 200,
      },
      {
        name: 'typeMeaning',
        width: 200,
      },
      // {
      //   name: 'levelMeaning',
      //   width: 200,
      // },
      {
        name: 'tagMeaning',
        width: 200,
      },
      {
        name: 'enabledFlag',
        width: 100,
        renderer: ({ value }) => enableRender(value),
      },
      {
        name: 'description',
      },
      {
        name: 'presetFlag',
        width: 140,
        renderer: ({ value }) => yesOrNoRender(value),
        header: () => {
          return (
            <>
              {intl.get('hiam.labelManagement.model.labelManagement.presetFlag').d('是否内置标签')}{' '}
              <Tooltip
                title={intl
                  .get('hiam.labelManagement.view.title.presetFlag')
                  .d('用于标识标签是否属于系统内置的，即标识是否是系统内部使用的标签')}
              >
                <Icon type="help_outline" />
              </Tooltip>
            </>
          );
        },
      },
      {
        name: 'inheritFlag',
        width: 120,
        renderer: ({ value }) => yesOrNoRender(value),
        header: () => {
          return (
            <>
              {intl.get('hiam.labelManagement.model.labelManagement.inheritFlag').d('是否可继承')}
              <Tooltip
                title={intl
                  .get('hiam.labelManagement.view.title.inheritFlag')
                  .d(
                    '在标签标识的数据具有层级结构时，用于标识高层级数据的标签是否可继承到低层级数据'
                  )}
              >
                <Icon type="help_outline" />
              </Tooltip>
            </>
          );
        },
      },
      {
        name: 'visibleFlag',
        width: 130,
        renderer: ({ value }) => yesOrNoRender(value),
        header: () => {
          return (
            <>
              {intl.get('hiam.labelManagement.model.labelManagement.visibleFlag').d('是否页面可见')}
              <Tooltip
                title={intl
                  .get('hiam.labelManagement.view.title.visibleFlag')
                  .d('用于标识在业务数据中，该标签是否可在页面中查看')}
              >
                <Icon type="help_outline" />
              </Tooltip>
            </>
          );
        },
      },
      {
        name: 'action',
        width: 80,
        renderer: ({ record }) => {
          const { data = {} } = record;
          const operators = [];
          if (record.get('presetFlag') === 0) {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${this.props.match.path}.button.edit`,
                      type: 'button',
                      meaning: '标签管理-编辑',
                    },
                  ]}
                  onClick={() => this.handleUpdate(false, data)}
                  disabled={record.presetFlag}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          return operatorRender(operators);
        },
      },
    ];
  }

  /**
   * 新建/编辑知识类别
   * @param {boolean} isCreate
   * @param {object} [record={}]
   * @memberof labelManagement
   */
  @Bind()
  handleUpdate(isCreate, record = {}) {
    const { id } = record;
    this.drawerDs = new DataSet(drawerDS());
    this.drawerDs.create({}, 0);
    const drawerProps = {
      isCreate,
      ds: this.drawerDs,
      id: isCreate ? '' : id,
    };
    Modal.open({
      closable: true,
      key: 'knowledge-category',
      title: isCreate
        ? intl.get('hiam.labelManagement.view.message.crete').d('新建标签')
        : intl.get('hiam.labelManagement.view.message.edit').d('编辑标签'),
      drawer: true,
      style: {
        width: 500,
      },
      children: <Drawer {...drawerProps} />,
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

  render() {
    const { location, match } = this.props;
    return (
      <>
        <Header title={intl.get('hiam.labelManagement.view.message.title').d('标签管理')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '标签管理-新建',
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
