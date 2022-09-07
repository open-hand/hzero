/**
 * 可以存放容器的容器
 */

import { isFunction, some } from 'lodash';

import createDropField from './createDropField';
import { DragType } from '../../config';
import { hasTemplate } from '../../utils';

const DropFieldTabsTabPane = createDropField({
  // 接收 左侧 tpl 组件
  dropAcceptTypes: [DragType.dragComponent],
  canDrop: props => {
    const { currentEditField, currentEditComponent, component } = props;
    // 只有当当前 TabPane 是 激活状态时 才能 拖入组件
    return (
      currentEditField === component ||
      hasTemplate(some(component.children, child => hasTemplate(child, currentEditComponent)))
    );
  },
  drop: (props, monitor) => {
    // 如果在子组件中已经 drop 了, 这边就不需要继续 drop 了
    if (!monitor.didDrop()) {
      const { component } = props;
      const { onAddField } = props;
      if (isFunction(onAddField)) {
        onAddField(component, monitor.getItem().component);
      }
    }
  },
});

export default DropFieldTabsTabPane;
