/**
 * index
 * @author WY yang.wang06@hand-china.com
 * @date 2018/10/10
 */
import React from 'react';
import { map } from 'lodash';

import { templates } from '../utils';
import DropPage from './Drop/DropPage';
import ComponentRender from './ComponentRender/index';

import styles from './index.less';

class DesignerPanel extends React.Component {
  render() {
    const { config = {}, onAddComponent, onActiveComponent, ...otherProps } = this.props;
    const { fields: components } = config;
    return (
      <div className={styles['drop-page']}>
        {map(components, component => {
          // TODO 只有在模板中的组件才能加载
          if (templates[component.templateType]) {
            return (
              <ComponentRender
                {...otherProps}
                onActiveComponent={onActiveComponent}
                component={component}
                key={component.renderKey}
              />
            );
          }
          return null;
        })}
        <DropPage
          key="drop-page"
          onAddComponent={onAddComponent}
          onActiveComponent={onActiveComponent}
        />
      </div>
    );
  }
}

export default DesignerPanel;
