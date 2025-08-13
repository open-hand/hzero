/**
 * 属性编辑区域
 * @date 2018/11/13
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

import React from 'react';

import { Tabs } from 'hzero-ui';

import intl from 'utils/intl';

import styles from './index.less';

import DynamicForm from './DynamicForm';
import DynamicToolbar from './DynamicToolbar';
import DynamicTable from './DynamicTable';
import DynamicPage from './DynamicPage';
import DynamicTabs from './DynamicTabs';

export default class PropPanel extends React.Component {
  render() {
    const {
      component = {},
      field,
      onUpdateConfig,
      onRefresh,
      propKey,
      templates,
      config,
    } = this.props;
    let propElement = null;
    const propRenderProps = {
      key: propKey,
      component,
      field,
      onRefresh,
      templates,
      config,
    };
    switch (component.templateType) {
      case 'DynamicForm':
        propElement = React.createElement(DynamicForm, propRenderProps);
        break;
      case 'DynamicToolbar':
        propElement = React.createElement(DynamicToolbar, propRenderProps);
        break;
      case 'DynamicTable':
        propElement = React.createElement(DynamicTable, propRenderProps);
        break;
      case 'DynamicModal':
        break;
      case 'DynamicTabs':
        propElement = React.createElement(DynamicTabs, propRenderProps);
        break;
      default:
        break;
    }
    return (
      <div className={styles['prop-panel']}>
        <Tabs>
          {propElement && (
            <Tabs.TabPane
              tab={intl
                .get(
                  `hpfm.ui.page.componentName.${(component && component.templateCode) ||
                    (field && field.componentCode)}`
                )
                .d('组件属性')}
              key={propKey}
            >
              {propElement}
            </Tabs.TabPane>
          )}
          <Tabs.TabPane tab={intl.get('hpfm.ui.model.page.attribute').d('页面属性')} key="page">
            <DynamicPage config={config} onUpdateConfig={onUpdateConfig} />
          </Tabs.TabPane>
        </Tabs>
      </div>
    );
  }
}
