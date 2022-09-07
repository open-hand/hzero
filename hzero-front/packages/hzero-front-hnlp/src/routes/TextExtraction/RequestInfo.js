/**
 * 请求信息
 * RequestInfo
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-05
 * @copyright 2019-07-05 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { Row, Col, Card, Form } from 'hzero-ui';
import { isEmpty } from 'lodash';

import { getAccessToken } from 'utils/utils';
import { API_HOST, HZERO_NLP, AUTH_HOST } from 'utils/config';

import {
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_2_LAYOUT,
  DETAIL_CARD_THIRD_CLASSNAME,
} from 'utils/constants';

export default class RequestInfo extends Component {
  static propTypes = {
    isTenant: PropTypes.bool.isRequired,
    // organizationId: PropTypes.number,
    languageMessage: PropTypes.object.isRequired,
    // text: PropTypes.string, // 识别文本
    // templateCode: PropTypes.string, // 模板编码
    // tenantId: PropTypes.number,
    // context: PropTypes.arrayOf(PropTypes.shape({
    //   contextType: PropTypes.string.isRequired,
    //   contextValue: PropTypes.string.isRequired,
    // })),
    // requestError: PropTypes.array,
  };

  render() {
    const {
      isTenant,
      organizationId,
      languageMessage,
      text,
      templateCode,
      context = [],
      tenantId,
      requestError = [],
    } = this.props;
    const requestHasError = !isEmpty(requestError);
    const contextInfo = context || [];
    const requestEle = requestHasError ? (
      <pre>{`${requestError.map(r => `  ${r}`).join('\n')}`}</pre>
    ) : (
      <pre>
        {`  {
    "text": ${JSON.stringify(text)},
    "templateCode": ${JSON.stringify(templateCode)},
    "context": [
${contextInfo.map(r => `      ${JSON.stringify(r)}`).join(',\n')}
    ]${isTenant ? '' : `,\n    "tenantId": ${JSON.stringify(tenantId)}`}
  }`}
      </pre>
    );
    return (
      <Row {...EDIT_FORM_ROW_LAYOUT}>
        <Col {...FORM_COL_2_LAYOUT}>
          <Card
            className={DETAIL_CARD_THIRD_CLASSNAME}
            title={<h3>{languageMessage.view.message.requestToken}</h3>}
            bordered={false}
          >
            <Form layout="vertical">
              <Form.Item label={languageMessage.view.message.requestMethod}>
                <pre> POST</pre>
              </Form.Item>
              <Form.Item label={languageMessage.view.message.requestURL}>
                <pre>{`  ${AUTH_HOST}/oauth/token`}</pre>
              </Form.Item>
              <Form.Item label={languageMessage.view.message.requestHeader}>
                <pre>
                  {`  Authorization:Basic {Base64 clientid:secret} 
  #${languageMessage.view.message.example} 
  #  Basic Y2xpZW50OnNlY3JldA==
  #  ${languageMessage.view.message.Authorization}
  Content-Type:application/x-www-form-urlencoded`}
                </pre>
              </Form.Item>
              <Form.Item label={languageMessage.view.message.requestBody}>
                <pre>
                  {`  grant_type:password
  username:{${languageMessage.view.message.userName}}
  password:{${languageMessage.view.message.password}}
  # ${languageMessage.view.message.passwordTips}
  # ${languageMessage.view.message.passwordSuggest}
  scope:default`}
                </pre>
              </Form.Item>
            </Form>
          </Card>
        </Col>
        <Col {...FORM_COL_2_LAYOUT}>
          <Card
            className={DETAIL_CARD_THIRD_CLASSNAME}
            title={<h3>{languageMessage.view.message.request}</h3>}
            bordered={false}
          >
            <Form layout="vertical">
              <Form.Item label={languageMessage.view.message.requestMethod}>
                <pre> POST</pre>
              </Form.Item>
              <Form.Item label={languageMessage.view.message.requestURL}>
                <pre>
                  {`  ${API_HOST}${HZERO_NLP}/v1/${
                    isTenant ? `${organizationId}/` : ''
                  }text-extract/do`}
                </pre>
              </Form.Item>
              <Form.Item label={languageMessage.view.message.requestHeader}>
                <pre>
                  {`  Content-Type: application/json
  Authorization: Bearer ${getAccessToken()}`}
                </pre>
              </Form.Item>
              <Form.Item label={languageMessage.view.message.requestBody}>{requestEle}</Form.Item>
            </Form>
          </Card>
        </Col>
      </Row>
    );
  }
}
