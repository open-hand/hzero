import uuid from 'uuid/v4';
/**
 * 不做任何操作 仅返回 undefined
 */
export function noop() {}

/**
 * 返回 uuid 生成的新的 id
 * @return {string}
 */
export function getNewUUIDKey() {
  return uuid();
}

/**
 * 对保存 field 公共的处理
 * 会改变 field
 * @param {Object} field
 */
export function commonParseForField(template, field) {
  const updateField = field;
  if (updateField.isCreate) {
    delete updateField.isCreate;
    delete updateField.fieldId;
    delete updateField.rootNode;
    delete updateField.childNode;
  }
  return updateField;
}
