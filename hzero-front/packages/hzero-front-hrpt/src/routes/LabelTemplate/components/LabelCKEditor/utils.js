/**
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/12
 * @copyright 2019 ® HAND
 */

// import intl from 'utils/intl';

import { labelTemplateLabelInsertConfig } from '@/stores/labelTemplateDS';

export const elementUnit = 'mm';

/**
 * 检查是否存在 某个 dialog
 * @param CKEDITOR
 * @param dialogName
 */
export function hasDialog(CKEDITOR, dialogName) {
  if (CKEDITOR) {
    CKEDITOR.ui.hasDialog(dialogName);
  }
  return false;
}

/**
 * 拼接命名
 * @param {string} posName
 * @return {string}
 */
export function buildName(posName) {
  return `hzero_label_${posName}`;
}

/**
 * 将编辑的数据data, 设置属性/样式到元素element上
 * @param data
 * @param element
 */
export function transformDataToElement(data, element) {
  const { constants } = labelTemplateLabelInsertConfig();
  const configuration = {
    width: data.width,
    height: data.height,
    // shape, tag, value, dataType
  };
  const styles = {};
  function setElementStyle(styleName, styleValue) {
    element.setStyle(styleName, styleValue);
    if (styleName !== 'background-color') {
      styles[styleName] = styleValue;
    } else {
      element.setAttribute('data-backgroundcolor', styleValue);
    }
  }
  function removeElementStyle(styleName) {
    element.removeStyle(styleName);
    if (styleName === 'background-color') {
      element.removeAttribute('data-backgroundcolor');
    }
    delete styles[styleName];
  }
  switch (data.shape) {
    case constants.shape.horizontalLine:
      configuration.shape = 'horizontalLine';
      configuration.tag = 'span';
      break;
    case constants.shape.verticalLine:
      configuration.shape = 'verticalLine';
      configuration.tag = 'span';
      break;
    case constants.shape.rectangle:
      configuration.shape = 'rectangle';
      configuration.code = data.code;
      configuration.borderWidth = data.borderWidth;
      configuration.borderColor = data.borderColor;
      break;
    case constants.shape.circle:
      configuration.shape = 'circle';
      configuration.code = data.code;
      configuration.borderWidth = data.borderWidth;
      configuration.borderColor = data.borderColor;
      break;
    // 圆形 和 方形 的 tag 由 type 决定
    default:
      break;
  }
  if (!configuration.tag) {
    // 圆形 和 方形 要设置额外的数据
    switch (data.type) {
      case constants.type.text:
        configuration.tag = 'span';
        configuration.type = 'text';
        break;
      case constants.type.qr:
        configuration.tag = 'img';
        configuration.type = 'qr';
        break;
      case constants.type.bar:
        configuration.tag = 'img';
        configuration.type = 'bar';
        break;
      case constants.type.img:
        configuration.tag = 'img';
        configuration.type = 'img';
        break;
      default:
        // no reach here
        break;
    }
    switch (data.sourceType) {
      case constants.sourceType.text:
        configuration.dataType = 'text';
        configuration.value = data.text;
        break;
      case constants.sourceType.param:
        configuration.dataType = 'param';
        configuration.value = data.param;
        break;
      case constants.sourceType.upload:
        configuration.dataType = 'upload';
        configuration.value = data.src;
        break;
      default:
        break;
    }
  }
  element.setAttribute('contenteditable', false);
  element.setAttribute('data-shape', configuration.shape);
  element.setAttribute('data-width', configuration.width);
  element.setAttribute('data-height', configuration.height);
  element.setAttribute('data-value', configuration.value);
  element.setAttribute('data-type', configuration.type);
  element.setAttribute('data-datatype', configuration.dataType);
  element.setAttribute('data-code', configuration.code);
  element.setAttribute('data-borderwidth', configuration.borderWidth);
  element.setAttribute('data-bordercolor', configuration.borderColor);
  // style
  setElementStyle('position', 'relative');
  setElementStyle('display', 'inline-block');
  setElementStyle('box-sizing', 'border-box');
  setElementStyle('width', `${configuration.width}${elementUnit}`);
  setElementStyle('height', `${configuration.height}${elementUnit}`);
  setElementStyle('border-width', `${configuration.borderWidth}${elementUnit}`);
  setElementStyle('border-style', `solid`);
  setElementStyle('border-color', configuration.borderColor);
  setElementStyle('border-radius', configuration.shape === 'circle' ? '50%' : '0');
  if (['horizontalLine', 'verticalLine'].includes(configuration.shape)) {
    removeElementStyle('border-shape');
    removeElementStyle('border-color');
    removeElementStyle('border-radius');
  }
  let bgc = '#';
  switch (configuration.shape) {
    case 'circle':
    case 'rectangle':
      bgc += 'c';
      break;
    case 'horizontalLine':
    case 'verticalLine':
      bgc += '0';
      break;
    default:
      bgc += '6';
      break;
  }
  switch (configuration.dataType) {
    case 'param':
    case 'text':
      bgc += '6';
      break;
    case 'upload':
      bgc += 'c';
      break;
    default:
      bgc += '0';
      break;
  }
  switch (configuration.type) {
    case 'text':
      bgc += '6';
      break;
    case 'qr':
      // element.setAttribute('alt', intl.get('hrpt.labelTemplate.view.title.cke.c.qr').d('二维码'));
      // element.setText(intl.get('hrpt.labelTemplate.view.title.cke.c.qr').d('二维码'));
      bgc += 'c';
      break;
    case 'bar':
      // element.setAttribute('alt', intl.get('hrpt.labelTemplate.view.title.cke.c.bar').d('条码'));
      // element.setText(intl.get('hrpt.labelTemplate.view.title.cke.c.bar').d('条码'));
      bgc += 'c';
      break;
    case 'img':
      // img
      // element.setAttribute('alt', intl.get('hrpt.labelTemplate.view.title.cke.c.img').d('图片'));
      // element.setText(intl.get('hrpt.labelTemplate.view.title.cke.c.img').d('图片'));
      element.setAttribute('data-src', configuration.value);
      element.removeAttribute('data-value');
      bgc += 'c';
      break;
    default:
      bgc += '0';
      break;
  }
  // // 图片的处理
  // if (['img', 'bar'].includes(configuration.type)) {
  //   element.setAttribute('width', `${configuration.width}${elementUnit}`);
  //   element.setAttribute('height', `${configuration.height}${elementUnit}`);
  // }
  // if (configuration.tag === 'span') {
  element.setText(configuration.value);
  // }
  setElementStyle('background-color', bgc);
  if (['verticalLine', 'horizontalLine'].includes(configuration.shape)) {
    element.setText(configuration.shape);
    setElementStyle('color', bgc);
    setElementStyle('overflow', 'hidden');
  }
  element.setAttribute(
    'data-style',
    Object.keys(styles)
      .map(sN => `${sN}: ${styles[sN]};`)
      .join(' ')
  );
}

export function transformElementToData(element) {
  const { constants } = labelTemplateLabelInsertConfig();
  // 获取所有的信息
  const configuration = {};
  const initialData = {};
  configuration.shape = element.data('shape');
  configuration.width = element.data('width');
  configuration.height = element.data('height');
  configuration.value = element.data('value');
  configuration.type = element.data('type');
  configuration.dataType = element.data('datatype');
  configuration.code = element.data('code');
  configuration.borderColor = element.data('bordercolor');
  configuration.borderWidth = element.data('borderwidth');
  initialData.width = +configuration.width;
  initialData.height = +configuration.height;
  switch (configuration.shape) {
    case 'horizontalLine':
      initialData.shape = constants.shape.horizontalLine;
      break;
    case 'verticalLine':
      initialData.shape = constants.shape.verticalLine;
      break;
    case 'rectangle':
      initialData.shape = constants.shape.rectangle;
      initialData.code = configuration.code;
      initialData.borderColor = configuration.borderColor;
      initialData.borderWidth = configuration.borderWidth;
      break;
    case 'circle':
      initialData.shape = constants.shape.circle;
      initialData.code = configuration.code;
      initialData.borderColor = configuration.borderColor;
      initialData.borderWidth = configuration.borderWidth;
      break;
    default:
      // no operator
      return {};
  }
  switch (configuration.type) {
    case 'text':
      initialData.type = constants.type.text;
      break;
    case 'qr':
      initialData.type = constants.type.qr;
      break;
    case 'bar':
      initialData.type = constants.type.bar;
      break;
    case 'img':
      initialData.type = constants.type.img;
      configuration.value = element.data('src');
      break;
    default:
      break;
  }
  switch (configuration.dataType) {
    case 'param':
      initialData.sourceType = constants.sourceType.param;
      initialData.param = configuration.value;
      break;
    case 'upload':
      initialData.sourceType = constants.sourceType.upload;
      initialData.src = configuration.value;
      break;
    case 'text':
      initialData.sourceType = constants.sourceType.text;
      initialData.text = configuration.value;
      break;
    default:
      break;
  }
  return initialData;
}
