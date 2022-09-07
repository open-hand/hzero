/**
 * mode: vertical: 垂直 horizontal：水平 horizontal
 * factorTitle: 因子卡片标题，默认值 数据点
 * expressionTitle：表达式公式标题 默认值 预警公式
 * isReadOnly：是否只读
 * data：公式因子
 * initialExpression：初始化公式
 * onRef：引用
 * predictCode：
 * dataValidatePass：
 */
import React, { PureComponent } from 'react';
import { Row, Col, TextField, Form, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import intl from 'utils/intl';
import notification from 'utils/notification';
import request from 'utils/request';
import { getCurrentOrganizationId } from 'utils/utils';
import { API_PREFIX, CODE_UPPER_REG } from '@/utils/constants';
import ExpressionDraggableCard from '@/routes/components/ExpressionDraggableCard';
import FactorCard from '@/routes/components/FactorCard';
import Exchange from '@/routes/components/Exchange';

export default class WarningRule extends PureComponent {
  constructor(props) {
    super(props);
    const {
      DS,
      initialExpression = [],
      predictCode,
      formCompData = {},
      dataValidatePass = () => true,
      dsFields = [],
    } = props;
    this.state = {
      // modalType: '', // Modal是设备（device）还是设备模板（template）
      focusFactorItem: {}, // 当前选中的数据点和监控指标
      focusFactorItemType: '', // 当前选中的相关因子项是数据点（property）还是监控指标（index）
      expressionFactorList: initialExpression, // 编辑的表达式数据(包括数据点、操作符、操作数)
      focusExpressionItemIndex: '', // 当前选中的表达式因子项对象在表达式列表中的index，便于删除
      expression: this.handleExpression(initialExpression),
      countTimeValue: 60, // 统计时间段值
      // expressionCorrelationList: [], // 编辑的表达式中的相关因子数据
      // focusThingItem: {}, // 当前选中的物/物模板对象
      // factorList: {}, // 物/物模板的数据点、监测指标数据
      // treeNodeList: [], // treeNode的key、id、name属性集，用于搜索匹配扩展
      // deviceTmpltSearch: '', // 物模板搜索值
      // thingData: [], // 物模板数据
      // treeData: [], // 带有key属性的treeData
      // autoExpandParent: true,
      // selectedKeys: [],
    };
    this.expressionFactorIdCount = 0; // 表达式的因子Id
    this.predictDS =
      DS ||
      new DataSet({
        name: 'predictCode',
        fields: [
          {
            name: 'predictCode',
            type: 'string',
            required: true,
            validator: (changedValue) => {
              const repeatNotice = intl.get('hiot.data.point.predictCode.repeat').d('编码重复');
              if (dataValidatePass(changedValue)) {
                return request(
                  `${API_PREFIX}/v1/${getCurrentOrganizationId()}/thing-models/predict-rule-code`,
                  { query: { predictCode: changedValue } }
                ).then((isRepeat) => !isRepeat || repeatNotice);
              }
              return repeatNotice;
            },
            pattern: CODE_UPPER_REG,
            defaultValidationMessages: {
              patternMismatch: intl
                .get('hiot.common.view.validation.codeMsg')
                .d('全大写及数字，必须以字母、数字开头，可包含“_”'),
            },
          },
          ...dsFields,
        ],
      });
    this.predictDS.create({ predictCode, ...formCompData });
  }

  componentDidMount() {
    const { onRef } = this.props;
    if (onRef) {
      onRef(this);
    }
  }

  // 点击数据点或监测指标
  @Bind()
  clickFactorItem(item, type) {
    this.setState({
      focusFactorItem: item,
      focusFactorItemType: type,
    });
  }

  // 将当前选中的数据点或监测指标添加到表达式中
  @Bind()
  moveUp() {
    const { focusFactorItemType, focusFactorItem } = this.state;
    if (focusFactorItemType && focusFactorItem) {
      this.addExpressionItem(focusFactorItemType, focusFactorItem);
    }
  }

  /**
   * 处理预警公式的因子
   * @param expressionFactorList 预警公式数组
   * @returns {{formulaStr: *, dataPointsStr: *}} 处理后的结果
   */
  @Bind()
  handleExpression(expressionFactorList) {
    const dataPointsArr = [];
    let formulaStr = '';
    expressionFactorList.forEach((expressionItem) => {
      const { objectId, type, value, objectName, guid } = expressionItem;
      if (type === 'property') {
        dataPointsArr.push(objectName);
        // 设备模板没有guid，设备管理有
        formulaStr += `\${p_${guid || objectId}}`;
      } else {
        formulaStr += value;
      }
    });
    const dataPointsStr = dataPointsArr.join(';');
    return {
      formulaStr,
      dataPointsStr,
    };
  }

  // 添加选中的数据点到表达式对象列表中
  @Bind()
  addExpressionItem(expressionItemType, expressionItem) {
    // 获取 expressionFactorList 中最大的Id值
    const { expressionFactorList } = this.state;
    if (isEmpty(expressionFactorList)) {
      this.expressionFactorIdCount += 1;
    } else {
      const maxId = Math.max(...expressionFactorList.map(({ id }) => id));
      this.expressionFactorIdCount = maxId + 1;
    }
    const temp = {
      type: expressionItemType,
      id: this.expressionFactorIdCount,
    };
    // 聚合函数
    if (expressionItemType === 'aggression') {
      const { countTimeValue } = this.state;
      temp.value = expressionItem.key;
      temp.parameter = countTimeValue;
      temp.description = `${expressionItem.value}[${countTimeValue}s]`;
    } else if (expressionItemType === 'property') {
      // 数据点或监控指标index
      temp.thingId = null; // thingId 传null
      const { propertyModelId, propertyModelName, guid = null } = expressionItem;
      temp.objectId = propertyModelId; // 数据点
      temp.objectName = propertyModelName;
      temp.guid = guid; // guid传 null
    } else {
      // 操作符
      const { value, priority } = expressionItem;
      temp.value = value;
      temp.priority = priority;
    }
    const expressionList = [...expressionFactorList];
    expressionList.push(temp);
    this.setState({
      expressionFactorList: expressionList,
      expression: this.handleExpression(expressionList),
    });
  }

  // 将当前选中的数据点或监测指标从表达式中移除
  @Bind()
  moveDown() {
    const { focusExpressionItemIndex, expressionFactorList } = this.state;
    if (focusExpressionItemIndex !== '') {
      // 将表达式项删除
      const expressionList = [...expressionFactorList];
      expressionList.splice(focusExpressionItemIndex, 1);
      this.setState({
        expressionFactorList: expressionList,
        focusExpressionItemIndex: '',
        expression: this.handleExpression(expressionList),
      });
    }
  }

  // 修改统计时间段值
  onNumberChange = (value) => {
    this.setState({ countTimeValue: value });
  };

  // 点击表达式项进行的操作
  @Bind()
  clickExpressionItem(item, index) {
    if (item.type === 'property') {
      // 选中该表达式项
      this.setState({
        focusExpressionItemIndex: index,
      });
      /* if (this.state.modalType === 'thing') {
        if (this.state.focusThingItem.id !== item.thingId) {
          const thingItem = {
            id: item.thingId,
            name: item.thingName,
            thingTmpltId: item.thingId,
          };
          this.setState({
            focusThingItem: thingItem,
            // factorList: {}, // 清空factorList
            focusFactorItem: {}, // 清空当前选中的数据点和监控指标
            focusFactorItemType: '',
          });
          this.getFactorData(thingItem.id);
        }
        const temp = this.state.treeNodeList.find(thingItem => thingItem.id === item.thingId);
      } */
    } else {
      // 将表达式项删除
      const { expressionFactorList } = this.state;
      const expressionList = [...expressionFactorList];
      expressionList.splice(index, 1);
      this.setState({
        expressionFactorList: expressionList,
        expression: this.handleExpression(expressionList),
        // 清空选中的表达式项
        focusExpressionItemIndex: '',
      });
    }
  }

  // 拖放表达式项
  @Bind()
  onDragEnd(tags) {
    this.setState({
      expressionFactorList: tags,
      expression: this.handleExpression(tags),
      // 清空选中的表达式项
      focusExpressionItemIndex: '',
    });
  }

  // 删除操作符
  @Bind()
  handleClickDelete(tag) {
    const tags = this.state.expressionFactorList.filter((t) => tag.id !== t.id);
    this.setState({
      expressionFactorList: tags,
      expression: this.handleExpression(tags),
    });
  }

  /**
   * 校验预警公式
   * @returns {boolean}
   */
  @Bind()
  validateExpression() {
    const { expressionFactorList } = this.state;
    // 表达式的长度大于等于三
    if (expressionFactorList.length < 3) {
      notification.warning({
        message: intl.get(`hiot.common.message.error.formula`).d('预警公式有误'),
      });
      return false;
    }
    // 表达式中至少包含一个操作符
    if (!expressionFactorList.some(({ priority }) => priority === 1)) {
      notification.warning({
        message: intl
          .get(`hiot.common.message.error.formula.atLeast.one.operator`)
          .d('表达式中应该至少包含一个操作符'),
      });
      return false;
    }
    // 表达式中至少包含一个数据点、一个操作符
    if (this.validateProperty(expressionFactorList)) {
      notification.warning({
        message: intl
          .get(`hiot.common.message.error.formula.atLeast.one.dataPoint`)
          .d('表达式中应该至少包含一个数据点'),
      });
      return false;
    }
    // 聚合函数的下一个应该为数据点
    if (this.validateAggression(expressionFactorList)) {
      notification.warning({
        message: intl
          .get(`hiot.common.message.error.formula.aggression.error`)
          .d('聚合函数只能聚合一个数据点'),
      });
      return false;
    }
    /*  let resultStr = '';
      // 不包含特殊字符
      expressionFactorList.forEach(expressionItem => {
        const { type, value } = expressionItem;
        if (type === 'property') {
          resultStr += 10;
        } else {
          resultStr += value;
        }
      });
      try {
        // eslint-disable-next-line no-eval
        const result = eval(resultStr);
        // eslint-disable-next-line no-eval
        // 判断结果是否是正无穷或者负无穷
        if (result === -Infinity || result === Infinity || Number.isNaN(result)) {
          notification.warning({
            message: intl.get(`hiot.common.message.error.formula`).d('预警公式有误'),
          });
          return false;
        }
      } catch (error) {
        notification.warning({
          message: intl.get(`hiot.common.message.error.formula`).d('预警公式有误'),
        });
        return false;
      } */
    return true;
  }

  /**
   * 校验聚合函数，聚合函数后面必须跟一个数据点
   * @param expressionFactorList 表达式列表
   * @returns {*}
   */
  @Bind()
  validateAggression(expressionFactorList) {
    const len = expressionFactorList.length;
    return expressionFactorList.some(({ type }, index, self) => {
      if (type === 'aggression' && index + 1 < len - 1) {
        const { type: nextType } = self[index + 1];
        return nextType !== 'property';
      } else {
        return false;
      }
    });
  }

  /**
   * 校验公式中必须包含一个数据点
   * @param expressionFactorList 表达式列表
   * @returns {boolean}
   */
  @Bind()
  validateProperty(expressionFactorList) {
    return !expressionFactorList.some(({ type }) => type === 'property');
  }

  /**
   * 校验监测指标
   * @returns {boolean}
   */
  @Bind()
  validateMonitorIndex() {
    const { expressionFactorList } = this.state;
    if (isEmpty(expressionFactorList)) {
      notification.warning({
        message: intl.get('hiot.monitor.message.warning.no.warnFormula').d('指标公式没有填写'),
      });
      return false;
    }
    if (expressionFactorList.length < 2) {
      notification.warning({
        message: intl.get('hiot.monitor.message.warning.error.formula').d('监测指标公式有误'),
      });
      return false;
    }
    if (this.validateProperty(expressionFactorList)) {
      notification.warning({
        message: intl
          .get(`hiot.common.message.error.formula.atLeast.one.dataPoint`)
          .d('表达式中应该至少包含一个数据点'),
      });
      return false;
    }
    if (this.validateAggression(expressionFactorList)) {
      notification.warning({
        message: intl
          .get(`hiot.common.message.error.formula.aggression.error`)
          .d('聚合函数只能聚合一个数据点'),
      });
      return false;
    }
    return true;
  }

  render() {
    const {
      focusFactorItem,
      focusFactorItemType,
      expressionFactorList,
      expression,
      focusExpressionItemIndex,
      countTimeValue,
    } = this.state;
    const {
      data = [],
      isReadOnly = false,
      mode = 'horizontal',
      factorTitle,
      expressionTitle,
      inputComps = [],
    } = this.props;

    // 数据点和监控指标选择框加聚合函数框
    const factorCard = (
      <FactorCard
        disabled={isReadOnly}
        // showAggression // 是否显示聚合函数
        countTimeValue={countTimeValue}
        factorList={
          data.length > 0
            ? Object.values(
                data
                  .map((item) => ({ [item.propertyModelId]: item }))
                  .reduce((sumItem, item) => ({ ...sumItem, ...item }))
              )
            : data
        }
        focusFactorItem={focusFactorItem} // 当前选中的因子对象
        focusFactorItemType={focusFactorItemType} // 当前选中的因子对象的类型 property/index
        clickFactorItem={this.clickFactorItem} // 选中因子项
        factorTitle={factorTitle}
        addExpressionItem={this.addExpressionItem} // 添加聚合函数
        onNumberChange={this.onNumberChange} // 修改统计时间段值
        // correlationFactorList={expressionCorrelationList} // 相关因子列表中该物或物模板的选中因子数据集
        // thingId={this.state.focusThingItem.thingTmpltId}
      />
    );
    // 移动操作框
    const exchange = <Exchange moveUp={this.moveUp} moveDown={this.moveDown} mode={mode} />;
    // 表达式显示框加操作符选择框
    const expressionDraggableCard = (
      <ExpressionDraggableCard
        disabled={isReadOnly}
        mode={mode}
        expressionTitle={expressionTitle}
        expressionFactorList={expressionFactorList} // 表达式因子列表
        expression={expression}
        addExpressionItem={this.addExpressionItem} // 添加操作符
        onDragEnd={this.onDragEnd} // 拖放结束函数
        clickExpressionItem={this.clickExpressionItem} // 点击表达式因子项的操作
        handleClickDelete={this.handleClickDelete} // 删除表达式因子项的操作
        focusExpressionItemIndex={focusExpressionItemIndex} // 当前选中的表达式因子项Index
      />
    );
    return (
      <>
        {mode === 'horizontal' && (
          <Form columns={3} dataSet={this.predictDS}>
            <TextField
              label={intl.get('hiot.common.code').d('编码')}
              name="predictCode"
              disabled={isReadOnly}
              required
            />
            {inputComps}
          </Form>
        )}
        {mode === 'horizontal' ? (
          <Row style={{ display: 'flex' }}>
            <Col span={10}>{factorCard}</Col>
            <Col span={1}>{exchange}</Col>
            <Col span={13}>{expressionDraggableCard}</Col>
          </Row>
        ) : (
          <Row>
            <Col>{factorCard}</Col>
            <Col>{exchange}</Col>
            <Col>{expressionDraggableCard}</Col>
          </Row>
        )}
      </>
    );
  }
}
