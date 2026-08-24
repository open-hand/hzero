import React from 'react';
import ReactDOM from 'react-dom';
import { Bind } from 'lodash-decorators';
import { Popover } from 'hzero-ui';
import { Button } from 'choerodon-ui/pro';
import { isArray, merge } from 'lodash';

import intl from 'utils/intl';

import Position from './Position';
import styles from './index.less';

class Guider {
  constructor() {
    this.currentStep = 0;
    this.currentElement = null;
    this.steps = [];
    this.isLast = true;
    this.isFirst = true;
    this.isActivated = false;
    this.fixed = false;
    this.hints = [];
    this.options = {
      showButtons: true,
      overlayPadding: 5,
      allowClose: false,
      allowInteraction: true,
      opacity: 0.4,
      backgroundColor: 'black',
    };
  }

  timer;

  componentWillUnmount() {
    this.handleClose();
  }

  @Bind()
  querySelectorPromise(querySelector) {
    const { fixed } = this;

    return new Promise((resolve, reject) => {
      let count = 0;
      this.timer = setInterval(() => {
        let node = null;
        if (fixed) {
          const arr = document.querySelectorAll(querySelector);
          node = arr[arr.length - 1];
        } else {
          node = document.querySelector(querySelector);
        }
        count++;
        if (node) {
          clearInterval(this.timer);
          this.timer = null;
          resolve(node);
        } else if (count >= 5) {
          clearInterval(this.timer);
          this.timer = null;
          reject(new Error('Element not find'));
        }
      }, 500);
    });
  }

  @Bind()
  isDomElement(element) {
    return element && typeof element === 'object' && 'nodeType' in element;
  }

  @Bind()
  createNodeFromString(htmlString, selector) {
    if (selector) {
      const domElement = document.querySelector(selector);
      if (domElement) {
        return domElement;
      }
    }
    const div = document.createElement('div');
    div.innerHTML = htmlString.trim();
    return div.firstChild;
  }

  @Bind()
  isInView(currentElement) {
    const { currentStep, steps } = this;
    const { option: { getContainer = '.page-container' } = {} } = steps[currentStep];
    let top = currentElement.offsetTop;
    let left = currentElement.offsetLeft;
    const width = currentElement.offsetWidth;
    const height = currentElement.offsetHeight;

    let el = currentElement;

    while (el.offsetParent) {
      el = el.offsetParent;
      top += el.offsetTop;
      left += el.offsetLeft;
    }
    const container = document.body.querySelector(getContainer);

    if (container) {
      const { scrollTop = 0, scrollLeft = 0 } = container;
      return (
        top >= scrollTop + 128 &&
        left >= scrollLeft &&
        top + height <= window.pageYOffset + window.innerHeight &&
        left + width <= window.pageXOffset + window.innerWidth
      );
    } else {
      return (
        top >= 128 &&
        left >= 0 &&
        top + height <= window.pageYOffset + window.innerHeight &&
        left + width <= window.pageXOffset + window.innerWidth
      );
    }
  }

  @Bind()
  scrollManually(currentElement) {
    const { currentStep, steps } = this;
    const { option: { getContainer = '.page-container' } = {} } = steps[currentStep];
    let container = null;
    if (this.isDomElement(getContainer)) {
      container = getContainer;
    } else {
      container = document.body.querySelector(getContainer);
    }
    const elementRect = currentElement.getBoundingClientRect();
    const absoluteElementTop = elementRect.top;
    if (absoluteElementTop > window.innerHeight) {
      container.scrollTo(0, 0);
    } else {
      const middle = absoluteElementTop - window.innerHeight;
      container.scrollTo(0, middle);
    }
  }

  @Bind()
  bringInView(currentElement) {
    if (!currentElement) {
      return;
    }
    if (!currentElement.scrollIntoView) {
      this.scrollManually(currentElement);
      return;
    }
    try {
      currentElement.scrollIntoView({
        behavior: 'instant',
        block: 'center',
      });
    } catch (e) {
      this.scrollManually(currentElement);
    }
  }

  // @Bind()
  //  getScrollParent(element) {
  //   let style = window.getComputedStyle(element);
  //   const excludeStaticParent = (style.position === "absolute");
  //   const overflowRegex = /(auto|scroll)/;

  //   if (style.position === "fixed") return document.body;

  //   for (let parent = element; (parent = parent.parentElement);) {
  //     style = window.getComputedStyle(parent);
  //     // if (excludeStaticParent && style.position === "static") {
  //     //   continue;
  //     // }
  //     if (overflowRegex.test(style.overflow + style.overflowY + style.overflowX)) return parent;
  //   }

  //   return document.body;
  // }

  @Bind()
  reset(flag) {
    const { currentElement } = this;
    clearInterval(this.timer);
    this.timer = null;
    const node = document.body.querySelector('#guider-stage');
    if (node) {
      ReactDOM.unmountComponentAtNode(node);
      if (currentElement) {
        currentElement.classList.remove(styles.highlighted);
      }
      if (flag) {
        this.isActivated = false;
        const overlay = document.body.querySelector(`#guider-overlay`);
        if (overlay && overlay.parentElement) {
          document.body.removeChild(overlay);
        }
        document.body.removeChild(node);
      }
    }
  }

  @Bind()
  handleNext() {
    const { currentStep, steps, isActivated } = this;
    const nextStep = steps[currentStep + 1];
    if (isActivated) {
      if (!nextStep) {
        this.handleClose();
      } else {
        const { option: { fixed } = {} } = nextStep;
        const { onNext = (e) => e } = steps[currentStep];
        onNext();
        this.reset();
        this.currentStep = currentStep + 1;
        this.fixed = fixed;
        this.highLight(nextStep.element, currentStep + 1);
      }
    }
  }

  @Bind()
  handleLast() {
    const { currentStep, steps, isActivated } = this;
    const lastStep = steps[currentStep - 1];
    if (isActivated) {
      if (!lastStep) {
        this.handleClose();
      } else {
        const { option: { fixed } = {} } = lastStep;
        const { onLast = (e) => e } = steps[currentStep];
        onLast();
        this.reset();
        this.currentStep = currentStep - 1;
        this.fixed = fixed;
        this.highLight(lastStep.element, currentStep - 1);
      }
    }
  }

  @Bind()
  handleClose() {
    clearInterval(this.timer);
    this.timer = null;
    this.reset(true);
    const { currentStep, steps } = this;
    if (steps[currentStep]) {
      const { onClose = (e) => e } = steps[currentStep];
      onClose();
    }
  }

  @Bind()
  showPopover(popoverOptions = {}, position, offset, flag) {
    const {
      overlayPadding,
      showButtons: popoverShowButtons,
      allowInteraction: interactionFlag,
    } = this.options;
    const { isLast, isFirst } = this;
    const { title, content, showButtons = popoverShowButtons } = popoverOptions;
    const { offsetX, offsetY, selfWidth, selfHeight, allowInteraction = interactionFlag } = offset;
    const requiredPadding = overlayPadding * 2;
    const style = {};
    const width = selfWidth === 0 ? position.right - position.left : selfWidth;
    const height = selfHeight || position.bottom - position.top;
    // style.border = '1px solid #aaa';
    style.position = 'absolute';
    style.width = `${width + requiredPadding}px`;
    style.height = `${height + requiredPadding}px`;
    style.top = `${position.top - overlayPadding + offsetY}px`;
    style.left = `${position.left - overlayPadding + offsetX}px`;
    if (!selfWidth && !selfHeight && !flag) {
      style.top = `50%`;
      style.left = `50%`;
    }
    if (!allowInteraction) {
      const node = document.body.querySelector('#guider-stage');
      node.style.zIndex = 2002;
      style.zIndex = 2002;
    }
    const ButtonsRender = () => (
      <div className={styles['guider-buttons']}>
        {showButtons && (
          <>
            <Button onClick={this.handleClose}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>
            {!isFirst && (
              <Button onClick={this.handleLast}>
                {intl.get('hzero.common.button.previous').d('上一步')}
              </Button>
            )}
            {!isLast && (
              <Button onClick={this.handleNext}>
                {intl.get('hzero.common.button.next').d('下一步')}
              </Button>
            )}
          </>
        )}
      </div>
    );
    const contentText = (
      <div>
        {content}
        {ButtonsRender()}
      </div>
    );
    const popover = (
      <Popover
        title={title || ''}
        overlayStyle={{ zIndex: '2003' }}
        content={contentText}
        visible
        autoAdjustOverflow
      >
        <div style={{ ...style }} />
      </Popover>
    );
    return popover;
  }

  @Bind()
  getCalculatedPosition(node) {
    const { body } = document;
    const { documentElement } = document;
    const scrollTop = window.pageYOffset || documentElement.scrollTop || body.scrollTop;
    const scrollLeft = window.pageXOffset || documentElement.scrollLeft || body.scrollLeft;
    const elementRect = node.getBoundingClientRect();
    return new Position({
      top: elementRect.top + scrollTop,
      left: elementRect.left + scrollLeft,
      right: elementRect.left + scrollLeft + elementRect.width,
      bottom: elementRect.top + scrollTop + elementRect.height,
    });
  }

  @Bind()
  async GetElementAndDetail(current) {
    const querySelector = current;
    let domElement = null;

    if (!querySelector) {
      // TODO:
      const { currentStep, steps } = this;
      if (steps[currentStep]) {
        const { option: { getContainer = '.page-container', offsetY = 0 } = {} } = steps[
          currentStep
        ];
        let container = null;
        if (this.isDomElement(getContainer)) {
          container = getContainer;
        } else {
          container = document.body.querySelector(getContainer);
        }
        if (container && container.scrollTo) {
          if (offsetY > window.innerHeight) {
            const middle = offsetY - window.innerHeight;
            container.scrollTo(0, middle);
          } else {
            container.scrollTo(0, 0);
          }
        }
        return {
          position: new Position({
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
          }),
        };
      }
    }
    if (this.isDomElement(querySelector)) {
      domElement = querySelector;
    } else {
      domElement = await this.querySelectorPromise(querySelector);
    }
    if (!domElement) {
      return null;
    }
    this.bringInView(domElement);
    const position = this.getCalculatedPosition(domElement);
    return {
      position,
      element: domElement,
    };
  }

  @Bind()
  highLight(selector, index = 0) {
    setTimeout(() => {
      this.GetElementAndDetail(selector)
        .then((res) => {
          // if (res) {
          const { overlayPadding, allowClose, opacity, backgroundColor } = this.options;
          const { steps } = this;

          const { option = {}, onHighlighted = (e) => e, popover: popoverOption } = steps[index];
          const { position = {}, element } = res;

          this.currentElement = element;
          this.isLast = steps.length === index + 1;
          this.isFirst = index === 0;

          if (element) {
            element.classList.add(styles.highlighted);
          }

          const node = this.createNodeFromString(
            `<div id='guider-stage'></div>`.trim(),
            '#guider-stage'
          );

          node.style.position = 'absolute';
          node.style.width = `100%`;
          node.style.height = `100%`;
          node.style.top = `0px`;
          node.style.left = `0px`;
          node.style.opacity = `0.4`;
          node.style.zIndex = `-1`;

          document.body.appendChild(node);
          // document.body.appendChild(ctx);

          const {
            allowInteraction,
            allowClose: allowCloseDetail = allowClose,
            offsetX = 0,
            offsetY = 0,
            width: selfWidth = 0,
            height: selfHeight = 0,
            getContainer = '.page-container',
          } = option;

          let ElementFlag = false;
          if (element) {
            ElementFlag = true;
          }
          const popover = this.showPopover(
            popoverOption,
            position,
            { allowInteraction, offsetX, offsetY, selfWidth, selfHeight },
            ElementFlag
          );
          ReactDOM.render(popover, node);

          const requiredPadding = overlayPadding * 2;

          // const overlay = this.createNodeFromString(`<canvas id='guider-canvas'></canvas>`.trim(), '#guider-canvas');
          // const ctx = overlay.getContext('2d');
          // overlay.onclick = null;
          // if (allowCloseDetail) {
          //   overlay.addEventListener('click', this.handleNext, false);
          // }
          // else {
          //   overlay.removeEventListener('click', this.handleNext, false);
          // }
          // overlay.width = window.outerWidth;
          // overlay.height = scrollHeight;
          // overlay.style.position = 'absolute';
          // overlay.style.top = '0px';
          // overlay.style.left = '0px';
          // overlay.style.opacity = opacity;
          // overlay.classList.add(styles.canvas);
          // ctx.fillStyle = backgroundColor;
          // ctx.fillRect(0, 0, window.outerWidth, scrollHeight);
          const width = position.right - position.left;
          // ctx.clearRect(`${position.left - overlayPadding}`, `${position.top - overlayPadding}`, `${width + requiredPadding}`, `${height + requiredPadding}`);
          // document.body.appendChild(overlay);

          const overlay = this.createNodeFromString(
            `<div id='guider-overlay'></div>`.trim(),
            '#guider-overlay'
          );

          // overlay.removeChild(overlay.childNodes );
          overlay.innerHTML = '';

          // TODO:
          let container = null;
          let scrollTop = 0;
          if (this.isDomElement(getContainer)) {
            container = getContainer;
          } else {
            container = document.body.querySelector(getContainer);
          }
          if (container) {
            const { scrollTop: containerScrollTop = 0 } = container;
            scrollTop = containerScrollTop;
          }
          // const { scrollHeight } = container;
          // const scrollTop=0

          const div = document.createElement('div');
          div.style.position = 'absolute';
          div.style.width = `${position.left - overlayPadding + offsetX}px`;
          div.style.height = `100%`;
          div.style.top = `0px`;
          div.style.left = `0px`;
          div.style.opacity = opacity;
          div.style.backgroundColor = backgroundColor;
          div.style.zIndex = 2000;

          const div2 = document.createElement('div');
          div2.style.position = 'absolute';
          div2.style.width = `${width + requiredPadding + selfWidth}px`;
          div2.style.height = `${position.top - overlayPadding + offsetY}px`;
          div2.style.top = `0px`;
          div2.style.left = `${position.left - overlayPadding + offsetX}px`;
          div2.style.opacity = opacity;
          div2.style.backgroundColor = backgroundColor;
          div2.style.zIndex = 2000;

          const div3 = document.createElement('div');
          div3.style.position = 'absolute';
          div3.style.width = `${width + requiredPadding + selfWidth}px`;
          div3.style.height = `calc(100% - ${
            position.bottom - overlayPadding - offsetY - selfHeight - scrollTop
          }px)`;
          div3.style.top = `${position.bottom + overlayPadding + offsetY + selfHeight}px`;
          div3.style.left = `${position.left - overlayPadding + offsetX}px`;
          div3.style.opacity = opacity;
          div3.style.backgroundColor = backgroundColor;
          div3.style.zIndex = 2000;

          const div4 = document.createElement('div');
          div4.style.position = 'absolute';
          div4.style.width = `${
            window.outerWidth - position.right - overlayPadding - offsetX - selfWidth
          }px`;
          div4.style.height = `100%`;
          div4.style.top = `0px`;
          div4.style.left = `${position.right + overlayPadding + offsetX + selfWidth}px`;
          div4.style.opacity = opacity;
          div4.style.backgroundColor = backgroundColor;
          div4.style.zIndex = 2000;

          if (allowCloseDetail) {
            div.addEventListener('click', this.handleNext, false);
            div2.addEventListener('click', this.handleNext, false);
            div3.addEventListener('click', this.handleNext, false);
            div4.addEventListener('click', this.handleNext, false);
          } else {
            div.removeEventListener('click', this.handleNext, false);
            div2.removeEventListener('click', this.handleNext, false);
            div3.removeEventListener('click', this.handleNext, false);
            div4.removeEventListener('click', this.handleNext, false);
          }

          overlay.appendChild(div);
          overlay.appendChild(div2);
          overlay.appendChild(div3);
          overlay.appendChild(div4);

          document.body.appendChild(overlay);

          onHighlighted();
        })
        .catch(() => {
          this.handleNext();
        });
    }, 100);
  }

  @Bind()
  start() {
    this.isActivated = true;
    const { steps } = this;
    if (isArray(steps)) {
      const { option: { fixed = false } = {}, onStart = (e) => e } = steps[0];
      onStart();
      this.currentStep = 0;
      this.isLast = steps.length < 2;
      this.fixed = fixed;
      this.highLight(steps[0].element);
    } else {
      const { option: { fixed = false } = {}, onStart = (e) => e } = steps;
      onStart();
      this.currentStep = 0;
      this.fixed = fixed;
      this.highLight(steps.element);
    }
  }

  defineOptions(options) {
    merge(this.options, options);
  }

  mergeSteps(steps) {
    this.steps = merge(this.steps, steps);
  }

  defineSteps(steps) {
    this.steps = steps;
  }

  defineHints(hints) {
    this.hints = hints;
  }

  @Bind()
  async GetElementAndDetailHint(querySelector) {
    let domElement = null;
    if (this.isDomElement(querySelector)) {
      domElement = querySelector;
    } else {
      domElement = await this.querySelectorPromise(querySelector);
    }
    if (!domElement) {
      return null;
    }
    const position = this.getCalculatedPosition(domElement);
    return {
      position,
      element: domElement,
    };
  }
}

const guider = new Guider();

export default guider;

window.guider = guider;
