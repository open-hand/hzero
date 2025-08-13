/**
 * index
 * @description
 * @author WY yang.wang06@hand-china.com
 * @date 2018/10/10
 */

/* eslint-disable class-methods-use-this */

import React from 'react';

import { map } from 'lodash';

import { templates } from '../utils';
import DragComponent from './DragComponent';
import DragField from './DragField';

export default class LeftComponent extends React.Component {
  constructor(props) {
    super(props);
    this.renderComponents = this.renderComponents.bind(this);
    // this.renderContainers = this.renderContainers.bind(this);
  }

  render() {
    return <div className="pick-box-panel-container">{this.renderComponents()}</div>;
  }

  renderComponents() {
    const { activeComponentCode } = this.props;
    const template = templates[activeComponentCode];
    if (template) {
      switch (template.templateType) {
        case 'DynamicTabs':
        case 'DynamicModal':
          return this.renderTemplates(template.accepts);
        default:
          return this.renderFields(template.accepts);
      }
    }
    return this.renderTemplates(templates);
  }

  renderTemplates(acceptTemplates) {
    return map(acceptTemplates, (component, componentCode) => {
      return <DragComponent component={component} key={componentCode} />;
    });
  }

  renderFields(acceptFields) {
    return map(acceptFields, (component, componentCode) => {
      return <DragField component={component} key={componentCode} />;
    });
  }
}
