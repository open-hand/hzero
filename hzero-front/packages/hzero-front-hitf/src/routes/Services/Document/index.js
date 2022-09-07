/**
 * Document - 接口文档
 * @date: 2019/5/16
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */
import React, { Component } from 'react';
import { Button, Drawer, Radio, Spin, Tabs } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { groupBy, isEmpty, isFunction } from 'lodash';
import { connect } from 'dva';
import { Controlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/mode/javascript/javascript'; // javascript/json 样式
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/dracula.css';
import intl from 'utils/intl';
// import { DOCS_URI } from '@/constants/constants';
import { yesOrNoRender } from 'utils/renderer';
import notification from 'utils/notification';
import BasicInfo from './BasicInfo';
import GeneralParams from './GeneralParams';
import Demo from './Demo';
import Remark from './Remark';
import TreeParams from './TreeParams';
import RawParams from './RawParams';
import TestCase from '../TestCase';
import styles from './index.less';

const { TabPane } = Tabs;
const RadioGroup = Radio.Group;
const refsMap = {
  REQ: {
    HEADER: 'requestHeaderRef',
    GET: 'queryParamsRef',
    PATH: 'pathParamsRef',
    BODY: {
      'multipart/form-data': 'formDataRef',
      'text/xml': 'xmlRef',
      'application/x-www-form-urlencoded': 'urlencodedRef',
      'application/json': 'jsonRef',
    },
  },
  RESP: {
    HEADER: 'responseHeaderRef',
    BODY: {
      'text/xml': 'respXmlRef',
      'application/json': 'respJsonRef',
      raw: 'respRawRef',
    },
  },
};
/**
 * 接口文档
 * @extends {Component} - React.Component
 * @reactProps {obejct} currentInterface - 当前接口数据
 * @reactProps {object} authenticationData - 服务授权数据
 * @reactProps {boolean} visible - 侧滑是否可见
 * @reactProps {object} currentInterfaceType - 接口类型
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @reactProps {Function} onCancel - 关闭侧滑
 * @return React.element
 */
@connect(({ services, loading }) => ({
  services,
  documentLoading:
    loading.effects['services/queryDocument'] || loading.effects['services/updateDocument'],
  saveDocumentLoading: loading.effects['services/updateDocument'],
  paramsTableLoading:
    loading.effects['services/queryParams'] || loading.effects['services/deleteParams'],
  paramsConfirmLoading:
    loading.effects['services/createParams'] || loading.effects['services/updateParams'],
  reqDemoLoading: loading.effects['services/queryReqDemo'],
  recognizeParamLoading: loading.effects['services/recognizeParam'],
}))
export default class Document extends Component {
  state = {
    changeTypeLoading: false,
  };

  defaultReqMimeType = 'multipart/form-data';

  defaultRespMimeType = 'text/xml';

  /**
   * 关闭接口文档
   */
  @Bind()
  closeDrawer() {
    const { onCancel, dispatch } = this.props;
    onCancel();
    dispatch({
      type: 'services/updateState',
      payload: {
        documentData: {},
        requestParamsAll: null, // 和请求相关的所有参数
        responseParamsAll: null, // 和响应相关的所有参数
        reqDemo: '',
      },
    });
  }

  /**
   * 新开文档预览窗口
   */
  @Bind()
  openPreview() {
    const { currentInterface } = this.props;
    window.open(
      `${process.env.BASE_PATH || '/'}pub/hitf/document-view/${currentInterface.interfaceId}`
    );
  }

  /**
   * 切换横向选项卡
   * @param {string} activeKey 当前选中选项卡
   */
  @Bind()
  changeTab(activeKey) {
    if (activeKey === 'testcase') {
      this.queryTestCaseList();
    }

    if (activeKey === 'demo') {
      this.queryReqDemo();
    }
  }

  /**
   * 切换Body类型时
   * @param {*} e EVENT
   */
  @Bind()
  changeBodyType(e) {
    this.saveDocument({
      reqMimeType: e.target.value,
      reqRawFlag: e.target.value === 'raw' ? 1 : 0,
    });
  }

  /**
   * 切换响应头部类型时
   * @param {*} e EVENT
   */
  @Bind()
  changeRespType(e) {
    this.saveDocument({
      respMimeType: e.target.value,
      respRawFlag: e.target.value === 'raw' ? 1 : 0,
    });
  }

  /**
   * 切换raw类型
   * @param {object} rawType raw类型
   */
  @Bind()
  changeRawType(rawType) {
    this.saveDocument(rawType);
  }

  /**
   * 保存参数
   * @param {*} values 新建或编辑后的参数数据
   * @param {*} flag 编辑或新建
   */
  @Bind()
  handleSaveParams({ values, flag, cb = (e) => e }) {
    const {
      dispatch,
      currentInterface,
      services: { documentData },
    } = this.props;
    dispatch({
      type: flag === 'create' ? 'services/createParams' : 'services/updateParams',
      payload: {
        documentId: documentData.documentId,
        interfaceId: currentInterface.interfaceId,
        paramValueType: 'STRING',
        ...values,
      },
    }).then((res) => {
      if (res) {
        const { actionType, paramType, mimeType = '' } = values;
        notification.success();
        dispatch({
          type: 'services/queryParams',
          payload: documentData.documentId,
        });
        if (values.mimeType !== 'raw') {
          const ref =
            values.paramType === 'BODY'
              ? refsMap[actionType][paramType][mimeType]
              : refsMap[actionType][paramType];
          if (isFunction(cb)) {
            cb();
          }
          this[ref].closeDrawer();
        }
      }
    });
  }

  /**
   * 删除参数
   * @param {object} record 当前行数据
   */
  @Bind()
  handleDeleteParams(record) {
    const {
      dispatch,
      services: { documentData },
    } = this.props;
    dispatch({
      type: 'services/deleteParams',
      payload: {
        ...record,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        dispatch({
          type: 'services/queryParams',
          payload: documentData.documentId,
        });
      }
    });
  }

  /**
   * 查询请求示例
   */
  @Bind()
  queryReqDemo() {
    const {
      dispatch,
      services: { documentData },
      currentInterface: { interfaceId },
    } = this.props;
    dispatch({
      type: 'services/queryReqDemo',
      payload: {
        interfaceId,
        documentId: documentData.documentId,
      },
    });
  }

  /**
   * 查询测试用例列表
   * @param {object} params - 分页参数
   */
  @Bind()
  queryTestCaseList(params = {}) {
    const {
      dispatch,
      currentInterface: { interfaceId },
    } = this.props;
    dispatch({
      type: 'services/queryTestCase',
      payload: { page: params, interfaceId },
    });
  }

  /**
   * 保存文档（不含参数）
   * @param {object} mimeTypeObj - mimeType对象
   */
  @Bind()
  saveDocument(mimeTypeObj = null) {
    if (this.basicInfoForm) {
      const documentName = this.basicInfoForm.getValues();
      if (documentName === false) {
        return;
      }
    }
    this.setState({
      changeTypeLoading: !!mimeTypeObj,
    });
    const {
      dispatch,
      currentInterface,
      services: {
        documentData: {
          documentId,
          documentName,
          respSuccessStatus,
          respSuccessMimeType,
          respSuccessDemo,
          respFailedStatus,
          respFailedMimeType,
          respFailedDemo,
          remark,
          reqRemark,
          respRemark,
          reqMimeType,
          respMimeType,
          reqRootType,
          respRootType,
          reqRawFlag,
          respRawFlag,
          objectVersionNumber,
          _token,
        },
      },
    } = this.props;
    const detailEditor = this.detailedDes
      ? (this.detailedDes.staticTextEditor || {}).current.editor
      : null;
    const reqEditor = this.requestDes
      ? (this.requestDes.staticTextEditor || {}).current.editor
      : null;
    const respEditor = this.responseDes
      ? (this.responseDes.staticTextEditor || {}).current.editor
      : null;
    let successDemoValues = {
      respSuccessStatus,
      respSuccessMimeType,
      respSuccessDemo,
    };
    let failedDemoValues = {
      respFailedStatus,
      respFailedMimeType,
      respFailedDemo,
    };
    if (this.successDemoForm) {
      successDemoValues = this.successDemoForm.getValues();
    }
    if (this.failedDemoForm) {
      failedDemoValues = this.failedDemoForm.getValues();
    }
    let totalValues = {
      documentName: this.basicInfoForm ? this.basicInfoForm.getValues() : documentName,
      remark: detailEditor ? detailEditor.getData() : remark,
      reqRemark: reqEditor ? reqEditor.getData() : reqRemark,
      respRemark: respEditor ? respEditor.getData() : respRemark,
      ...successDemoValues,
      ...failedDemoValues,
      reqMimeType: reqMimeType || this.defaultReqMimeType,
      respMimeType: respMimeType || this.defaultRespMimeType,
      reqRootType,
      respRootType,
      reqRawFlag,
      respRawFlag,
    };
    if (mimeTypeObj) {
      totalValues = { ...totalValues, ...mimeTypeObj };
    }
    dispatch({
      type: 'services/updateDocument',
      payload: {
        documentId,
        interfaceId: currentInterface && currentInterface.interfaceId,
        objectVersionNumber,
        _token,
        ...totalValues,
      },
    }).then((res) => {
      if (res) {
        if (!mimeTypeObj) {
          notification.success();
        }
        dispatch({
          type: 'services/queryDocument',
          payload: currentInterface && currentInterface.interfaceId,
        });
      }
    });
  }

  /**
   * 参数识别
   */
  @Bind()
  handleRecognizeParams() {
    const {
      dispatch,
      tenantId,
      currentInterface: { interfaceId },
      services: { documentData },
    } = this.props;
    dispatch({
      type: 'services/recognizeParam',
      payload: { interfaceId, organizationId: tenantId },
      interfaceId,
    }).then((res) => {
      if (res) {
        notification.success();
        dispatch({
          type: 'services/queryParams',
          payload: documentData.documentId,
        });
      }
    });
  }

  render() {
    const {
      services: {
        documentData,
        requestParamsAll,
        responseParamsAll,
        reqDemo,
        enumMap: {
          requestHeaderTypes = [],
          paramValueTypes = [],
          respContentTypes = [],
          mimeTypes = [],
          rawMimeTypes = [],
          rootTypes = [],
        },
      },
      visible,
      currentInterface,
      documentLoading = false,
      recognizeParamLoading = false,
      saveDocumentLoading,
      paramsConfirmLoading,
      paramsTableLoading = false,
      reqDemoLoading = false,
      currentInterfaceType,
    } = this.props;
    const { changeTypeLoading } = this.state;
    const respMimeTypes = (mimeTypes && mimeTypes.slice(2)) || [];
    let handledReqMimeType = this.defaultReqMimeType;
    let handledRespMimeType = this.defaultRespMimeType;
    let bodyData = {};
    let respBodyData = {};
    let resRawType = 'text/plain';
    let respRawType = 'text/plain';
    if (requestParamsAll && requestParamsAll.BODY) {
      bodyData = groupBy(requestParamsAll.BODY, 'mimeType');
    }
    if (responseParamsAll && responseParamsAll.BODY) {
      respBodyData = groupBy(responseParamsAll.BODY, 'mimeType');
    }

    if (documentData.reqMimeType) {
      if (mimeTypes && mimeTypes.find((item) => item.meaning === documentData.reqMimeType)) {
        handledReqMimeType = documentData.reqMimeType;
      } else {
        handledReqMimeType = 'raw';
        resRawType = documentData.reqMimeType;
      }
    }

    if (documentData.respMimeType) {
      if (
        respMimeTypes.length &&
        respMimeTypes.find((item) => item.meaning === documentData.respMimeType)
      ) {
        handledRespMimeType = documentData.respMimeType;
      } else {
        handledRespMimeType = 'raw';
        respRawType = documentData.respMimeType;
      }
    }
    const drawerProps = {
      title: intl.get('hitf.services.view.button.document').d('编辑接口文档'),
      onClose: this.closeDrawer,
      visible,
      mask: true,
      maskStyle: { backgroundColor: 'rgba(0,0,0,.85)' },
      placement: 'right',
      destroyOnClose: true,
      width: 1000,
      style: {
        height: 'calc(100% - 55px)',
        overflow: 'auto',
        padding: 12,
      },
    };
    const basicInfoProps = {
      currentInterface,
      currentInterfaceType,
      documentName: documentData.documentName,
      onRecognize: this.handleRecognizeParams,
      recognizeParamLoading,
      wrappedComponentRef: (form) => {
        this.basicInfoForm = form;
      },
    };
    const commonParamsProps = {
      loading: paramsTableLoading,
      confirmLoading: paramsConfirmLoading,
      actionType: 'REQ',
      interfaceId: currentInterface && currentInterface.interfaceId,
      onSave: this.handleSaveParams,
      onDelete: this.handleDeleteParams,
      mainColumns: [
        {
          title: intl.get('hitf.services.model.services.paramName').d('参数名'),
          dataIndex: 'paramName',
          width: 150,
        },
        {
          title: intl.get('hitf.services.model.services.requiredFlag').d('是否必填'),
          dataIndex: 'requiredFlag',
          width: 100,
          render: yesOrNoRender,
        },
        {
          title: intl.get('hitf.services.model.services.formatRegexp').d('格式限制'),
          dataIndex: 'formatRegexp',
          width: 150,
        },
        {
          title: intl.get('hitf.services.model.services.remark').d('说明'),
          dataIndex: 'remark',
        },
        {
          title: intl.get('hitf.services.model.services.demo').d('示例'),
          dataIndex: 'valueDemo',
          width: 150,
        },
      ],
    };
    const commonBodyParamsColumns = [
      {
        title: intl.get('hitf.services.model.services.paramName').d('参数名'),
        dataIndex: 'paramName',
        width: 150,
      },
      {
        title: intl.get('hitf.services.model.services.paramValueType').d('类型'),
        dataIndex: 'paramValueType',
      },
      {
        title: intl.get('hitf.services.model.services.requiredFlag').d('是否必填'),
        dataIndex: 'requiredFlag',
        width: 100,
        render: yesOrNoRender,
      },
      {
        title: intl.get('hitf.services.model.services.formatRegexp').d('格式限制'),
        dataIndex: 'formatRegexp',
        width: 150,
      },
      {
        title: intl.get('hitf.services.model.services.remark').d('说明'),
        dataIndex: 'remark',
      },
      {
        title: intl.get('hitf.services.model.services.demo').d('示例'),
        dataIndex: 'valueDemo',
        width: 150,
      },
    ];
    const specificBodyColumns = [...commonBodyParamsColumns];
    specificBodyColumns.splice(1, 0, {
      title: intl.get('hitf.services.model.services.defaultValue').d('默认值'),
      dataIndex: 'defaultValueLongtext',
      width: 150,
    });

    // 请求头参数
    const requestHeaderProps = {
      ...commonParamsProps,
      paramType: 'HEADER',
      dataSource: requestParamsAll && requestParamsAll.HEADER,
      requestHeaderTypes,
      mainColumns: [
        {
          title: intl.get('hitf.services.model.services.param').d('参数'),
          dataIndex: 'paramName',
          width: 150,
        },
        {
          title: intl.get('hitf.services.model.services.defaultValue').d('默认值'),
          dataIndex: 'defaultValue',
          width: 150,
        },
      ],
      ref: (node) => {
        this.requestHeaderRef = node;
      },
    };
    const defaultValueCol = [
      {
        title: intl.get('hitf.services.model.services.defaultValue').d('默认值'),
        dataIndex: 'defaultValue',
        width: 150,
      },
    ];
    // GET/URL参数
    const queryParamsProps = {
      ...commonParamsProps,
      mainColumns: commonParamsProps.mainColumns.concat(defaultValueCol),
      paramType: 'GET',
      dataSource: requestParamsAll && requestParamsAll.GET,
      ref: (node) => {
        this.queryParamsRef = node;
      },
    };
    // PATH参数
    const pathParamsProps = {
      ...commonParamsProps,
      mainColumns: commonParamsProps.mainColumns.concat(defaultValueCol),
      paramType: 'PATH',
      dataSource: requestParamsAll && requestParamsAll.PATH,
      ref: (node) => {
        this.pathParamsRef = node;
      },
    };
    // 响应头参数
    const responseHeaderProps = {
      ...requestHeaderProps,
      actionType: 'RESP',
      dataSource: responseParamsAll && responseParamsAll.HEADER,
      ref: (node) => {
        this.responseHeaderRef = node;
      },
    };
    // 响应成功示例
    const successDemoFormProps = {
      respContentTypes,
      demoType: 'Success',
      status: documentData.respSuccessStatus,
      mimeType: documentData.respSuccessMimeType,
      demo: documentData.respSuccessDemo,
      wrappedComponentRef: (form) => {
        this.successDemoForm = form;
      },
    };
    // 响应失败示例
    const failedDemoFormProps = {
      respContentTypes,
      demoType: 'Failed',
      status: documentData.respFailedStatus,
      mimeType: documentData.respFailedMimeType,
      demo: documentData.respFailedDemo,
      wrappedComponentRef: (form) => {
        this.failedDemoForm = form;
      },
    };
    // 详细说明
    const detailedDesProps = {
      content: documentData.remark,
      onRef: (node) => {
        this.detailedDes = node;
      },
    };
    // 请求说明
    const requestDesProps = {
      content: documentData.reqRemark,
      onRef: (node) => {
        this.requestDes = node;
      },
    };
    // 响应说明
    const responseDesProps = {
      content: documentData.respRemark,
      onRef: (node) => {
        this.responseDes = node;
      },
    };
    // formData格式Body
    const formDataProps = {
      ...commonParamsProps,
      paramValueTypes,
      paramType: 'BODY',
      mimeType: 'multipart/form-data',
      dataSource: bodyData['multipart/form-data'] || [],
      mainColumns: specificBodyColumns,
      ref: (node) => {
        this.formDataRef = node;
      },
    };
    // XML格式Body
    const xmlProps = {
      paramValueTypes,
      loading: paramsTableLoading,
      confirmLoading: paramsConfirmLoading,
      paramType: 'BODY',
      mimeType: 'text/xml',
      dataSource: bodyData['text/xml'] || [],
      mainColumns: commonBodyParamsColumns,
      actionType: 'REQ',
      interfaceId: currentInterface && currentInterface.interfaceId,
      onSave: this.handleSaveParams,
      onDelete: this.handleDeleteParams,
      ref: (node) => {
        this.xmlRef = node;
      },
    };
    // application/x-www-form-urlencoded格式Body
    const urlencodedProps = {
      ...commonParamsProps,
      paramValueTypes,
      paramType: 'BODY',
      mimeType: 'application/x-www-form-urlencoded',
      dataSource: bodyData['application/x-www-form-urlencoded'] || [],
      mainColumns: specificBodyColumns,
      ref: (node) => {
        this.urlencodedRef = node;
      },
    };
    // json格式Body
    const jsonProps = {
      rootTypes,
      paramValueTypes,
      loading: paramsTableLoading,
      confirmLoading: paramsConfirmLoading,
      paramType: 'BODY',
      reqRootType: documentData.reqRootType,
      respRootType: documentData.respRootType,
      mimeType: 'application/json',
      dataSource: bodyData['application/json'] || [],
      mainColumns: commonBodyParamsColumns,
      actionType: 'REQ',
      interfaceId: currentInterface && currentInterface.interfaceId,
      onSave: this.handleSaveParams,
      onDelete: this.handleDeleteParams,
      onChangeRoot: this.saveDocument,
      ref: (node) => {
        this.jsonRef = node;
      },
    };
    // raw格式Body
    const rawProps = {
      loading: paramsTableLoading,
      confirmLoading: paramsConfirmLoading,
      rawMimeTypes,
      paramType: 'BODY',
      mimeType: 'raw',
      resRawType,
      respRawType,
      dataSource: bodyData.raw || '',
      actionType: 'REQ',
      interfaceId: currentInterface && currentInterface.interfaceId,
      reqRawFlag: documentData.reqRawFlag,
      respRawFlag: documentData.respRawFlag,
      onSave: this.handleSaveParams,
      onRawChange: this.changeRawType,
    };
    // xml格式响应体
    const respXmlProps = {
      ...xmlProps,
      actionType: 'RESP',
      dataSource: respBodyData['text/xml'] || [],
      ref: (node) => {
        this.respXmlRef = node;
      },
    };
    // json格式响应体
    const respJsonProps = {
      ...jsonProps,
      actionType: 'RESP',
      dataSource: respBodyData['application/json'] || [],
      ref: (node) => {
        this.respJsonRef = node;
      },
    };
    // raw格式响应体
    const respRawProps = {
      ...rawProps,
      dataSource: respBodyData.raw || '',
      actionType: 'RESP',
    };
    // 测试用例
    const testCaseProps = {
      interfaceId: currentInterface && currentInterface.interfaceId,
      onSearch: this.queryTestCaseList,
    };
    return (
      <Drawer {...drawerProps}>
        <Spin spinning={documentLoading || recognizeParamLoading}>
          <BasicInfo {...basicInfoProps} />
          <Tabs
            defaultActiveKey="params"
            animated={false}
            className={styles['top-tabs']}
            onChange={this.changeTab}
            forceRender
          >
            <TabPane
              tab={intl.get('hitf.document.view.title.paramsInfo').d('参数信息')}
              key="params"
            >
              <Tabs
                animated={false}
                defaultActiveKey="requestHeader"
                tabPosition="left"
                className={styles['sub-params-tabs']}
              >
                <TabPane
                  tab={intl.get('hitf.document.view.title.requestHeader').d('请求头部')}
                  key="requestHeader"
                >
                  <GeneralParams {...requestHeaderProps} />
                </TabPane>
                <TabPane
                  tab={intl.get('hitf.document.view.title.queryParams').d('GET/URL参数')}
                  key="queryParams"
                >
                  <GeneralParams {...queryParamsProps} />
                </TabPane>
                <TabPane
                  tab={intl.get('hitf.document.view.title.pathParams').d('路径参数')}
                  key="pathParams"
                >
                  <GeneralParams {...pathParamsProps} />
                </TabPane>
                {currentInterface && currentInterface.requestMethod !== 'GET' && (
                  <TabPane
                    tab={intl.get('hitf.document.view.title.bodyParams').d('BODY参数')}
                    key="body"
                  >
                    <RadioGroup
                      name="radiogroup"
                      value={handledReqMimeType}
                      onChange={this.changeBodyType}
                    >
                      {mimeTypes.length &&
                        mimeTypes.map(({ value, meaning }) => (
                          <Radio key={value} value={meaning}>
                            {meaning}
                          </Radio>
                        ))}
                    </RadioGroup>
                    {handledReqMimeType === 'multipart/form-data' && (
                      <GeneralParams {...formDataProps} />
                    )}
                    {handledReqMimeType === 'application/x-www-form-urlencoded' && (
                      <GeneralParams {...urlencodedProps} />
                    )}
                    {handledReqMimeType === 'text/xml' && <TreeParams {...xmlProps} />}
                    {handledReqMimeType === 'application/json' && <TreeParams {...jsonProps} />}
                    {handledReqMimeType === 'raw' && <RawParams {...rawProps} />}
                  </TabPane>
                )}
                <TabPane
                  tab={intl.get('hitf.document.view.title.responseHeader').d('响应头部')}
                  key="responseHeader"
                >
                  <GeneralParams {...responseHeaderProps} />
                </TabPane>
                <TabPane
                  tab={intl.get('hitf.document.view.title.responseBody').d('响应结果')}
                  key="responseBody"
                >
                  <RadioGroup
                    name="radiogroup"
                    value={handledRespMimeType}
                    onChange={this.changeRespType}
                  >
                    {respMimeTypes.length &&
                      respMimeTypes.map(({ value, meaning }) => (
                        <Radio key={value} value={meaning}>
                          {meaning}
                        </Radio>
                      ))}
                  </RadioGroup>
                  {handledRespMimeType === 'text/xml' && <TreeParams {...respXmlProps} />}
                  {handledRespMimeType === 'application/json' && <TreeParams {...respJsonProps} />}
                  {handledRespMimeType === 'raw' && <RawParams {...respRawProps} />}
                </TabPane>
              </Tabs>
            </TabPane>
            <TabPane tab={intl.get('hitf.document.view.title.demo').d('示例')} key="demo">
              <Tabs
                animated={false}
                defaultActiveKey="requestDemo"
                tabPosition="left"
                className={styles['sub-params-tabs']}
              >
                <TabPane
                  tab={intl.get('hitf.document.view.title.requestDemo').d('请求示例')}
                  key="requestDemo"
                >
                  <Spin spinning={reqDemoLoading}>
                    {!isEmpty(reqDemo) ? (
                      <CodeMirror
                        autoScroll
                        className={styles['hzero-codemirror']}
                        value={reqDemo}
                        readOnly
                        options={{
                          mode: 'javascript',
                          lineNumbers: true,
                        }}
                      />
                    ) : (
                      <div style={{ color: '#666', height: '300px' }}>
                        {intl.get('hitf.document.view.message.empty').d('暂无')}
                      </div>
                    )}
                  </Spin>
                </TabPane>
                <TabPane
                  tab={intl.get('hitf.document.view.title.successDemo').d('成功示例')}
                  key="successDemo"
                >
                  <Demo {...successDemoFormProps} />
                </TabPane>
                <TabPane
                  tab={intl.get('hitf.document.view.title.failedDemo').d('失败示例')}
                  key="failedDemo"
                >
                  <Demo {...failedDemoFormProps} />
                </TabPane>
              </Tabs>
            </TabPane>
            <TabPane
              tab={intl.get('hitf.document.view.title.detailDes').d('详细说明')}
              key="detail"
            >
              <Tabs
                animated={false}
                defaultActiveKey="detailedDes"
                tabPosition="left"
                className={styles['sub-params-tabs']}
              >
                <TabPane
                  tab={intl.get('hitf.document.view.title.detailDes').d('详细说明')}
                  key="detailedDes"
                >
                  <Remark {...detailedDesProps} />
                </TabPane>
                <TabPane
                  tab={intl.get('hitf.document.view.title.requestDes').d('请求说明')}
                  key="requestDes"
                >
                  <Remark {...requestDesProps} />
                </TabPane>
                <TabPane
                  tab={intl.get('hitf.document.view.title.responseDes').d('响应说明')}
                  key="responseDes"
                >
                  <Remark {...responseDesProps} />
                </TabPane>
              </Tabs>
            </TabPane>
            <TabPane
              tab={intl.get('hitf.document.view.title.testcase').d('测试用例')}
              key="testcase"
            >
              <TestCase {...testCaseProps} />
            </TabPane>
          </Tabs>
        </Spin>
        <div className={styles['hiam-interface-model-btns']}>
          <Button onClick={this.closeDrawer} style={{ marginRight: 8 }}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button onClick={this.openPreview} style={{ marginRight: 8 }}>
            {intl.get('hitf.services.view.button.view').d('预览')}
          </Button>
          {!(changeTypeLoading && documentLoading) ? (
            <Button
              type="primary"
              onClick={() => this.saveDocument(null)}
              loading={saveDocumentLoading}
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </Button>
          ) : (
            <Button type="primary">{intl.get('hzero.common.button.save').d('保存')}</Button>
          )}
        </div>
      </Drawer>
    );
  }
}
