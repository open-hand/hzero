import React, { PureComponent } from 'react';
import { Card } from 'choerodon-ui';
import { DataSet, Form, TextField } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { isEmpty } from 'lodash';
import { fieldMappingTableDS, onlyReadFormDS } from '@/stores/Orchestration/DataTransformDS';
import LogicOperation from '@/components/LogicOperation';
import DATA_TRANSFORM_LANG from '@/langs/orchDataTransformLang';

export default class MappingDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.fieldMappingTableDS = new DataSet({
      ...fieldMappingTableDS(),
    });
    this.onlyReadFormDS = new DataSet({
      ...onlyReadFormDS(),
    });
    props.onRef(this);
  }

  componentDidMount() {
    this.init();
  }

  /**
   * 数据加载
   */
  @Bind
  init() {
    const { currentRecord, castField, tenantId } = this.props;
    const { conditionJson, evaluateExpression } = currentRecord.toData();
    this.onlyReadFormDS.loadData([{ castField }]);
    if (currentRecord.status === 'add') {
      // 新建再编辑情况
      this.fieldMappingTableDS.create({ conditionJson, evaluateExpression, tenantId });
    } else {
      // 保存过后再编辑情况
      this.fieldMappingTableDS.loadData([{ conditionJson, evaluateExpression }]);
    }
  }

  @Bind()
  getFormat(format) {
    const { jsonLogicFormat: conditionJson, stringFormat: evaluateExpression } = format;
    const { current } = this.fieldMappingTableDS;
    const sourceMappingFields = this.formatExpression(evaluateExpression);
    current.set('conditionJson', conditionJson);
    current.set('evaluateExpression', evaluateExpression);
    current.set('sourceMappingFields', sourceMappingFields);
  }

  /**
   * 格式化条件字符串
   */
  formatExpression(expression) {
    let temp = expression;
    if (isEmpty(temp)) {
      return '';
    }
    // 匹配浮点数或整数正则
    const pattern = /([+]\d+[.]\d+|[-]\d+[.]\d+|\d+[.]\d+|[+]\d+|[-]\d+|\d+)/gi;
    // 删除原字符串首个双引号
    temp = temp.replace(/"/, '');
    // 删除原字符串最后一个双引号
    temp = temp.replace(/(.*)"/, '$1');
    // 原字符串中所有的双引号替换为单引号
    temp = temp.replace(/\\"/g, "'");
    const matchList = temp.match(pattern);
    // 把字符串中的数字字符串的单引号删掉
    matchList.forEach((str) => {
      let formatedStr = str.replace(/"/, '');
      formatedStr = formatedStr.replace(/(.*)"/, '$1');
      const macther = new RegExp(`'${formatedStr}'`);
      temp = temp.replace(macther, formatedStr);
    });
    return temp;
  }

  render() {
    const { logicValue, disabledFlag } = this.props;
    const logicOperationProps = {
      readOnly: disabledFlag,
      value: logicValue,
      onGetFormat: this.getFormat,
    };
    return (
      <>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{DATA_TRANSFORM_LANG.BASIC_INFO}</h3>}
        >
          <Form dataSet={this.onlyReadFormDS} columns={2} disabled>
            <TextField name="castField" />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{DATA_TRANSFORM_LANG.CONDITION_MAINTAIN}</h3>}
        >
          <LogicOperation {...logicOperationProps} />
        </Card>
      </>
    );
  }
}
