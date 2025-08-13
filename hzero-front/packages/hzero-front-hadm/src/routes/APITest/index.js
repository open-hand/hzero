import React, { useState, useMemo, useRef } from 'react';
import {
  Spin,
  DataSet,
  Button,
  Icon,
  Tooltip,
  Table,
  TextField,
  TextArea,
  Select as Test,
} from 'choerodon-ui/pro';
import { Row, Col } from 'choerodon-ui';
import { message } from 'hzero-ui';
import axios from 'axios';
import copy from 'copy-to-clipboard';
import classnames from 'classnames';
import Hjson from 'hjson';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import { API_HOST } from 'utils/config';
import { getAccessToken } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import ApiTree from './ApiTree';
import './index.less';
import emptyApi from '@/assets/img/noright.svg';
import jsonFormat from './format';

import { detailDS, detailParamDS } from '../../stores/apiTestDS';

const withTokenAxios = axios.create();
const jsonMimeType = 'application/json';

withTokenAxios.defaults = {
  ...withTokenAxios.defaults,
  headers: {
    ...(withTokenAxios.defaults || {}).headers,
    'Content-Type': jsonMimeType,
    Accept: jsonMimeType,
    'X-Requested-With': 'XMLHttpRequest',
  },
};

// Add a request interceptor
withTokenAxios.interceptors.request.use(
  (config) => {
    let { url = '' } = config;
    if (url.indexOf('://') === -1 && !url.startsWith('/_api')) {
      url = `${API_HOST}${url}`;
    }
    // Do something before request is sent
    return {
      ...config,
      url,
      headers: {
        ...config.headers,
        Authorization: `bearer ${getAccessToken()}`,
      },
    };
  },
  (err) => {
    return Promise.reject(err);
  }
);

withTokenAxios.interceptors.response.use(
  (res) => {
    const { status, data } = res;
    if (status === 204 || status === 200) {
      return res;
    }
    if (data && data.failed) {
      // notification.error({
      //   message: data.message,
      // });
      throw res;
    } else {
      return res;
    }
  },
  (err) => {
    throw err.response;
  }
);

const apiTest = () => {
  const detailDs = useMemo(() => new DataSet(detailDS), []);
  const detailParamDs = useMemo(() => new DataSet(detailParamDS()), []);

  const [values, setValues] = useState('');
  const [isShowTree, setIsShowTree] = useState(true);
  const [currentNode, setCurrentNode] = useState(null);
  const [detailFlag, setDetailFlag] = useState('empty');
  const [responseHeader, setResponseHeader] = useState('');
  const [response, setResponse] = useState('');
  const [urlPathValues, setUrlPathValues] = useState({});
  const [taArr, setTaArr] = useState({});
  const [statusCode, setStatusCode] = useState(200);
  const [isSending, setIsSending] = useState(false);
  const [isShowResult, setIsShowResult] = useState(null);
  const [apiDetail, setApiDetail] = useState({
    description: '[]',
    responses: [],
  });
  const [requestUrl, setRequestUrl] = useState(null);

  const fileInput = useRef(undefined);
  const responseRef = useRef(undefined);

  const handleRefresh = () => {
    if (detailFlag !== 'empty') {
      loadDetail(currentNode);
    }
  };

  /**
   * 加载API详情数据
   * @param node 左侧树结构选中的节点
   */
  const loadDetail = async (node) => {
    setIsShowResult(null);
    setIsSending(false);
    setUrlPathValues({});
    setTaArr({});
    setIsSending(false);
    setDetailFlag('loading');
    setRequestUrl(null);
    setValues('');
    const { version, operationId, refController, service } = node[0];
    detailDs.setQueryParameter('version', version);
    detailDs.setQueryParameter('operationId', operationId);
    detailDs.refController = refController;
    detailDs.servicePrefix = service;
    await detailDs.query();
    const data = detailDs.toData()[0];
    data.paths.some((item) => {
      if (item.operationId === operationId) {
        const { basePath, url } = item;
        setApiDetail(item);
        detailParamDs.loadData(item.parameters);
        setDetailFlag('done');
        setRequestUrl(`${basePath}${url}`);
        return true;
      }
      return false;
    });
  };

  const getRightContent = () => {
    let rightContent;
    if (detailFlag === 'done') {
      rightContent = (
        <React.Fragment>
          {getApiDetail()}
          {getTest()}
        </React.Fragment>
      );
    } else if (detailFlag === 'loading') {
      rightContent = (
        <Spin spinning={detailFlag === 'loading'} style={{ flex: 1, marginTop: '30%' }} />
      );
    } else {
      rightContent = (
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            height: 250,
            margin: '88px auto',
            padding: '50px 75px',
          }}
        >
          <img src={emptyApi} alt="" />
          <div style={{ marginLeft: 40 }}>
            <div style={{ fontSize: '14px', color: 'rgba(0,0,0,0.65)' }}>
              {intl.get('hadm.apiTest.view.empty.find.not').d('检测到您未选择任何API')}
            </div>
            <div style={{ fontSize: '20px', marginTop: 10 }}>
              {intl
                .get('hadm.apiTest.view.empty.try.choose')
                .d('请在左侧树状图中选择您要查看的API')}
            </div>
          </div>
        </div>
      );
    }

    return rightContent;
  };

  const getApiDetail = () => {
    const { url, innerInterface, code, method, remark, consumes, produces } = apiDetail;
    const desc = apiDetail.description || '[]';
    const responseDataExample =
      apiDetail && apiDetail.responses.length ? apiDetail.responses[0].body || 'false' : '{}';
    let handledDescWithComment = Hjson.parse(responseDataExample, { keepWsc: true });

    handledDescWithComment = jsonFormat(handledDescWithComment);
    const handledDesc = Hjson.parse(desc);
    const { permission = { roles: [] } } = handledDesc;
    const intlPrefix = 'hadm.apiTest.model.apiTest';

    const roles =
      permission.roles.length &&
      permission.roles.map((item) => ({
        name: intl.get(`${intlPrefix}.default.role`).d('默认角色'),
        value: item,
      }));

    const tableValue = [
      {
        name: intl.get(`${intlPrefix}.code`).d('权限编码'),
        value: (
          <>
            <span>{code}</span>
            <Tooltip title={intl.get('hzero.common.button.copy').d('复制')}>
              <Icon
                type="library_books"
                onClick={() => {
                  handleCopy(code);
                }}
                style={{ cursor: 'pointer', verticalAlign: 'text-bottom', marginLeft: '4px' }}
              />
            </Tooltip>
          </>
        ),
      },
      {
        name: intl.get(`${intlPrefix}.method`).d('请求方式'),
        value: method,
      },
      {
        name: intl.get(`${intlPrefix}.url`).d('路径'),
        value: url,
      },
      {
        name: intl.get(`hzero.common.view.description`).d('描述'),
        value: remark,
      },
      {
        name: intl.get(`${intlPrefix}.action`).d('Action'),
        value: permission && permission.action,
      },
      {
        name: intl.get(`${intlPrefix}.level`).d('权限层级'),
        value:
          permission && permission.permissionLevel
            ? permission.permissionLevel.toLowerCase()
            : intl.get('hzero.common.currency.none').d('无'),
      },
      {
        name: intl.get(`${intlPrefix}.login.accessible`).d('是否为登录可访问'),
        value:
          permission && permission.permissionLogin
            ? intl.get('hzero.common.status.yes').d('是')
            : intl.get('hzero.common.status.no').d('否'),
      },
      {
        name: intl.get(`${intlPrefix}.public.permission`).d('是否为公开权限'),
        value:
          permission && permission.permissionPublic
            ? intl.get('hzero.common.status.yes').d('是')
            : intl.get('hzero.common.status.no').d('否'),
      },
      {
        name: intl.get(`${intlPrefix}.request.format`).d('请求格式'),
        value: consumes[0],
      },
      {
        name: intl.get(`${intlPrefix}.response.format`).d('响应格式'),
        value: produces[0],
      },
    ];

    if (roles) {
      tableValue.splice(5, 0, ...roles);
    }

    return (
      <div className="c7n-iam-apitest-content-right-container">
        <div className="c7n-iam-apitest-content-right-container-title">
          <span
            className={classnames(
              'c7n-iam-apitest-content-right-container-title-methodTag',
              `c7n-iam-apitest-content-right-container-title-methodTag-${method}`
            )}
          >
            <span>{method}</span>
          </span>
          <span className="c7n-iam-apitest-content-right-container-title-url">{url}</span>
          <span
            className={classnames('c7n-iam-apitest-content-right-container-title-rangeTag', {
              'c7n-iam-apitest-content-right-container-title-rangeTag-inner': innerInterface,
              'c7n-iam-apitest-content-right-container-title-rangeTag-outer': !innerInterface,
            })}
          >
            {innerInterface
              ? intl.get(`${intlPrefix}.inner`).d('内部')
              : intl.get(`${intlPrefix}.outer`).d('公开')}
          </span>
        </div>
        <div className="c7n-iam-apitest-content-right-container-info">
          <div className="c7n-iam-apitest-content-right-container-info-title">
            <span>{intl.get(`${intlPrefix}.interfaceInfo`).d('接口信息')}</span>
            <span>{intl.get(`${intlPrefix}.responseInfo`).d('响应数据')}</span>
          </div>
          <div className="c7n-iam-apitest-content-right-container-info-content">
            <div className="c7n-iam-apitest-content-right-container-info-interfaceinfo">
              {tableValue.map(({ name, value }, index) => (
                <Row
                  // eslint-disable-next-line react/no-array-index-key
                  key={`${name}-${index}`}
                  className="c7n-iam-apitest-content-right-container-info-interfaceinfo-row"
                >
                  <Col span={7}>{name}</Col>
                  <Col span={17}>{value}</Col>
                </Row>
              ))}
            </div>
            <div className="c7n-iam-apitest-content-right-container-info-responsedata">
              <pre>
                <code>{handledDescWithComment}</code>
              </pre>
            </div>
          </div>
        </div>
      </div>
    );
  };

  const getTest = () => {
    let curlContent;
    const upperMethod = {
      get: 'GET',
      post: 'POST',
      options: 'options',
      put: 'PUT',
      delete: 'DELETE',
      patch: 'PATCH',
    };

    let handledStatusCode;
    let codeClass;
    if (statusCode) {
      [handledStatusCode] = String(statusCode).split('');
      switch (handledStatusCode) {
        case '1':
          codeClass = 'c7n-iam-apitest-code-1';
          break;
        case '2':
          codeClass = 'c7n-iam-apitest-code-2';
          break;
        case '3':
          codeClass = 'c7n-iam-apitest-code-3';
          break;
        case '4':
          codeClass = 'c7n-iam-apitest-code-4';
          break;
        case '5':
          codeClass = 'c7n-iam-apitest-code-5';
          break;
        default:
          break;
      }
    }

    const handleUrl = encodeURI(requestUrl);
    const handleMethod = upperMethod[apiDetail.method];
    const token = getAccessToken();
    const bodyStr = (values || '').replace(/\n/g, '\\\n');
    let body = '';
    if (bodyStr) {
      body = `-d '${bodyStr}' `;
    }

    if (handleMethod === 'GET') {
      curlContent = `curl -X ${handleMethod} \\
    '${handleUrl}' \\
    --header 'Accept: application/json' \\
    --header 'Authorization: Bearer ${token}'`;
    } else {
      curlContent = `curl -X ${handleMethod} \\
    '${handleUrl}' \\
    --header 'Content-Type: application/json' \\
    --header 'Accept: application/json' \\
    --header 'Authorization: Bearer ${token}' \\
    ${body}`;
    }

    const { method } = apiDetail;
    const requestColumns = [
      {
        name: 'name',
        width: '15%',
        className: 'api-test-name',
        renderer: ({ record, text }) => {
          if (record.get('required')) {
            return (
              <Tooltip title={text}>
                <p className="api-test-name-p">
                  <span style={{ color: '#d50000' }}>*</span>
                  <span>{text}</span>
                </p>
              </Tooltip>
            );
          } else {
            return <Tooltip title={text}>{text}</Tooltip>;
          }
        },
      },
      {
        title: intl.get('hadm.apiTest.model.apiTest.request.data').d('请求数据'),
        name: 'inDefault',
        key: 'inDefault',
        width: 350,
        renderer: ({ text, record }) => {
          const type = record.get('type');
          if (type === 'file') {
            return (
              <div className="uploadContainer">
                <input type="file" name="file" ref={fileInput} />
                <Button onClick={relateChoose}>
                  <Icon type="file_upload" />{' '}
                  {intl.get('hzero.c7nProUI.Upload.file_selection').d('选择文件')}
                </Button>
                <div className="emptyMask" />
              </div>
            );
          } else if (!type || type === 'array') {
            return <div className="showTextAreaContainer">{text}</div>;
          } else if (type === 'boolean') {
            return <span>{text}</span>;
          }
          return <span>{text}</span>;
        },
        editor: (record) => {
          let editableNode;
          const type = record.get('type');
          if (!type) {
            editableNode = (
              <TextArea
                resize="none"
                onChange={(e) => {
                  setValues(e);
                }}
              />
            );
          } else if (type === 'boolean') {
            editableNode = (
              <Test
                onChange={(e) => {
                  handleSelectChange(record.get('name'), e === null ? null : (!!e).toString());
                }}
              />
            );
          } else if (type === 'array') {
            editableNode = (
              <TextArea
                resize="none"
                onKeyDown={(e) => {
                  if (e.keyCode === 13) {
                    e.preventDefault();
                    e.target.value = `${e.target.value}\r`;
                  }
                }}
                onChange={(e) => {
                  changeTextareaValue(record.get('name'), type, e);
                }}
              />
            );
          } else if (type === 'file') {
            editableNode = <div />;
          } else {
            editableNode = (
              <TextField
                autoComplete="off"
                onChange={(e) => {
                  changeNormalValue(record.get('name'), record.get('in'), e);
                }}
              />
            );
          }
          return editableNode;
        },
      },
      {
        title: intl.get('hadm.apiTest.model.apiTest.request.data.type').d('请求数据类型'),
        name: 'type',
        key: 'type',
        renderer: ({ text, record }) => {
          if (text === 'integer' && record.get('format') === 'int64') {
            return 'long';
          } else if (text === 'array') {
            return 'Array[string]';
          } else if (!text) {
            if (record.get('schema') && record.get('schema').type && !record.get('body')) {
              return record.get('schema').type;
            } else {
              let normalBody;
              let value;
              if (record.get('body')) {
                value = Hjson.parse(record.get('body'), { keepWsc: true });
                normalBody = Hjson.stringify(value, {
                  bracesSameLine: true,
                  quotes: 'all',
                  separator: true,
                });
                value = jsonFormat(value);
              } else {
                value = null;
                normalBody = null;
              }
              return (
                <div style={{ maxWidth: '500px' }} className="??-container">
                  Example Value
                  <Tooltip
                    placement="left"
                    title={intl.get('hadm.apiTest.view.title.copyLeft').d('点击复制到左侧')}
                  >
                    <div
                      className="body-container"
                      onClick={() => {
                        copyToLeft(normalBody, record);
                      }}
                    >
                      <pre>
                        <code>{value}</code>
                      </pre>
                    </div>
                  </Tooltip>
                </div>
              );
            }
          } else {
            return text;
          }
        },
      },
      {
        title: intl.get('hadm.apiTest.model.apiTest.param.desc').d('参数描述'),
        name: 'description',
        key: 'description',
        width: '10%',
        renderer: ({ text }) => {
          return <Tooltip title={text}>{text}</Tooltip>;
        },
      },
      {
        title: intl.get('hadm.apiTest.model.apiTest.param.type').d('参数类型'),
        name: 'in',
        key: 'in',
        width: '8%',
        renderer: ({ text }) => {
          if (text !== 'body') {
            return text;
          } else {
            return <div>{text}</div>;
          }
        },
      },
    ];

    return (
      <div className="c7n-iam-apitest-content-right-container-interface-test">
        <div className="c7n-interface-test-response-params">
          <h5>{intl.get('hadm.apiTest.model.apiTest.request.parameter').d('请求参数')}</h5>
          <Table
            dataSet={detailParamDs}
            columns={requestColumns}
            rowHeight="auto"
            onRow={({ record }) => {
              const type = record.get('type');
              if (type === 'array' || !type) {
                return {
                  className: 'api-test-textarea',
                };
              } else if (type === 'boolean') {
                return {
                  className: 'api-test-boolean',
                };
              } else if (type === 'file') {
                return {
                  className: 'api-test-file',
                };
              } else {
                return {
                  className: 'api-test-others',
                };
              }
            }}
          />
        </div>
        <div className="c7n-url-container">
          <div style={{ marginBottom: '30px' }}>
            <span
              className={classnames(
                'method',
                `c7n-iam-apitest-content-right-container-title-methodTag-${method}`
              )}
            >
              {method}
            </span>
            <Tooltip
              title={requestUrl}
              placement="top"
              overlayStyle={{ wordBreak: 'break-all' }}
              arrowPointAtCenter
            >
              <input type="text" value={requestUrl || ''} readOnly />
            </Tooltip>
            {!isSending ? (
              <Button
                funcType="raised"
                color="primary"
                loading={false}
                htmlType="submit"
                onClick={handleSubmit}
              >
                {intl.get('hzero.common.button.send').d('发送')}
              </Button>
            ) : (
              <Button funcType="raised" color="primary" loading>
                {intl.get('hadm.apiTest.model.apiTest.sending').d('发送中')}
              </Button>
            )}
            <Button
              funcType="raised"
              color="primary"
              style={{ marginLeft: '8px' }}
              onClick={() => {
                handleCopy(requestUrl);
              }}
            >
              {intl.get('hzero.common.button.copy').d('复制')}
            </Button>
          </div>
        </div>
        <div
          style={{
            textAlign: 'center',
            paddingTop: '100px',
            display: isShowResult === false ? 'block' : 'none',
          }}
        >
          <Spin size="large" />
        </div>
        <div
          className="c7n-response-container"
          style={{ display: isShowResult === true ? 'block' : 'none' }}
        >
          <div className="c7n-response-code" ref={responseRef}>
            <h5>{intl.get('hadm.apiTest.model.apiTest.response.code').d('响应码')}</h5>
            <span className={classnames('c7n-iam-apitest-statusCode', `${codeClass}`)}>
              {statusCode}
            </span>
          </div>
          <div className="c7n-response-body">
            <h5>{intl.get('hadm.apiTest.model.apiTest.response.body').d('响应主体')}</h5>
            <div className="response-body-container">
              <pre>
                <code>{response instanceof Object ? jsonFormat(response) : `${response}`}</code>
              </pre>
              <Icon
                type="library_books"
                onClick={() => {
                  handleCopy(response instanceof Object ? JSON.stringify(response) : `${response}`);
                }}
              />
              <textarea style={{ position: 'absolute', zIndex: -10 }} id="responseContent" />
            </div>
          </div>
          <div className="c7n-response-body">
            <h5>{intl.get('hadm.apiTest.model.apiTest.response.headers').d('响应头部')}</h5>
            <div className="response-body-container">
              <pre>
                <code>{jsonFormat(responseHeader)}</code>
              </pre>
              <Icon
                type="library_books"
                onClick={() => {
                  handleCopy(JSON.stringify(responseHeader));
                }}
              />
              <textarea style={{ position: 'absolute', zIndex: -10 }} id="responseHeader" />
            </div>
          </div>
          <div className="c7n-curl">
            <h5>CURL</h5>
            <div className="curl-container">
              <pre>
                <code>{curlContent}</code>
              </pre>
              <Icon
                type="library_books"
                onClick={() => {
                  handleCopy(curlContent);
                }}
              />
              <textarea style={{ position: 'absolute', zIndex: -10 }} id="curlContent" />
            </div>
          </div>
        </div>
      </div>
    );
  };

  const copyToLeft = (value, record) => {
    setValues(value);
    record.set('inDefault', value);
  };

  const changeTextareaValue = (name, type, e) => {
    if (type === 'array') {
      changeNormalValue(name, 'array', e);
    }
  };

  const handleSelectChange = (name, select) => {
    changeNormalValue(name, 'query', select);
  };

  const changeNormalValue = (name, valIn, e) => {
    let query = '';
    let newRequestUrl = `${apiDetail.basePath}${apiDetail.url}`;
    urlPathValues[`{${name}}`] = e;
    Object.entries(urlPathValues).forEach((items) => {
      newRequestUrl = items[1] ? newRequestUrl.replace(items[0], items[1]) : newRequestUrl;
    });
    if (valIn === 'query' || valIn === 'array') {
      if (e !== null) {
        const arr = e.split('\n');
        taArr[name] = arr;
        setTaArr(taArr);
      } else {
        delete taArr[name];
        setTaArr(taArr);
      }
    }
    Object.entries(taArr).forEach((a) => {
      const entrieName = a[0];
      if (Array.isArray(a[1])) {
        a[1].forEach((v) => {
          query = `${query}&${entrieName}=${v}`;
        });
      } else {
        query = `${query}&${entrieName}=${a[1]}`;
      }
    });
    query = query.replace('&', '?');
    setUrlPathValues(urlPathValues);
    setRequestUrl(`${newRequestUrl}${query}`);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    let err = false;
    await detailParamDs.map(async (record, index, array) => {
      const flag = await record.validate('all');
      err = !flag || err;
      if (!err && index === array.length - 1) {
        setIsSending(true);
        setIsShowResult(false);
        if (fileInput.current) {
          const formData = new FormData();
          if (!fileInput.current.files[0]) {
            notification.error({
              message: intl.get('hadm.apiTest.view.title.selectFile').d('请选择文件'),
            });
            setIsSending(false);
            setIsShowResult(null);
            return false;
          }
          formData.append('file', fileInput.current.files[0]);
          withTokenAxios({
            url: requestUrl,
            method: apiDetail.method,
            baseURL: `${API_HOST}`,
            headers: { Authorization: `bearer ${getAccessToken()}` },
            data: formData,
          })
            .then((res) => {
              handleResponse(res);
              // setResponseHeader(jsonFormat(res.headers));
              // setResponse(res.data instanceof Object ? jsonFormat(res.data) : `${res.data}`);
              // setStatusCode(res.status);
              // setIsSending(false);
              // setIsShowResult(true);
              // responseRef.current.scrollIntoView();
            })
            .catch((error) => {
              handleResponse(error);
              // setResponseHeader(jsonFormat(error.headers));
              // setResponse(error.data instanceof Object ? jsonFormat(error.data) : `${error.data}`);
              // setStatusCode(error.status);
              // setIsSending(false);
              // setIsShowResult(true);
              // responseRef.current.scrollIntoView();
            });
        } else if (values) {
          try {
            Hjson.parse(values || '');
          } catch {
            notification.error({
              message: intl.get('hadm.apiTest.view.title.JSONParse').d('JSON解析失败'),
            });
            setIsSending(false);
            setIsShowResult(null);
            return false;
          }
          withTokenAxios({
            url: requestUrl,
            method: apiDetail.method,
            baseURL: `${API_HOST}`,
            headers: { Authorization: `bearer ${getAccessToken()}` },
            data: Hjson.parse(values || ''),
          })
            .then((res) => {
              handleResponse(res);
              // setResponseHeader(jsonFormat(res.headers));
              // setResponse(res.data instanceof Object ? jsonFormat(res.data) : `${res.data}`);
              // setStatusCode(res.status);
              // setIsSending(false);
              // setIsShowResult(true);
              // responseRef.current.scrollIntoView();
            })
            .catch((error) => {
              handleResponse(error);
              // setResponseHeader(jsonFormat(error.headers));
              // setResponse(error.data instanceof Object ? jsonFormat(error.data) : `${error.data}`);
              // setStatusCode(error.status);
              // setIsSending(false);
              // setIsShowResult(true);
              // responseRef.current.scrollIntoView();
            });
        } else {
          withTokenAxios({
            url: requestUrl,
            method: apiDetail.method,
            baseURL: `${API_HOST}`,
            headers: { Authorization: `bearer ${getAccessToken()}` },
          })
            .then((res) => {
              handleResponse(res);
              // setResponseHeader(jsonFormat(res.headers));
              // setResponse(res.data instanceof Object ? jsonFormat(res.data) : `${res.data}`);
              // setStatusCode(res.status);
              // setIsSending(false);
              // setIsShowResult(true);
              // responseRef.current.scrollIntoView();
            })
            .catch((error) => {
              handleResponse(error);
              // setResponseHeader(jsonFormat(error.headers));
              // setResponse(error.data instanceof Object ? jsonFormat(error.data) : `${error.data}`);
              // setStatusCode(error.status);
              // setIsSending(false);
              // setIsShowResult(true);
              // responseRef.current.scrollIntoView();
            });
        }
      }
    });
    if (detailParamDs.records.length === 0) {
      setIsSending(true);
      setIsShowResult(false);
      if (fileInput.current) {
        const formData = new FormData();
        formData.append('file', fileInput.current.files[0]);
        withTokenAxios({
          url: requestUrl,
          method: apiDetail.method,
          baseURL: `${API_HOST}`,
          headers: { Authorization: `bearer ${getAccessToken()}` },
          data: formData,
        })
          .then((res) => {
            handleResponse(res);
            // setResponseHeader(jsonFormat(res.headers));
            // setResponse(res.data instanceof Object ? jsonFormat(res.data) : `${res.data}`);
            // setStatusCode(res.status);
            // setIsSending(false);
            // setIsShowResult(true);
            // responseRef.current.scrollIntoView();
          })
          .catch((error) => {
            handleResponse(error);
            // setResponseHeader(jsonFormat(error.headers));
            // setResponse(error.data instanceof Object ? jsonFormat(error.data) : `${error.data}`);
            // setStatusCode(error.status);
            // setIsSending(false);
            // setIsShowResult(true);
            // responseRef.current.scrollIntoView();
          });
      } else if (values) {
        try {
          Hjson.parse(values || '');
        } catch {
          notification.error({
            message: intl.get('hadm.apiTest.view.title.JSONParse').d('JSON解析失败'),
          });
          setIsSending(false);
          setIsShowResult(null);
          return false;
        }
        withTokenAxios({
          url: requestUrl,
          method: apiDetail.method,
          baseURL: `${API_HOST}`,
          headers: { Authorization: `bearer ${getAccessToken()}` },
          data: Hjson.parse(values || ''),
        })
          .then((res) => {
            handleResponse(res);
            // setResponseHeader(jsonFormat(res.headers));
            // setResponse(res.data instanceof Object ? jsonFormat(res.data) : `${res.data}`);
            // setStatusCode(res.status);
            // setIsSending(false);
            // setIsShowResult(true);
            // responseRef.current.scrollIntoView();
          })
          .catch((error) => {
            handleResponse(error);
            // setResponseHeader(jsonFormat(error.headers));
            // setResponse(error.data instanceof Object ? jsonFormat(error.data) : `${error.data}`);
            // setStatusCode(error.status);
            // setIsSending(false);
            // setIsShowResult(true);
            // responseRef.current.scrollIntoView();
          });
      } else {
        withTokenAxios({
          url: requestUrl,
          method: apiDetail.method,
          baseURL: `${API_HOST}`,
          headers: { Authorization: `bearer ${getAccessToken()}` },
        })
          .then((res) => {
            handleResponse(res);
            // setResponseHeader(jsonFormat(res.headers));
            // setResponse(res.data instanceof Object ? jsonFormat(res.data) : `${res.data}`);
            // setStatusCode(res.status);
            // setIsSending(false);
            // setIsShowResult(true);
            // responseRef.current.scrollIntoView();
          })
          .catch((error) => {
            handleResponse(error);
            // setResponseHeader(jsonFormat(error.headers));
            // setResponse(error.data instanceof Object ? jsonFormat(error.data) : `${error.data}`);
            // setStatusCode(error.status);
            // setIsSending(false);
            // setIsShowResult(true);
            // responseRef.current.scrollIntoView();
          });
      }
    }
  };

  const handleResponse = (res) => {
    setResponseHeader(res.headers);
    setResponse(res.data instanceof Object ? res.data : `${res.data}`);
    setStatusCode(res.status);
    setIsSending(false);
    setIsShowResult(true);
    if (responseRef.current) {
      responseRef.current.scrollIntoView();
    }
  };

  const handleCopy = (res) => {
    copy(res);
    if (copy(res)) {
      message.success(
        <span>{intl.get('hadm.ruleConfig.view.message.copy').d('复制成功')}</span>,
        'top'
      );
    } else {
      message.error(
        <span>{intl.get('hadm.ruleConfig.view.message.copyFail').d('复制失败')}</span>,
        'top'
      );
    }
  };

  const relateChoose = () => {
    fileInput.current.click();
  };

  return (
    <>
      <Header title={intl.get('hadm.apiTest.view.title.interface').d('API测试')}>
        <Button onClick={handleRefresh} icon="refresh">
          {intl.get('hzero.common.button.refresh').d('刷新')}
        </Button>
      </Header>
      <Content style={{ height: 'calc(100% - 32px)' }}>
        <div className="c7n-iam-apitest-content">
          {!isShowTree && (
            <div className="c7n-iam-apitest-bar">
              <div
                role="none"
                className="c7n-iam-apitest-bar-button"
                onClick={() => {
                  setIsShowTree(true);
                }}
              >
                <Icon type="navigate_next" />
              </div>
              <p
                role="none"
                onClick={() => {
                  setIsShowTree(true);
                }}
              >
                {intl.get('hadm.apiTest.view.title.interfaces').d('接口库')}
              </p>
            </div>
          )}
          <div
            className={classnames({
              'c7n-iam-apitest-content-tree-container': isShowTree,
              'c7n-iam-apitest-content-tree-container-hidden': !isShowTree,
            })}
          >
            <ApiTree
              onClose={() => {
                setIsShowTree(false);
              }}
              getDetail={loadDetail}
              setCurrentNode={setCurrentNode}
              setDetailFlag={setDetailFlag}
            />
          </div>
          <div className="c7n-iam-apitest-content-right">{getRightContent()}</div>
        </div>
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hadm.apiTest'] })(apiTest);
