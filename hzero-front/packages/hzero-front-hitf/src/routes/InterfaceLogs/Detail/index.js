/*
 * Detail - 接口监控详情
 * @date: 2018/09/17 15:40:00
 * @author: LZH <zhaohui.liu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Card, Col, Form, Row, Button } from 'hzero-ui';
import JsonArea from 'react-json-view';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import queryString from 'query-string';
import { Content, Header } from 'components/Page';
import intl from 'utils/intl';
import { camelCase } from 'lodash';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { downloadFile } from 'services/api';
import { HZERO_HITF } from 'utils/config';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT_COL_3,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';
import jsonFormat from '@/components/JsonFormat';
import styles from '../index.less';

function buildMultiLine(arr, key) {
  const lineSepChar = `
`;
  return arr
    .map((r) => {
      if (r[key]) {
        return r[key].split('\n').join(lineSepChar);
      } else {
        return lineSepChar;
      }
    })
    .join(lineSepChar);
}

@connect(({ loading, interfaceLogs }) => ({
  fetchLogsDetailLoading: loading.effects['interfaceLogs/fetchLogsDetail'],
  interfaceLogs,
  organizationId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: ['hitf.interfaceLogs'] })
export default class Detail extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      interfaceRespLoading: false,
      respLoading: false,
      interfaceReqLoading: false,
      reqLoading: false,
    };
  }

  componentDidMount() {
    this.getData();
  }

  @Bind()
  getData() {
    const { dispatch, match } = this.props;
    const { interfaceLogId, invokeKey } = match.params;
    dispatch({
      type: 'interfaceLogs/fetchLogsDetail',
      payload: { interfaceLogId, invokeKey },
    });
  }

  @Bind()
  handleDetailedInfo(data) {
    const handledData = {
      reqBodyParam: data.reqBodyParam ? this.handleTransJson(data.reqBodyParam) : '',
      respContent: data.respContent ? this.handleTransJson(data.respContent) : '',
      interfaceReqBodyParam: data.interfaceReqBodyParam
        ? this.handleTransJson(data.interfaceReqBodyParam)
        : '',
      interfaceRespContent: data.interfaceRespContent
        ? this.handleTransJson(data.interfaceRespContent)
        : '',
    };
    return handledData;
  }

  /**
   * JSON字符串转换
   * @param {string} value - 需要处理的值
   */
  @Bind()
  handleTransJson(value) {
    const obj = this.handleTransObj(value);
    const handledValue = obj === null ? value : jsonFormat(obj);
    return handledValue;
  }

  /**
   * 将JSON字符串转换为格式化JSON
   * @param {string} - str JSON字符串
   */
  @Bind()
  handleTransObj(str) {
    let result = null;
    try {
      result = JSON.parse(str);
    } catch (err) {
      return null;
    }
    return result;
  }

  /**
   * JSON字符串转换为json格式
   */
  handleParse(response) {
    let data = '';
    try {
      data = JSON.parse(response || '{}');
    } catch (error) {
      data = {
        error: intl.get('hitf.interfaceLogs.view.message.translateError').d('字段解析失败'),
      };
    }
    return data;
  }

  async handleDownload(sourceType) {
    const {
      interfaceLogs: { detail },
    } = this.props;
    const { interfaceLogDtlList = [] } = detail;
    const { interfaceLogId, interfaceLogDtlId } = interfaceLogDtlList[0];
    const loadingName = `${camelCase(sourceType)}Loading`;
    this.setState({ [loadingName]: true });
    await downloadFile({
      requestUrl: isTenantRoleLevel()
        ? `${HZERO_HITF}/v1/${getCurrentOrganizationId()}/interface-logs/download`
        : `${HZERO_HITF}/v1/interface-logs/download`,
      method: 'GET',
      queryParams: [
        {
          name: 'logId',
          value: interfaceLogId,
        },
        {
          name: 'logDtlId',
          value: interfaceLogDtlId,
        },
        {
          name: 'type',
          value: sourceType,
        },
      ],
    });
    this.setState({ [loadingName]: false });
  }

  render() {
    const {
      location: { search, pathname },
      interfaceLogs: { detail },
      fetchLogsDetailLoading,
    } = this.props;
    const { interfaceRespLoading, respLoading, interfaceReqLoading, reqLoading } = this.state;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const basePath = '/hitf/interface-logs';
    let stacktraceList = '';
    if (detail.interfaceLogDtlList && detail.interfaceLogDtlList.length) {
      stacktraceList = buildMultiLine(detail.interfaceLogDtlList, 'stacktrace');
    }
    const { interfaceLogDtlList = [] } = detail;
    const {
      respDownload,
      interfaceRespDownload,
      reqDownload,
      interfaceReqDownload,
      interfaceReqBodyParam,
      interfaceRespContent,
      reqBodyParam,
      respContent,
    } = interfaceLogDtlList[0] || {};

    const commonSetting = {
      name: null,
      displayDataTypes: false,
      collapseStringsAfterLength: 3000,
    };

    const reqParamProps = {
      ...commonSetting,
      src: this.handleParse(interfaceReqBodyParam),
    };

    const respProps = {
      ...commonSetting,
      src: this.handleParse(interfaceRespContent),
    };

    const reqBodyParamProps = {
      ...commonSetting,
      src: this.handleParse(reqBodyParam),
    };

    const respContentProps = {
      ...commonSetting,
      src: this.handleParse(respContent),
    };

    return (
      <>
        <Header
          title={intl.get('hitf.interfaceLogs.view.message.interfaceLogsDetail').d('接口监控详情')}
          backPath={
            pathname.indexOf('/private') === 0
              ? `/private${basePath}/list?access_token=${accessToken}`
              : `${basePath}/list`
          }
        />
        <Content className={styles['interface-logs-detail']}>
          <Card
            key="interface-logs-basic"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hitf.interfaceLogs.view.message.baseMessage').d('基本信息')}</h3>}
            loading={fetchLogsDetailLoading}
          >
            <Form className="more-fields-form">
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hitf.interfaceLogs.view.message.invokeKey').d('请求ID')}
                  >
                    {detail.invokeKey}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hitf.interfaceLogs.view.message.serverCode').d('服务代码')}
                  >
                    {detail.serverCode}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hitf.interfaceLogs.view.message.serverName').d('服务名称')}
                  >
                    {detail.serverName}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hitf.interfaceLogs.view.message.interfaceCode').d('接口代码')}
                  >
                    {detail.interfaceCode}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hitf.interfaceLogs.view.message.interfaceName').d('接口名称')}
                  >
                    {detail.interfaceName}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hitf.interfaceLogs.view.message.clientId').d('客户端ID')}
                  >
                    {detail.clientId}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hitf.interfaceLogs.view.message.external.reqTime')
                      .d('第三方接口请求时间')}
                  >
                    {detail.interfaceRequestTime}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hitf.interfaceLogs.view.message.ip').d('请求IP')}
                  >
                    {detail.ip}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hitf.interfaceLogs.view.message.internal.requestMethod')
                      .d('平台接口请求方式')}
                  >
                    {detail.requestMethod}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hitf.interfaceLogs.view.message.internal.responseTime')
                      .d('平台接口响应时间(ms)')}
                  >
                    {detail.responseTime}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hitf.interfaceLogs.view.message.external.respTime')
                      .d('第三方接口响应时间(ms)')}
                  >
                    {detail.interfaceResponseTime}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hitf.interfaceLogs.view.message.external.interfaceType')
                      .d('第三方接口类型')}
                  >
                    {detail.interfaceType}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hitf.interfaceLogs.view.message.external.interfaceUrl')
                      .d('第三方接口地址')}
                  >
                    {detail.interfaceUrl}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hitf.interfaceLogs.view.message.internal.responseStatus')
                      .d('平台接口响应状态')}
                  >
                    {detail.responseStatus === 'success'
                      ? intl.get('hitf.interfaceLogs.view.message.success').d('成功')
                      : intl.get('hitf.interfaceLogs.view.message.failed').d('失败')}
                  </Form.Item>
                </Col>
              </Row>

              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hitf.interfaceLogs.modal.interfaceLogs.interfaceServerVersion')
                      .d('服务版本')}
                  >
                    {detail.formatInterfaceServerVersion}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hitf.interfaceLogs.modal.interfaceLogs.interfaceVersion')
                      .d('接口版本')}
                  >
                    {detail.formatInterfaceVersion}
                  </Form.Item>
                </Col>
              </Row>

              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col span={24} className={styles['col-all-form']}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT_COL_3}
                    label={intl.get('hitf.interfaceLogs.view.message.userAgent').d('User-Agent')}
                  >
                    {detail.userAgent}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col span={24} className={styles['col-all-form']}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT_COL_3}
                    label={intl.get('hitf.interfaceLogs.view.message.referer').d('Referer')}
                  >
                    {detail.referer}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Card>
          <Card
            key="req-param-json"
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            loading={fetchLogsDetailLoading}
            title={
              <h3>
                {intl
                  .get('hitf.interfaceLogs.model.interfaceLogs.external.reqParam')
                  .d('第三方接口调用参数')}
              </h3>
            }
          >
            {interfaceReqDownload ? (
              <div>
                <span style={{ marginRight: '10px' }}>
                  {intl
                    .get('hitf.interfaceLogs.model.interfaceLogs.downloadTip')
                    .d('日志内容过大，请点击下载来查看更多日志: ')}
                </span>
                <Button
                  type="primary"
                  loading={interfaceReqLoading}
                  onClick={() => this.handleDownload('INTERFACE_REQ')}
                >
                  {intl.get('hzero.common.button.download').d('下载')}
                </Button>
              </div>
            ) : (
              <div className={styles['json-area']}>
                <JsonArea {...reqParamProps} />
              </div>
            )}
          </Card>
          <Card
            key="resp-json"
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            loading={fetchLogsDetailLoading}
            title={
              <h3>
                {intl
                  .get('hitf.interfaceLogs.model.interfaceLogs.external.resp')
                  .d('第三方接口响应内容')}
              </h3>
            }
          >
            {interfaceRespDownload ? (
              <div>
                <span style={{ marginRight: '10px' }}>
                  {intl
                    .get('hitf.interfaceLogs.model.interfaceLogs.downloadTip')
                    .d('日志内容过大，请点击下载来查看更多日志: ')}
                </span>
                <Button
                  type="primary"
                  loading={interfaceRespLoading}
                  onClick={() => this.handleDownload('INTERFACE_RESP')}
                >
                  {intl.get('hzero.common.button.download').d('下载')}
                </Button>
              </div>
            ) : (
              <div className={styles['json-area']}>
                <JsonArea {...respProps} />
              </div>
            )}
          </Card>
          <Card
            key="req-body-param-json"
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            loading={fetchLogsDetailLoading}
            title={
              <h3>
                {intl
                  .get('hitf.interfaceLogs.model.interfaceLogs.internal.reqBodyParam')
                  .d('平台接口调用参数')}
              </h3>
            }
          >
            {reqDownload ? (
              <div>
                <span style={{ marginRight: '10px' }}>
                  {intl
                    .get('hitf.interfaceLogs.model.interfaceLogs.downloadTip')
                    .d('日志内容过大，请点击下载来查看更多日志: ')}
                </span>
                <Button
                  type="primary"
                  loading={reqLoading}
                  onClick={() => this.handleDownload('REQ')}
                >
                  {intl.get('hzero.common.button.download').d('下载')}
                </Button>
              </div>
            ) : (
              <div className={styles['json-area']}>
                <JsonArea {...reqBodyParamProps} />
              </div>
            )}
          </Card>
          <Card
            key="resp-content-json"
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            loading={fetchLogsDetailLoading}
            title={
              <h3>
                {intl
                  .get('hitf.interfaceLogs.model.interfaceLogs.internal.respContent')
                  .d('平台接口响应内容')}
              </h3>
            }
          >
            {respDownload ? (
              <div>
                <span style={{ marginRight: '10px' }}>
                  {intl
                    .get('hitf.interfaceLogs.model.interfaceLogs.downloadTip')
                    .d('日志内容过大，请点击下载来查看更多日志: ')}
                </span>
                <Button
                  type="primary"
                  loading={respLoading}
                  onClick={() => this.handleDownload('RESP')}
                >
                  {intl.get('hzero.common.button.download').d('下载')}
                </Button>
              </div>
            ) : (
              <div className={styles['json-area']}>
                <JsonArea {...respContentProps} />
              </div>
            )}
          </Card>
          <Card
            key="interface-logs-error"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>{intl.get('hitf.interfaceLogs.view.message.stacktraceMessage').d('异常信息')}</h3>
            }
            loading={fetchLogsDetailLoading}
          >
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col>
                <Form.Item>
                  <pre className={styles['multi-line-information-exception']}>{stacktraceList}</pre>
                </Form.Item>
              </Col>
            </Row>
          </Card>
        </Content>
      </>
    );
  }
}
