/**
 * ruleConfig 规则配置
 * @date: 2020-4-30
 * @author: jmy <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React from 'react';
import { Table, ModalContainer, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';
import intl from 'utils/intl';

import { initDS } from '../../stores/ruleConfigDS';

@formatterCollections({ code: ['hadm.ruleConfig', 'hadm.common'] })
export default class ruleConfig extends React.Component {
  initDs = new DataSet(initDS());

  get columns() {
    return [
      {
        name: 'index',
        width: 100,
        renderer: ({ record }) => {
          return record.index + 1;
        },
      },
      // {
      //   name: 'dsUrl',
      // },
      {
        name: 'datasourceGroupName',
        // width: 200,
      },
      {
        name: 'proxySchemaName',
        width: 200,
      },
      // {
      //   name: 'proxyDsUrl',
      //   width: 200,
      // },
      {
        name: 'action',
        width: 160,
        renderer: ({ record }) => {
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
                    meaning: '规则配置-编辑',
                  },
                ]}
                onClick={() => this.handleUpdate(false, record)}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          operators.push({
            key: 'delete',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${this.props.match.path}.button.delete`,
                    type: 'button',
                    meaning: '规则配置-删除',
                  },
                ]}
                onClick={() => this.handleDelete(record)}
              >
                {intl.get('hzero.common.button.delete').d('删除')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.delete').d('删除'),
          });
          return operatorRender(operators);
        },
      },
    ];
  }

  /**
   * 新建/编辑
   * @param {boolean} isCreate
   * @memberof ruleConfig
   */
  @Bind()
  handleUpdate(isCreate, record) {
    const { history } = this.props;
    if (isCreate) {
      history.push('/hadm/rule-config/detail/create');
    } else {
      history.push(`/hadm/rule-config/detail/${record.get('ruleHeaderId')}`);
    }
  }

  /**
   * 删除
   * @param {object} [record={}]
   * @memberof ruleConfig
   */
  @Bind()
  async handleDelete(record = {}) {
    await this.initDs.delete(record);
    // if(res){
    //   await this.initDs.query();
    // }
  }

  render() {
    const { location, match } = this.props;
    return (
      <>
        <Header
          title={intl.get('hadm.ruleConfig.view.message.title.ruleConfig').d('Proxy规则配置')}
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '规则配置-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.handleUpdate(true)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.refresh`,
                type: 'button',
                meaning: '规则配置-刷新',
              },
            ]}
            icon="sync"
            onClick={() => this.initDs.query()}
          >
            {intl.get('hzero.common.button.refresh').d('刷新')}
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
