/**
 * StructureField - 字段结构
 * @date: 2020-4-3
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { DataSet, Table, Modal } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import notification from 'utils/notification';
import React, { PureComponent } from 'react';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import withProps from 'utils/withProps';

import { Header, Content } from 'components/Page';
import { TagRender, enableRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import axios from 'axios';
import {
  STRUCTURE_CATEGORY_TAGS,
  STRUCTURE_BIZ_USAGE_TAGS,
  ENABLED_FLAG_FIELDS,
  STRUCTURE_COMPOSITION_TAGS,
} from '@/constants/CodeConstants';

import StructureFieldHeaderDS from '../../../stores/StructureField/StructureFieldHeaderDS';
import StructureFieldHeaderDrawer from './StructureFieldHeaderDrawer';

const organizationId = getCurrentOrganizationId();
const viewModalKey = Modal.key();

/**
 * 结构字段汇总界面
 * @extends {Component} - PureComponent
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hitf.structureField', 'hitf.chargeSet', 'view.validation'] })
@withProps(
  () => {
    // 结构字段头 数据源
    const tableDS = new DataSet({ ...StructureFieldHeaderDS() });
    return { tableDS };
  },
  { cacheState: true }
)
export default class StructureFieldHeader extends PureComponent {
  // 头页面数据源
  headerFormDS;

  componentDidMount() {
    const { tableDS } = this.props;
    tableDS.query();
  }

  /**
   * 保存 头数据
   */
  @Bind()
  async saveHeader() {
    const { tableDS } = this.props;
    // 先判断数据是否填写完整
    if (await this.headerFormDS.validate()) {
      await this.headerFormDS.submit();
      await tableDS.query();
    } else {
      notification.info({
        message: intl.get('hitf.structureField.view.message.validate').d('请先完善必输内容'),
      });
      return false;
    }
  }

  /**
   * 新建/编辑
   * @param {string} openType - 打开页面类型 READ/EDIT
   * @param {object} record - 行记录
   */
  @Bind()
  async openHeaderDrawer(openType, record) {
    // 新建/编辑头页面数据源
    this.headerFormDS = new DataSet({
      ...StructureFieldHeaderDS(),
      autoQuery: false,
      queryParameter: {
        headerId: record && record.get('headerId'),
      },
    });

    // 编辑，需要查询数据
    if (openType === 'EDIT') {
      await this.headerFormDS.query();
    } else {
      await this.headerFormDS.create();
    }

    // 组件参数
    const detialProps = {
      formDS: this.headerFormDS,
      openType,
    };
    this.modal = Modal.open({
      drawer: true,
      key: viewModalKey,
      style: { width: '33%' },
      okCancel: true,
      title:
        openType === 'EDIT'
          ? intl.get('hitf.structureField.view.title.structureFieldHeader.edit').d('编辑结构字段头')
          : intl
              .get('hitf.structureField.view.title.structureFieldHeader.create')
              .d('新建结构字段头'),
      children: <StructureFieldHeaderDrawer {...detialProps} />,
      okText: intl.get('hzero.common.button.save').d('保存'),
      onOk: this.saveHeader,
    });
  }

  /**
   * 启用/禁用
   * @param {string} type - 类型 ENABLED/DISABLED
   * @param {object} record - 行记录
   */
  @Bind()
  enableOrDisable(type, record) {
    const { tableDS } = this.props;
    const url = isTenantRoleLevel()
      ? `${HZERO_HITF}/v1/${organizationId}/structure-field-headers`
      : `${HZERO_HITF}/v1/structure-field-headers`;

    let title = intl.get('hitf.structureField.view.meaasge.confirm.enable').d('确定启用？');
    let message = intl.get('hitf.structureField.view.meaasge.enable.success').d('启用成功');
    let failedMsg = intl
      .get('hitf.structureField.view.meaasge.enable.wait')
      .d('启用失败，请稍后再试。');

    if (type === 'DISABLED') {
      title = intl.get('hitf.structureField.view.meaasge.confirm.disable').d('确定禁用？');
      message = intl.get('hitf.structureField.view.meaasge.disable.success').d('禁用成功');
      failedMsg = intl
        .get('hitf.structureField.view.meaasge.disable.wait')
        .d('禁用失败，请稍后再试。');
    }

    Modal.confirm({
      title,
      onOk: async () => {
        // 请求参数
        const data = {
          ...record.toData(),
          enabledFlag: type !== 'DISABLED',
        };

        try {
          const res = await axios.put(url, data);
          if (res & res.failed) {
            notification.error({
              message: res.message,
            });
          } else {
            // 启用/禁用成功 刷新数据
            tableDS.query();
            notification.success({
              message,
            });
          }
        } catch (err) {
          notification.error({
            message: failedMsg,
          });
        }
      },
    });
  }

  /**
   * 明细
   * @param {object} record - 行记录
   */
  @Bind()
  detail(record) {
    const { dispatch } = this.props;
    // 页面打开类型 READ/EDIT
    let openType = 'EDIT';
    if (ENABLED_FLAG_FIELDS.YES === record.get('enabledFlag')) {
      openType = 'READ';
    }

    dispatch(
      routerRedux.push({
        pathname: `/hitf/structure-field/line/${openType}/${record.get('headerId')}`,
      })
    );
  }

  /**
   * 删除
   * @param {number} record - 行记录
   */
  @Bind()
  delete(record) {
    const { tableDS } = this.props;
    tableDS.delete(record);
  }

  render() {
    const {
      tableDS,
      match: { path },
    } = this.props;
    const columns = [
      {
        header: intl.get('hitf.structureField.model.structureFieldHeader.sort').d('序号'),
        lock: 'left',
        width: 70,
        align: 'center',
        renderer: ({ record }) => (tableDS.currentPage - 1) * tableDS.pageSize + record.index + 1,
      },
      {
        name: 'structureName',
      },
      {
        name: 'structureNum',
        width: 150,
        renderer: ({ value, record }) => <a onClick={() => this.detail(record)}>{value}</a>,
      },
      {
        name: 'structureCategory',
        align: 'center',
        width: 100,
        renderer: ({ value }) => TagRender(value, STRUCTURE_CATEGORY_TAGS),
      },
      {
        name: 'composition',
        align: 'center',
        width: 100,
        renderer: ({ value }) => TagRender(value, STRUCTURE_COMPOSITION_TAGS),
      },
      { name: 'structureDesc' },
      {
        name: 'bizUsage',
        align: 'center',
        width: 130,
        renderer: ({ value }) => TagRender(value, STRUCTURE_BIZ_USAGE_TAGS),
      },
      {
        name: 'enabledFlag',
        align: 'center',
        width: 100,
        renderer: ({ value }) => enableRender(value),
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        lock: 'right',
        align: 'center',
        width: 200,
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.enable`,
                      type: 'button',
                      meaning: '结构字段定义-启用',
                    },
                  ]}
                  onClick={() => this.enableOrDisable('ENABLED', record)}
                >
                  {intl.get('hzero.common.button.enable').d('启用')}
                </ButtonPermission>
              ),
              key: 'enable',
              len: 2,
              title: intl.get('hzero.common.button.enable').d('启用'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.disable`,
                      type: 'button',
                      meaning: '结构字段定义-禁用',
                    },
                  ]}
                  onClick={() => this.enableOrDisable('DISABLED', record)}
                >
                  {intl.get('hzero.common.button.disable').d('禁用')}
                </ButtonPermission>
              ),
              key: 'disable',
              len: 2,
              title: intl.get('hzero.common.button.disable').d('禁用'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.detail`,
                      type: 'button',
                      meaning: '结构字段定义-明细',
                    },
                  ]}
                  onClick={() => this.detail(record)}
                >
                  {intl.get('hitf.structureField.view.button.detail').d('明细')}
                </ButtonPermission>
              ),
              key: 'detail',
              len: 2,
              title: intl.get('hitf.structureField.view.button.detail').d('明细'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '结构字段定义-编辑',
                    },
                  ]}
                  onClick={() => this.openHeaderDrawer('EDIT', record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.delete`,
                      type: 'button',
                      meaning: '结构字段定义-删除',
                    },
                  ]}
                  onClick={() => this.delete(record)}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          // 【启用】状态的记录不可【编辑】【删除】
          const tempActions = actions.filter((item) =>
            record.get('enabledFlag') === ENABLED_FLAG_FIELDS.YES
              ? ['disable', 'detail'].includes(item.key)
              : ['enable', 'detail', 'edit', 'delete'].includes(item.key)
          );
          return operatorRender(tempActions, record, { limit: 4 });
        },
      },
    ];
    return (
      <div>
        <Header
          title={intl
            .get('hitf.structureField.view.message.title.structureFieldHeader')
            .d('结构字段定义')}
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '结构字段定义-新建',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={() => this.openHeaderDrawer('CREATE')}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={tableDS} columns={columns} queryFieldsLimit={3} />
        </Content>
      </div>
    );
  }
}
