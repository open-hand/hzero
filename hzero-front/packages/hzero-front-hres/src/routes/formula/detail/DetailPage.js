/**
 * 公式 - 公式模块列表页
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-10-14
 * @LastEditTime: 2019-10-23 22:01
 * @Copyright: Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { connect } from 'dva';
import intl from 'utils/intl';
import { routerRedux } from 'dva/router';
import { Header } from 'components/Page';
import { getCurrentOrganizationId } from 'utils/utils';
import {
  DataSet,
  Button,
  Form,
  TextArea,
  Select,
  TextField,
  Spin,
  Row,
  Col,
} from 'choerodon-ui/pro';
import { Table, Layout } from 'hzero-ui';
import { isEmpty } from 'lodash';
import notification from 'utils/notification';
import querystring from 'querystring';
import { Bind } from 'lodash-decorators';
import formatterCollections from 'utils/intl/formatterCollections';
import { queryTreeData, validateFormula } from '@/services/formulaService';
import { saveNode } from '@/utils/saveNode';
import FormulaDS from '../stores/FormulaDS';
import styles from './index.less';

/**
 * 公式
 * @extends {Component} - Component
 * @reactProps {Object} [history={}]
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 *
 */
@connect()
@formatterCollections({ code: ['hres.flow', 'hres.common'] })
@observer
export default class DetailPage extends Component {
  formulaDS = new DataSet(FormulaDS());

  state = {
    treeData: false,
  };

  async componentDidMount() {
    const {
      location: { search },
      match,
    } = this.props;
    const { id, code } = match.params;
    const { componentName } = querystring.parse(search.substring(1));
    const res = await queryTreeData(code);
    if (!isEmpty(res) && res.failed && res.message) {
      return false;
    } else if (!isEmpty(res)) {
      if (res.children) {
        this.setState({
          treeData: res.children,
        });
      }
    }
    if (isEmpty(componentName)) {
      this.formulaDS.create({
        id,
        tenantId: getCurrentOrganizationId(),
      });
    } else {
      this.formulaDS.setQueryParameter('ruleCode', code);
      this.formulaDS.setQueryParameter('formulaName', componentName);
      this.formulaDS.query();
    }
  }

  /**
   * 点击树添加进公式编辑框
   * @param record
   */
  onClick = (record) => {
    const { children = [], fullName = undefined } = record;
    const inps = document.getElementsByTagName('TextArea');
    const startPos = inps[0].selectionStart || 0;
    const endPos = inps[0].selectionEnd || 0;
    if (isEmpty(children)) {
      const tempValue = this.formulaDS.current.get('formulaText') || '';
      const fullNames = `{${fullName}}`;
      const final =
        tempValue.substring(0, startPos) +
        fullNames +
        tempValue.substring(endPos, tempValue.length);
      this.formulaDS.current.set('formulaText', final);
    }
  };

  /**
   * 保存
   * @returns {Promise<boolean>}
   */
  @Bind()
  async handleSubmit() {
    const {
      dispatch,
      location: { search },
      match,
    } = this.props;
    const { code, id } = match.params;
    const { pageType, ruleName } = querystring.parse(search.substring(1));
    this.formulaDS.current.set('ruleCode', code);
    const data = {
      formulaText: this.formulaDS.current.get('formulaText'),
      tenantId: getCurrentOrganizationId(),
      ruleCode: this.formulaDS.current.get('ruleCode'),
    };
    const validateRes = await validateFormula(data);
    if (validateRes === true) {
      const res = await this.formulaDS.submit();
      if (!isEmpty(res) && !res.status) {
        await saveNode(res.content[0].processNode, id);
        const pathname = `/hres/rules/flow/detail/${code}`;
        dispatch(
          routerRedux.push({
            pathname,
            search: querystring.stringify({ pageType, ruleName }),
          })
        );
        return false;
      } else if (res === false) {
        notification.error({
          message: intl.get('hres.common.notification.required').d('存在必输字段未填写'),
        });
        return false;
      } else {
        notification.warning({
          message: intl.get('hres.common.notification.unmodified').d('表单未做修改'),
        });
        return false;
      }
    } else if (!isEmpty(validateRes) && validateRes.failed) {
      notification.error({
        message: validateRes.message,
      });
    }
  }

  render() {
    const columns = [
      {
        dataIndex: 'data',
        key: 'fullName',
      },
    ];

    const {
      location: { search },
      match,
    } = this.props;
    const { treeData } = this.state;
    const { code } = match.params;
    const { componentName, pageType, ruleName } = querystring.parse(search.substring(1));
    const frozenFlag = pageType === 'view';
    const isChange = this.formulaDS.current ? this.formulaDS.current.dirty : false;

    return (
      <>
        <Header
          title={`${intl.get('hres.flow.model.flow.formulaCmp').d('公式组件')}_${ruleName}`}
          backPath={`/hres/rules/flow/detail/${code}?pageType=${pageType}&ruleName=${ruleName}`}
          isChange={isChange}
        >
          {!frozenFlag && (
            <Button icon="save" color="primary" onClick={this.handleSubmit}>
              {intl.get('hzero.common.button.save').d('保存')}
            </Button>
          )}
        </Header>
        <Layout className={styles['layout-wrapper']}>
          <Layout.Sider width={300}>
            {treeData ? (
              <Table
                className={styles['tree-wrapper']}
                size="small"
                defaultExpandAllRows
                showHeader={false}
                pagination={false}
                columns={columns}
                dataSource={treeData}
                onRow={(record) => ({
                  onClick: this.onClick.bind(this, record),
                })}
                rowKey="data"
              />
            ) : (
              <Spin />
            )}
          </Layout.Sider>
          <Layout>
            <Layout.Header>
              <Row>
                <Col span={4}>
                  <b>{intl.get('hres.flow.view.message.func').d('常用函数:')}</b>
                </Col>
              </Row>
              <Row>
                <Col span={6}>{intl.get('hres.flow.view.message.abs').d('1、绝对值：ABS(A)')}</Col>
                <Col span={6}>
                  {intl.get('hres.flow.view.message.concat').d('2、多字段拼接：CONCAT(A,B,C,...)')}
                </Col>
                <Col span={6}>
                  {intl.get('hres.flow.view.message.max').d('3、最大值：MAX(A,B,C,...)')}
                </Col>
                <Col span={6}>
                  {intl.get('hres.flow.view.message.min').d('4、最小值：MIN(A,B,C,...)')}
                </Col>
                <Col span={6}>
                  {intl.get('hres.flow.view.message.round').d('5、四舍五入：ROUND(A,B)')}
                </Col>
                <Col span={6}>
                  {intl.get('hres.flow.view.message.if').d('6、比较判定：IF(A==B,C,D)')}
                </Col>
                <Col span={6}>{intl.get('hres.flow.view.message.add').d('7、加：A+B')}</Col>
                <Col span={6}>{intl.get('hres.flow.view.message.minus').d('8、减：A-B')}</Col>
                <Col span={6}>{intl.get('hres.flow.view.message.times').d('9、乘：A*B')}</Col>
                <Col span={6}>{intl.get('hres.flow.view.message.divided').d('10、除：A/B')}</Col>
                <Col span={6}>
                  {intl.get('hres.flow.view.message.remainder').d('11、取余：A%B')}
                </Col>
                <Col span={6}>
                  {intl.get('hres.flow.view.message.less').d('12、小于等于：A<=B')}
                </Col>
                <Col span={6}>
                  {intl.get('hres.flow.view.message.more').d('13、大于等于：A>=B')}
                </Col>
                <Col span={6}>{intl.get('hres.flow.view.message.neq').d('14、不等于：A!=B')}</Col>
                <Col span={6}>{intl.get('hres.flows.view.message.equal').d('15、等于：A==B')}</Col>
                <Col span={6}>{intl.get('hres.flow.view.message.and').d('16、与：A&&B')}</Col>
                <Col span={6}>{intl.get('hres.flow.view.message.or').d('17、或：A||B')}</Col>
                <Col span={6}>{intl.get('hres.flow.view.message.false').d('18、非：!A')}</Col>
                <Col span={6}>
                  {intl.get('hres.flow.view.message.up').d('19、向上取整：ROUNDUP(A,B)')}
                </Col>
                <Col span={6}>
                  {intl.get('hres.flow.view.message.down').d('20、向下取整：ROUNDDOWN(A,B)')}
                </Col>
                <Col span={6}>{intl.get('hres.flow.view.message.pow').d('21、幂次：POW(A,B)')}</Col>
                <Col span={6}>
                  {intl.get('hres.flow.view.message.code').d('22、编码规则：CODE(A)')}
                </Col>
                <Col span={6}>
                  {intl
                    .get('hres.flow.view.message.dateformat')
                    .d('23、日期格式化：DATEFORMAT(日期,日期掩码)')}
                </Col>
                <Col span={6}>
                  {intl
                    .get('hres.flow.view.message.date2str')
                    .d('24、日期转字符：DATE2STR(日期,日期掩码)')}
                </Col>
                <Col span={6}>
                  {intl
                    .get('hres.flow.view.message.str2date')
                    .d('25、字符转日期：STR2DATE(字符,日期掩码)')}
                </Col>
                <Col span={6}>
                  {intl.get('hres.flow.view.message.currentDate').d('26、当前日期: NOW(日期掩码)')}
                </Col>
                <Col span={6}>
                  {intl
                    .get('hres.flow.view.message.DateOperation')
                    .d('27、日期运算(+/-):DATE_ARITHMETIC(日期,差量,日期类型)')}
                </Col>
                <Col span={24}>
                  {intl
                    .get('hres.flow.view.message.intervalMsgFx')
                    .d(
                      '28、区间级别: INTERVAL_GRADE(表达式, INTERVAL(A,B,...), GRADE("A","B","C"....))'
                    )}
                </Col>
              </Row>
            </Layout.Header>
            <Layout.Content>
              <Form dataSet={this.formulaDS} disabled={frozenFlag} columns={2}>
                <TextField name="formulaName" restrict="\S" disabled={componentName} />
                <Select name="formulaType" />
                <TextArea name="formulaText" newLine resize="vertical" colSpan={2} />
              </Form>
            </Layout.Content>
          </Layout>
        </Layout>
      </>
    );
  }
}
