import React from 'react';
import { RegisterEdge } from 'gg-editor';
import { INS_STATUS_GANTT, INS_STATUS } from '@/constants/constants';

class FlowEdge extends React.Component {
  render() {
    const config = {
      getStyle(item) {
        const model = item.getModel();
        const { statusCode, size, renderColorFlag } = model;
        const color =
          INS_STATUS_GANTT[
            INS_STATUS.FAILED === statusCode && renderColorFlag ? INS_STATUS.SUCCESSFUL : statusCode
          ];
        return {
          stroke: color || '#b4bac2',
          lineWidth: size || 2,
          startArrow: false,
          endArrow: true,
        };
      },
    };

    return <RegisterEdge name="flow-edge" config={config} extend="flow-smooth" />;
  }
}

export default FlowEdge;
