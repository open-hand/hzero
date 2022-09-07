/**
 * showAggression 是否显示聚合函数
 * factorList 所有因子集合
 * focusFactorItem 当前选中的因子对象
 * focusFactorItemType 当前选中的因子对象的类型 property/index
 * clickFactorItem： function 选中因子项方法
 * factorTitle: 因子标题 默认数据点
 * addExpressionItem function 添加聚合函数
 * onNumberChange function 修改统计时间段值
 * countTimeValue 聚合函数统计时间段
 * disabled: 是否禁用
 */
import React from 'react';
import intl from 'utils/intl';
import { Icon, Tooltip, Row } from 'choerodon-ui/pro';
import { InputNumber } from 'choerodon-ui';

import classNames from './Expression.less';

// 聚合函数列表，key作为唯一标识，value用于显示
const aggressionList = [
  { key: 'AVERAGE', value: intl.get('hiot.components.average').d('平均') },
  { key: 'MIN', value: intl.get('hiot.components.max').d('最大') },
  { key: 'MAX', value: intl.get('hiot.components.min').d('最小') },
  { key: 'SUM', value: intl.get('hiot.components.sum').d('总和') },
];

export default class FactorCard extends React.Component {
  render() {
    const {
      // showAggression = false, // 是否显示聚合函数
      // modalType = '', // thing设备 template 设备模板
      factorList = [], // 因子选择数据
      focusFactorItem = {}, // 当前选中的因子对象
      focusFactorItemType = '', // 当前选中的因子对象的类型 property/index
      clickFactorItem, // 选中因子项
      disabled,
      factorTitle = intl.get('hiot.factor.data.point').d('数据点'),
      addExpressionItem, // 添加聚合函数
      onNumberChange, // 修改统计时间段值
      countTimeValue,
      // thingId = '', // 当前选中的thing的id
      // correlationFactorList = [], // 相关因子列表中该物或物模板的选中因子数据集
    } = this.props;

    return (
      <div className={classNames['factor-selection']}>
        {/* 数据点和监控指标选择框 */}
        <div className={classNames['factor-card']}>
          <Row>
            <p style={{ paddingLeft: '24px', fontSize: '20px', fontWeight: 600 }}>{factorTitle}</p>
          </Row>
          {/* 显示数据点 */}
          {factorList &&
            factorList.length > 0 &&
            factorList.map(propertyItem => {
              let factorClassName = classNames['correlation-factor-item'];
              if (
                focusFactorItem.propertyModelId === propertyItem.propertyModelId &&
                focusFactorItemType === 'property'
              ) {
                // 是否是当前选中的数据点/监测指标
                factorClassName += ` ${classNames['correlation-factor-item-focus']}`;
              }
              return (
                <div
                  role="none"
                  key={propertyItem.propertyModelId}
                  title={propertyItem.propertyModelName}
                  style={{ maxWidth: '400px' }}
                  className={factorClassName}
                  onClick={e => {
                    e.stopPropagation();
                    if (!disabled) {
                      clickFactorItem(propertyItem, 'property');
                    }
                  }}
                >
                  {propertyItem.propertyModelName}
                </div>
              );
            })}
          {/* 显示设备 */}
          {/* {modalType === 'thing' &&
          factorList &&
          factorList.length > 0 &&
          factorList.map(propertyItem => {
            let factorClassName = classNames['correlation-factor-item'];
            selectedFactorList.map(item => {
              if (item.objectId === propertyItem.objectId && item.type === 'property') {
                // 表达式中有使用
                factorClassName += ` ${classNames['correlation-factor-item-select']}`;
              }
              return factorClassName;
            });
            if (
              focusFactorItem.objectId === propertyItem.objectId &&
              focusFactorItemType === 'property'
            ) {
              // 是否是当前选中的数据点/监测指标
              factorClassName += ` ${classNames['correlection-factor-item-focus']}`;
            }
            return (
              <div
                role="none"
                key={propertyItem.objectId}
                title={propertyItem.objectName}
                style={{ maxWidth: '400px' }}
                className={factorClassName}
                onClick={e => {
                  e.stopPropagation();
                  if (!disabled) {
                    clickFactorItem(propertyItem, 'property');
                  }
                }}
              >
                {propertyItem.objectName}
              </div>
            );
          })} */}
          {/* 显示监测指标 */}
          {/* {factorList.index &&
          factorList.index.length > 0 &&
          factorList.index.map(indexItem => {
            let factorClassName = classNames['correlation-factor-item'];
            selectedFactorList.map(item => {
              if (item.objectId === indexItem.indexId && item.type === 'index') {
                // 表达式中有使用
                factorClassName += ` ${classNames['correlation-factor-item-select']}`;
              }
              return factorClassName;
            });
            if (
              focusFactorItem.indexId === indexItem.indexId &&
              focusFactorItemType === 'index'
            ) {
              // 是否是当前选中的数据点/监测指标
              factorClassName += ` ${classNames['correlection-factor-item-focus']}`;
            }
            return (
              <div
                role="none"
                key={indexItem.indexId}
                title={indexItem.name}
                style={{ maxWidth: '400px' }}
                className={factorClassName}
                onClick={e => {
                  e.stopPropagation();
                  if (!disabled) {
                    clickFactorItem(indexItem, 'index');
                  }
                }}
              >
                {indexItem.name}
              </div>
            );
          })} */}
        </div>
        {/* 聚合函数操作框 */}
        <div className={classNames['factor-aggression']}>
          <div className={classNames['aggression-list']}>
            {aggressionList.map(aggressionItem => (
              <div
                role="none"
                className={classNames['expression-aggression-item']}
                key={aggressionItem.key}
                onClick={e => {
                  e.stopPropagation();
                  if (!disabled) {
                    addExpressionItem('aggression', aggressionItem);
                  }
                }}
              >
                {aggressionItem.value}
              </div>
            ))}
            <Tooltip
              title={intl
                .get('hiot.component.agg-func.tip')
                .d('聚合函数只能作用于单个数据点或监测指标，不支持聚合嵌套')}
            >
              <Icon type="question-circle" className={classNames['tips-icon']} />
            </Tooltip>
          </div>
          <div className={classNames['aggression-parameter']}>
            <InputNumber
              disabled={disabled}
              label={`${intl.get('hiot.component.count.time').d('统计时间段')}(s):`}
              min={1}
              max={86400}
              value={countTimeValue}
              precision={0}
              onChange={onNumberChange}
            />
          </div>
        </div>
      </div>
    );
  }
}
