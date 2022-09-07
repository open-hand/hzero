/**
 * 公式编辑弹窗
 * @Author: zhangzhicen <zhicen.zhang@hand-china.com>
 * @Date: 2019/10/28 15:54
 * @LastEditTime: 2019/10/28 15:54
 * @Copyright: Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { DataSet, Modal, TextArea, Form, Spin, Row, Col } from 'choerodon-ui/pro';
import { Layout, Table } from 'hzero-ui';
import { isEmpty } from 'lodash';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { HZERO_HRES } from 'utils/config';
import styles from './index.less';
import { queryTreeData } from '../../../services/formulaService';

const modalKey = Modal.key();

class FormulaModel extends Component {
  ds;

  constructor(props) {
    super(props);
    this.ds = new DataSet({
      autoCreate: false,
      autoQuery: false,
      autoQueryAfterSubmit: false,
      transport: {
        read: ({ data, params }) => ({
          url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/formula-component`,
          params: { tenantId: getCurrentOrganizationId(), ...params, ...data },
          method: 'GET',
        }),
        submit: ({ data }) => {
          const { tenantId, ruleCode, formulaText } = data[0] || [];
          if (!formulaText) {
            return false;
          }
          return {
            url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/rule/validate-formula`,
            params: { tenantId, ruleCode, formulaText },
          };
        },
      },
      fields: [
        {
          name: 'formulaText',
          type: 'string',
          label: intl.get('hres.ruleComponent.model.ruleCmp.formulaContent').d('公式内容'),
        },
      ],
    });

    this.props.callback(this.ds);
    this.state = {
      treeData: false,
    };
  }

  async componentDidMount() {
    const { record, ruleCode } = this.props;
    const create = {
      formulaText: record.get('formula'),
      tenantId: getCurrentOrganizationId(),
      ruleCode,
    };
    this.ds.create(create);
    const res = await queryTreeData(ruleCode);
    if (!isEmpty(res) && res.failed && res.message) {
      return false;
    } else if (!isEmpty(res)) {
      if (res.children) {
        this.setState({
          treeData: res.children,
        });
      }
    }
  }

  /**
   * 点击树添加进SQL编辑框
   * @param record
   */
  onClick = (record) => {
    const { children = [], fullName = undefined } = record;
    const inps = document.getElementsByTagName('TextArea');
    const startPos = inps[0].selectionStart || 0;
    const endPos = inps[0].selectionEnd || 0;
    if (isEmpty(children)) {
      const tempValue = this.ds.current.get('formulaText') || '';
      const fullNames = `{${fullName}}`;
      const final =
        tempValue.substring(0, startPos) +
        fullNames +
        tempValue.substring(endPos, tempValue.length);
      this.ds.current.set('formulaText', final);
    }
  };

  render() {
    const columns = [
      {
        dataIndex: 'data',
        key: 'data',
      },
    ];
    const { treeData } = this.state;

    return (
      <Layout className={styles['layout-wrapper']}>
        <Layout.Sider style={{ width: 300, height: 450 }}>
          {treeData ? (
            <Table
              size="small"
              className={styles['layout-wrapper']}
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
              <Col span={6}>{intl.get('hres.flow.view.message.remainder').d('11、取余：A%B')}</Col>
              <Col span={6}>{intl.get('hres.flow.view.message.less').d('12、小于等于：A<=B')}</Col>
              <Col span={6}>{intl.get('hres.flow.view.message.more').d('13、大于等于：A>=B')}</Col>
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
            <Form dataSet={this.ds} columns={2}>
              <TextArea name="formulaText" rows={10} newLine resize="vertical" colSpan={2} />
            </Form>
          </Layout.Content>
        </Layout>
      </Layout>
    );
  }
}

let formulaDS;

function callback(ds) {
  formulaDS = ds;
}

export default function openFormulaModel(record, ruleCode) {
  Modal.open({
    key: modalKey,
    title: intl.get('hres.flow.view.title.formula').d('公式编辑'),
    fullScreen: true,
    destroyOnClose: true,
    children: <FormulaModel callback={callback} record={record} ruleCode={ruleCode} />,
    onOk: () =>
      // eslint-disable-next-line no-async-promise-executor
      new Promise(async (resolve) => {
        const list = formulaDS.current;
        let res;
        try {
          res = await formulaDS.submit();
        } catch (e) {
          return resolve(false);
        }
        if (!res || res.success) {
          record.set('formula', list.get('formulaText'));
          resolve();
        }
      }),
  });
}
