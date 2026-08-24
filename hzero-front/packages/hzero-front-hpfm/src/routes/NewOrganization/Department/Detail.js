/**
 * @since 2019-12-03
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, Output, Form } from 'choerodon-ui/pro';
import { Row, Col, Icon, Divider } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

import { employeeDetailFormDS } from '@/stores/departmentDS';
import { fetchEmployeeDetail } from '@/services/newOrganizationService';

import styles from './index.less';

export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.employeeDetailFormDS = new DataSet(employeeDetailFormDS());
    this.state = {
      partTimePositionNode: [],
    };
  }

  componentDidMount() {
    const { employeeId } = this.props;
    let { partTimePositionNode } = this.state;
    fetchEmployeeDetail({
      employeeId,
      customizeUnitCode: 'HPFM.ORG_LIST.DEPARTMENT.EMPLOYEE.DETAIL',
    }).then((data) => {
      const {
        list,
        employeeCode = '',
        name = '',
        gender = '',
        mobile = '',
        email = '',
        status = '',
        enabledFlag = '',
      } = data;
      const { unitName = '', positionName = '' } = list.filter(
        (item) => item.primaryPositionFlag === 1
      )[0];

      this.employeeDetailFormDS.loadData([
        {
          employeeCode,
          name,
          gender: this.displayGender(gender),
          mobile,
          email,
          unitName,
          positionName,
          status: this.displayStatus(status),
          enabledFlag: this.displayEnabledFlag(enabledFlag),
        },
      ]);
      partTimePositionNode = list
        .filter((item) => item.primaryPositionFlag === 0)
        .map((item) => [
          <Output
            newLine
            key="partTimeDepartment"
            label={intl
              .get('hpfm.organization.model.department.partTimeDepartmentInfo')
              .d('兼职部门')}
            value={item.unitName}
          />,
          <Output
            key="partTimePosition"
            colSpan={2}
            label={intl
              .get('hpfm.organization.model.department.partTimePositionInfo')
              .d('兼职岗位')}
            value={item.positionName}
          />,
        ]);
      this.setState({ partTimePositionNode });
    });
  }

  @Bind()
  displayStatus(value) {
    let textFiled;
    switch (value) {
      case 'ON':
        textFiled = intl.get('hpfm.organization.model.department.onPosition').d('在职');
        break;
      case 'TRIAL':
        textFiled = intl.get('hpfm.organization.model.department.trial').d('试用');
        break;
      case 'INTERNSHIP':
        textFiled = intl.get('hpfm.organization.model.department.practice').d('实习');
        break;
      case 'LEAVE':
        textFiled = intl.get('hpfm.organization.model.department.leave').d('离职');
        break;
      default:
        return;
    }
    return textFiled;
  }

  @Bind()
  displayGender(value) {
    if (!value) {
      return intl.get('hpfm.organization.model.department.women').d('女');
    } else {
      return intl.get('hpfm.organization.model.department.men').d('男');
    }
  }

  @Bind()
  displayEnabledFlag(value) {
    if (value) {
      return intl.get('hzero.common.status.yes').d('是');
    } else {
      return intl.get('hzero.common.status.no').d('否');
    }
  }

  @Bind()
  handleBackPage() {
    const { handleBack } = this.props;
    handleBack();
  }

  render() {
    const { partTimePositionNode } = this.state;
    const { customizeForm } = this.props;
    return (
      <React.Fragment>
        <Row>
          <Col>
            <Icon
              type="arrow_back"
              style={{ cursor: 'pointer', marginBottom: '4px' }}
              onClick={this.handleBackPage}
            />
            <span style={{ marginLeft: '6px' }}>
              {intl.get('hpfm.organization.view.title.employeeInformation').d('员工信息详情')}
            </span>
          </Col>
        </Row>
        <Divider />
        {customizeForm(
          {
            code: 'HPFM.ORG_LIST.DEPARTMENT.EMPLOYEE.DETAIL',
          },
          <Form dataSet={this.employeeDetailFormDS} columns={3} className={styles['detail-from']}>
            <Output name="employeeCode" />
            <Output name="name" />
            <Output name="gender" />
            <Output name="mobile" />
            <Output colSpan={2} name="email" />
            <Output newLine name="unitName" />
            <Output name="positionName" />
            <Output newLine name="status" />
            <Output name="enabledFlag" />
          </Form>
        )}
        <Form columns={3}>{partTimePositionNode}</Form>
      </React.Fragment>
    );
  }
}
