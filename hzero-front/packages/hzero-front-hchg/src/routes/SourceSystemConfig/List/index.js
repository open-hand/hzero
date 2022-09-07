/**
 * SourceSystemConfig - 来源系统配置汇总
 * @date: 2020-3-3
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { DataSet, Table, Modal } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import notification from 'utils/notification';
import React, { PureComponent } from 'react';
import withProps from 'utils/withProps';
import { enableRender, operatorRender } from 'utils/renderer';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { HZERO_CHG } from 'hzero-front/lib/utils/config';
import {
  getCurrentOrganizationId,
  isTenantRoleLevel,
  tableScrollWidth,
} from 'hzero-front/lib/utils/utils';
import { ENABLED_FLAG_FIELDS } from '@/constants/constants';
import { axios } from '../../../components';

import SourceSystemConfigDS from '../../../stores/SourceSystemConfig/SourceSystemConfigDS';
import SourceSystemConfigDetail from '../Detail';

const organizationId = getCurrentOrganizationId();
const viewModalKey = Modal.key();

/**
 * 来源系统配置汇总
 * @extends {Component} - PureComponent
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@withProps(
  () => {
    // 来源系统配置数据源
    const tableDS = new DataSet({ ...SourceSystemConfigDS() });
    return { tableDS };
  },
  { cacheState: true }
)
@formatterCollections({ code: ['hchg.sourceSystemConfig'] })
export default class SourceSystemConfig extends PureComponent {
  // 详情页面数据源
  formDS;

  componentDidMount() {
    const { tableDS } = this.props;
    tableDS.query();
  }

  /**
   * 启用/禁用
   */
  @Bind()
  async enableOrDisable(record, type) {
    const { tableDS } = this.props;
    // 是否启用标志
    const enableFlag = type === 'ENABLE';
    Modal.confirm({
      title: enableFlag
        ? intl.get(`hchg.sourceSystem.meaasge.confirm.enable`).d('确定启用？')
        : intl.get(`hchg.sourceSystem.meaasge.confirm.disable`).d('确定禁用？'),
      onOk: async () => {
        let url = isTenantRoleLevel()
          ? `${HZERO_CHG}/v1/${organizationId}/source-system-configs`
          : `${HZERO_CHG}/v1/source-system-configs`;
        url = enableFlag ? `${url}/enabled` : `${url}/disabled`;
        try {
          const res = await axios.put(url, [record.get('sourceSystemId')]);
          if (res.failed) {
            notification.error({
              message: res.message,
            });
          } else {
            // 启用/禁用成功 刷新数据
            notification.success({
              message: enableFlag
                ? intl.get(`hchg.sourceSystem.meaasge.enable.success`).d('启用成功')
                : intl.get(`hchg.sourceSystem.meaasge.disable.success`).d('禁用成功'),
            });
          }
          tableDS.query();
        } catch (err) {
          notification.error({
            message: enableFlag
              ? intl.get(`hchg.sourceSystem.meaasge.enable.wait`).d('启用失败，请稍后再试。')
              : intl.get(`hchg.sourceSystem.meaasge.disable.wait`).d('禁用失败，请稍后再试。'),
          });
        }
      },
    });
  }

  /**
   * 保存
   */
  @Bind()
  async save() {
    const { tableDS } = this.props;
    // 先判断数据是否填写完整
    if (await this.formDS.validate()) {
      await this.formDS.submit();
      tableDS.query();
    } else {
      notification.info({
        message: intl.get('hchg.sourceSystem.view.message.validate').d('请先完善必输内容'),
      });
      return false;
    }
  }

  /**
   * 新建
   */
  @Bind()
  create() {
    // 新建页面数据源
    this.formDS = new DataSet({
      ...SourceSystemConfigDS(),
      autoQuery: false,
    });
    this.formDS.create();
    // 组件参数
    const detialProps = {
      formDS: this.formDS,
      openType: 'CREATE', // 页面打开类型
    };
    Modal.open({
      drawer: true,
      key: 'sourceSystemConfig-create',
      style: { width: '33%' },
      title: intl
        .get('hchg.sourceSystem.view.title.sourceSystemConfig.create')
        .d('新建来源系统配置'),
      children: <SourceSystemConfigDetail {...detialProps} />,
      okText: intl.get('hzero.common.button.save').d('保存'),
      onOk: () => this.save(),
    });
  }

  /**
   * 查看详情
   */
  @Bind()
  async viewDetail(record, openType) {
    // 查看/编辑页面数据源
    this.formDS = new DataSet({
      ...SourceSystemConfigDS(),
      autoQuery: false,
      queryParameter: {
        sourceSystemId: record.get('sourceSystemId'),
      },
    });
    // 组件参数
    const detialProps = {
      formDS: this.formDS,
      openType,
    };
    // enabledFlag=true，编辑；false，查看
    const enabledFlag = openType === 'EDIT';
    this.modal = Modal.open({
      drawer: true,
      key: viewModalKey,
      style: { width: '33%' },
      okCancel: enabledFlag,
      title: enabledFlag
        ? intl.get('hchg.sourceSystem.view.title.sourceSystemConfig.edit').d('编辑来源系统配置')
        : intl.get('hchg.sourceSystem.view.title.sourceSystemConfig.view').d('查看来源系统配置'),
      children: <SourceSystemConfigDetail {...detialProps} />,
      okText: enabledFlag
        ? intl.get('hzero.common.button.save').d('保存')
        : intl.get('hzero.common.button.cancel').d('取消'),
      okProps: { color: enabledFlag ? 'primary' : 'default' },
      onOk: () => (enabledFlag ? this.save() : {}),
    });
  }

  render() {
    const {
      tableDS,
      match: { path },
    } = this.props;
    const columns = [
      {
        header: intl.get('hchg.sourceSystem.model.sourceSystemConfig.sort').d('序号'),
        align: 'center',
        lock: 'left',
        width: 70,
        renderer: ({ record }) => (tableDS.currentPage - 1) * tableDS.pageSize + record.index + 1,
      },
      {
        name: 'systemNum',
        align: 'center',
        width: 240,
        renderer: ({ value, record }) => (
          <a onClick={() => this.viewDetail(record, 'VIEW')}>{value}</a>
        ),
      },
      {
        name: 'systemName',
      },
      {
        name: 'callbackUrl',
        width: 240,
      },
      {
        name: 'enabledFlag',
        align: 'center',
        renderer: ({ value }) => enableRender(value),
      },
      {
        name: 'remark',
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 160,
        lock: 'right',
        align: 'center',
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '来源系统配置-编辑',
                    },
                  ]}
                  onClick={() => this.viewDetail(record, 'EDIT')}
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
                      code: `${path}.button.enable`,
                      type: 'button',
                      meaning: '来源系统配置-启用',
                    },
                  ]}
                  onClick={() => this.enableOrDisable(record, 'ENABLE')}
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
                      meaning: '来源系统配置-禁用',
                    },
                  ]}
                  onClick={() => this.enableOrDisable(record, 'DISABLE')}
                >
                  {intl.get('hzero.common.button.disable').d('禁用')}
                </ButtonPermission>
              ),
              key: 'disable',
              len: 2,
              title: intl.get('hzero.common.button.disable').d('禁用'),
            },
          ];
          // 状态=启用显示禁用按钮，状态=禁用显示启用/编辑按钮
          const tempActions = actions.filter((item) =>
            record.get('enabledFlag') === ENABLED_FLAG_FIELDS.YES
              ? ['disable'].includes(item.key)
              : ['edit', 'enable'].includes(item.key)
          );
          return operatorRender(tempActions);
        },
      },
    ];
    return (
      <>
        <Header
          title={intl
            .get('hchg.sourceSystem.view.message.title.sourceSystemConfig')
            .d('来源系统配置')}
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '来源系统配置汇总-新建',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={this.create}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table
            dataSet={tableDS}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            queryFieldsLimit={3}
          />
        </Content>
      </>
    );
  }
}
