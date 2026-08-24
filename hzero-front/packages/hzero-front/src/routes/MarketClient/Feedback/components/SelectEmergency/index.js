import React, { useState, useEffect, useRef } from 'react';
import { Select } from 'hzero-ui';
import { isValidArray } from '../../utils/base';
import { EMERGENCY } from '../../utils/constants';
import { queryPriority } from '../../services';

const EmergencySelect = React.forwardRef((props) => {
  const [options, setOptions] = useState([]);
  const currentValueRef = useRef(null);
  const { disabled = false, hpmsCode, ...rest } = props;

  useEffect(() => {
    query();
  }, [hpmsCode]);

  const query = async (value = null) => {
    const res = await queryPriority({ hpmsCode: hpmsCode || null });
    if (res && res.failed) {
      setOptions([]);
      return;
    }
    if (value !== currentValueRef.current) return;
    setOptions(res || []);
    if (isValidArray(res) && res.length === 1) {
      props.onChange(res[0]);
    }
  };
  return (
    <Select {...rest} disabled={disabled}>
      {options.map((item) => (
        <Select.Option key={item} value={item}>
          {EMERGENCY[item.toLowerCase()]}
        </Select.Option>
      ))}
    </Select>
  );
});

export default EmergencySelect;
