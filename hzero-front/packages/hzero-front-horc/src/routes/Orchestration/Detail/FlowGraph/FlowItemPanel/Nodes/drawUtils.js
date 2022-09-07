/**
 * drawUtils
 * @author baitao.huang@hand-china.com
 * @date 2020/4/9
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import { INS_STATUS_GANTT, INS_STATUS } from '@/constants/constants';

/**
 * 自定义节点
 * @param {Object} param
 * @param {Object} param.item
 * @param {String} param.item.overrideColor
 * @param {String} param.item.status
 * @param {String} param.logoText
 * @param {String} param.primaryColor
 * @param {String} param.fillColor
 */
const drawNode = (param) => {
  const { item, logoText, primaryColor, fillColor } = param;
  const model = item.getModel();
  const { label, size, overrideColor, statusCode } = model;
  const modelColor = overrideColor || fillColor;
  model.color = overrideColor || primaryColor;
  let [width, height] = size.split('*');
  width = parseInt(width, 10);
  height = parseInt(height, 10);
  const group = item.getGraphicGroup();
  const startX = -(width / 2);
  const startY = -(height / 2);

  // 1. 整体 rect
  const keyShape = group.addShape('rect', {
    attrs: {
      x: startX,
      y: startY,
      width,
      height,
      radius: 4,
      type: 'rect',
      fill: modelColor,
      stroke: primaryColor,
    },
  });

  // group.addShape('image', {
  //   attrs: {
  //     x: startX,
  //     y: startY,
  //     width,
  //     height,
  //     img: icon,
  //   },
  // });
  // 2. logo rect
  group.addShape('rect', {
    attrs: {
      x: startX + 8,
      y: startY + 10,
      width: 28,
      height: 28,
      fill: fillColor,
      radius: 4,
      stroke: primaryColor,
      type: 'rect',
    },
  });
  group.addShape('text', {
    attrs: {
      x: startX + 22,
      y: startY + 32,
      width: 28,
      height: 28,
      text: logoText,
      fontSize: 13,
      textAlign: 'center',
      fill: primaryColor,
      type: 'text',
    },
  });
  group.addShape('text', {
    attrs: {
      fontSize: 12,
      textAlign: 'center',
      fill: 'rgba(51, 51, 51, 0.9)',
      x: 0,
      y: startY + 14 + 20 - 4,
      text: label,
      type: 'text',
    },
  });
  if (statusCode) {
    let statusText = '···';
    const statusColor = INS_STATUS_GANTT[statusCode] || '#108ee9';
    switch (statusCode) {
      case INS_STATUS.FAILED:
      case INS_STATUS.KILLED:
        statusText = '✘';
        break;
      case INS_STATUS.SUCCESSFUL:
        statusText = '✔';
        break;
      default:
        break;
    }
    // 3. 状态 circle
    group.addShape('circle', {
      attrs: {
        x: -startX - 16,
        y: 0,
        r: 8,
        fill: '#fff',
        stroke: statusColor,
      },
    });
    group.addShape('text', {
      attrs: {
        fontSize: 12,
        textAlign: 'center',
        fill: statusColor,
        x: -startX - 16,
        y: 6,
        text: statusText,
        type: 'text',
      },
    });
  }
  return keyShape;
};

export { drawNode };
