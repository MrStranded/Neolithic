export class Dashboard {
  constructor(components) {
    this.components = components;
  }

  render() {
    let content = `<section class="dashboard">`;
    for (let c of this.components) {
      content += c.render();
    }
    content += `</section>`;

    return content;
  }
}

export class DashboardButton {
  constructor(url, image, title) {
    this.url = url;
    this.image = image;
    this.title = title;
  }

  render() {
    return `
      <a href="${this.url}" class="dashboardButton" aria-label="${this.title}">
        <img src="img/${this.image}" alt="">
        <h2 class="title">${this.title}</h2>
      </a>
    `;
  }
}
