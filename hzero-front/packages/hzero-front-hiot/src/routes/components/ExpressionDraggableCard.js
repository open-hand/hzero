/**
 * mode 显示样式 vertical: 垂直 horizontal，水平 horizontal 默认水平
 * expressionTitle 公式标题 默认预警公式
 * expressionFactorList 表达式因子列表
 * expression
 * addExpressionItem function 添加操作符调用的方法
 * onDragEnd function 拖放结束函数
 * clickExpressionItem function 点击表达式因子项的操作
 * handleClickDelete function 删除表达式因子项的操作
 * focusExpressionItemIndex 当前选中的表达式因子项Index
 * disabled: 是否禁用
 */
import React from 'react';
import { Row } from 'choerodon-ui/pro';
import intl from 'utils/intl';

import { DraggableArea } from 'react-draggable-tags';
import deleteBtn from '@/assets/images/delete.png';

import classNames from './Expression.less';

// 操作符列表，key用于计算器显示，value用于表达式显示， priority用于对操作符进行分类
const operationList = [
  { key: '1', value: '1', priority: 0 },
  { key: '2', value: '2', priority: 0 },
  { key: '3', value: '3', priority: 0 },
  { key: '4', value: '4', priority: 0 },
  { key: '5', value: '5', priority: 0 },
  { key: '6', value: '6', priority: 0 },
  { key: '7', value: '7', priority: 0 },
  { key: '8', value: '8', priority: 0 },
  { key: '9', value: '9', priority: 0 },
  { key: '0', value: '0', priority: 0 },
  { key: '(', value: '(', priority: 5 },
  { key: ')', value: ')', priority: 5 },
  { key: '+', value: '+', priority: 3 },
  { key: '-', value: '-', priority: 3 },
  { key: '*', value: '*', priority: 3 },
  { key: '/', value: '/', priority: 3 },
  { key: 'x^2', value: '^2', priority: 4 },
  { key: 'x^3', value: '^3', priority: 4 },
  { key: '√', value: '√', priority: 4 },
  { key: '.', value: '.', priority: 0 },
  { key: 'ln', value: 'ln', priority: 4 },
];

const logicList = [
  { key: '&', value: '&', priority: 2 },
  { key: '||', value: '||', priority: 2 },
  { key: '!', value: '!', priority: 2 },
  { key: '>', value: '>', priority: 1 },
  { key: '>=', value: '>=', priority: 1 },
  { key: '<', value: '<', priority: 1 },
  { key: '<=', value: '<=', priority: 1 },
  { key: '==', value: '==', priority: 1 },
  { key: '!=', value: '!=', priority: 1 },
];

export default class ExpressionDraggableCard extends React.Component {
  render() {
    const {
      expressionFactorList = [], // 表达式因子列表
      addExpressionItem, // 添加操作符
      onDragEnd, // 拖放结束函数
      clickExpressionItem, // 点击表达式因子项的操作
      handleClickDelete, // 删除表达式因子项的操作
      focusExpressionItemIndex = '', // 当前选中的表达式因子项index
      onlyShowObject = false, // 只显示数据点和监测指标的名称
      disabled,
      expressionTitle = intl.get('hiot.common.warn.formula').d('预警公式'),
      mode,
    } = this.props;
    const allOperatorList = mode === 'horizontal' ? operationList.concat(logicList) : operationList;
    return (
      <div className={classNames['expression-with-operation']}>
        {/* 表示式显示框 */}
        <div className={classNames['expression-content-card']}>
          <Row>
            <p
              style={{ paddingLeft: '24px', marginTop: '10px', fontSize: '20px', fontWeight: 600 }}
            >
              {expressionTitle}
            </p>
          </Row>
          {expressionFactorList.length > 0 && (
            <DraggableArea
              tags={expressionFactorList.map(ef => ({ ...ef, undraggable: disabled }))}
              disabled={disabled}
              render={({ tag, index }) => (
                <div className={classNames['expression-factor-item']}>
                  {/* 操作数操作符 */}
                  {tag.type === 'operation' && (
                    <div className={classNames['expression-operation-item']}>
                      {!disabled && (
                        <img
                          role="none"
                          alt="×"
                          className={classNames.delete}
                          src={deleteBtn}
                          onClick={() => handleClickDelete(tag)}
                        />
                      )}
                      {tag.value}
                    </div>
                  )}
                  {/* 数据点 */}
                  {(tag.type === 'property' || tag.type === 'index') && (
                    <div
                      className={
                        focusExpressionItemIndex === index
                          ? `${classNames['expression-object-item']} ${
                              classNames['expression-object-item-focus']
                            }`
                          : classNames['expression-object-item']
                      }
                      role="none"
                      onClick={e => {
                        e.stopPropagation();
                        if (!disabled) {
                          clickExpressionItem(tag, index);
                        }
                      }}
                    >
                      {/* 指标因子前部分的设备信息 */}
                      {/* {!onlyShowObject && (
                        <div className={classNames['expression-thing']} title={tag.objectName}>
                          {tag.objectName}
                        </div>
                      )} */}
                      <div
                        className={
                          onlyShowObject
                            ? `${classNames['expression-object']} ${
                                classNames['expression-object-entry']
                              }`
                            : classNames['expression-object']
                        }
                        title={tag.objectName}
                      >
                        {tag.objectName}
                      </div>
                    </div>
                  )}
                  {/* 聚合函数 */}
                  {tag.type === 'aggression' && (
                    <div className={classNames['expression-aggression-item']}>
                      <img
                        role="none"
                        alt="×"
                        className={classNames.delete}
                        src={deleteBtn}
                        onClick={() => {
                          if (!disabled) {
                            handleClickDelete(tag);
                          }
                        }}
                      />
                      {tag.description}
                    </div>
                  )}
                </div>
              )}
              onChange={tags => {
                if (!disabled) {
                  onDragEnd(tags);
                }
              }}
            />
          )}
        </div>
        {/* 计算操作符选择框 */}
        <div className={classNames['expression-operation-selection']}>
          {allOperatorList.map(operationItem => (
            <div
              role="none"
              className={classNames['expression-operation-item']}
              key={operationItem.key}
              onClick={e => {
                e.stopPropagation();
                if (!disabled) {
                  addExpressionItem('operation', operationItem);
                }
              }}
            >
              {operationItem.key}
            </div>
          ))}
        </div>
      </div>
    );
  }
}
