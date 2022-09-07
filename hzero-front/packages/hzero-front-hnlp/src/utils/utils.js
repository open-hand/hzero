import { isEmpty, isString, omit } from 'lodash';
import React from 'react';
import PropTypes from 'prop-types';

/**
 * 通过表单的ref获取表单的数据
 * @param {React.ref} ref
 * @returns {{}|*}
 */
export function getFieldsValueByRef(ref) {
  if (ref.current) {
    const { form } = ref.current.props;
    return form.getFieldsValue();
  }
  return {};
}

/**
 * 通过表单的ref获取表单的数据
 * @param {React.ref} ref
 * @returns {{}|*}
 */
export async function getValidationFieldsValueByRef(ref) {
  if (ref.current) {
    const { form } = ref.current.props;
    return new Promise((resolve, reject) => {
      form.validateFields((err, fieldsValue) => {
        if (err) {
          reject(err);
        } else {
          resolve(fieldsValue);
        }
      });
    });
  }
  return Promise.reject(); // 没有表单
}

export function proxyComponentMethod(options) {
  const {
    config = [
      {
        method: 'onClick',
        args: ['record'],
      },
    ],
    omitProps = [],
  } = options || {};
  const propTypes = {};
  config.forEach(({ method }) => {
    propTypes[method] = PropTypes.func.isRequired;
  });
  return function ProxyComponentMethod(ProxyComponent) {
    const displayName = isString(ProxyComponent)
      ? ProxyComponent
      : ProxyComponent.displayName || ProxyComponent.name || 'UnKnow';
    class ProxyMethodComponent extends React.Component {
      constructor(props) {
        super(props);
        const proxyMethods = {};
        config.forEach(({ method, args = [] }) => {
          proxyMethods[method] = (...oriArgs) => {
            const { [method]: proxyMethod } = this.props;
            proxyMethod(...args.map(arg => this.props[arg]), ...oriArgs);
          };
        });
        this.proxyMethods = proxyMethods;
      }

      render() {
        if (isEmpty(omitProps)) {
          return React.createElement(ProxyComponent, { ...this.props, ...this.proxyMethods });
        } else {
          return React.createElement(ProxyComponent, {
            ...omit(this.props, omitProps),
            ...this.proxyMethods,
          });
        }
      }
    }
    ProxyMethodComponent.displayName = displayName;
    ProxyMethodComponent.propTypes = propTypes;
    return ProxyMethodComponent;
  };
}
