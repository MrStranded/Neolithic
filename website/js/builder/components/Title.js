export class Title {
  constructor(title) {
    this.title = title;
  }

  render() {
    return `<h1>${this.title}</h1>`;
  }
}
