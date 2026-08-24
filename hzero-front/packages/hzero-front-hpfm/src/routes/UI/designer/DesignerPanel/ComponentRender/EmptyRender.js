/**
 * EmptyRender
 * @author WY yang.wang06@hand-china.com
 * @date 2018/10/10
 */
// import React from 'react';
// import { map } from 'lodash';
//
// import DrawDragField from '../Drag/DrawDragField';
//
// import styles from '../index.less';
//
// export default class EmptyRender extends React.Component {
//   render() {
//     const { component = {}, onActiveField, onSwapField, onAddField, onRemoveField } = this.props;
//     return (
//       <div className={styles['common-render']}>
//         {map(component.fields, field => {
//           return (
//             <DrawDragField
//               component={field}
//               key={field.id}
//               componentId={component.id}
//               onActiveField={onActiveField}
//               pComponent={component}
//               onSwapField={onSwapField}
//               onAddField={onAddField}
//               onRemoveField={onRemoveField}
//             />
//           );
//         })}
//       </div>
//     );
//   }
// }

/**
 * empty render, use to placeholder legal component
 * @return {null}
 */
export default function EmptyRender() {
  return null;
}
