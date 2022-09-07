import React, { PureComponent } from 'react';
import { Spin } from 'choerodon-ui';
import { Form, TextField, Select, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';
import { castLineFormDS } from '@/stores/Orchestration/DataTransformDS';

export default class CastLineDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      detailLoading: false,
      castType: '',
    };
    this.castLineFormDS = new DataSet({
      ...castLineFormDS({
        onFieldUpdate: this.handleFieldUpdate,
      }),
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
    const { currentRecord, tenantId } = this.props;
    if (isUndefined(currentRecord)) {
      // 新建情况
      this.castLineFormDS.create({ tenantId });
    } else if (currentRecord.status === 'add') {
      // 新建再编辑情况
      const data = currentRecord.toData();
      this.castLineFormDS.create(data);
      this.setState({ castType: data.castType });
    } else {
      // 保存过后再编辑情况
      const data = currentRecord.toData();
      this.castLineFormDS.loadData([data]);
      this.setState({ castType: data.castType });
    }
  }

  /**
   * 监听数据转换类型变化
   */
  @Bind()
  handleFieldUpdate({ name, value }) {
    if (name === 'castType') {
      this.setState({ castType: value });
    }
  }

  render() {
    const { detailLoading, castType } = this.state;
    const { disabledFlag } = this.props;
    return (
      <Spin spinning={detailLoading}>
        <Form dataSet={this.castLineFormDS} columns={1} disabled={disabledFlag}>
          <TextField name="castRoot" restrict="a-zA-Z0-9-_./" />
          <TextField name="castField" restrict="a-zA-Z0-9-_./" />
          <Select name="castType" onChange={(val) => this.setState({ castType: val })} />
          {castType === 'LOV' && <TextField name="castLovCode" />}
          {castType === 'LOV' && <TextField name="castLovField" />}
          {castType === 'LOV' && <Select name="castLovLang" />}
        </Form>
      </Spin>
    );
  }
}
