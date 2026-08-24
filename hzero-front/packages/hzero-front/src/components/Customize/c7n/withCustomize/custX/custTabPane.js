import { isEmpty, isArray } from 'lodash';
import { coverConfig } from '../customizeTool';

export default function custTabPane(options = {}, tabs) {
  const { code } = options;
  const { custConfig: config, loading } = this.state;
  if (loading) return null;
  if (!code || isEmpty(config[code])) return tabs;
  const { fields = [] } = config[code];
  fields.sort((p, n) => (p.seq === undefined || n.seq === undefined ? -1 : p.seq - n.seq));
  const childrenMap = {};
  const newChildren = [];
  const refTabs = tabs;
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
  const defaultActive = fields.find((field) => field.defaultActive === 1);
  if (defaultActive) {
    refTabs.props.activeKey = defaultActive.fieldCode;
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
      targetPane.props.tab = fieldName;
    }
    if (visible !== 0) {
      newChildren.push(targetPane);
    }
    delete childrenMap[fieldCode];
  });
  Object.keys(childrenMap).forEach((i) => newChildren.push(childrenMap[i]));
  refTabs.props.children = newChildren;
  return tabs;
}
