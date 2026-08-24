import React, { PureComponent } from 'react';
import { Input, Icon } from 'hzero-ui';

export default class AssignLevelValueInput extends PureComponent {
  render() {
    const { textValue, onClick = e => e, ref } = this.props;
    return (
      <Input
        readOnly
        ref={ref}
        value={textValue}
        onClick={onClick}
        style={{
          verticalAlign: 'middle',
          position: 'relative',
          top: -1,
        }}
        addonAfter={<Icon type="search" onClick={onClick} style={{ cursor: 'pointer' }} />}
      />
    );
  }
}
