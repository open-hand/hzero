import React, { forwardRef } from 'react';
import { Select } from 'hzero-ui';

const { Option } = Select;

const CategorySelect = ({ onChange, categories = [], ...rest }, ref) => {
  return (
    <Select ref={ref} onChange={onChange} {...rest}>
      {categories.map(({ value, meaning }) => (
        <Option key={`${value}`}>{meaning}</Option>
      ))}
    </Select>
  );
};

export default forwardRef(CategorySelect);
