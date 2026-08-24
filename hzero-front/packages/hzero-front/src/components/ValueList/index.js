/**
 * index
 * 只用于表单内部
 * @date 2018/10/15
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';

import { Select } from 'hzero-ui';
import { isFunction, isArray, isEmpty, map, isEqual } from 'lodash';

import request from 'utils/request';
import notification from 'utils/notification';
import { getEnvConfig } from 'utils/iocUtils';
import { getCurrentOrganizationId } from 'utils/utils';

const { HZERO_PLATFORM } = getEnvConfig();

function mapTo$Options({ options = [], valueField = 'valueField', displayField = 'meaning' }) {
  return map(options, (opt) => (
    <Select.Option key={opt[valueField]} value={opt[valueField]}>
      {opt[displayField]}
    </Select.Option>
  ));
}

/**
 * 获取 查询值集的方法
 * queryUrl 有值 返回方法 (params) => request(params)
 * 否则 返回 queryService
 * @param {string} queryUrl - 查询url
 * @param {function} queryService - 查询方法
 */
function getValueRequest(queryUrl, queryService) {
  if (queryUrl) {
    return (params) =>
      request(queryUrl, {
        method: 'GET',
        query: params,
      });
  }
  return queryService;
}

/**
 * 统一查询独立、SQL、URL类型的值集
 * @param {Object} params - 额外的查询参数
 * @param {String} params.lovCode - 值集code
 */
async function queryUnifyIdpValue(params = {}) {
  return request(`${HZERO_PLATFORM}/v1/lovs/data`, {
    method: 'GET',
    query: params,
  });
}

// /**
//  * 查询单个独立值集值
//  * @param {String} params.lovCode - 值集code
//  */
// async function queryIdpValue(params = {}) {
//   return request(`${HZERO_PLATFORM}/v1/lovs/value`, {
//     query: params,
//   });
// }

export default class ValueList extends React.Component {
  static defaultProps = {
    valueField: 'value',
    displayField: 'meaning',
    // textField 将显示字段回写到表单中
    lazyLoad: true,
  };

  state = {
    // needLazyLoad: true, // 在检测到 更新属性变化后, 置为 true, 点击后变为 false
  };

  constructor(props) {
    super(props);
    this.handleSelectChange = this.handleSelectChange.bind(this);
    this.handleFocus = this.handleFocus.bind(this);

    // 处理查询值集的 结果
    this.handleQueryOk = this.handleQueryOk.bind(this);
    this.handleQueryError = this.handleQueryError.bind(this);

    this.getTextField = this.getTextField.bind(this);
    this.getTextValue = this.getTextValue.bind(this);

    // 处理 options
    if (isArray(props.options)) {
      const { lovCode, queryParams = {} } = this.props;
      let nowParams = queryParams || {};
      if (isFunction(queryParams)) {
        nowParams = queryParams();
      }
      if (lovCode) {
        nowParams.lovCode = lovCode;
      }
      this.state = {
        $options: mapTo$Options({
          options: props.options,
          valueField: props.valueField,
          displayField: props.displayField,
        }),
        // shouldUpdateProps: {
        //   lovCode,
        //   queryUrl,
        //   queryService,
        //   queryParams: nowParams,
        //   options,
        //   valueField,
        //   displayField,
        // },
      };
    }
  }

  render() {
    const {
      lovCode,
      options,
      value,
      textValue,
      onFocus,
      onChange,
      valueField,
      displayField,
      lazyLoad,
      ...otherProps
    } = this.props;
    const { $options, needLazyLoad = true } = this.state;
    const selectProps = {
      onChange: this.handleSelectChange,
    };
    if (lazyLoad && needLazyLoad) {
      selectProps.onFocus = this.handleFocus; //
      selectProps.value = $options ? value : this.getTextValue() || value;
    } else {
      selectProps.value = value;
    }
    return React.createElement(Select, { ...otherProps, ...selectProps }, $options);
  }

  componentDidMount() {
    const { lazyLoad, form, textField, textValue } = this.props;
    if (!lazyLoad) {
      this.handleDeal$Options();
    } else if (form && textField && this.getTextValue() && textValue) {
      // 设置表单值
      form.setFieldsValue({
        [this.getTextField()]: textValue,
      });
    }
  }

  componentDidUpdate(prevProps) {
    // 当 queryPrams 变动时,需要重新查询
    const { needLazyLoad = true } = this.state;
    const {
      lovCode,
      queryUrl,
      queryService,
      queryParams = {},
      options,
      valueField,
      displayField,
      lazyLoad,
    } = this.props;
    let nowParams = queryParams || {};
    if (isFunction(queryParams)) {
      nowParams = queryParams();
    }
    if (lovCode) {
      nowParams.lovCode = lovCode;
    }
    const shouldUpdateProps = {
      lovCode,
      queryUrl,
      queryService,
      queryParams: nowParams,
      options,
      valueField,
      displayField,
    };

    const {
      lovCode: oldLovCode,
      queryUrl: oldQueryUrl,
      queryService: oldQueryService,
      queryParams: oldQueryParams = {},
      options: oldOptions,
      valueField: oldValueField,
      displayField: oldDisplayField,
    } = prevProps;
    let oldNowParams = oldQueryParams || {};
    if (isFunction(oldQueryParams)) {
      oldNowParams = oldQueryParams();
    }
    if (lovCode) {
      oldNowParams.lovCode = lovCode;
    }
    const oldShouldUpdateProps = {
      lovCode: oldLovCode,
      queryUrl: oldQueryUrl,
      queryService: oldQueryService,
      queryParams: oldNowParams,
      options: oldOptions,
      valueField: oldValueField,
      displayField: oldDisplayField,
    };

    if (lazyLoad && !needLazyLoad && !isEqual(oldShouldUpdateProps, shouldUpdateProps)) {
      // warn 如果更新了 更新条件 并且是懒加载, 则将需要懒加载设置为空
      // eslint-disable-next-line react/no-did-update-set-state
      this.setState({ needLazyLoad: true });
    } else if (!lazyLoad && !isEqual(oldShouldUpdateProps, shouldUpdateProps)) {
      this.handleDeal$Options();
    }
  }

  handleQueryOk(res) {
    const { lovCode, queryParams = {}, valueField, displayField } = this.props;
    let nowParams = queryParams || {};
    if (isFunction(queryParams)) {
      nowParams = queryParams();
    }
    if (lovCode) {
      nowParams.lovCode = lovCode;
    }
    let $options;
    if (isEmpty(res)) {
      $options = [];
    } else if (isArray(res)) {
      // 没有分页的返回
      $options = mapTo$Options({ options: res, valueField, displayField });
    } else if (res.failed) {
      // 出错
      notification.error({ message: res.message });
    } else {
      // 有分页的返回
      $options = mapTo$Options({ options: res.content, valueField, displayField });
    }
    this.setState({
      // shouldUpdateProps: {
      //   lovCode,
      //   queryUrl,
      //   queryService,
      //   queryParams: nowParams,
      //   options,
      //   valueField,
      //   displayField,
      // },
      $options,
    });
  }

  handleQueryError() {}

  handleDeal$Options() {
    const {
      lovCode,
      queryUrl,
      queryService,
      queryParams = {},
      options,
      valueField,
      displayField,
    } = this.props;
    let nowParams = queryParams || {};
    if (isFunction(queryParams)) {
      nowParams = queryParams();
    }
    if (lovCode) {
      nowParams.lovCode = lovCode;
    }
    if (options) {
      // 直接有 options
      this.setState({
        $options: mapTo$Options({ options, valueField, displayField }),
        // shouldUpdateProps: {
        //   lovCode,
        //   queryUrl,
        //   queryService,
        //   queryParams: nowParams,
        //   options,
        //   valueField,
        //   displayField,
        // },
      });
    } else {
      let rq = getValueRequest(queryUrl, queryService);
      if (!rq && lovCode) {
        // 查询 lov
        rq = queryUnifyIdpValue;
      }
      if (isFunction(rq)) {
        rq({ ...this.getTenantQueryParams(), ...nowParams }).then(
          this.handleQueryOk,
          this.handleQueryError
        );
      }
    }
  }

  handleFocus(...args) {
    const { onFocus } = this.props;
    this.setState({
      needLazyLoad: false,
    });
    this.handleDeal$Options();
    if (isFunction(onFocus)) {
      onFocus(...args);
    }
  }

  handleSelectChange(value, option) {
    const { onChange, form } = this.props;
    if (isFunction(onChange)) {
      onChange(value, option);
    }
    const textField = this.getTextField();
    if (form && textField) {
      if (isArray(option)) {
        // 通过 Option 的 children 拿到显示值
        form.setFieldsValue({ [textField]: option.map((item) => item.props.children) });
      } else {
        // 通过 Option 的 children 拿到显示值
        form.setFieldsValue({ [textField]: option.props.children });
      }
    }
  }

  /**
   * 获取 显示字段 对应的 后端存储数据
   */
  getTextField() {
    const { form, textField } = this.props;
    if (textField) {
      form.getFieldDecorator(textField);
    }
    return textField;
  }

  /**
   * 获取显示值
   */
  getTextValue() {
    const { form, textField, value, textValue } = this.props;
    if (!value) {
      return;
    }
    let formTextValue;
    if (form && textField) {
      formTextValue = form.getFieldValue(textField);
    }
    if (value && formTextValue) {
      return formTextValue;
    }
    return textValue;
  }

  getTenantQueryParams() {
    const organizationId = getCurrentOrganizationId();
    return {
      organizationId,
      tenantId: organizationId,
    };
  }
}
