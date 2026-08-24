import React from 'react';
import { Input, Select } from 'hzero-ui';

const { Option } = Select;

/**
 *  组件需传值，1.一个数组 queryArray:[{queryLabel:''(必传),queryName:''(必传), inputProps: {}(可不传)}]
 *
 * @export
 * @class OptionInput
 * @extends {React.Component}
 */
export default class OptionInput extends React.Component {
  constructor(props) {
    super(props);
    const { queryArray = [] } = props;
    this.state = {
      fieldValue: null,
      fieldName: queryArray[0].queryName,
      inputProps: queryArray[0].inputProps, // input框的特定属性
    };
  }

  handleSelectChange = optionValue => {
    const { value, queryArray = [] } = this.props;
    const { fieldValue } = this.state;

    const queryArrayItem = queryArray.filter(o => o.queryName === optionValue);
    const { inputProps } = queryArrayItem[0];

    this.setState({ fieldName: optionValue, inputProps }, () => {
      if (value) {
        this.fireChange({
          [optionValue]: fieldValue,
        });
      }
    });
  };

  fireChange(valueList) {
    const { onChange } = this.props;
    if (onChange) {
      onChange(valueList);
    }
  }

  handleInputChange = e => {
    const { fieldName } = this.state;
    this.setState({ fieldValue: e.target.value });
    this.fireChange({
      [fieldName]: e.target.value,
    });
  };

  render() {
    const { queryArray = [], value, ...otherProps } = this.props;
    const { inputProps } = this.state;

    let selectValue = queryArray[0].queryName;
    let inputValue = null;
    if (value && Object.keys(value).length >= 1) {
      // eslint-disable-next-line
      selectValue = Object.keys(value)[0];
      inputValue = value[selectValue];
    }

    const selectBefore = (
      <Select
        style={{ minWidth: 80 }}
        defaultValue={selectValue}
        onChange={this.handleSelectChange}
      >
        {queryArray.map(item => (
          <Option key={item.queryName} value={item.queryName}>
            {item.queryLabel}
          </Option>
        ))}
      </Select>
    );
    return (
      <Input
        {...otherProps}
        {...inputProps}
        value={inputValue}
        addonBefore={selectBefore}
        onChange={this.handleInputChange}
      />
    );
  }
}
