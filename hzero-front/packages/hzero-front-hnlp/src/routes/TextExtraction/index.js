/**
 * 内容识别
 * TextTraction
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-06-04
 * @copyright 2019-06-04 © HAND
 */

import React, { Component } from 'react';
import { Button, Card, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';

import { Content, Header } from 'components/Page';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { DETAIL_CARD_CLASSNAME, DETAIL_CARD_TABLE_CLASSNAME } from 'utils/constants';

import CreateForm from './CreateForm';
import Result from './Result';

import styles from './styles.less';
import RequestInfo from '@/routes/TextExtraction/RequestInfo';

@connect(mapStateToProps, mapDispatchToProps)
@formatterCollections({ code: ['hnlp.textExtraction'] })
export default class TextTraction extends Component {
  constructor(props) {
    super(props);
    this.createFormRef = React.createRef();
    this.state = {
      requestInfo: {}, // 请求的参数
    };
  }

  /**
   * 在组件卸载后(页面tab关闭后) 清除 result 的内容
   */
  componentWillUnmount() {
    const { updateState } = this.props;
    updateState({
      result: {},
    });
  }

  getLanguageMessage() {
    return {
      model: {
        textTraction: {
          template: intl.get('hnlp.textExtraction.model.textTraction.template').d('模板'),
          text: intl.get('hnlp.textExtraction.model.textTraction.text').d('识别文本'),
          contextType: intl
            .get('hnlp.textExtraction.model.textTraction.contextType')
            .d('contextType'),
          contextKey: intl.get('hnlp.textExtraction.model.textTraction.contextKey').d('contextKey'),
          tagType: intl.get('hnlp.textExtraction.model.textTraction.tagType').d('标记类型'),
          tagValue: intl.get('hnlp.textExtraction.model.textTraction.tagValue').d('标记值'),
          tenant: intl.get('hnlp.textExtraction.model.textTraction.tenant').d('租户'),
        },
      },
      view: {
        title: {
          extractionSource: intl
            .get('hnlp.textExtraction.view.title.extractionSource')
            .d('识别内容'),
          extractionResult: intl
            .get('hnlp.textExtraction.view.title.extractionResult')
            .d('识别结果'),
          resultStatus: intl.get('hnlp.textExtraction.view.title.resultStatus').d('识别状态码'),
          resultMsg: intl.get('hnlp.textExtraction.view.title.resultMsg').d('识别消息'),
        },
        message: {
          // requestInfo 内容
          requestMethod: intl.get('hnlp.textExtraction.view.message.v').d('请求方式'),
          requestToken: intl
            .get('hnlp.textExtraction.view.message.requestToken')
            .d('token获取请求'),
          request: intl.get('hnlp.textExtraction.view.message.request').d('识别测试请求'),
          requestURL: intl.get('hnlp.textExtraction.view.message.requestURL').d('请求URL'),
          requestHeader: intl.get('hnlp.textExtraction.view.message.requestHeader').d('头信息'),
          example: intl.get('hnlp.textExtraction.view.message.example').d('示例：'),
          Authorization: intl
            .get('hnlp.textExtraction.view.message.Authorization')
            .d('其中Y2xpZW50OnNlY3JldA==为clientid值:secret值base64加密后的值'),
          requestBody: intl.get('hnlp.textExtraction.view.message.requestBody').d('请求报文'),
          userName: intl.get('hnlp.textExtraction.view.message.userName').d('用户名'),
          password: intl.get('hnlp.textExtraction.view.message.password').d('密码'),
          passwordTips: intl
            .get('hnlp.textExtraction.view.message.passwordTipsRSA')
            .d('密码需要RSA加密'),
          passwordSuggest: intl
            .get('hnlp.textExtraction.view.message.passwordSuggest')
            .d('建议用http://tool.oschina.net/encrypt?type=3进行加密'),
        },
      },
      common: {
        btn: {
          submit: intl.get('hzero.common.button.submit').d('提交'),
          add: intl.get('hzero.common.button.add').d('新增'),
          del: intl.get('hzero.common.button.delete').d('删除'),
        },
        validation: {
          notNull(name) {
            return intl.get('hzero.common.validation.notNull', { name });
          },
        },
      },
    };
  }

  @Bind()
  async handleSubmit() {
    if (this.createFormRef.current) {
      try {
        const submitData = await this.createFormRef.current.getAsyncSubmitData();
        const { create } = this.props;
        this.setState({
          requestInfo: submitData,
        });
        create({ record: submitData }).then((res) => {
          if (res) {
            notification.success();
          }
        });
      } catch (err) {
        const requestError = [];
        Object.keys(err).forEach((erFieldCode) => {
          requestError.push(...(err[erFieldCode].errors || []).map((er) => er.message));
        });
        this.setState({
          requestInfo: {
            requestError,
          },
        });
      }
    }
  }

  render() {
    const { organizationId, result, isTenant, createLoading = false } = this.props;
    const { requestInfo } = this.state;
    const languageMessge = this.getLanguageMessage();
    return (
      <>
        <Header
          title={intl.get('hnlp.textExtraction.view.message.title.textExtraction').d('识别测试')}
        >
          <Button
            htmlType="button"
            type="primary"
            icon="plus"
            onClick={this.handleSubmit}
            loading={createLoading}
          >
            {languageMessge.common.btn.submit}
          </Button>
        </Header>
        <Content className={styles['text-extraction']}>
          <Spin spinning={createLoading}>
            <Card
              bordered={false}
              title={<h3>{languageMessge.view.title.extractionSource}</h3>}
              className={DETAIL_CARD_CLASSNAME}
            >
              <RequestInfo
                {...requestInfo}
                isTenant={isTenant}
                organizationId={organizationId}
                languageMessage={languageMessge}
              />
              <CreateForm
                wrappedComponentRef={this.createFormRef}
                languageMessage={languageMessge}
                organizationId={organizationId}
                isTenantRoleLevel={isTenant}
              />
            </Card>
            <Card
              bordered={false}
              title={<h3>{languageMessge.view.title.extractionResult}</h3>}
              className={DETAIL_CARD_TABLE_CLASSNAME}
            >
              <Result
                languageMessage={languageMessge}
                result={result}
                isTenantRoleLevel={isTenant}
              />
            </Card>
          </Spin>
        </Content>
      </>
    );
  }
}

function mapStateToProps({ nlpTextExtraction = {}, loading = {} }) {
  const { result } = nlpTextExtraction;
  return {
    result,
    organizationId: getCurrentOrganizationId(),
    isTenant: isTenantRoleLevel(),
    createLoading: loading.effects['nlpTextExtraction/create'],
  };
}

function mapDispatchToProps(dispatch) {
  return {
    create: (payload) =>
      dispatch({
        type: 'nlpTextExtraction/create',
        payload,
      }),
    updateState: (payload) =>
      dispatch({
        type: 'nlpTextExtraction/updateState',
        payload,
      }),
  };
}
