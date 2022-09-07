/* eslint-disable no-shadow */
import React from 'react';

export default (props) => {
  const { value, setValue, readonly } = props;

  const onChange = (e) => {
    let { value } = e.target;
    if (value === '') value = undefined;
    setValue(value);
  };

  return <input type="time" value={value || ''} disabled={readonly} onChange={onChange} />;
};
