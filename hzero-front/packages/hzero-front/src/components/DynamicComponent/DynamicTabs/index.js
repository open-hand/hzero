import React from 'react';
import { Tabs } from 'hzero-ui';
import { omit, map, slice } from 'lodash';

import { dynamicTabsOmitProps } from '../config';
import { get } from '../utils';

export default class DynamicTabs extends React.Component {
  ref = {};

  render() {
    const panes = this.renderPanes();
    const otherProps = omit(this.props, dynamicTabsOmitProps);
    return (
      <Tabs {...otherProps} animated={false}>
        {panes}
      </Tabs>
    );
  }

  renderPanes() {
    const { children = [], fields, context } = this.props;
    const tabPanes = map(fields, field => {
      const { props: { tplFrom = -1, tplTo = -1, ...tabPaneProps } = {} } = field;
      const tabPane = map(slice(children, tplFrom, tplTo), tpl => {
        const DynamicComponent = get('DynamicComponent');
        return <DynamicComponent context={context} template={tpl} onRef={this.onRef(tpl)} />;
      });
      return (
        <Tabs.TabPane {...tabPaneProps} key={field.fieldName} tab={field.fieldLabel}>
          {tabPane}
        </Tabs.TabPane>
      );
    });
    return tabPanes;
  }

  /**
   * @param {Object} template - 模板的配置
   * @returns {Function} 将 DynamicComponent 对应组件的this设置到 this.ref 中
   */
  onRef(template) {
    return tplRef => {
      this.ref[template.templateCode] = tplRef;
    };
  }
}
