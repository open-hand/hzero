import React, { Component } from 'react';
import { Form, Button, DataSet, Lov, TextField, IntlField, Switch, Select } from 'choerodon-ui/pro';
import intl from 'utils/intl';
import { isArray, isNull } from 'lodash';
import { formDS, packageDS, packageFormDS, saveFormDS } from '../../stores/DeviceCollectionDS';
import styles from './deviceDrawer.less';

const modelPrompt = 'hiot.deviceCollection.view';

export default class DeviceDrawer extends Component {
  state = {
    step: 1,
  };

  componentDidMount() {
    this.handleQuery();
  }

  formDs = new DataSet(formDS());

  packageDs = new DataSet(packageDS());

  saveFormDs = new DataSet(saveFormDS());

  setDrawerVisible = (state) => {
    this.props.setDrawerVisible(state);
  };

  handleQuery = async () => {
    const { isNew, dcDeviceId } = this.props;
    if (!isNew) {
      this.formDs.setQueryParameter('dcDeviceId', dcDeviceId);
      const res = await this.formDs.query();
      if (res && isArray(res) && res.length > 0) {
        const { packageName } = res[0];
        this.packageDs.queryParameter = {
          packageName,
        };
        const packages = await this.packageDs.query();
        if (packages && isArray(packages) && packages.length > 0) {
          this.getPackageForm();
        }
      }
    }
  };

  firstStep = async () => {
    const gatewayCode = await this.formDs.current.getField('gatewayCode').checkValidity();
    const dcDeviceCode = await this.formDs.current.getField('dcDeviceCode').checkValidity();
    const description = await this.formDs.current.getField('description').checkValidity();
    const heartbeatCycle = await this.formDs.current.getField('heartbeatCycle').checkValidity();
    const gatewayObject = await this.formDs.current.getField('gatewayObject').checkValidity();

    if (gatewayCode && dcDeviceCode && description && heartbeatCycle && gatewayObject) {
      this.setState({
        step: this.state.step + 1,
      });
    }
  };

  secondStep = async () => {
    const validator = await this.formDs.current.getField('packageId').checkValidity();
    if (validator) {
      this.setState({
        step: this.state.step + 1,
      });
    }
  };

  onSave = async () => {
    const { refTable, onCancel = (e) => e } = this.props;
    const formData = this.formDs.toData()[0];
    const packageFormData = this.packageFormDs.toData()[0];
    formData.connectInfo = JSON.stringify(packageFormData);

    const validate = await this.packageFormDs.validate(false, false);
    if (validate) {
      this.formDs.queryParameter = {
        ...formData,
      };

      try {
        const res = await this.formDs.submit();
        if (res) {
          if (res && !res.failed) {
            onCancel();
            refTable();
          }
        }
      } catch (err) {
        // error
      }
    }
  };

  footer = (step) => {
    const { onCancel = (e) => e } = this.props;
    if (step === 1) {
      return (
        <div className="btnDrawer">
          <Button color="primary" onClick={this.firstStep} style={{ height: '28px' }}>
            {intl.get(`${modelPrompt}.button.nextStep`).d('下一步')}
          </Button>
          <Button onClick={onCancel} style={{ height: '28px', minWidth: '68px' }}>
            {intl.get(`hzero.common.button.cancel`).d('取消')}
          </Button>
        </div>
      );
    } else if (step === 2) {
      return (
        <div className="btnDrawer">
          <Button color="primary" onClick={this.secondStep} style={{ height: '28px' }}>
            {intl.get(`${modelPrompt}.button.nextStep`).d('下一步')}
          </Button>
          <Button
            onClick={() => {
              this.setState({
                step: this.state.step - 1,
              });
            }}
            style={{ height: '28px' }}
          >
            {intl.get(`${modelPrompt}.button.preStep`).d('上一步')}
          </Button>
          <Button onClick={onCancel} style={{ height: '28px', minWidth: '68px' }}>
            {intl.get(`hzero.common.button.cancel`).d('取消')}
          </Button>
        </div>
      );
    } else {
      return (
        <div className="btnDrawer">
          <Button color="primary" onClick={this.onSave} style={{ height: '28px', width: '68px' }}>
            {intl.get(`hzero.common.button.sure`).d('确定')}
          </Button>
          <Button
            onClick={() => {
              this.setState({
                step: this.state.step - 1,
              });
            }}
            style={{ height: '28px' }}
          >
            {intl.get(`${modelPrompt}.button.preStep`).d('上一步')}
          </Button>
          <Button onClick={onCancel} style={{ height: '28px', minWidth: '68px' }}>
            {intl.get(`hzero.common.button.cancel`).d('取消')}
          </Button>
        </div>
      );
    }
  };

  onSelect = async (value, oldValue, form) => {
    if (!isNull(value)) {
      const text = form.getField('packageId').getText();
      this.formDs.current.set('packageName', text);
      this.packageDs.queryParameter = {
        packageName: text,
      };
      const res = await this.packageDs.query();
      if (res && isArray(res) && res.length > 0) {
        this.getPackageForm();
      }
    }
  };

  sortElement = (property) => {
    return (a, b) => {
      if (a[property] < b[property]) {
        return -1;
      }
      if (a[property] > b[property]) {
        return 1;
      }
      return 0;
    };
  };

  getPackageForm = () => {
    const { isNew } = this.props;

    let packages = this.packageDs.toData();
    const elements = [];

    packages = packages.sort(this.sortElement('orderPriority'));
    packages.forEach((packageItem) => {
      const element = {};
      element.name = packageItem.attributeCode;
      element.label = intl.get(`${packageItem.multilingualCode}`).d(`${packageItem.description}`);
      element.defaultValidationMessages = {
        valueMissing: intl
          .get(`${packageItem.tipMultilingualCode}`)
          .d(`${packageItem.tipDescription}`),
      };
      let pattern = packageItem.checkRule;
      if (['PORT', 'Station Number'].includes(packageItem.attributeCode)) {
        pattern = pattern.substring(1, pattern.length - 1);
      }
      element.pattern = `${pattern}`;
      element.required = packageItem.inputFlag === 1;
      element.readOnly = packageItem.inputFlag === 2;
      if (!isNew) {
        const { connectInfo } = this.formDs.current.toData();
        if (connectInfo !== '[object Object]') {
          const object = JSON.parse(this.formDs.current.toData().connectInfo);
          for (const obj in object) {
            if (obj === packageItem.attributeCode) {
              element.defaultValue = object[obj];
            }
          }
        }
      }

      if (packageItem.inputBoxType === 'text') {
        element.inputBoxType = 'TextField';
        element.type = 'string';
      } else if (packageItem.inputBoxType === 'dropdown') {
        element.lookupCode = packageItem.valueCode;
        element.inputBoxType = 'Select';
        element.type = 'string';
      } else if (packageItem.inputBoxType === 'lov') {
        element.lovCode = packageItem.valueCode;
        element.type = 'object';
        element.inputBoxType = 'Lov';
      }
      elements.push(element);
    });
    this.packageFormDs = new DataSet({
      ...packageFormDS(),
      fields: elements,
    });
  };

  formCell = () => {
    const packages = this.packageDs.toData();
    return packages.map((packageItem) => {
      const element = {};
      const name = packageItem.attributeCode;
      const { isNew } = this.props;
      if (isNew && this.packageFormDs.current) {
        this.packageFormDs.current.init(name, packageItem.defaultValue);
      }
      if (packageItem.inputBoxType === 'text') {
        element.inputBoxType = 'TextField';
        element.type = 'string';
        return <TextField name={name} key={name} />;
      } else if (packageItem.inputBoxType === 'dropdown') {
        return <Select name={name} key={name} />;
      } else if (packageItem.inputBoxType === 'Lov') {
        return <Lov name={name} key={name} />;
      }
      return <span />;
    });
  };

  render() {
    const { step } = this.state;
    return (
      <div className={styles['drawer-wrap']}>
        <div className={styles['drawer-from-wrap']}>
          {step === 1 && (
            <>
              <Form dataSet={this.formDs} labelWidth={110}>
                <Lov name="gatewayObject" />
                <TextField name="dcDeviceCode" />
                <IntlField name="description" />
                <TextField name="heartbeatCycle" step={1} />
                <Switch name="simulatorFlag" />
                <Switch name="enableFlag" />
              </Form>
            </>
          )}
          {step === 2 && (
            <>
              <Form dataSet={this.formDs}>
                <Select name="packageId" onChange={this.onSelect} />
              </Form>
            </>
          )}
          {step === 3 && (
            <>
              <Form dataSet={this.packageFormDs}>{this.formCell()}</Form>
            </>
          )}
        </div>
        <div className={styles['buttons-wrap']}>{this.footer(step)}</div>
      </div>
    );
  }
}
