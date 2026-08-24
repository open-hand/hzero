import React from 'react';
import { Tabs, Modal } from 'hzero-ui';
import { map, forEach, find, some, isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

// import intl from 'utils/intl';

import DropFieldTabsTabPane from '../../Drop/DropFieldTabsTabPane';

import ComponentRender from '..';
import { TabPane as DesignTabPane, hasTemplate } from '../../../utils';
import { convertInitExtraField, convertInitExtraTemplate } from '../../../convertUtils';

import styles from '../../index.less';

export default class DynamicTabsRender extends React.Component {
  render() {
    const { component, ...otherComponentRenderProps } = this.props;
    const { fields = [] } = component;
    const { currentEditComponent, currentEditField } = this.props;
    let hasFieldActive = false;
    const className = [styles['dynamic-tabs']];
    if (!currentEditField && currentEditComponent === component) {
      className.push(styles['dynamic-tabs-active']);
    }
    const templates = map(fields, field => {
      if (currentEditField === field) {
        hasFieldActive = true;
      }
      const currentTabComponents = field.children;
      const tabPaneChildren = isEmpty(currentTabComponents) ? (
        <div className={styles['dynamic-tabs-placeholder']}>拖入组件</div>
      ) : (
        map(currentTabComponents, tpl => {
          return (
            <ComponentRender {...otherComponentRenderProps} component={tpl} key={tpl.templateId} />
          );
        })
      );
      return (
        <Tabs.TabPane key={field.fieldName} tab={field.fieldLabel}>
          <DropFieldTabsTabPane
            onAddField={this.handleAddField}
            pComponent={component}
            component={field}
            currentEditComponent={currentEditComponent}
            currentEditField={currentEditField}
          >
            <React.Fragment>{tabPaneChildren}</React.Fragment>
          </DropFieldTabsTabPane>
        </Tabs.TabPane>
      );
    });
    if (hasFieldActive) {
      className.push(styles['dynamic-tabs-field-active']);
    }
    return (
      <div className={className.join(' ')} onClick={this.handleTabsComponentClick}>
        <Tabs
          type="editable-card"
          onChange={this.handleTabChange}
          onEdit={this.handleTabsEdit}
          onTabClick={this.handleTabClick}
        >
          {templates}
        </Tabs>
      </div>
    );
  }

  /**
   * 标签点击需要阻止冒泡
   * @param {string} activeTabKey
   * @param {import('react').SyntheticEvent} e
   * @memberof DynamicTabsRender
   */
  @Bind()
  handleTabClick(activeTabKey, e) {
    e.stopPropagation();
    this.handleTabChange(activeTabKey);
  }

  @Bind()
  handleTabsComponentClick() {
    this.handleActiveTemplate();
  }

  @Bind()
  handleActiveTemplate() {
    const { onActiveComponent, component } = this.props;
    onActiveComponent(component);
  }

  @Bind()
  handleActiveField(field) {
    const { component, onActiveField } = this.props;
    onActiveField(component, field);
  }

  @Bind()
  handleTabChange(activeKey) {
    const { component, currentEditField } = this.props;
    const { fields = [] } = component;
    const field = find(fields, f => f.fieldName === activeKey);
    if (field && currentEditField !== field) {
      this.handleActiveField(field);
    }
  }

  /**
   * 新增 Tabs.Pane
   * 需要组织冒泡
   */
  @Bind()
  handleAddTabPane(e) {
    if (e) {
      e.stopPropagation();
    }
    const { component } = this.props;
    const newField = convertInitExtraField(component, DesignTabPane);
    const { fields = [] } = component;
    component.fields = [...fields, newField];
    if (component.fields.length === 1) {
      const { onActiveField } = this.props;
      onActiveField(component, newField);
    } else {
      this.forceUpdate();
    }
  }

  @Bind()
  handleTabsEdit(targetKey, action) {
    if (action === 'add') {
      this.handleAddTabPane();
    }
    if (action === 'remove') {
      this.handleTabPaneRemove(targetKey);
    }
  }

  /**
   * 删除某一个 tabPane
   */
  @Bind()
  handleTabPaneRemove(tabPaneKey) {
    const that = this;
    const { component = {}, currentEditComponent, currentEditField } = that.props;
    const { fields = [] } = component;
    const nextFields = [];
    let removeField;
    forEach(fields, field => {
      if (field.fieldName === tabPaneKey) {
        removeField = field;
      } else {
        nextFields.push(field);
      }
    });
    Modal.confirm({
      content: `是否删除当前标签: ${removeField.fieldLabel}`,
      onOk: () => {
        component.fields = nextFields;
        if (currentEditComponent === component) {
          if (currentEditField === removeField) {
            if (nextFields[0]) {
              that.handleActiveField(nextFields[0]);
            } else {
              that.handleActiveTemplate();
            }
            return; // 删除 tabsTabPane 逻辑结束
          }
        } else if (
          some(removeField.children, fieldC => hasTemplate(fieldC, currentEditComponent))
        ) {
          if (nextFields[0]) {
            that.handleActiveField(nextFields[0]);
          } else {
            that.handleActiveTemplate();
          }
          return; // 删除 tabsTabPane 逻辑结束
        }
        that.forceUpdate(); // 删除 tabsTabPane 逻辑结束
      },
    });
  }

  @Bind()
  handleAddField(field, newTpl) {
    const newTemplate = convertInitExtraTemplate(newTpl);
    const { children = [] } = field;
    const newChildren = [...children, newTemplate];
    // eslint-disable-next-line no-param-reassign
    field.children = newChildren;
    this.forceUpdate();
  }
}
