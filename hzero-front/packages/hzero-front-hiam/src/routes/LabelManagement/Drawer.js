import React from 'react';
import { Form, TextField, Select, IntlField, Switch, Tooltip } from 'choerodon-ui/pro';
import { Icon } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

export default class Drawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      type: undefined,
    };
  }

  componentDidMount() {
    const { ds, id } = this.props;
    if (id) {
      ds.setQueryParameter('id', id);
      ds.query().then((res) => {
        if (res) {
          this.setState({ type: res.type });
          if (res.type === 'API') {
            const tag = ds.getField('tag');
            tag.set('required', true);
          }
        }
      });
    }
  }

  @Bind()
  handleSelect() {
    const { ds } = this.props;
    const type = ds.getField('type').getValue();
    const tag = ds.getField('tag');
    if (this.state.type !== type) {
      this.setState({ type });
      if (type === 'API') {
        tag.set('required', true);
      } else {
        tag.set('required', false);
      }
      ds.records[0].set('tag', undefined);
    }
  }

  render() {
    const { ds, isCreate } = this.props;
    const { type } = this.state;
    return (
      <Form dataSet={ds} labelWidth={120}>
        <TextField name="name" disabled={!isCreate} maxLength={64} />
        <Select name="type" disabled={!isCreate} onChange={this.handleSelect} />
        {/* <Select name="fdLevel" disabled={!isCreate} /> */}
        {type === 'API' ? <Select name="tag" /> : <TextField name="tag" maxLength={240} />}
        <IntlField name="description" />
        <Switch name="enabledFlag" />
        <Switch
          name="inheritFlag"
          disabled={!isCreate}
          label={
            <>
              {intl.get('hiam.labelManagement.model.labelManagement.inheritFlag').d('是否可继承')}
              <Tooltip
                title={intl
                  .get('hiam.labelManagement.view.title.inheritFlag')
                  .d(
                    '在标签标识的数据具有层级结构时，用于标识高层级数据的标签是否可继承到低层级数据'
                  )}
              >
                <Icon type="help_outline" />
              </Tooltip>
            </>
          }
        />
        <Switch
          name="visibleFlag"
          label={
            <>
              {intl.get('hiam.labelManagement.model.labelManagement.visibleFlag').d('是否页面可见')}
              <Tooltip
                title={intl
                  .get('hiam.labelManagement.view.title.visibleFlag')
                  .d('用于标识在业务数据中，该标签是否可在页面中查看')}
              >
                <Icon type="help_outline" />
              </Tooltip>
            </>
          }
        />
      </Form>
    );
  }
}
