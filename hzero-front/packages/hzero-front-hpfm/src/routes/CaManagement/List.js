/**
 * List - CA证书管理 - 卡片列表
 * @date: 2019-9-10
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Card, Popconfirm, Form, Tooltip, List } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { dateRender } from 'utils/renderer';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

import styles from './index.less';
import Upload from './Upload.js';

const layout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 16,
  },
};

/**
 * 卡片列表
 * @extends {Component} - React.Component
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Function} onDelete - 删除证书
 * @return React.element
 */
export default class ListTable extends Component {
  /**
   * 分页切换
   * @param {number} page - 当前页码
   * @param {number} pageSize - 每页显示条数
   */
  @Bind()
  handleChange(page, pageSize) {
    const { onChange } = this.props;
    const pageConfig = {
      current: page,
      pageSize,
    };
    onChange(pageConfig);
  }

  render() {
    const { loading, dataSource, pagination, onDelete = () => {} } = this.props;
    const isTenant = isTenantRoleLevel();
    const tenantId = getCurrentOrganizationId(); // 租户级只能看到自己添加的证书，无法操作
    const pages = {
      ...pagination,
      showSizeChanger: true,
      onChange: (page, pageSize) => this.handleChange(page, pageSize),
      onShowSizeChange: (current, size) => this.handleChange(current, size),
    };
    return (
      <List
        loading={loading}
        className={styles['hpfm-ca-list']}
        grid={{
          gutter: 12,
          xs: 1,
          sm: 2,
          md: 4,
          lg: 4,
          xl: 4,
          xxl: 4,
        }}
        dataSource={dataSource}
        pagination={dataSource.length ? pages : false}
        renderItem={item => (
          <List.Item key={item.interfaceServerId}>
            <Card
              title={
                <Tooltip arrowPointAtCenter title={item.domainName}>
                  <span>{item.domainName}</span>
                </Tooltip>
              }
              bodyStyle={{ height: isTenant && item.tenantId !== tenantId ? '347px' : '300px' }}
              hoverable
              actions={
                isTenant && item.tenantId !== tenantId
                  ? []
                  : [
                    <Upload isCreate={false} id={item.certificateId} />,
                    <Popconfirm
                      title={intl
                          .get('hpfm.caManagement.view.message.confirm.delete')
                          .d('是否删除此证书？')}
                      onConfirm={() => onDelete(item)}
                    >
                      <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                    </Popconfirm>,
                    ]
              }
            >
              <Card.Meta
                description={
                  <Form>
                    <Form.Item
                      label={intl.get('hzero.common.model.common.tenantId').d('租户')}
                      {...layout}
                    >
                      {item.tenantName}
                    </Form.Item>
                    <Form.Item
                      label={intl
                        .get('hpfm.caManagement.model.caManagement.issuerDomainName')
                        .d('颁发者')}
                      {...layout}
                    >
                      {item.issuerDomainName}
                    </Form.Item>
                    <Form.Item
                      label={intl
                        .get('hpfm.caManagement.model.caManagement.creationDate')
                        .d('有效期从')}
                      {...layout}
                    >
                      {dateRender(item.startDate)}
                    </Form.Item>
                    <Form.Item
                      label={intl.get('hpfm.caManagement.model.caManagement.endDate').d('有效期至')}
                      {...layout}
                    >
                      {dateRender(item.endDate)}
                    </Form.Item>
                  </Form>
                }
              />
            </Card>
          </List.Item>
        )}
      />
    );
  }
}
