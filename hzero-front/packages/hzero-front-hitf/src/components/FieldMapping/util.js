import _ from 'lodash';
import uuidv4 from 'uuid/v4';

export const pxToNum = (text) => {
  if (typeof text === 'string' && text.match(/(px)$/)) {
    const data = text.replace(/(px)$/, '');
    return Number(data);
  }
  return NaN;
};

export const getOffset = (ele) => {
  let element = ele;
  let top = 0;
  let left = 0;
  while (element) {
    top += element.offsetTop;
    left += element.offsetLeft;
    element = element.offsetParent;
  }
  return {
    left,
    top,
  };
};
// 根据入参relation生成含线条坐标位置的relation
export const calCoord = (data = [], FieldMapping) => {
  const baseXY = getOffset(FieldMapping.sourceCom.boxEle.offsetParent);
  const {
    source: { data: sourceData },
    target: { data: targetData },
  } = FieldMapping.props;
  return data.map((item) => {
    const temp = item;
    let sourceNum = 0;
    let targetNum = 0;
    const sourceEle = FieldMapping.sourceCom.boxEle.querySelector('.column-content');
    const targetEle = FieldMapping.targetCom.boxEle.querySelector('.column-content');
    const sourceName = temp.source.key;
    const targetName = temp.target.key;
    sourceNum = findNodeIndex(sourceData, sourceName);
    targetNum = findNodeIndex(targetData, targetName);
    const sourcePoint =
      sourceEle.getElementsByTagName('li')[sourceNum] &&
      sourceEle.getElementsByTagName('li')[sourceNum].querySelector('.column-icon');
    const targetPoint =
      targetEle.getElementsByTagName('li')[targetNum] &&
      targetEle.getElementsByTagName('li')[targetNum].querySelector('.column-icon');
    // lines-area的position为absolute时，使用注释的代码
    // temp.source.x = getOffset(sourcePoint).left - baseXY.left + 6;
    // position为relation,且lines-area的宽度为200px
    temp.source.x = 0;
    temp.source.y = getOffset(sourcePoint).top - baseXY.top + 6;
    // position为relation,且lines-area的宽度为200px
    temp.target.x = 190;
    // lines-area的position为absolute时，使用注释的代码
    // temp.target.x = getOffset(targetPoint).left - baseXY.left + 3;
    temp.target.y = getOffset(targetPoint).top - baseXY.top + 6;
    return temp;
  });
};

/**
 * 获取节点的index
 */
const findNodeIndex = (list, key) => {
  let findIndex = 0;
  const recursion = (data) => {
    data.forEach((item) => {
      if (item.key === key) {
        findIndex = item.index;
      } else if (item.children) {
        recursion(item.children);
      }
    });
  };
  recursion(list);
  return findIndex;
};

// json的key提取
export const jsonParse = (inputData) => {
  let index = 0;
  const recursion = (
    data,
    iterate = [],
    level = 1,
    parentName = null,
    parentPath = '',
    parentTypePath = ''
  ) => {
    let keys = iterate;
    const dot = _.isEmpty(parentPath) ? '' : '.';
    const formatData = mergeArray(data);
    keys = _.keys(formatData).map((item) => {
      const fieldType = getFieldType(formatData[item]);
      const temp = {
        level,
        parentName,
        parentPath,
        parentTypePath,
        name: item,
        type: fieldType,
        index: index++,
        key: uuidv4(),
      };
      if (_.isObject(formatData[item])) {
        _.set(
          temp,
          'children',
          recursion(
            formatData[item],
            keys,
            level + 1,
            item,
            `${parentPath}${dot}${item}`,
            `${parentTypePath}${dot}${fieldType}`
          )
        );
      }
      return temp;
    });
    return keys;
  };
  const result = recursion(inputData);
  return result;
};

// 合并对象数组，生成map
export const mergeArray = (param) => {
  if (!_.isArray(param)) {
    return param;
  }
  let obj = {};
  param.forEach((record) => {
    obj = { ...obj, ...record };
  });
  return obj;
};

export const getFieldType = (data) => {
  return /^\[object\s(.*)\]$/.exec(Object.prototype.toString.call(data))[1];
};

/**
 * 根据脚本script生成连线
 */
export const transformLine = (script, sourceData = [], targetData = []) => {
  // 去除\n,\t,用于字符串匹配
  let temp = _.replace(script, /\s+/g, '');
  temp = _.replace(temp, /[\r\n]/g, '');
  const splitList = _.split(temp, '---');
  if (splitList.length !== 2) {
    // eslint-disable-next-line no-console
    console.error('valid script!');
    return;
  }
  const destructList = destructStript(splitList[1]);
  let relations = [];
  let actualKeyIndex = -1;
  const recursion = (strList, parentValue) => {
    for (let index = 0; index < strList.length; ) {
      actualKeyIndex++;
      const str = strList[index];
      const splitStrList = _.words(str, /[^:]+/g);
      if (splitStrList.length === 2) {
        const key = splitStrList[0];
        const value = splitStrList[1];
        if (value.includes('=>array')) {
          const matchList = findMatchList(index, strList, '{', '}');
          index += matchList.length;
          const newValue = _.replace(value, '=>array', '');
          recursion(matchList, mergeValue(parentValue, newValue));
        } else if (value === '[') {
          const matchList = findMatchList(index, strList, '{', '}');
          index += matchList.length;
          recursion(matchList, value);
        } else {
          relations.push({
            sourcePath: value.includes('01') ? mergeValue(parentValue, value) : value,
            targetPath: findParentKey(actualKeyIndex, key, destructList),
          });
        }
      }
      index++;
    }
  };
  recursion(destructList, '');
  relations = relations.map((relation) => {
    const { sourcePath, targetPath } = relation;
    const formatSourcePath = deleteFirstName(sourcePath);
    const sourceNode = findNodeByPath(formatSourcePath, sourceData);
    const targetNode = findNodeByPath(targetPath, targetData);
    return {
      source: sourceNode || {},
      target: targetNode || {},
    };
  });
  return relations;
};

/**
 * 查找脚本中当前字段的父字段
 */
const findParentKey = (keyIndex, key, strList) => {
  const keys = [key];
  for (let index = keyIndex - 1; index >= 0; ) {
    const str = strList[index];
    if (str === '{' && index > 0) {
      const matchList = findMatchList(index - 1, strList, '{', '}', '}]');
      // 找到key的上一个parentKey
      if (keyIndex < matchList.length + index) {
        const temp = strList[index - 1];
        const splitStrList = temp.split(':');
        if (splitStrList.length === 2) {
          keys.unshift(splitStrList[0]);
        }
      }
    }
    index--;
  }
  return keys.toString().replace(/,/g, '.');
};

/**
 * 更加路径找到节点
 */
const findNodeByPath = (path, treeList) => {
  const paths = path.split('.');
  const { length } = paths;
  let currentNode = {};
  const findNode = (list = [], count = 0) => {
    if (paths[count]) {
      const node = _.head(list.filter((item) => item.name === paths[count]));
      if (count === length - 1) {
        currentNode = node;
      } else if (node && node.children) {
        findNode(node.children, count + 1);
      }
    }
  };
  findNode(treeList, 0);
  return currentNode;
};

/**
 * 删除第一个name
 */
const deleteFirstName = (value) => {
  return value
    .split('.')
    .filter((_val, valIndex) => valIndex !== 0)
    .toString()
    .replace(/,/g, '.');
};

/**
 * value合并
 */
const mergeValue = (parentValue, value) => {
  if (_.isEmpty(parentValue)) {
    return value;
  }
  const filterValue = deleteFirstName(value);
  return `${parentValue}.${filterValue}`;
};

/**
 * 找到和“{”匹配的“}”;
 * 找到和“[”匹配的“]”;
 */
const findMatchList = (leftIndex, list, leftFlag, rightFlag, specialFlag) => {
  let level = 0;
  const matchList = [];
  for (let index = leftIndex + 1; index < list.length; index++) {
    const str = list[index];
    if (str === leftFlag) {
      level++;
    }
    if (level > 0) {
      matchList.push(str);
    }
    if (str === rightFlag || str === specialFlag) {
      level--;
    }
    if (level === 0) {
      return matchList;
    }
  }
  return matchList;
};

/**
 * 脚本字符串解析，生成解析数组
 */
const destructStript = (script) => {
  let destructionList = [];
  // 过滤出左{结构
  const removeLeftList = _.words(script, /[^{]+/g);
  removeLeftList.forEach((str) => {
    destructionList.push('{');
    destructionList.push(str);
    // 过滤出“[”结构
    // const removeSubLeftList = str.split('[');
    // destructionList.push(removeSubLeftList[0]);
    // if (removeSubLeftList.length === 2) {
    //   destructionList.push('[');
    // }
  });
  const lastStr = _.last(destructionList);
  const lastIndex = _.lastIndexOf(destructionList, lastStr);
  _.remove(destructionList, (_value, index) => index === lastIndex);
  // 过滤出右}结构
  const removeRightList = _.words(lastStr, /[^}]+/g);
  removeRightList.forEach((str) => {
    destructionList.push(str);
    destructionList.push('}');
  });
  let temps = [];
  destructionList = destructionList
    .filter((str) => str !== ',')
    .forEach((str) => {
      // 匹配类似于map(xxx, xxx) -> 结构，替换成=>array
      const formatStr = _.replace(str, /map\(.*?\)->/g, '=>array');
      const removeDotList = _.words(formatStr, /[^,]+/g);
      temps = [...temps, ...removeDotList];
      return formatStr;
    });
  return temps;
};

/**
 * 获取同行关系
 */
export const getSameLineRel = (sourceData = [], targetData = []) => {
  const relations = [];
  const recursion = (sources, targets) => {
    sources.forEach((source, sourceIndex) => {
      const target = targets[sourceIndex];
      if (target && !source.children && !target.children) {
        relations.push({ source, target });
      }
      if (source.children && target && target.children) {
        recursion(source.children, target.children);
      }
    });
  };
  recursion(sourceData, targetData);
  return relations;
};

/**
 * 获取同名关系
 */
export const getSameNameRel = (sourceData = [], targetData = []) => {
  const relations = [];
  const recursion = (sources = [], targets = []) => {
    sources.forEach((source, sourceIndex) => {
      const target = targets.filter((item) => item.name === source.name)[0];
      if (target && !source.children && !target.children) {
        relations.push({ source, target });
      }
      if (source.children && targets[sourceIndex]) {
        recursion(source.children, targets[sourceIndex].children);
      }
    });
  };
  recursion(sourceData, targetData);
  return relations;
};

/**
 * 连线关系转换为脚本
 */
export const getRelationScript = (relations = []) => {
  const assembleList = [];
  relations.forEach((relation) => {
    const { source, target } = relation;
    const {
      parentPath: sourceParentPath,
      parentTypePath: sourceParentTypePath,
      name: sourceName,
      type: sourceType,
    } = source;
    const {
      parentPath: targetParentPath,
      parentTypePath: targetParentTypePath,
      name: targetName,
    } = target;
    const assembleData = relationAssemble(
      targetParentPath,
      targetParentTypePath,
      targetName,
      `${sourceParentPath}.${sourceName}`,
      `${sourceParentTypePath}.${sourceType}`
    );
    assembleList.push(assembleData);
  });
  if (_.isEmpty(assembleList)) {
    return insertScriptHeader('');
  }
  const relationScript = assembleList[0];
  for (let index = 1; index < assembleList.length; index++) {
    const assemble = assembleList[index];
    _.merge(relationScript, assemble);
  }
  return translateObject(relationScript);
};

/**
 * 关系数组组装
 * @param {string} parentPath 目标字段的父路径
 * @param {string} parentTypePath 目标字段类型的父路径
 * @param {string} name 目标字段
 * @param {string} value 目标字段对应的来源字段的全路径
 * @param {string} sourceParentTypePath 目标字段类型对应的来源字段的类型全路径
 */
const relationAssemble = (
  parentPath = '',
  parentTypePath = '',
  name,
  value,
  sourceParentTypePath
) => {
  const assembleData = {};
  const path = [];
  if (_.isEmpty(parentPath)) {
    path.push(name);
    _.set(assembleData, path, value);
    return assembleData;
  }
  const parentPathList = parentPath.split('.');
  const parentTypePathList = parentTypePath.split('.');
  parentPathList.forEach((item, index) => {
    const type = parentTypePathList[index];
    path.push(item);
    if (type === 'Array') {
      path.push(0);
    }
  });
  path.push(name);
  _.set(assembleData, path, `${value}=>${sourceParentTypePath}`);
  return assembleData;
};

/**
 * 对结构进行翻译
 * REPLACE_FLAG_{level} 占位符， level代表层级
 */
const translateObject = (relationScript) => {
  let funcContent = `REPLACE_FLAG_0_0`;
  /**
   * @param {string} name 传递map函数的key
   * @param {Object} script 子节点信息
   * @param {Number} level 遍历的层级
   * @param {Number} preIndex 父节点所在的层级的下标
   * @param {string} path 递归时生成的目标字段的路径
   * @param {Number} mapTimes 同一个map函数下是否再包含其它map函数
   */
  const recursion = (name, script, level = 0, preIndex = 0, path = '', mapTimes = 0) => {
    const formatData = mergeArray(script);
    _.keys(formatData).forEach((key, index) => {
      const relation = formatData[key];
      const dot = level === 0 ? '' : ',';
      const newLine = level === 0 ? '' : '\n';
      const targetPath = _.isEmpty(path) ? key : `${path}.${key}`;
      const { renderType, mapFiled, prefixField = '', prefixFields = '' } = getRenderType(
        targetPath,
        relationScript
      );
      if (renderType === 'mapFunc') {
        // map函数中如果出现其它map函数，则prefixField需加上01
        let prefix = mapTimes > 0 ? `${prefixField}01` : prefixFields;
        prefix = _.isEmpty(prefixField) ? '' : `${prefix}.`;
        // 同一层级且都为数组，生成map函数
        const mapStr = `${newLine}${createTab(
          level
        )}${key}: ${prefix}${mapFiled} map (${mapFiled}01, ${_.camelCase(
          `indexOf ${mapFiled}01`
        )}) -> {REPLACE_FLAG_${level + 1}_${index}\n${createTab(
          level
        )}}${dot}REPLACE_FLAG_${level}_${preIndex}`;
        funcContent = _.replace(funcContent, `REPLACE_FLAG_${level}_${preIndex}`, mapStr);
        recursion(
          { key: `${mapFiled}01`, level },
          relation,
          level + 1,
          index,
          targetPath,
          mapTimes + 1
        );
      } else if (renderType === 'Array') {
        // 生成数组对象结构
        const arrayStr = `${newLine}${createTab(level)}${key}: [{REPLACE_FLAG_${
          level + 1
        }_${index}\n${createTab(level)}}]${dot}REPLACE_FLAG_${level}_${preIndex}`;
        funcContent = _.replace(funcContent, `REPLACE_FLAG_${level}_${preIndex}`, arrayStr);
        recursion(name, relation, level + 1, index, targetPath, mapTimes);
      } else if (renderType === 'Object') {
        // 生成map对象结构
        const str = `${newLine}${createTab(level)}${key}: {REPLACE_FLAG_${
          level + 1
        }_${index}\n${createTab(level)}}${dot}REPLACE_FLAG_${level}_${preIndex}`;
        funcContent = _.replace(funcContent, `REPLACE_FLAG_${level}_${preIndex}`, str);
        recursion(name, relation, level + 1, index, targetPath, mapTimes);
      } else {
        // 生成字符串结构
        const replacedPath = replacePath(name, relation);
        const str = `${newLine}${createTab(
          level
        )}${key}: ${replacedPath}${dot}REPLACE_FLAG_${level}_${preIndex}`;
        funcContent = _.replace(funcContent, `REPLACE_FLAG_${level}_${preIndex}`, str);
      }
    });
  };
  recursion('', relationScript, 0, 0, '');
  funcContent = deletePlaceHolder(funcContent, relationScript);
  funcContent = replaceEmpty(funcContent);
  funcContent = insertScriptHeader(funcContent);
  return funcContent;
};

/**
 * 获取当前字段对应的来源字段的类型
 * @param {string} fieldPath 需要查询字段的当前路径
 * @param {Object} relationScript 脚本
 * @returns { renderType, mapFiled, prefixField } renderType: 渲染的类型，mapFiled需要生成map函数的当前键，prefixField: mapFiled的上一个字段
 */
const getRenderType = (fieldPath, relationScript) => {
  let tempScript = relationScript;
  const paths = fieldPath.split('.');
  // 获取需查询字段的孩子，从其孩子开始遍历
  paths.forEach((path) => {
    const obj = mergeArray(tempScript);
    tempScript = obj[path];
  });
  // 需查询字段的类型
  const targetFieldType = getFieldType(tempScript);
  if (targetFieldType !== 'Array' && targetFieldType !== 'Object') {
    return targetFieldType;
  }
  const sameLevelTypes = [];
  const sameLevelFields = [];
  // 单个前缀字段
  let prefixField = '';
  // 多个前缀字段
  let prefixFields = '';
  const recursion = (script, level = 0) => {
    const formatData = mergeArray(script);
    _.keys(formatData).forEach((key) => {
      const relation = formatData[key];
      if (_.isObject(relation)) {
        recursion(relation, level + 1);
      } else {
        const [fieldNamePath, fieldTypePath] = relation.split('=>');
        const fieldList = fieldNamePath.split('.');
        const fieldTypeList = fieldTypePath.split('.');
        // 获取fieldPath子属性下所有level为paths.length的字段类型
        sameLevelTypes.push(fieldTypeList[paths.length - 1]);
        sameLevelFields.push(fieldList[paths.length - 1]);
        prefixField = fieldList[paths.length - 2];
        prefixFields = _.join(
          fieldList.filter((_field, index) => index <= paths.length - 2),
          '.'
        );
      }
    });
  };
  recursion(tempScript);
  const sourceFieldType = sameLevelTypes[0];
  // 只判断同一层级下双方的字段类型是否都为Array，是的话就返回生成map的标记
  if (targetFieldType === 'Array' && sourceFieldType === 'Array') {
    return { renderType: 'mapFunc', mapFiled: sameLevelFields[0], prefixField, prefixFields };
  } else {
    return { renderType: targetFieldType };
  }
};

/**
 * 删除空结构，[{}], {}，map 函数
 */
const replaceEmpty = (content) => {
  let temp = content;
  // 去除\n,\t,用于字符串匹配
  let str = _.replace(content, /\s+/g, '');
  str = _.replace(str, /[\r\n]/g, '');
  const originContents = content.split(',');
  const offContents = str.split(',');
  offContents.forEach((item, index) => {
    if (item.includes('[{}]')) {
      // 去除空数组
      temp = _.replace(temp, `${originContents[index]},`, '');
    } else if (item.includes(')->{}')) {
      // 去除空函数
      temp = _.replace(temp, `${originContents[index - 1]},`, '');
      temp = _.replace(temp, `${originContents[index]},`, '');
    } else if (item.includes('{}')) {
      // 去除空map
      temp = _.replace(temp, `${originContents[index]},`, '');
    }
  });
  return temp;
};

/**
 * 替换路径关系
 */
const replacePath = (name, wholePath) => {
  const realPath = wholePath.split('=>')[0];
  const wholePaths = realPath.split('.');
  const { key = '', level } = name;
  const temp = key.substring(0, key.length - 2);
  // 是否替换路径
  const isReplace = _.endsWith(key, '01') && temp === wholePaths[level];
  if (!isReplace) {
    return realPath;
  }
  let paths = wholePaths.map((path, index) => {
    if (index === level) {
      return key;
    } else {
      return path;
    }
  });
  paths = paths.filter((_o, index) => index > level - 1);
  return paths.toString().replace(/,/g, '.');
};

/**
 * 剔除占位符
 */
const deletePlaceHolder = (replacedStr, relationScript) => {
  let str = replacedStr;
  if (str === 'REPLACE_FLAG_0_0') {
    return '';
  }
  const recursion = (script, level = 0, preIndex = 0) => {
    const formatData = mergeArray(script);
    _.keys(formatData).forEach((key, index) => {
      const relation = formatData[key];
      str = _.replace(str, `REPLACE_FLAG_${level}_${index}`, '');
      str = _.replace(str, `REPLACE_FLAG_${level}_${preIndex}`, '');
      if (_.isObject(relation)) {
        recursion(relation, level + 1, index);
      }
    });
  };
  recursion(relationScript);
  // 去除空行
  str = _.replace(str, /${tab}(${tab})*( )*(${tab})*${tab}/g, '\n');
  // 去除最外层结构 TARGET:
  str = _.replace(str, 'TARGET: ', '');
  return str;
};

/**
 * 插入脚本头
 * 先固定为
 * %dw 2.0
 * output application/json
 * ---
 */
export const insertScriptHeader = (script) => {
  const scriptHeader = '%dw 2.0\t\noutput application/json\t\n---\t\n';
  const newScript = `${scriptHeader}${script}`;
  return newScript;
};

/**
 * 生成tab建
 * @param {number} level tab个数
 */
const createTab = (level) => {
  let tabs = '';
  for (let index = 0; index < level; index++) {
    tabs = `${tabs}\t`;
  }
  return tabs;
};
