/**
 * Dimension - 数据权限维度
 * @date: 2019-10-30
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { DataSet, Table, Button, CheckBox } from 'choerodon-ui/pro';
import { Row, Col, Checkbox as OriginCheckbox } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import { HZERO_IAM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import notification from 'utils/notification';
import intl from 'utils/intl';
import Checkbox from 'components/Checkbox';
import { queryIdpValue } from 'services/api';

import { dimensionDS } from '@/stores/SecurityGroupDS';
import styles from './index.less';

const CheckboxGroup = OriginCheckbox.Group;
// const RadioGroup = Radio.Group;
const organizationId = getCurrentOrganizationId();
const levelUrl = isTenantRoleLevel() ? `/${organizationId}` : '';

export default class Dimension extends Component {
  originData;

  dimensionDS = new DataSet({
    ...dimensionDS(),
    transport: {
      read: ({ data, params }) => {
        this.setState({
          bizRoleAuthorityLines: [],
          userRoleAuthorityLines: [],
          currentAuthScope: null, // 当前选中的范围
        });
        const { secGrpId, isSelf = false, roleId, secGrpSource } = this.props;
        let newParams = { ...data, ...params };
        if (!isSelf) {
          newParams = {
            ...newParams,
            roleId,
            secGrpSource,
          };
        }
        return {
          url: isSelf
            ? `${HZERO_IAM}/v1${levelUrl}/${secGrpId}/sec-grp-dcl-dims`
            : `${HZERO_IAM}/v1${levelUrl}/${secGrpId}/sec-grp-dcl-dims/assigned`,
          params: newParams,
          method: 'get',
        };
      },
      submit: ({ data }) => {
        let dataList = [];
        const { secGrpId } = this.props;
        dataList = data.map((val) => {
          const nextData = { ...val };
          const { changingSourceList = [], ...rest } = nextData;
          return { ...rest, secGrpDclDimLineList: changingSourceList };
        });
        return {
          url: `${HZERO_IAM}/v1${levelUrl}/${secGrpId}/sec-grp-dcl-dims`,
          data: dataList,
        };
      },
    },
    events: {
      load: ({ dataSet }) => {
        this.originData = dataSet.toData();
        this.onRow(dataSet.current);
      },
      submitSuccess: ({ dataSet }) => {
        this.originData = dataSet.toData();
        this.onRow(dataSet.current);
      },
    },
  });

  constructor(props) {
    super(props);
    this.state = {
      roleAuthScopeCode: [],
      currentAuthScope: null, // 当前选中的范围
      authUserDefaultValue: [], // 数据为用户范围时的选中值
      authDefaultValue: [], // 权限维度默认选中的列表
      bizRoleAuthorityLines: [],
      userRoleAuthorityLines: [],
      checkedList: [],
    };
  }

  async componentDidMount() {
    const lookupData = await queryIdpValue('HIAM.AUTHORITY_SCOPE_CODE');
    this.setState({
      roleAuthScopeCode: lookupData,
    });
  }

  get columns() {
    const { isSelf } = this.props;
    const { roleAuthScopeCode } = this.state;
    return [
      { name: 'docTypeName' },
      {
        name: 'authScopeCode',
        renderer: ({ record }) => (
          <>
            {roleAuthScopeCode.map((item) => (
              <CheckBox
                // value={item.value}
                // key={item.authTypeCode}
                checked={record.get('secGrpDclDimCheckedFlag') === 1}
                style={{
                  marginRight: 0,
                  padding: '0 10px',
                  display: record.get('authScopeMeaning') === item.meaning ? '' : 'none',
                }}
                onChange={this.clickRadio}
                disabled={isSelf ? !record.get('editEnableFlag') : true}
              >
                {item.meaning}
              </CheckBox>
            ))}
          </>
        ),
      },
    ];
  }

  /**
   * 点击单选框时触发
   * @param {string} value
   * @param {string} radioValue
   * @param {number} id
   * @memberof Dimension
   */
  @Bind()
  clickRadio(value) {
    const { currentAuthScope } = this.state;
    this.dimensionDS.current.set('secGrpDclDimCheckedFlag', value ? 1 : 0);
    this.onRow(this.dimensionDS.current);
    if (!value) {
      // this.setState({
      //   userRoleAuthorityLines: [],
      //   bizRoleAuthorityLines: [],
      // });
      if (currentAuthScope === 'BIZ') {
        this.setState({ authDefaultValue: [] });
      } else if (currentAuthScope === 'USER') {
        this.setState({ authUserDefaultValue: [] });
      }
    }
  }

  /**
   * 权限值变更
   * @param {Array} checkedList - 选中的权限项列表
   */
  @Bind()
  authOnChange(checkedList) {
    const { currentAuthScope } = this.state;
    const selectedRow = this.dimensionDS.current.toData();
    const sourceList = selectedRow.secGrpDclDimLineList; // 查找原始权限List
    // const originRow = this.originData.find(item => item.docTypeId === selectedRow.docTypeId);
    // const originRoleAuthorityLines = originRow.secGrpDclDimLineList || [];

    this.setState({
      checkedList,
    });
    if (checkedList) {
      this.dimensionDS.current.set('secGrpDclDimCheckedFlag', 1);
    } else {
      this.dimensionDS.current.set('secGrpDclDimCheckedFlag', 0);
    }

    // 改变后的权限值
    const changedSourceList = sourceList.map((item) => {
      let nextItem = { ...item };
      // 权限维度范围是业务范围
      if (item.dimensionType === currentAuthScope) {
        nextItem = {
          ...nextItem,
          secGrpDclDimLineCheckedFlag: checkedList.includes(item.authTypeCode) ? 1 : 0,
        };
      }
      return nextItem;
    });
    // 改变了的权限值
    // const changingSourceList = changedSourceList.filter(item =>
    //   originRoleAuthorityLines.find(
    //     oldItem =>
    //       oldItem.authTypeCode === item.authTypeCode &&
    //       oldItem.secGrpDclDimLineCheckedFlag !== item.secGrpDclDimLineCheckedFlag
    //   )
    // );
    // 选择权限值项时触发范围修改
    // if (currentAuthScope === 'USER') {
    //   const params = {
    //     target: {
    //       value: 'USER',
    //     },
    //   };
    //   this.scopeOnChange(params, true);
    // } else if (currentAuthScope === 'BIZ') {
    //   const params = {
    //     target: {
    //       value: 'BIZ',
    //     },
    //   };
    //   this.scopeOnChange(params, true);
    // }

    this.dimensionDS.current.set('secGrpDclDimLineList', changedSourceList);
    this.dimensionDS.current.set('changingSourceList', changedSourceList);
    if (currentAuthScope === 'BIZ') {
      this.setState({ authDefaultValue: checkedList }, () => {
        // this.handleSubmit();
      });
    } else {
      this.setState({ authUserDefaultValue: checkedList }, () => {
        // this.handleSubmit();
      });
    }
  }

  @Bind()
  handleClick() {
    const { currentAuthScope, checkedList } = this.state;
    if (currentAuthScope === 'BIZ') {
      this.setState({ authDefaultValue: checkedList }, () => {
        this.handleSubmit();
      });
    } else {
      this.setState({ authUserDefaultValue: checkedList }, () => {
        this.handleSubmit();
      });
    }
  }

  /**
   * 点击行
   * @param {object} record - 表格行记录
   */
  @Bind()
  onRow(record) {
    const currentRecord = record.toData();
    const { secGrpDclDimLineList = [], authScopeCode } = currentRecord;
    let authDefaultValue = [];

    const bizRoleAuthorityLines =
      (currentRecord.secGrpDclDimLineList &&
        currentRecord.secGrpDclDimLineList.filter((item) => item.dimensionType === 'BIZ')) ||
      [];

    const userRoleAuthorityLines =
      (currentRecord.secGrpDclDimLineList &&
        currentRecord.secGrpDclDimLineList.filter((item) => item.dimensionType === 'USER')) ||
      [];

    if (secGrpDclDimLineList) {
      authDefaultValue = secGrpDclDimLineList
        .filter((item) => !!item.secGrpDclDimLineCheckedFlag)
        .map((item) => item.authTypeCode);
    }
    const authUserDefaultValue =
      (secGrpDclDimLineList &&
        secGrpDclDimLineList
          .filter((item) => item.dimensionType === 'USER' && !!item.secGrpDclDimLineCheckedFlag)
          .map((item) => item.authTypeCode)) ||
      [];
    this.setState({
      authDefaultValue,
      authUserDefaultValue,
      currentAuthScope: authScopeCode,
      bizRoleAuthorityLines,
      userRoleAuthorityLines,
    });
  }

  // 更新后提交
  @Bind()
  async handleSubmit() {
    const res = await this.dimensionDS.submit();
    if (!isEmpty(res) && res.failed && res.message) {
      notification.warning({ message: res.message });
    } else if (!isEmpty(res) && res.success) {
      // this.dimensionDS.query();
    }
    // else {
    //   notification.warning({
    //     message: intl.get('hiam.securityGroup.view.message.form.noChange').d('表单未做修改'),
    //   });
    // }
  }

  @Bind()
  renderAuthDim() {
    const { isSelf } = this.props;
    const {
      currentAuthScope, // 当前选中的维度类型
      authUserDefaultValue = [], // 数据为用户范围时的默认选中列表
      authDefaultValue = [], // 默认选中的列表
      bizRoleAuthorityLines,
      userRoleAuthorityLines,
    } = this.state;
    const emptyComponent = (
      <h3 style={{ color: 'gray' }}>
        {intl.get('hiam.securityGroup.view.title.secGrp.empty').d('暂无数据')}
      </h3>
    );
    const selectedRow = this.dimensionDS.current ? this.dimensionDS.current.toData() : {};
    if (isEmpty(selectedRow) || !currentAuthScope) return emptyComponent; // 未选中行或权限维度范围没有值时，不显示权限值
    if (!isEmpty(selectedRow)) {
      if (currentAuthScope === 'USER') {
        return isEmpty(userRoleAuthorityLines) ? (
          emptyComponent
        ) : (
          <CheckboxGroup
            value={
              this.dimensionDS.current.get('secGrpDclDimCheckedFlag') === 1
                ? authUserDefaultValue
                : []
            }
            onChange={this.authOnChange}
            style={{ width: '100%' }}
          >
            <Row>
              {userRoleAuthorityLines.map((item) => (
                <Col span={24} key={item.authTypeCode}>
                  <Checkbox
                    value={item.authTypeCode}
                    key={item.authTypeCode}
                    style={{ height: 30 }}
                    disabled={!isSelf || !item.deleteEnableFlag}
                  >
                    {item.authTypeMeaning}
                  </Checkbox>
                </Col>
              ))}
            </Row>
          </CheckboxGroup>
        );
      }
      return isEmpty(bizRoleAuthorityLines) ? (
        emptyComponent
      ) : (
        <CheckboxGroup
          style={{ width: '100%' }}
          onChange={this.authOnChange}
          value={
            this.dimensionDS.current.get('secGrpDclDimCheckedFlag') === 1 ? authDefaultValue : []
          }
        >
          <Row>
            {bizRoleAuthorityLines.map((item) => (
              <Col span={24} key={item.authTypeCode}>
                <Checkbox
                  style={{ height: 30 }}
                  key={item.authTypeCode}
                  value={item.authTypeCode}
                  disabled={!isSelf || !item.deleteEnableFlag}
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
    return (
      <>
        <Row gutter={24}>
          <Col style={{ float: 'right' }}>
            <Button type="primary" icon="save" onClick={this.handleClick}>
              {intl.get('hzero.common.button.save').d('保存')}
            </Button>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={16}>
            <Table
              dataSet={this.dimensionDS}
              columns={this.columns}
              queryFieldsLimit={1}
              onRow={({ record }) => ({
                onClick: () => this.onRow(record),
              })}
            />
          </Col>
          <Col span={8}>
            <Row>
              <Col>
                <div className={styles['authDims-container']}>
                  {intl.get('hiam.roleManagement.view.title.authDimension').d('权限维度')}
                </div>
              </Col>
            </Row>
            <Row>
              <Col>
                <div className={styles['authDims-item']}>{this.renderAuthDim()}</div>
              </Col>
            </Row>
          </Col>
        </Row>
      </>
    );
  }
}
