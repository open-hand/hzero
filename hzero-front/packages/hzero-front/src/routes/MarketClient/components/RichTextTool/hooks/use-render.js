import { useRef, useEffect } from 'react';
import ReactDOM from 'react-dom';

const root = document.body;

/**
 * 将React组件挂载到body节点
 */
export default function useRender() {
  const containerRef = useRef(document.createElement('div'));

  useEffect(() => {
    const container = containerRef.current;
    root.appendChild(container);
    return () => {
      root.removeChild(container);
    };
  }, []);

  function render(component) {
    return ReactDOM.createPortal(component, containerRef.current);
  }
  return { renderInBody: render };
}
