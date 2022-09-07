import React from 'react';

const iconSize = [12, 12];
class Line extends React.Component {
  removeHandle = (edit) => {
    if (edit) {
      this.props.removeRelation(this.props.data);
    }
  };

  mouseOver = (edit) => {
    if (edit) {
      // this.props.toTop(this.props.data);
    }
  };

  render() {
    const {
      startX = 0,
      startY = 0,
      endX = 0,
      endY = 0,
      currentRelation,
      data,
      edit,
      closeIcon,
    } = this.props;
    return (
      // eslint-disable-next-line jsx-a11y/mouse-events-have-key-events
      <g
        className={`path-end ${currentRelation === data ? 'active' : ''} ${edit ? '' : 'disabled'}`}
        onMouseOver={() => this.mouseOver(edit)}
        onClick={() => this.removeHandle(edit)}
      >
        <path
          className="line"
          d={`M${startX}, ${startY} L${endX}, ${endY}`}
          markerEnd="url(#markerArrow)"
        />
        <image
          className="icon-remove"
          x={(endX + startX - iconSize[0]) / 2}
          y={(endY + startY - iconSize[1]) / 2}
          width={iconSize[0]}
          height={iconSize[1]}
          xlinkHref={
            closeIcon ||
            'data:image/png;base64,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'
          }
        />
      </g>
    );
  }
}

export default Line;
