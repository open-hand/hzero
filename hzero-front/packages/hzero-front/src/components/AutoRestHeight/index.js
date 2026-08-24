/**
 *
 * 自动设置Table高度
 * @data: 2020/8/19
 * @author: Nemo <yingbin.jiang@hand-chian.com>
 * @copyright: Copyright (c) 2020, Hand
 */

import React, { useRef, useEffect, useState } from 'react';
import ReactDOM from 'react-dom';
import { isEqual } from 'lodash';

// export interface AutoRestHeightProps extends HTMLAttributes<Element> {
//   diff: number;  // 差距间隙，可以用作微调
//   topSelector: string; // 内部 top 位置的 query selector
// }

/**
 * 自动适应高度
 */
const AutoRestHeight = ({ children, diff = 80, topSelector, style, bodyStyle, type = 'c7n' }) => {
  const childrenRef = useRef(null);
  const frame = useRef(0);
  const [height, setHeight] = useState('auto');

  useEffect(() => {
    const resize = () => {
      if (childrenRef.current) {
        // eslint-disable-next-line
        const childrenRefWrapperDom = ReactDOM.findDOMNode(childrenRef.current);
        let childrenRefDom = childrenRefWrapperDom;
        if (topSelector) {
          childrenRefDom = childrenRefDom.querySelector(topSelector);
        }
        const { top: offsetTop } = childrenRefDom.getBoundingClientRect();
        setHeight(window.innerHeight - offsetTop - diff);
        if (childrenRef.current.handleResize) {
          childrenRef.current.handleResize();
        }
      }
    };
    resize();
    const handler = () => {
      cancelAnimationFrame(frame.current);
      frame.current = requestAnimationFrame(resize);
    };
    window.addEventListener('resize', handler);
    return () => {
      cancelAnimationFrame(frame.current);
      window.removeEventListener('resize', handler);
    };
  }, []);

  if (React.isValidElement(children)) {
    if (isEqual(type, 'hzero-ui')) {
      let newScroll = {
        y: height - 40,
      };
      if (children.props && children.props.scroll) {
        const { x: newX } = children.props.scroll;
        newScroll = {
          x: newX,
          y: newScroll.y,
        };
      }
      return React.cloneElement(children, {
        ref: childrenRef,
        scroll: newScroll,
        bodyStyle: {
          ...bodyStyle,
          height: height - 40,
        },
        style: {
          ...style,
          height,
        },
      });
    } else {
      return React.cloneElement(children, {
        ref: childrenRef,
        style: {
          ...style,
          height,
        },
      });
    }
  } else {
    return null;
  }
};

export default AutoRestHeight;
