/**
 * Table - 菜单配置 - 列表页面表格
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { operatorRender, TagRender } from 'utils/renderer';
import { INTERFACE_STATUS_TAGS, SERVICE_TYPE_TAGS } from '@/constants/constants';
import { Button as ButtonPermission } from 'components/Permission';
import { API_HOST, HZERO_HITF } from 'utils/config';
import notification from 'utils/notification';
import * as ClipBoard from 'clipboard-polyfill/text';
import Search from '../../Detail/Search';

export default class ServiceList extends PureComponent {
  @Bind()
  handleCopyInvokeAddr(record = {}) {
    const { publishUrl } = record;
    const absolutePublishUrl = this.getAbsolutePublishUrl(publishUrl);
    ClipBoard.writeText(absolutePublishUrl).then(
      // eslint-disable-next-line func-names
      function () {
        notification.success();
      }
    );
  }

  /**
   * 获取绝对路径的发布地址
   * @param publishUrl publishUrl
   * @returns {string} AbsolutePublishUrl
   */
  @Bind()
  getAbsolutePublishUrl(publishUrl = '') {
    return ''.concat(API_HOST).concat(HZERO_HITF).concat(publishUrl);
  }

  render() {
    const {
      dataSource = [],
      loading,
      serviceTypes,
      interfaceStatus,
      fetchInformation = (e) => e,
      pagination = {},
      onChange = (e) => e,
    } = this.props;
    const tableColumns = [
      {
        title: intl.get('hitf.services.model.services.interfaceName').d('接口名称'),
        dataIndex: 'interfaceName',
        width: 180,
        fixed: 'left',
      },
      {
        title: intl.get('hitf.services.model.services.interfaceCode').d('接口编码'),
        dataIndex: 'interfaceCode',
        width: 160,
      },
      {
        title: intl.get('hitf.services.model.services.publishType').d('发布类型'),
        align: 'center',
        dataIndex: 'publishTypeMeaning',
        render: (text, record) => {
          return TagRender(record.publishType, SERVICE_TYPE_TAGS, text);
        },
        width: 120,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        align: 'center',
        width: 100,
        render: (text, record) => {
          return TagRender(record.status, INTERFACE_STATUS_TAGS, text);
        },
        dataIndex: 'statusMeaning',
      },
      {
        title: intl.get('hitf.services.model.services.invokeAddr').d('透传地址'),
        dataIndex: 'publishUrl',
        render: (text) => {
          return this.getAbsolutePublishUrl(text);
        },
      },
      {
        title: intl.get('hitf.services.view.message.current.version').d('当前版本'),
        dataIndex: 'formatVersion',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        fixed: 'right',
        render: (text, record) => {
          const operators = [
            {
              key: 'copy',
              ele: (
                <ButtonPermission type="text" onClick={() => this.handleCopyInvokeAddr(record)}>
                  {intl.get('hzero.common.button.copy').d('复制')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.copy').d('复制'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    const tableProps = {
      dataSource,
      pagination,
      loading,
      onChange,
      rowKey: 'interfaceId',
      bordered: true,
      columns: tableColumns,
      scroll: { x: tableScrollWidth(tableColumns) },
    };

    const searchProps = {
      serviceTypes,
      interfaceStatus,
      onSearch: fetchInformation,
    };

    return (
      <div>
        <Search {...searchProps} />
        <Table {...tableProps} />
      </div>
    );
  }
}
