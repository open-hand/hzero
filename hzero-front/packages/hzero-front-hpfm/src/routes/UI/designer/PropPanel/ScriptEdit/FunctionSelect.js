/**
 * 用于方法类型的组件编辑方法
 * FunctionSelect.js
 * @date 2018/11/15
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

import React from 'react';
import PropTypes from 'prop-types';
import { Select } from 'hzero-ui';
import { forEach } from 'lodash';

export default class FunctionSelect extends React.Component {
  static defaultProps = {
    components: [],
    filterComponentScript: () => true,
    scripts: [],
  };

  static propTypes = {
    components: PropTypes.array,
    filterComponentScript: PropTypes.func,
    scripts: PropTypes.array,
  };

  render() {
    const { components, filterComponentScript, scripts, ...otherProps } = this.props;
    return (
      <Select {...otherProps}>
        {getComponentScripts(components, filterComponentScript)}
        {getPageScripts(scripts)}
      </Select>
    );
  }
}

/**
 * 通过 components 获取 components 的 Option.Group
 * @param {object} components
 * @param {function} filterScript
 */
export function getComponentScripts(components = [], filterScript = () => true) {
  const optionGroups = [];
  forEach(components, component => {
    const optGroupProps = {
      label: component.description,
      key: component.templateCode,
    };
    const options = [];
    switch (component.templateType) {
      case 'DynamicForm':
        if (filterScript(component, 'submit') !== false) {
          options.push(
            <Select.Option
              key={`this.ref[${component.templateCode}].submit`}
              value={`this.ref[${component.templateCode}].submit`}
            >
              submit
            </Select.Option>
          );
        }
        if (filterScript(component, 'query') !== false) {
          options.push(
            <Select.Option
              key={`this.ref[${component.templateCode}].query`}
              value={`this.ref[${component.templateCode}].query`}
            >
              query
            </Select.Option>
          );
        }
        break;
      case 'DynamicTable':
        if (filterScript(component, 'reload') !== false) {
          options.push(
            <Select.Option
              key={`this.ref[${component.templateCode}].reload`}
              value={`this.ref[${component.templateCode}].reload`}
            >
              reload
            </Select.Option>
          );
        }
        if (filterScript(component, 'load') !== false) {
          options.push(
            <Select.Option
              key={`this.ref[${component.templateCode}].load`}
              value={`this.ref[${component.templateCode}].load`}
            >
              load
            </Select.Option>
          );
        }
        break;
      default:
        break;
    }
    if (options.length > 0) {
      optionGroups.push(<Select.OptGroup {...optGroupProps}>{options}</Select.OptGroup>);
    }
  });
  return optionGroups;
}

export function getPageScripts(scripts = []) {
  const options = [];
  forEach(scripts, script => {
    options.push(
      <Select.Option value={`this.${script.name}`} key={`this.${script.name}`}>
        {script.name}
      </Select.Option>
    );
  });
  return <Select.OptGroup label="页面脚本">{options}</Select.OptGroup>;
}
