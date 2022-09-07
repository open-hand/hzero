import React from 'react';
import { Button, Col, Form, Lov, Row, Table } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

export default class RuleDrawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      alertRelId: '',
    };
  }

  componentDidMount() {
    const { isCreate } = this.props;
    if (!isCreate) {
      this.fetchData();
    }
  }

  async fetchData() {
    const { data, alertRuleDs, editRuleDrawerDs } = this.props;
    const { alertName, alertCode, alertRelId } = data;
    this.setState({
      alertRelId,
    });
    alertRuleDs.setQueryParameter('alertCode', alertCode);
    alertRuleDs.setQueryParameter('alertRelId', alertRelId);
    await alertRuleDs.query();
    alertRuleDs.setQueryParameter('alertRelId', '');
    editRuleDrawerDs.current.set('alertCode', alertCode);
    editRuleDrawerDs.current.set('alertName', alertName);
  }

  get alertRuleColumns() {
    const { isOnlyView = false } = this.props;
    return [{ name: 'targetKey' }, { name: 'sourceLov', editor: !isOnlyView }];
  }

  // 下拉框变化时触发
  @Bind()
  onSelectChange(value, record) {
    record.set('sourceLov', '');
  }

  // Lov变化时触发
  @Bind()
  onLovChange(value = {}) {
    const { alertRuleDs } = this.props;
    if (value) {
      alertRuleDs.setQueryParameter('alertCode', value.alertCode);
      this.setState({
        alertRelId: value.alertRelId,
      });
      alertRuleDs.query();
    } else {
      alertRuleDs.setQueryParameter('alertCode', '');
    }
  }

  @Bind()
  async handleReload() {
    const { alertRelId } = this.state;
    const { alertRuleDs } = this.props;
    alertRuleDs.refresh = true;
    alertRuleDs.setQueryParameter('alertRelId', alertRelId);
    await alertRuleDs.query();
    alertRuleDs.refresh = false;
    alertRuleDs.setQueryParameter('alertRelId', '');
  }

  render() {
    const { isCreate, alertRuleDs, editRuleDrawerDs } = this.props;
    return (
      <>
        <Row gutter={24}>
          <Col span={18}>
            <Form dataSet={editRuleDrawerDs}>
              <Lov
                name="alertLov"
                disabled={!isCreate}
                onChange={(value) => {
                  this.onLovChange(value);
                }}
              />
            </Form>
          </Col>
          {!isCreate && (
            <Col span={6}>
              <Button icon="sync" style={{ margin: '10px' }} onClick={this.handleReload}>
                {intl.get('hzero.common.button.reload').d('重新加载')}
              </Button>
            </Col>
          )}
        </Row>
        <Table dataSet={alertRuleDs} columns={this.alertRuleColumns} />
      </>
    );
  }
}
