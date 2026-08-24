import React from 'react';
import { isEmpty, isArray } from 'lodash';
import { coverConfig } from '../customizeTool';

export default function custCollapse(options = {}, collapse) {
  const { code } = options;
  const { custConfig: config, loading } = this.state;
  if (loading) return null;
  if (!code || isEmpty(config[code])) return collapse;
  const { fields = [] } = config[code];
  fields.sort((p, n) => (p.seq === undefined || n.seq === undefined ? -1 : p.seq - n.seq));
  const childrenMap = {};
  const newChildren = [];
  const refTabs = collapse;
  const refChildren = refTabs.props.children;
  const tools = this.getToolFuns();
  if (isArray(refChildren)) {
    refChildren.forEach((i) => {
      if (i.props && i.key !== undefined) {
        childrenMap[i.key] = i;
      }
    });
  } else if (refChildren && refChildren.props && refChildren.key) {
    childrenMap[refChildren.key] = refChildren;
  }
  const defaultActive = [];
  fields.every((field) => field.defaultActive === 1 && defaultActive.push(field.fieldCode));
  if (defaultActive.length > 0) {
    refTabs.props.activeKey = defaultActive;
  }
  fields.forEach((i) => {
    const { fieldName, fieldCode, conditionHeaderDTOs } = i;
    const { visible } = {
      visible: i.visible,
      ...coverConfig(conditionHeaderDTOs, tools, ['required', 'editable']),
    };
    const targetPane = childrenMap[fieldCode];
    if (!targetPane) return;
    if (fieldName !== undefined && targetPane && targetPane.props) {
      const oldHeader = targetPane.props.header;
      if (typeof oldHeader === 'function') {
        targetPane.props.header = oldHeader(fieldName);
      } else {
        targetPane.props.header = <h3>{fieldName}</h3>;
      }
    }
    if (visible !== 0) {
      newChildren.push(targetPane);
    }
    delete childrenMap[fieldCode];
  });
  Object.keys(childrenMap).forEach((i) => newChildren.push(childrenMap[i]));
  refTabs.props.children = newChildren;
  return collapse;
}
