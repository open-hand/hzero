/**
 * DataDimensionTab - 安全组数据权限维度
 * @date: 2019-11-28
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Table, Row, Col, Checkbox as OriginCheckbox, Radio } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import Checkbox from 'components/Checkbox';

import styles from './index.less';

const CheckboxGroup = OriginCheckbox.Group;
const RadioGroup = Radio.Group;

export default class DataDimensionTab extends Component {
  constructor(props) {
    super(props);
    this.state = {
      currentAuthScope: null, // 当前选中的范围
      authUserDefaultValue: null, // 数据为用户范围时的选中值
      authDefaultValue: [], // 权限维度默认选中的列表
      bizRoleAuthorityLines: [],
      userRoleAuthorityLines: [],
      currentRow: {},
    };
  }

  get columns() {
    return [
      {
        title: intl.get('hiam.subAccount.model.role.docType').d('单据'),
        dataIndex: 'docTypeName',
      },
      {
        title: intl.get('hiam.subAccount.model.role.authRang').d('权限维度范围'),
        dataIndex: 'authScopeMeaning',
      },
    ];
  }

  @Bind()
  onRow(record) {
    this.setState({ currentRow: record });
    const { secGrpDclDimLineList = [], authScopeCode } = record;
    let authDefaultValue = [];

    const bizRoleAuthorityLines =
      (record.secGrpDclDimLineList &&
        record.secGrpDclDimLineList.filter(item => item.dimensionType === 'BIZ')) ||
      [];

    const userRoleAuthorityLines =
      (record.secGrpDclDimLineList &&
        record.secGrpDclDimLineList.filter(item => item.dimensionType === 'USER')) ||
      [];

    if (secGrpDclDimLineList) {
      authDefaultValue = secGrpDclDimLineList
        .filter(item => !!item.secGrpDclDimLineCheckedFlag)
        .map(item => item.authTypeCode);
    }

    const authUserDefaultValue =
      (secGrpDclDimLineList &&
        secGrpDclDimLineList
          .filter(item => item.dimensionType === 'USER' && !!item.secGrpDclDimLineCheckedFlag)
          .map(item => item.authTypeCode)[0]) ||
      [];

    this.setState({
      authDefaultValue,
      authUserDefaultValue,
      currentAuthScope: authScopeCode,
      bizRoleAuthorityLines,
      userRoleAuthorityLines,
    });
  }

  /**
   * 高亮显示选中行
   * @param {object} record - 行数据
   */
  @Bind()
  addHighlight(record) {
    const { currentRow } = this.state;
    return record.docTypeId === currentRow.docTypeId ? styles['auth-row-hover'] : '';
  }

  @Bind()
  renderAuthDim() {
    const {
      currentRow,
      currentAuthScope,
      authDefaultValue,
      authUserDefaultValue,
      bizRoleAuthorityLines,
      userRoleAuthorityLines,
    } = this.state;
    const emptyComponent = (
      <h3 style={{ color: 'gray' }}>
        {intl.get('hiam.securityGroup.view.title.secGrp.empty').d('暂无数据')}
      </h3>
    );
    if (isEmpty(currentRow) || !currentAuthScope) return emptyComponent; // 未选中行或权限维度范围没有值时，不显示权限值
    if (!isEmpty(currentRow)) {
      if (currentAuthScope === 'USER') {
        return isEmpty(userRoleAuthorityLines) ? (
          emptyComponent
        ) : (
          <RadioGroup
            value={authUserDefaultValue}
            onChange={this.authOnChange}
            style={{ width: '100%' }}
          >
            <Row>
              {userRoleAuthorityLines.map(item => (
                <Col span={24} key={item.authTypeCode}>
                  <Radio
                    value={item.authTypeCode}
                    key={item.authTypeCode}
                    style={{ height: 30 }}
                    disabled
                  >
                    {item.authTypeMeaning}
                  </Radio>
                </Col>
              ))}
            </Row>
          </RadioGroup>
        );
      }
      return isEmpty(userRoleAuthorityLines) ? (
        emptyComponent
      ) : (
        <CheckboxGroup
          style={{ width: '100%' }}
          onChange={this.authOnChange}
          value={authDefaultValue}
        >
          <Row>
            {bizRoleAuthorityLines.map(item => (
              <Col span={24} key={item.authTypeCode}>
                <Checkbox
                  style={{ height: 30 }}
                  key={item.authTypeCode}
                  value={item.authTypeCode}
                  disabled
                >
                  {item.authTypeMeaning}
                </Checkbox>
              </Col>
            ))}
          </Row>
        </CheckboxGroup>
      );
    }
  }

  render() {
    const { dataSource, loading, pagination } = this.props;
    return (
      <Row gutter={24}>
        <Col span={16}>
          <Table
            rowKey="docTypeId"
            loading={loading}
            dataSource={dataSource}
            columns={this.columns}
            bordered
            pagination={pagination}
            rowClassName={this.addHighlight}
            onRow={record => ({
              onClick: () => this.onRow(record),
            })}
          />
        </Col>
        <Col span={8}>
          <>
            <Row>
              <Col>
                <div className={styles['authDims-container']}>
                  {intl.get('hiam.subAccount.view.title.authDims').d('权限值')}
                </div>
              </Col>
            </Row>
            <Row>
              <Col>
                <div className={styles['authDims-item']}>{this.renderAuthDim()}</div>
              </Col>
            </Row>
          </>
        </Col>
      </Row>
    );
  }
}
