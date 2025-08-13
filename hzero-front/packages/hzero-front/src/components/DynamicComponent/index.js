/* eslint-disable react/require-default-props */
import React from 'react';
import PropTypes from 'prop-types';
import { Spin } from 'hzero-ui';
import { omit, isString, get, startsWith, forEach, pick } from 'lodash';

import DynamicToolbar from './DynamicToolbar';
import DynamicForm from './DynamicForm';
import DynamicSearchForm from './DynamicSearchForm';
import DynamicTable from './DynamicTable';
import DynamicModal from './DynamicModal';

import { dealObjectProps, set } from './utils';
import {
  dynamicComponentOmitProps,
  dynamicComponentOmitConfigProps,
  contextPrefix,
  contextOmitDynamicTableProps,
} from './config';
import DynamicTabs from './DynamicTabs';

set('DynamicToolbar', DynamicToolbar);
set('DynamicForm', DynamicForm);
set('DynamicSearchForm', DynamicSearchForm);
set('DynamicTable', DynamicTable);
set('DynamicModal', DynamicModal);
set('DynamicTabs', DynamicTabs);

export default class DynamicComponent extends React.Component {
  static propTypes = {
    template: PropTypes.object,
  };

  static defaultProps = {};

  dealCommonContainerProps() {
    const { context, template = {} } = this.props;
    const otherProps = omit(this.props, dynamicComponentOmitProps);
    const configProps = dealObjectProps(omit(template, dynamicComponentOmitConfigProps));
    let templateProps = template.props;
    switch (template.templateType) {
      case 'DynamicTable':
        templateProps = omit(template.props, contextOmitDynamicTableProps);
        break;
      // form 和 toolbar 还没有要在运行时处理的属性
      case 'DynamicForm':
      case 'DynamicToolbar':
      default:
        break;
    }
    const dealProps = dealObjectProps(templateProps, context);
    return {
      ...configProps,
      ...otherProps,
      ...dealProps,
      context,
    };
  }

  getDynamicComponent() {
    const { context, template = {} } = this.props;
    let dynamicComponent = null;
    let props = {};
    // DynamicModal
    if (template.templateType === 'DynamicModal') {
      const otherProps = omit(this.props, dynamicComponentOmitProps);
      const configProps = omit(template, dynamicComponentOmitConfigProps);
      // 处理 Modal 的 afterSave 属性, afterHide, afterShow
      forEach(template.props, (v, k) => {
        switch (k) {
          case 'afterSave':
          case 'afterHide':
          case 'afterShow':
            if (template.props[k]) {
              if (isString(template.props[k]) && startsWith(template.props[k], contextPrefix)) {
                const attributePath = template.props[k].substr(5);
                Object.defineProperty(props, k, {
                  get: () => get(context, attributePath),
                  enumerable: true,
                });
              } else {
                props[k] = v;
              }
            }
            break;
          default:
            props[k] = v;
        }
      });
      forEach(otherProps, (v, k) => {
        props[k] = v;
      });
      forEach(configProps, (v, k) => {
        props[k] = v;
      });
    } else {
      props = this.dealCommonContainerProps();
    }
    // DynamicModal
    switch (template.templateType) {
      case 'DynamicForm':
        dynamicComponent = React.createElement(DynamicForm, props);
        break;
      case 'DynamicToolbar':
        dynamicComponent = React.createElement(DynamicToolbar, props);
        break;
      case 'DynamicSearchForm':
        dynamicComponent = React.createElement(DynamicSearchForm, props);
        break;
      case 'DynamicTable':
        dynamicComponent = React.createElement(DynamicTable, {
          ...props,
          ...pick(template.props, contextOmitDynamicTableProps),
        });
        break;
      case 'DynamicModal':
        dynamicComponent = React.createElement(DynamicModal, props);
        break;
      case 'DynamicTabs':
        dynamicComponent = React.createElement(DynamicTabs, props);
        break;
      default:
        dynamicComponent = null;
        break;
    }
    return dynamicComponent;
  }

  render() {
    const dynamicComponent = this.getDynamicComponent();
    return <Spin spinning={dynamicComponent === null}>{dynamicComponent}</Spin>;
  }
}
