export class NewsContainer {
  constructor(components) {
    this.components = components;
  }

  render() {
    let content = `<section class="blog">`;
    for (let c of this.components) {
      content += c.render();
    }
    content += `</section>`;

    return content;
  }
}

export class NewsItem {
  constructor(title, date, parts) {
    this.title = title;
    this.date = date;
    this.parts = parts;
  }

  render() {
    let content = `<article class="blogEntry">
        <h2 class="title">${this.title}</h2>
        <p class="date">${this.date}</p>
        <div class="content">`;

    for (let c of this.parts) {
      content += c.render();
    }

    content += `</div>
        </article>`;

    return content;
  }
}

export class NewsItemPart {
  constructor(content) {
    this.content = content;
  }

  render() {
    return `<div class="part">${this.content}</div>`;
  }
}
