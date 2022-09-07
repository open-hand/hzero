import { isNil } from 'lodash';

export function getFieldConfig({ required, editable, visible }) {
  const newFieldConfig = { visible };
  if (required !== -1) {
    newFieldConfig.required = !!required;
  }

  if (visible === 0) {
    newFieldConfig.required = false;
  }

  if (editable !== -1 && !isNil(editable)) {
    newFieldConfig.disabled = !editable;
  }
  return newFieldConfig;
}

export function getColumnsConfig({ visible, fixed, width }) {
  const newColumnsConfig = {};
  if (visible !== -1) {
    newColumnsConfig.hidden = !visible;
  }
  if (fixed === 'L') {
    newColumnsConfig.lock = 'left';
  } else if (fixed === 'R') {
    newColumnsConfig.lock = 'right';
  } else if (fixed === 'N') {
    newColumnsConfig.fixed = undefined;
  }
  if (width !== undefined) {
    newColumnsConfig.width = width;
  }
  return newColumnsConfig;
}
