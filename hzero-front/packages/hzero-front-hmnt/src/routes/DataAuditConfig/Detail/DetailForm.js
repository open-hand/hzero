/**
 * DetailForm - 数据变更审计配置详情页 - 表单
 * @date: 2019/4/16
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import intl from 'utils/intl';
import { Form, Row, Col } from 'hzero-ui';
import { EDIT_FORM_ROW_LAYOUT, FORM_COL_3_LAYOUT, EDIT_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;

/**
 * 数据变更审计配置详情
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {!Object} dataSource - 数据源
 * @reactProps {boolean} loading - 数据加载是否完成
 * @return React.element
 */
export default class DetailForm extends PureComponent {
  render() {
    const { dataSource } = this.props;
    const { serviceName, tableName, displayName } = dataSource;
    return (
      <Form layout="inline" className="more-fields-search-form">
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hmnt.dataAuditConfig.model.dataAuditConfig.serviceName').d('服务名')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {serviceName}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hmnt.dataAuditConfig.model.dataAuditConfig.tableName').d('表名')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {tableName}
            </FormItem>
          </Col>
          {displayName && (
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl
                  .get('hmnt.dataAuditConfig.model.dataAuditConfig.displayName')
                  .d('展示名称')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {displayName}
              </FormItem>
            </Col>
          )}
        </Row>
      </Form>
    );
  }
}
