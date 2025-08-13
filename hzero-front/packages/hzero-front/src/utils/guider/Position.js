export default class Position {
  constructor({ left = 0, top = 0, right = 0, bottom = 0 } = {}) {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
  }

  canHighlight() {
    return this.left < this.right && this.top < this.bottom;
  }
}
