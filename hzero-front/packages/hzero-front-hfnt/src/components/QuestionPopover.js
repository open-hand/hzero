/**
 * 问号，提示渲染
 * @author aaron.yi
 * @date 2020/7/13
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */
import React from 'react';
import { Popover, Icon } from 'hzero-ui';

const QuestionPopover = (props) => {
  const {
    text = '',
    message,
    iconStyle = { marginLeft: 2 },
    iconType = 'question-circle',
    ...extraProps
  } = props;
  return (
    <>
      {text}
      <Popover content={message} overlayStyle={{ maxWidth: 450 }} {...extraProps}>
        <Icon style={iconStyle} type={iconType} />
      </Popover>
    </>
  );
};

export default QuestionPopover;
