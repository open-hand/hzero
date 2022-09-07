import React, { useEffect, useMemo, useRef, useState } from 'react';
import Viewer from 'react-viewer';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { Content, Header } from 'components/Page';
import { Form, Input, Checkbox, Row, Col, Radio, Button, Spin, Icon, Modal } from 'hzero-ui';
import notification from 'utils/notification';
import { closeTab } from 'utils/menuTab';
import { downloadFileByAxios } from 'services/api';
import uuid from 'node-uuid';
import { connect } from 'dva';
import ImgPoint from '../../../assets/market/point.svg';
import BaseUpload from './components/BaseUpload';
import LazyLoadingSelect from './components/LazyLoadingSelect';
import CategorySelect from './components/CategorySelect';
import RichTextTool from '../components/RichTextTool';
import LogTips from './components/LogTips';
import UserTermsModal from '../components/UserTermsModal';
import ImgFeedbackTips from '../../../assets/market/feedback-tips.png';
import IconStart from '../../../assets/market/status/start.svg';
import IconFinished from '../../../assets/market/status/finished.svg';
import {
  queryCategory,
  querySubCategory,
  queryMapCategory,
  submitOrder,
  saveAsFile,
  queryDisplayUrl,
  queryHzeroVersion,
  startTraceLogs,
  queryTraceLogsStatus,
  endTraceLogs,
} from './services';
import { MARKET_USER_INFO_KEY } from '../utils/constants.js';
import { getMarketUserInfo, queryMarketConfig, saveMarketConfig } from '../Home/services';
import { getCategoryValue } from './utils/base';
import styles from './index.less';
import LoginModal from '../components/LoginModal';
import { marketUserLogin } from '../ServiceList/services';
import { TRACE_LOG } from './utils/internationalization';
import { HZERO_ADM } from '../../../utils/config';

// const BACK_TO_HOME_URL = '/market-client/home';

const formItemLayout = {
  labelAlign: 'right',
  labelCol: {
    span: 2,
  },
  wrapperCol: {
    span: 22,
  },
};

// 默认一级分类选择产品
const defaultCategorySelect = 'product';

// 上传日志分类
const LOG_TYPE = [
  // {
  //   key: 'needSystemInfo',
  //   value: 'JVM信息',
  // },
  {
    key: 'needDependenciesInfo',
    value: '依赖信息',
  },
  {
    key: 'needTraceInfo',
    value: 'trace日志',
  },
  // {
  //   key: 'needConfigurationInfo',
  //   value: '配置信息',
  // },
  // {
  //   key: 'needServiceInfo',
  //   value: '服务实例信息',
  // },
];

// 日志收集状态
const TRACE_STATUS = {
  NO: 'no', // 未开始
  RUNNING: 'running', // 正在进行中
  FINISHED: 'finished', // 完成
};

let displayCode = null;
let marketConfig = null;
const Feedback = ({ form, location, global: { hzeroUILocale } }) => {
  const search = formatSearch(location.search);
  const { getFieldDecorator, validateFields, getFieldValue, setFieldsValue } = form;
  const [firstCategories, setFirstCategories] = useState([]); // 一级分类集
  const [subCategories, setSubCategories] = useState([]); // 二级分类集
  const [headerIdList, setHeaderIdList] = useState([]); // headerId选项
  const [loading, setLoading] = useState(false);
  const [productVersion, setProductVersion] = useState(''); // 版本
  const [isShowRule, setIsShowRule] = useState(false); // 是否显示 hzero 用户体验计划
  const [isAgree, setIsAgree] = useState(false); // 是否同意加入hzero用户体验计划
  const [isRuleVisible, setIsRuleVisible] = useState(false); // 是否显示 hzero用户体验计划 弹框
  const [pageLoading, setPageLoading] = useState(false); // 页面加载状态
  const [viewerVisible, setViewerVisible] = useState(false);
  const [loginModalVisible, setLoginModalVisible] = useState(false); // 是否显示登陆弹框
  const [currentTraceStatus, setCurrentTraceStatus] = useState(() => TRACE_STATUS.NO); // 当前日志收集状态
  const [traceFile, setTraceFile] = useState(null); // trace 日志附件

  const editorRef = useRef();
  const cacheCurrentSelectedLog = useRef([]); // 缓存当前选中的一个日志
  const currentCategory = getFieldValue('category');
  const isCN = hzeroUILocale.locale === 'zh_CN'; // 是否语言为中文

  useEffect(() => {
    _init();
  }, []);

  // 填充标题
  useEffect(() => {
    setFieldsValue({
      title: `${getFirstCategoryValue(currentCategory)}-${getSubCategoryValue(
        getFieldValue('sourceHeaderId')
      )}`,
    });
  }, [getFieldValue('category'), getFieldValue('sourceHeaderId'), firstCategories, subCategories]);

  const _init = () => {
    // 默默获取一次用户信息
    getMarketUserInfo((res) => {
      if (res && !res.failed) {
        // 如果没有登录,重新登录
        if (!res.id) {
          setLoginModalVisible(true);
        } else {
          sessionStorage.setItem(MARKET_USER_INFO_KEY, JSON.stringify(res || ''));
        }
      }
    });

    // 初始化一级分类
    queryCategory().then((res) => {
      if (res && !res.failed) {
        setFirstCategories(res);
        setFieldsValue({ category: defaultCategorySelect });
      }
    });
    // 初始化 hzero 规则状态
    queryMarketConfig().then((res) => {
      if (res && !res.failed) {
        marketConfig = res;
        if (typeof res.joinFlag === 'boolean' && res.joinFlag === true) {
          setIsShowRule(false);
        } else {
          setIsShowRule(true);
        }
      }
    });
    // 查询当前hzero版本
    queryHzeroVersion().then((res) => {
      if (res && !res.failed) {
        setProductVersion(res.version);
      }
    });

    // 查询当前是否开启日志收集
    queryTraceLogsStatus().then((res = {}) => {
      if (res && !res.failed) {
        setCurrentTraceStatus(res.flag ? TRACE_STATUS.RUNNING : TRACE_STATUS.NO);
      }
    });
  };

  function formatSearch(_search) {
    const obj = {};
    const str = _search.substr(1);
    const arr = str.split('&');
    for (let i = 0; i < arr.length; i++) {
      const newArr = arr[i].split('=');
      // eslint-disable-next-line prefer-destructuring
      obj[newArr[0]] = decodeURIComponent(newArr[1]);
    }
    return obj;
  }

  const defaultRichText = useMemo(() => {
    return `
      <p><strong>【相关路径】:${search.fromName ? search.fromName : ''}[${
      search.fromPath ? search.fromPath : ''
    }]</strong></p>
      <p><strong>【组件/服务/产品版本】:${productVersion}</strong></p>
      <p><strong>【问题描述】:</strong></p>`;
  }, [currentCategory, productVersion]);

  // 初始化问题标题
  const initialTitle = () => {
    return `${currentCategory}-`;
  };

  const getFirstCategoryValue = (category) => {
    return getCategoryValue(category, firstCategories, 'value', 'meaning');
  };

  const getSubCategoryValue = (subcategory) => {
    return getCategoryValue(subcategory, subCategories, 'sourceHeaderId', 'subcategory');
  };

  // application map 查询
  const getMapCategory = (params) => {
    if (!params) {
      setHeaderIdList([]);
      return;
    }
    setPageLoading(true);
    queryMapCategory(params).then((res) => {
      setPageLoading(false);
      if (res && !res.failed) {
        const headerIds = res.map(({ serviceId, serviceName }) => ({ serviceId, serviceName }));
        setHeaderIdList(headerIds);
        const defaultHeaderIds = headerIds.map((v) => v.serviceId);
        setFieldsValue({ headerId: defaultHeaderIds });
      }
    });
  };

  // 文件上传前
  const beforeUpload = (file, fileList) => {
    return !!(
      fileList &&
      fileList.length <= 3 &&
      fileList.every((one) => one.size / 1024 / 1024 < 20)
    );
  };

  const clearLogs = () => {
    setFieldsValue({ log: [] });
  };

  // 单选框
  const onAgreeChange = (e) => {
    e.stopPropagation();
    if (isAgree) {
      clearLogs();
    }
    setIsAgree(!isAgree);
  };

  const onSubCategoryChange = (sourceHeaderId, newSubCategories = []) => {
    const searchSubCategories = newSubCategories.length > 0 ? newSubCategories : subCategories;
    const { paramMap = {} } = searchSubCategories.find((v) => v.sourceHeaderId === sourceHeaderId);
    if (paramMap.length > 0) {
      getMapCategory(paramMap[0]);
    }
  };

  const onRuleClick = (e) => {
    e.stopPropagation();
    setIsRuleVisible(true);
  };

  const onLogCheckboxChange = (logs) => {
    // 检查是否同意过
    if (isShowRule && !isAgree) {
      setIsRuleVisible(true);
      setTimeout(() => {
        clearLogs();
      });
    }

    // 找出取消了哪个项
    if (logs.length < cacheCurrentSelectedLog.current.length) {
      const filter = cacheCurrentSelectedLog.current.filter((v) => !logs.includes(v));
      // 如果取消的是 trace日志, 检查是否结束追踪
      if (filter.includes('needTraceInfo')) {
        if (currentTraceStatus === TRACE_STATUS.RUNNING) {
          Modal.confirm({
            title: intl
              .get('hadm.marketclient.view.trace.cancel')
              .d('检测到trace日志还在收集中，是否结束trace日志收集以取消上传trace日志'),
            onOk() {
              cacheCurrentSelectedLog.current = logs;
              handleEndCollecting();
            },
            onCancel() {
              setFieldsValue({ log: cacheCurrentSelectedLog.current });
            },
          });
        }
      }
      return;
    }
    cacheCurrentSelectedLog.current = logs;
  };

  // 校验问题描述
  function checkProblemDesc() {
    let content = editorRef?.current?.getContent() || null;
    content = JSON.parse(content);
    if (content && content.blocks.length === 1 && content.blocks[0].text === '') {
      notification.error({
        message: intl
          .get('hzero.c7nProUI.Validator.value_missing', {
            label: intl.get('hmsg.messageQuery.model.messageQuery.content').d('内容'),
          })
          .d('请输入{label}'),
      });
      return false;
    }
    return true;
  }

  const handleRuleModalResult = (visible) => {
    if (visible) {
      setIsAgree(true);
      setFieldsValue({ log: cacheCurrentSelectedLog.current });
    } else {
      // 不同意,清空上传日志
      setIsAgree(false);
      clearLogs();
    }
    setIsRuleVisible(false);
  };

  const handleSubmit = () => {
    validateFields(async (err, values) => {
      if (!checkProblemDesc()) return;
      if (!err) {
        // 检查勾选了trace日志,但是没有收集到日志的情况
        if (values.log.includes('needTraceInfo')) {
          if (currentTraceStatus === TRACE_STATUS.RUNNING) {
            notification.warning({
              message: intl
                .get('hadm.marketclient.view.trace.cancel.order')
                .d('检测到trace日志还在收集中，请结束trace日志收集以提交工单'),
            });
            return;
          } else if (!traceFile) {
            notification.warning({
              message: intl.get('hadm.marketclient.view.trace.please').d('请收集trace日志'),
            });
            return;
          }
        }

        setLoading(true);
        // 先检查是否勾选规则
        if (isShowRule && isAgree) {
          const marketConfigRes = await saveMarketConfig({ ...marketConfig, joinFlag: true });
          if (!marketConfigRes || marketConfigRes.failed) {
            return;
          }
        }
        const content = JSON.parse(editorRef.current.getContent());
        if (values.log.length > 0) {
          // 添加跳转链接
          const display = await queryDisplayUrl();
          if (display && display.displayUrl) {
            displayCode = display.code;
            const tips = '客户端收集用户问题反馈数据信息：';
            const extraContent = [
              {
                key: '11111',
                text: '--------------------',
                type: 'unstyled',
                depth: 0,
                inlineStyleRanges: [{ offset: 0, length: 20, style: 'BOLD' }],
                entityRanges: [],
                data: {},
              },
              {
                key: '22222',
                text: `${tips}${display.displayUrl}`,
                type: 'unstyled',
                depth: 0,
                inlineStyleRanges: [
                  { offset: 0, length: tips.length + display.displayUrl.length, style: 'BOLD' },
                ],
                entityRanges: [],
                data: {},
              },
            ];

            content.blocks = content.blocks.concat(extraContent);
          } else {
            notification.error({
              message: intl.get('hadm.marketclient.view.modal.error').d('程序出现错误'),
            });
            return;
          }
        } else {
          displayCode = null;
        }
        const response = await saveAsFile({
          content: JSON.stringify(content),
          fileName: `${values.title.split(' ', '')}-${uuid()}.json`,
        });
        if (!response) {
          setLoading(false);
          return;
        }
        // 预提交的参数
        const preSubmit = values;
        // 处理 trace 日志
        if (preSubmit?.log.includes('needTraceInfo')) {
          // 如果收集中,必须关闭才能提交
          if (currentTraceStatus === TRACE_STATUS.RUNNING) {
            notification.warning({
              message: intl
                .get('hadm.marketclient.view.trace.cancel.order')
                .d('检测到trace日志还在收集中，请结束trace日志收集以提交工单'),
            });
            setLoading(false);
            return;
          }
          if (traceFile) {
            if (!preSubmit.attachmentList) {
              preSubmit.attachmentList = [];
            }
            preSubmit.attachmentList.push(traceFile);
          }
        }
        // 日志参数
        preSubmit.log.forEach((log) => {
          preSubmit[log] = true;
        });
        delete preSubmit.log;
        // 问题描述
        preSubmit.questionDescKey = response.data?.fileKey;
        // sourceType
        preSubmit.sourceType = preSubmit.category;
        // category
        preSubmit.category = firstCategories.find((v) => v.value === preSubmit.category)?.meaning;
        // displayCode
        if (displayCode) {
          preSubmit.displayCode = displayCode;
        }

        // 提交请求
        submitOrder(preSubmit).then((res) => {
          if (res && !res.failed) {
            notification.success({
              message: intl.get('hzero.c7nProUI.DataSet.submit_success').d('提交成功'),
            });
            // history.goBack();
            setLoading(false);
            closeTab('/market-client/feedback');
          } else {
            notification.error({ message: res.message });
            setLoading(false);
          }
        });
      }
    });
  };

  const renderSubCategory = () => {
    return (
      <Form.Item wrapperCol={{ span: 24 }}>
        {getFieldDecorator('sourceHeaderId', {
          initialValue: subCategories[0]?.sourceHeaderId,
          rules: [
            {
              required: true,
              message: intl
                .get('hzero.c7nProUI.Validator.value_missing', {
                  label: intl.get('hadm.marketclient.view.feedback.classify.second').d('二级分类'),
                })
                .d('请输入{label}'),
            },
          ],
        })(
          <LazyLoadingSelect
            subCategories={subCategories}
            setSubCategories={setSubCategories}
            category={currentCategory}
            onChange={onSubCategoryChange}
            queryApi={querySubCategory}
            onSubCategoryChange={onSubCategoryChange}
            setFieldsValue={setFieldsValue}
            setPageLoading={setPageLoading}
          />
        )}
      </Form.Item>
    );
  };

  const renderDependTipsContent = useMemo(() => {
    return (
      <div style={{ width: '300px', color: '#666', lineHeight: '18px' }}>
        {intl
          .get('hadm.marketclient.view.feedback.reliance.tips')
          .d(
            '依赖信息收集的数据是您服务中所使用的HZERO组件坐标和版本（POM依赖坐标），通过此信息能帮助我们了解您所使用的组件和版本，快速定位问题。样例：服务hzero-change的依赖信息如下'
          )}
        <img
          src={ImgFeedbackTips}
          alt="feedback"
          style={{ width: '100%', height: 'auto' }}
          onClick={() => setViewerVisible(true)}
        />
      </div>
    );
  }, []);

  const renderTraceTipsContent = useMemo(() => {
    return (
      <div style={{ width: '300px', color: '#666', lineHeight: '18px' }}>
        {TRACE_LOG[isCN ? 'CN' : 'EN']}
      </div>
    );
  }, []);

  const onLoginOk = () => {
    setLoginModalVisible(false);
    _init();
  };

  /**
   * 开始日志收集
   */
  const handleStartCollecting = () => {
    startTraceLogs().then((res) => {
      if (res && !res.failed) {
        setCurrentTraceStatus(TRACE_STATUS.RUNNING);
      }
    });
  };

  const handleEndCollecting = () => {
    endTraceLogs().then((res) => {
      if (res && !res.failed) {
        setCurrentTraceStatus(TRACE_STATUS.FINISHED);
        setTraceFile(res);
      }
    });
  };

  const renderTrace = () => {
    const TraceDoms = {
      [TRACE_STATUS.NO]: (
        <div className={styles.operation}>
          <span>
            <img className={styles.icon} src={IconStart} alt="start" />
            {intl.get('hadm.marketclient.view.feedback.log.noCollected').d('未收集')}
          </span>
          <Button onClick={handleStartCollecting}>
            {intl.get('hadm.marketclient.view.feedback.log.startCollecting').d('开始收集')}
          </Button>
        </div>
      ),
      [TRACE_STATUS.RUNNING]: (
        <div className={styles.operation}>
          <span>
            <Icon style={{ marginRight: '5px', fontSize: '10px' }} type="sync" spin />
            {intl.get('hadm.marketclient.view.feedback.log.collecting').d('收集中')}
          </span>
          <Button onClick={handleEndCollecting}>
            {intl.get('hadm.marketclient.view.feedback.log.stopCollecting').d('结束收集')}
          </Button>
        </div>
      ),
      [TRACE_STATUS.FINISHED]: (
        <div className={styles.operation}>
          <span>
            <img className={styles.icon} src={IconFinished} alt="finished" />
            {intl.get('hadm.marketclient.view.feedback.log.collected').d('已收集')}
          </span>
          <Button onClick={handleStartCollecting}>
            {intl.get('hadm.marketclient.view.feedback.log.recollect').d('重新收集')}
          </Button>
        </div>
      ),
    };
    return TraceDoms[currentTraceStatus];
  };

  const initialLogValue = () => {
    if (isShowRule) {
      return [];
    } else {
      const checked = LOG_TYPE.map((v) => v.key);
      cacheCurrentSelectedLog.current = checked;
      return checked;
    }
  };

  const handleDownloadFile = (fileKey) => {
    const queryParams = [{ name: 'fileKey', value: fileKey }];
    downloadFileByAxios({
      requestUrl: `${HZERO_ADM}/v1/market/work-order/download`,
      queryParams,
    });
  };

  const handleDeleteTraceLog = () => {
    setCurrentTraceStatus(TRACE_STATUS.NO);
    setTraceFile(null);
  };

  return (
    <div className={styles.wrapper}>
      <Header title={intl.get('hadm.marketclient.view.feedback.title').d('问题反馈')} />
      <Content>
        <Spin spinning={pageLoading}>
          <Form>
            <div className={styles['content-main']}>
              <div>
                <Row>
                  <Col span={12}>
                    <Form.Item
                      label={intl.get('hadm.marketclient.view.feedback.classify').d('问题分类')}
                      labelCol={{ span: 4 }}
                      wrapperCol={{ span: 19 }}
                    >
                      {getFieldDecorator('category', {
                        rules: [
                          {
                            required: true,
                            message: intl
                              .get('hadm.marketclient.view.feedback.required', {
                                label: intl
                                  .get('hadm.marketclient.view.feedback.classify')
                                  .d('问题分类'),
                              })
                              .d('{label}为必输项'),
                          },
                        ],
                      })(<CategorySelect categories={firstCategories} />)}
                    </Form.Item>
                  </Col>
                  <Col span={12}>{!!currentCategory && renderSubCategory()}</Col>
                </Row>
                <Form.Item
                  label={intl.get('hadm.marketclient.view.feedback.name').d('标题名称')}
                  {...formItemLayout}
                  style={{ marginTop: '8px', textAlign: 'right' }}
                >
                  {getFieldDecorator('title', {
                    initialValue: initialTitle(),
                    rules: [
                      {
                        required: true,
                        message: intl
                          .get('hadm.marketclient.view.feedback.required', {
                            label: intl.get('hadm.marketclient.view.feedback.name').d('标题名称'),
                          })
                          .d('{label}为必输项'),
                      },
                    ],
                  })(
                    <Input
                      placeholder={intl
                        .get('hzero.c7nProUI.Validator.value_missing', {
                          label: intl.get('hadm.marketclient.view.feedback.name').d('标题名称'),
                        })
                        .d('请输入{label}')}
                    />
                  )}
                </Form.Item>
                <Row style={{ marginTop: '30px' }}>
                  <Col span={2} style={{ textAlign: isCN ? 'right' : 'left' }}>
                    <span
                      style={{ color: 'rgba(0, 0, 0, 0.85)', marginRight: '8px', fontSize: '12px' }}
                    >
                      <span
                        style={{
                          color: '#f5222d',
                          marginRight: '4px',
                          float: isCN ? null : 'right',
                        }}
                      >
                        *
                      </span>
                      {intl.get('hadm.marketclient.view.feedback.description').d('问题描述')}:
                    </span>
                  </Col>
                  <Col span={22}>
                    <RichTextTool
                      mode="edit"
                      content={defaultRichText}
                      mediaAccepts={{ video: false }}
                      ref={editorRef}
                      style={{ height: 400 }}
                    />
                  </Col>
                </Row>
                <Row className={styles.upload}>
                  <Col span={12} offset={2}>
                    <div className={styles['upload-tips']}>
                      {intl
                        .get('hadm.marketclient.view.feedback.upload.description')
                        .d('最多只能上传3个文件，每个文件 20M 以内')}
                    </div>
                    <Form.Item>
                      {getFieldDecorator('attachmentList')(
                        <BaseUpload
                          multiple
                          text={intl.get('hzero.common.upload.text').d('上传附件')}
                          beforeUpload={beforeUpload}
                          size={20 * 1024 * 1024}
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col span={10}>
                    <div className={styles.tips}>
                      <img src={ImgPoint} alt="point" />
                      {intl
                        .get('hadm.marketclient.view.feedback.editor.tips')
                        .d('问题描述编辑框内可以直接粘贴图片喔~')}
                    </div>
                  </Col>
                </Row>
                {currentCategory === 'application' && (
                  <Form.Item
                    label={intl.get('hadm.marketclient.view.feedback.service').d('对应服务')}
                    {...formItemLayout}
                    style={{ marginBottom: 0 }}
                  >
                    {getFieldDecorator('serviceIds', {
                      initialValue: headerIdList.map((v) => v.serviceId),
                    })(
                      <Checkbox.Group style={{ width: '100%', marginTop: '5px' }}>
                        {headerIdList.map(({ serviceId, serviceName }) => (
                          <Checkbox value={serviceId}>{serviceName}</Checkbox>
                        ))}
                      </Checkbox.Group>
                    )}
                  </Form.Item>
                )}
                <Row>
                  <Col offset={2}>
                    <Button
                      type="primary"
                      size="large"
                      style={{ width: '190px' }}
                      onClick={handleSubmit}
                      loading={loading}
                    >
                      {intl.get('hadm.marketclient.button.submit').d('提交')}
                    </Button>
                  </Col>
                </Row>
              </div>
              <div className={styles['content-log']}>
                <div className={styles.log}>
                  <div className={styles.title}>
                    {intl.get('hadm.marketclient.view.feedback.upload.log').d('上传日志')}
                    <span className={styles.tips}>
                      {intl
                        .get('hadm.marketclient.view.feedback.upload.log.description')
                        .d('选择上传日志信息，以协助我们更快地定位及解决问题')}
                    </span>
                  </div>
                  <div className={styles.content}>
                    <Form.Item {...formItemLayout}>
                      {getFieldDecorator('log', {
                        initialValue: initialLogValue(),
                      })(
                        <Checkbox.Group onChange={onLogCheckboxChange}>
                          <Checkbox
                            value="needDependenciesInfo"
                            style={{ width: '200px', fontSize: '14px', marginTop: '24px' }}
                          >
                            {intl
                              .get('hadm.marketclient.view.feedback.reliance.info')
                              .d('依赖信息')}
                          </Checkbox>
                          <div style={{ marginTop: '8px' }}>
                            <LogTips
                              text={intl.get('hadm.marketclient.view.feedback.info').d('信息说明')}
                              content={renderDependTipsContent}
                            />
                          </div>
                          <Checkbox
                            value="needTraceInfo"
                            style={{ width: '200px', fontSize: '14px', marginTop: '24px' }}
                          >
                            Trace {intl.get('hadm.marketclient.view.feedback.log').d('日志')}
                          </Checkbox>
                          <div style={{ marginTop: '8px' }}>
                            <LogTips
                              text={intl.get('hadm.marketclient.view.feedback.info').d('信息说明')}
                              content={renderTraceTipsContent}
                            />
                          </div>
                        </Checkbox.Group>
                      )}
                    </Form.Item>
                    {isShowRule && (
                      <div className={styles.agree} onClick={onAgreeChange}>
                        <Radio value={isAgree} />
                        {intl
                          .get('hadm.marketclient.view.feedback.upload.log.term1')
                          .d('我同意加入')}
                        <a onClickCapture={onRuleClick}>
                          {' '}
                          《
                          {intl
                            .get('hadm.marketclient.view.home.description3')
                            .d('HZERO用户体验改进计划')}
                          》
                        </a>
                        {intl
                          .get('hadm.marketclient.view.feedback.upload.log.term2')
                          .d('后可选择上传日志')}
                      </div>
                    )}
                  </div>
                </div>
                <div className={styles.trace}>
                  {getFieldValue('log').includes('needTraceInfo') && (
                    <div
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                      }}
                    >
                      trace {intl.get('hadm.marketclient.view.feedback.log').d('日志')}：
                      {renderTrace()}
                    </div>
                  )}
                  {currentTraceStatus === TRACE_STATUS.FINISHED &&
                    traceFile &&
                    getFieldValue('log').includes('needTraceInfo') && (
                      <div className={styles.attachment}>
                        <a onClick={() => handleDownloadFile(traceFile.fileKey)}>
                          <Icon type="paper-clip" />
                          {traceFile.fileName}
                        </a>
                        <a onClick={handleDeleteTraceLog}>
                          <Icon type="delete" />
                        </a>
                      </div>
                    )}
                </div>
              </div>
            </div>
          </Form>
        </Spin>
      </Content>
      <UserTermsModal
        visible={isRuleVisible}
        editAble
        handleAgree={() => handleRuleModalResult(true)}
        handleDisagree={() => handleRuleModalResult(false)}
        handleCancel={() => setIsRuleVisible(false)}
      />
      <Viewer
        visible={viewerVisible}
        onClose={() => setViewerVisible(false)}
        images={[{ src: ImgFeedbackTips, alt: '' }]}
        zIndex={2000}
      />
      <LoginModal
        marketUserLogin={marketUserLogin}
        loginModalVisible={loginModalVisible}
        onCancel={() => {
          setLoginModalVisible(false);
          closeTab('/market-client/feedback');
          notification.warning({ message: '请先登录开放平台账号' });
        }}
        compelCloseModal
        onOk={onLoginOk}
      />
    </div>
  );
};

export default formatterCollections({
  code: ['hadm.marketclient', 'hzero.c7nProUI', 'hmsg.messageQuery'],
})(
  connect(({ global = {} }) => ({
    global,
  }))(Form.create()(Feedback))
);
