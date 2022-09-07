/**
 * utils.js
 * @date 2018/11/5
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

/* eslint-disable no-param-reassign */

import { forEach, set, toPath } from 'lodash';
import { attributeNameProp } from '../config';

const attrFlagField = 'attributeFlag';
const templateNoConfigAttr = [
  '_token',
  '_tls',
  'objectVersionNumber',
  'enabledFlag',
  'templateId',
  'templateCode',
  'templateType',
  'description',
  'orderSeq',
  'fields',
  // 后端传过来的数据
  'rootNode',
  'childNode',
];
const templateNoConfigAttrMap = {};
forEach(templateNoConfigAttr, attrName => {
  templateNoConfigAttrMap[attrName] = true;
});

/**
 * 判断是否是 attr
 * @param {Object} attr - 判断是不是 attribute 的对象
 * @returns {boolean}
 */
function isAttribute(attr) {
  return attr[attrFlagField];
}

/**
 * 将 templates config 的值 设置到 头数据中
 */
export function dealConfig(templates) {
  forEach(templates, template => {
    forEach(template.config, attr => {
      const attrPath = toPath(attr);
      set(template, attrPath, {
        ...attr,
        [attrFlagField]: true,
        [attributeNameProp]: attrPath[attrPath.length - 1],
      });
    });
    forEach(template.fields, field => {
      forEach(field.config, attr => {
        const attrPath = toPath(attr);
        set(field, attrPath, {
          ...attr,
          [attrFlagField]: true,
          [attributeNameProp]: attrPath[attrPath.length - 1],
        });
      });
      delete field.config;
    });
    delete template.config;
  });
}

/**
 * 将 templates 行数据的值 重新设置到 行数据中
 * @param templates
 */
export function parseConfig(templates) {
  forEach(templates, template => {
    forEach(template, (attr, attrK) => {
      if (!templateNoConfigAttrMap[attrK]) {
        setConfig(template, attrK, attr);
      }
    });
  });
}

function setConfig(component, attrK, attr) {
  if (isAttribute(attr)) {
    component[attrK] = attr;
  } else {
    forEach(attr, (v, k) => {
      setConfig(component, `[${attrK}][${k}]`, v);
    });
  }
}
