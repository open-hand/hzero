import React from 'react';
import { Input } from 'hzero-ui';
import Lov from 'components/Lov';
import ValueList from 'components/ValueList';

const render = (props) => {
  const { data, config, required, ...otherProps } = props;
  let textValue;
  if (
    config[data.itemCode] &&
    (data.itemTypeCode.toLowerCase() === 'lov_view' || data.itemTypeCode.toLowerCase() === 'lov') &&
    JSON.parse(config[data.itemCode])
  ) {
    textValue = JSON.parse(config[data.itemCode]);
  }

  switch (data.itemTypeCode.toLowerCase()) {
    case 'lov':
      return (
        <ValueList
          style={{ width: '100%' }}
          allowClear={!required}
          lovCode={data.valueSet}
          {...otherProps}
          textValue={textValue?.meaning}
        />
      );
    case 'lov_view':
      // let textValue;
      // if (config[data.itemCode] && JSON.parse(config[data.itemCode])) {
      //   textValue = JSON.parse(config[data.itemCode]);
      // }
      return (
        <Lov
          textValue={textValue?.meaning}
          allowClear={!required}
          code={data.valueSet}
          {...otherProps}
        />
      );
    default:
      return <Input type={data.itemTypeCode.toLowerCase()} {...otherProps} />;
  }
};

export default render;
