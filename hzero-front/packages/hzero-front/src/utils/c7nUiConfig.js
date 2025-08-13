/**
 * choerodon-ui - choerodon-ui 客制化配置文件
 * @Author: wuyunqiang <yunqiang.wu@hand-china.com>
 * @Date: 2019-08-15 09:12:30
 * @LastEditTime: 2019-08-27 09:47:01
 * @Copyright: Copyright (c) 2018, Hand
 */
import axios from 'axios';
import React, { useState } from 'react';
import { configure, message } from 'choerodon-ui';
import { Button, Form } from 'choerodon-ui/pro';
import { getConfig } from 'hzero-boot';
import { isEmpty } from 'lodash';
import { routerRedux } from 'dva/router';
// import './c7n-ued-polyfill.important.less';
import intl from 'utils/intl';
import notification from 'utils/notification';
import {
  generateUrlWithGetParam,
  getAccessToken,
  getCurrentOrganizationId,
  isTenantRoleLevel,
  getRequestId,
  setSession,
  getSession,
  isEqualOrganization,
} from 'utils/utils';
import { getEnvConfig, getDvaApp } from 'utils/iocUtils';
import { getMenuId } from './menuTab';

const jsonMimeType = 'application/json';

export const withTokenAxios = axios;

/**
 * 鉴权 401
 * @param status 状态码
 */
const authrIntercept = (status) => {
  if (status === 401) {
    const accessToken = getAccessToken();
    const { HZERO_OAUTH } = getEnvConfig();
    const dvaApp = getDvaApp();
    const language = getSession('language') || 'zh_CN';
    // FIXME:已处理过一次401后就不再处理
    const cacheLocation = encodeURIComponent(window.location.toString());
    if (accessToken) {
      withTokenAxios(`${HZERO_OAUTH}/public/token/kickoff`, {
        method: 'POST',
        params: {
          access_token: accessToken,
        },
      }).then((res) => {
        if (res.kickoff === 1) {
          // 跳转到踢下线界面
          // eslint-disable-next-line
          dvaApp._store.dispatch(
            routerRedux.push({
              pathname: '/public/kickoff',
              search: `?language=${language}`,
            })
          );
          setSession('redirectUrl', cacheLocation);
          setSession('isErrorFlag', false);
        } else {
          // token 失效, 跳转到 token失效页面
          dvaApp._store.dispatch(
            routerRedux.push({
              pathname: '/public/unauthorized',
              search: `?language=${language}`,
            })
          );
          setSession('isErrorFlag', true);
          // 登陆后需要跳回的界面， 放到session中
          setSession('redirectUrl', cacheLocation);
        }
      });
    }
    return false;
  }
  return true;
};

if (!withTokenAxios._HZERO_AXIOS_IS_CONFIGED) {
  // 微前端模式下， 这个语句块会多次执行， 所以加一个条件限制， 只能执行一次
  withTokenAxios.defaults = {
    ...withTokenAxios.defaults,
    headers: {
      ...(withTokenAxios.defaults || {}).headers,
      // Authorization: `bearer ${getAccessToken()}`,
      'Content-Type': jsonMimeType,
      Accept: jsonMimeType,
      'X-Requested-With': 'XMLHttpRequest',
      // baseURL: API_HOST,
    },
  };

  // Add a request interceptor
  withTokenAxios.interceptors.request.use(
    (config) => {
      let { url = '' } = config;
      const { API_HOST } = getEnvConfig();
      if (url.indexOf('://') === -1 && !url.startsWith('/_api')) {
        url = `${API_HOST}${url}`;
      }
      // Do something before request is sent
      const MenuId = getMenuId();

      // 添加额外的请求头
      const patchRequestHeaderConfig = getConfig('patchRequestHeader');
      let patchRequestHeader;
      if (patchRequestHeaderConfig) {
        if (typeof patchRequestHeaderConfig === 'function') {
          patchRequestHeader = patchRequestHeaderConfig();
        } else {
          patchRequestHeader = patchRequestHeaderConfig;
        }
      }

      if (MenuId) {
        return {
          ...config,
          url,
          headers: {
            ...config.headers,
            Authorization: `bearer ${getAccessToken()}`,
            'H-Menu-Id': `${getMenuId()}`,
            'H-Request-Id': `${getRequestId()}`,
            ...patchRequestHeader,
          },
        };
      } else {
        return {
          ...config,
          url,
          headers: {
            ...config.headers,
            Authorization: `bearer ${getAccessToken()}`,
            'H-Request-Id': `${getRequestId()}`,
            ...patchRequestHeader,
          },
        };
      }
    },
    (error) =>
      // Do something with request error
      Promise.reject(error)
  );

  withTokenAxios.interceptors.response.use((response) => {
    const {
      status,
      data,
      config: { url },
    } = response;
    if (!authrIntercept(status)) {
      return;
    }
    // 响应拦截
    const responseIntercept = getConfig('responseIntercept');
    if (responseIntercept) {
      if (typeof responseIntercept === 'function') {
        responseIntercept(url, status, data);
      }
    }
    if (status === 204) {
      return undefined;
    }
    if (data && data.failed) {
      // notification.error({
      //   message: data.message,
      // });
      throw data;
    } else {
      return data;
    }
  });
  withTokenAxios._HZERO_AXIOS_IS_CONFIGED = true;
}

// axios.defaults.headers.common.Authorization = `bearer ${getAccessToken()}`;
message.config({
  placement: 'bottomRight',
  bottom: 48,
  duration: 2,
});

const QueryBarMore = ({ queryFields, buttons, queryFieldsLimit = 3, dataSet, queryDataSet }) => {
  const [hidden, setHidden] = useState(true);
  const handleToggle = () => {
    setHidden(!hidden);
  };
  const query = async () => {
    if (await queryDataSet.validate(false, false)) {
      await dataSet.query();
    }
  };
  return (
    <div>
      {queryDataSet ? (
        <div style={{ display: 'flex', marginBottom: '10px', alignItems: 'flex-start' }}>
          <Form
            style={{ flex: 'auto' }}
            columns={queryFieldsLimit}
            dataSet={queryDataSet}
            onKeyDown={(e) => {
              if (e.keyCode === 13) return query();
            }}
          >
            {hidden ? queryFields.slice(0, queryFieldsLimit) : queryFields}
          </Form>
          <div style={{ marginTop: '10px', flexShrink: 0, display: 'flex', alignItems: 'center' }}>
            {queryFields.length > queryFieldsLimit && (
              <Button onClick={handleToggle}>
                {hidden
                  ? intl.get('hzero.common.button.viewMore').d('更多查询')
                  : intl.get('hzero.common.button.collected').d('收起查询')}
              </Button>
            )}
            <Button
              onClick={() => {
                queryDataSet.current.reset();
                dataSet.fireEvent('queryBarReset', {
                  dataSet,
                  queryFields,
                });
              }}
            >
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
            <Button dataSet={null} color="primary" onClick={query}>
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
          </div>
        </div>
      ) : null}
      {buttons && buttons.length ? <div style={{ marginBottom: 4 }}>{buttons}</div> : null}
    </div>
  );
};

const lovDefineAxiosConfig = (code, field) => {
  let publicFlag = false;
  if (field && field.get('lovPara')) {
    const { publicMode } = field.get('lovPara');
    publicFlag = publicMode;
  }
  const { API_HOST, HZERO_PLATFORM } = getEnvConfig();
  return {
    url: `${API_HOST}${HZERO_PLATFORM}/v1/${
      // eslint-disable-next-line no-nested-ternary
      publicFlag ? 'pub/' : isEqualOrganization() ? `${getCurrentOrganizationId()}/` : ''
    }lov-view/info?viewCode=${code}`,
    method: 'GET',
    transformResponse: [
      (data) => {
        // 对 data 进行任意转换处理
        let originData = {};

        try {
          originData = JSON.parse(data);
        } catch (e) {
          console.error(e, data);
          return data;
        }

        const {
          height,
          viewCode = code,
          valueField = 'value',
          displayField = 'name',
          pageSize = 5,
          queryFields = [],
          tableFields = [],
          // queryUrl,
        } = originData;
        let { title } = originData;
        if (originData.failed) {
          title = intl
            .get('hzero.common.c7n.lov.notDefine', { code })
            .d(`值集视图未定义: "${code}", 请维护值集视图!`);
        } else if (!originData.lovCode) {
          title = `lov ${code} loading...`;
        }
        const lovItems = [];
        // let tableWidth = 0;
        queryFields.forEach((queryItem = {}) => {
          const lovItem = {
            lovId: viewCode,
            lovItemId: `query_${queryItem.field}`,
            gridFieldName: queryItem.field,
            gridField: 'N',
            display: queryItem.label,
            autocompleteField: 'Y',
            conditionField: 'Y',
            isAutocomplete: 'N',
            conditionFieldWidth: null,
            conditionFieldLabelWidth: null,
            conditionFieldType: queryItem.dataType === 'LOV_CODE' ? 'object' : queryItem.dataType,
            conditionFieldSelectCode: queryItem.dataType === 'SELECT' ? queryItem.sourceCode : null,
            conditionFieldLovCode: queryItem.dataType === 'LOV_CODE' ? queryItem.sourceCode : null,
            conditionFieldName: null,
            conditionFieldTextfield: null,
            conditionFieldNewline: 'N',
            conditionFieldSelectUrl: null,
            conditionFieldSelectVf: null,
            conditionFieldSelectTf: null,
            conditionFieldSequence: 1,
            gridFieldSequence: 1,
          };
          lovItems.push(lovItem);
        });
        tableFields.forEach((tableItem) => {
          const lovItem = {
            lovId: viewCode,
            lovItemId: `table_${tableItem.dataIndex}`,
            gridFieldName: tableItem.dataIndex,
            gridFieldWidth: tableItem.width,
            gridFieldAlign: 'left',
            autocompleteField: 'Y',
            conditionField: 'N',
            isAutocomplete: 'N',
            gridField: 'Y',
            display: tableItem.title,
            conditionFieldWidth: null,
            conditionFieldLabelWidth: null,
            conditionFieldType: null,
            conditionFieldSelectCode: null,
            conditionFieldName: null,
            conditionFieldTextfield: null,
            conditionFieldNewline: 'N',
            conditionFieldSelectUrl: null,
            conditionFieldSelectVf: null,
            conditionFieldSelectTf: null,
            conditionFieldLovCode: null,
            conditionFieldSequence: 1,
            gridFieldSequence: 1,
          };
          lovItems.push(lovItem);
          // tableWidth += tableItem.width;
        });

        let queryColumns = 0;
        if (queryFields.length) {
          if (queryFields.length <= 2) {
            queryColumns = queryFields.length;
          } else {
            queryColumns = 2;
          }
        }

        return {
          originData: {
            lovCode: code,
            ...originData,
          },
          code: viewCode,
          title,
          description: title,
          lovId: viewCode,
          placeholder: title,
          sqlId: viewCode,
          customSql: null,
          queryColumns,
          customUrl: null,
          textField: displayField,
          valueField,
          delayLoad: 'N',
          needQueryParam: 'N',
          editableFlag: 'Y',
          canPopup: 'Y',
          lovPageSize: pageSize,
          treeFlag: 'N',
          idField: null,
          parentIdField: null,
          lovItems,
          width: 700,
          height,
        };
      },
    ],
  };
};

const lovQueryAxiosConfig = (code, lovConfig = {}, props, lovQueryUrl) => {
  const { queryUrl, lovCode } = lovConfig.originData || {};
  const { API_HOST, HZERO_PLATFORM } = getEnvConfig();
  const { data } = props;
  let url = `${API_HOST}${HZERO_PLATFORM}/v1/${
    isTenantRoleLevel() ? `${getCurrentOrganizationId()}/` : ''
  }lovs/data?lovCode=${lovCode}`;
  if (lovQueryUrl) {
    url = lovQueryUrl;
  } else if (queryUrl) {
    url = generateUrlWithGetParam(queryUrl, { lovCode });
    const organizationRe = /{organizationId}|{tenantId}/g;
    if (organizationRe.test(url)) {
      const tId = getCurrentOrganizationId();
      url = url.replace(organizationRe, tId);
    }
    if (!isEmpty(data)) {
      Object.keys(data).forEach((key) => {
        const lovParamsRe = RegExp(`({)${key}(})`, 'g');
        if (lovParamsRe.test(url)) {
          const val = data[key];
          url = url.replace(lovParamsRe, val);
        }
      });
    }
    // url = `${url}${url.indexOf('?') ? '&' : '?'}lovCode=${lovCode}`;
  }
  return {
    url,
    method: 'GET',
  };
};

const lookupAxiosConfig = ({ params, lookupCode }) => {
  let publicFlag = false;
  let lovParams = {};
  if (params) {
    const { publicMode, ...other } = params;
    publicFlag = publicMode;
    lovParams = other;
  }
  const { API_HOST, HZERO_PLATFORM } = getEnvConfig();
  return {
    url: lookupCode
      ? `${API_HOST}${HZERO_PLATFORM}/v1/${
          // eslint-disable-next-line no-nested-ternary
          publicFlag ? 'pub/' : isEqualOrganization() ? `${getCurrentOrganizationId()}/` : ''
        }lovs/data?lovCode=${lookupCode}`
      : undefined,
    method: 'GET',
    params: lovParams,
    transformResponse: (data) => {
      // 对 data 进行任意转换处理
      let originData = data || [];
      if (typeof data === 'string') {
        try {
          originData = JSON.parse(data);
        } catch (e) {
          originData = data;
        }
      }
      return originData;
    },
  };
};

// TODO: 批量查询lookupCode只支持独立值集，对于sql值集等的如何处理？？
// const lookupBatchAxiosConfig = codes => {
//   const url = `${API_HOST}${HZERO_PLATFORM}/v1/${
//     isTenantRoleLevel() ? `${getCurrentOrganizationId()}/` : ''
//   }lovs/value/batch`;
//   return {
//     url,
//     method: 'GET',
//     params: codes.reduce((obj, code) => {
//       // eslint-disable-next-line
//       obj[code] = code;
//       return obj;
//     }, {}),
//   };
// };

const generatePageQuery = ({ page, pageSize, sortName, sortOrder }) => ({
  page: page === undefined ? page : page - 1,
  size: pageSize,
  sort: sortName && (sortOrder ? `${sortName},${sortOrder}` : sortName),
});

configure({
  tableBorder: true,
  ripple: false,
  modalOkFirst: false,
  lovDefineAxiosConfig,
  lovQueryAxiosConfig,
  lookupAxiosConfig,
  lookupUrl: undefined,
  lovQueryUrl: undefined,
  // lookupBatchAxiosConfig,
  lookupAxiosMethod: 'GET',
  dataKey: 'content',
  totalKey: 'totalElements',
  axios: withTokenAxios,
  generatePageQuery,
  status: {
    add: 'create',
    update: 'update',
    delete: 'delete',
  },
  // iconfontPrefix: 'c7n',
  statusKey: '_status',
  tlsKey: '_tls',
  useColon: true,
  // eslint-disable-next-line react/jsx-props-no-spreading
  queryBar: (props) => <QueryBarMore {...props} />,
  feedback: {
    loadSuccess: () => {},
    loadFailed: (resp) => {
      if (resp && resp.failed) {
        notification.error({
          message: resp && resp.message,
        });
      } else if (resp && resp.response) {
        if (!authrIntercept(resp.response.status)) {
          return;
        }
        let m = require('../assets/icon_page_wrong.svg');
        if (m.__esModule) {
          m = m.default;
        }
        notification.error({
          icon: <></>,
          message: (
            <>
              <img src={m} alt="" className="ant-notification-notice-message-img" />
              <div className="ant-notification-notice-message-content">
                {intl.get(`hzero.common.requestNotification.${resp.response.status}`) ||
                  resp.message}
              </div>
            </>
          ),
          className: 'request error',
        });
      }
    },
    submitSuccess: (resp) => {
      notification.success({
        message: resp && resp.message,
      });
    },
    submitFailed: (resp) => {
      if (resp && resp.failed) {
        notification.error({
          message: resp && resp.message,
        });
      } else if (resp && resp.response) {
        let m = require('../assets/icon_page_wrong.svg');
        if (m.__esModule) {
          m = m.default;
        }
        notification.error({
          icon: <></>,
          message: (
            <>
              <img src={m} alt="" className="ant-notification-notice-message-img" />
              <div className="ant-notification-notice-message-content">
                {intl.get(`hzero.common.requestNotification.${resp.response.status}`) ||
                  resp.message}
              </div>
            </>
          ),
          className: 'request error',
        });
      }
    },
  },
  transport: {
    tls: ({ dataSet, name: fieldName }) => {
      // TODO: 先使用 dataSet.current 下个版本 c7n 会 把 record 传进来
      const _token = dataSet.current.get('_token');
      const { HZERO_PLATFORM } = getEnvConfig();
      return {
        url: `${HZERO_PLATFORM}/v1/multi-language`,
        method: 'GET',
        params: { _token, fieldName },
        transformResponse: (data) => {
          try {
            const jsonData = JSON.parse(data);
            if (jsonData && !jsonData.faied) {
              const tlsRecord = {};
              jsonData.forEach((intlRecord) => {
                tlsRecord[intlRecord.code] = intlRecord.value;
              });
              return [{ [fieldName]: tlsRecord }];
            }
          } catch (e) {
            // do nothing, use default error deal
          }
          return data;
        },
      };
    },
  },
});

export { withTokenAxios as axios, lovDefineAxiosConfig };
