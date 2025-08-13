import { isNullOrUndefined } from './base';

export default class Watermark {
  constructor(params) {
    this.params = Object.assign(
      {
        container: document.body,
        width: 250,
        height: 150,
        fontSize: 16,
        font: 'microsoft yahei',
        color: '#cccccc',
        content: 'watermark',
        rotate: -30,
        zIndex: 1000,
        opacity: 0.5,
      },
      params
    );

    this.params.x = isNullOrUndefined(params.x) ? this.params.width / 2 : params.x;
    this.params.y = isNullOrUndefined(params.y) ? this.params.height / 2 : params.y;
  }

  toDataURL() {
    const { width, height, fontSize, font, color, rotate, content, opacity, x, y } = this.params;
    const canvas = document.createElement('canvas');
    canvas.setAttribute('width', `${width}px`);
    canvas.setAttribute('height', `${height}px`);

    const ctx = canvas.getContext('2d');

    ctx.clearRect(0, 0, width, height);
    ctx.textBaseline = 'top';
    ctx.textAlign = 'left';
    ctx.fillStyle = color;
    ctx.globalAlpha = opacity;
    ctx.font = `${fontSize}px ${font}`;
    ctx.translate(x, y);
    ctx.rotate((Math.PI / 180) * rotate);
    ctx.translate(-x, -y - fontSize);
    ctx.fillText(content, x, y + fontSize);

    return canvas.toDataURL();
  }

  output() {
    const { zIndex, container } = this.params;
    this.watermarkDiv = document.createElement('div');
    this.watermarkDiv.setAttribute(
      'style',
      `
      position:absolute;
      top:0;
      left:0;
      width:100%;
      height:100%;
      z-index:${zIndex};
      pointer-events:none;
      background-repeat:repeat;
      background-image:url('${this.toDataURL()}')`
    );

    container.style.position = 'relative';
    container.insertBefore(this.watermarkDiv, container.firstChild);
  }

  destroy() {
    if (!this.watermarkDiv) return;
    this.watermarkDiv.remove();
  }
}
