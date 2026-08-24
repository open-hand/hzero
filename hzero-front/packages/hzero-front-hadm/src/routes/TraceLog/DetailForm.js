/**
 * Detail - 日志追溯详情
 * @date: 2020/2/26
 * @author: CJ <jing.chen04@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { PureComponent } from 'react';
import { Form, Row, Col, Card } from 'hzero-ui';
import classnames from 'classnames';

import intl from 'utils/intl';
import {
  EDIT_FORM_ROW_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT_COL_3,
  ROW_READ_WRITE_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  FORM_COL_3_LAYOUT,
  DETAIL_EDIT_FORM_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
} from 'utils/constants';
import styles from './index.less';

const FormItem = Form.Item;

/**
 * 详情表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {!Object} dataSource - 数据源
 * @return React.element
 */

@Form.create({ fieldNameProp: null })
export default class DetailForm extends PureComponent {
  render() {
    const { dataSource = {} } = this.props;
    return (
      <Card
        key="traceLog"
        bordered={false}
        className={DETAIL_CARD_TABLE_CLASSNAME}
        title={
          <h3>
            {`${intl.get(`hadm.traceLog.view.title.requestType`).d('调用类型-')}${
              dataSource.traceType
            }`}
          </h3>
        }
      >
        <Form className={classnames(DETAIL_EDIT_FORM_CLASSNAME, styles['detail-form'])}>
          {dataSource.traceType === 'HTTP' && (
            <>
              <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_READ_WRITE_CLASSNAME}>
                <Col span={24} className={styles['add-document-form']}>
                  <FormItem
                    label={intl.get('hadm.traceLog.model.traceLog.requestUrl').d('请求url')}
                    {...EDIT_FORM_ITEM_LAYOUT_COL_3}
                  >
                    {dataSource.requestUrl}
                  </FormItem>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_READ_WRITE_CLASSNAME}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl.get('hadm.traceLog.model.traceLog.method').d('方法类型')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {dataSource.method}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hadm.traceLog.model.traceLog.requestSizeBytes')
                      .d('请求字节数')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {dataSource.requestSizeBytes}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hadm.traceLog.model.traceLog.responseSizeBytes')
                      .d('响应字节数')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {dataSource.responseSizeBytes}
                  </FormItem>
                </Col>
              </Row>
            </>
          )}
          {dataSource.traceType === 'SQL' && (
            <>
              <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_READ_WRITE_CLASSNAME}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl.get('hadm.traceLog.model.traceLog.dbType').d('数据库类型')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {dataSource.dbType}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl.get('hadm.traceLog.model.traceLog.dbEndpoint').d('数据库地址')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {dataSource.dbEndpoint}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    label={intl.get('hadm.traceLog.model.traceLog.dbName').d('数据库名称')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {dataSource.dbName}
                  </FormItem>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_READ_WRITE_CLASSNAME}>
                <Col span={24} className={styles['add-document-form']}>
                  <FormItem
                    label={intl.get('hadm.traceLog.model.traceLog.url').d('sql')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {dataSource.sql}
                  </FormItem>
                </Col>
              </Row>
            </>
          )}
          {dataSource.traceType === 'FEIGN' && (
            <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_READ_WRITE_CLASSNAME}>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hadm.traceLog.model.traceLog.remoteHost').d('远程host')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {dataSource.remoteHost}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl.get('hadm.traceLog.model.traceLog.remotePort').d('远程端口号')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {dataSource.remotePort}
                </FormItem>
              </Col>
            </Row>
          )}
          <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_READ_WRITE_CLASSNAME}>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hadm.traceLog.model.traceLog.logDate').d('日期')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {dataSource.logDate}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hadm.traceLog.model.traceLog.appName').d('应用名')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {dataSource.appName}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hadm.traceLog.model.traceLog.threadName').d('线程名')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {dataSource.threadName}
              </FormItem>
            </Col>
          </Row>
          <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_READ_WRITE_CLASSNAME}>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hadm.traceLog.model.traceLog.resultCode').d('响应码')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {dataSource.resultCode}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hadm.traceLog.model.traceLog.traceId').d('调用ID')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {dataSource.traceId}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hadm.traceLog.model.traceLog.spanId').d('子调用ID')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {dataSource.spanId}
              </FormItem>
            </Col>
          </Row>
          <Row {...EDIT_FORM_ROW_LAYOUT} className={ROW_READ_WRITE_CLASSNAME}>
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hadm.traceLog.model.traceLog.sqlTimeConsuming').d('耗时')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                {dataSource.cost}
              </FormItem>
            </Col>
          </Row>
        </Form>
      </Card>
    );
  }
}
