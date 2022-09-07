import React, { PureComponent } from 'react';
import { Icon, Input } from 'hzero-ui';

export default class ParentDirInput extends PureComponent {
  render() {
    const { textValue, disabled = false, onClick = e => e } = this.props;
    return (
      <Input
        readOnly
        disabled={disabled}
        value={textValue}
        onClick={onClick}
        style={{
          verticalAlign: 'middle',
          position: 'relative',
          top: -1,
        }}
        addonAfter={
          <Icon
            type="search"
            onClick={!disabled && onClick}
            style={{ cursor: disabled ? 'not-allowed' : 'pointer' }}
          />
        }
      />
    );
  }
}
