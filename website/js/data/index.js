export async function getData() {
  let t = await import('../builder/components/Title.js');
  let d = await import('../builder/components/Dashboard.js');

  return [
    new t.Title('Neolithic Game'),
    new d.Dashboard([
      new d.DashboardButton('/news.html', 'screen02.png', 'News'),
      new d.DashboardButton('/docs.html', 'screen03.png', 'Documen&shy;tation'),
      new d.DashboardButton('/media.html', 'screen04.png', 'Screen&shy;shots'),
    ])
  ];
}
